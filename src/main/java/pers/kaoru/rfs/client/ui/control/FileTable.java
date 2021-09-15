package pers.kaoru.rfs.client.ui.control;

import pers.kaoru.rfs.client.BitCount;
import pers.kaoru.rfs.core.FileInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileTable extends JScrollPane {

    private final DefaultTableModel defaultTableModel;
    private final JTable table;

    public FileTable() {
        super();

        String[] columnNames = {"name", "attributes", "size", "last modified"};
        Object[][] data = {};
        defaultTableModel = new DefaultTableModel(data, columnNames);

        table = new JTable(defaultTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(0).setCellRenderer(new FileTableCellRenderer());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(29);
        table.setAutoCreateRowSorter(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setVisible(true);

        setViewportView(table);
        setVisible(true);
    }

    public void addRow(FileInfo info) {
        var name = info.getName();
        var isDir = info.isDirectory() ? "DIR" : "FILE";
        var size = info.getSize();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var last = simpleDateFormat.format(new Date(info.getLast()));
        defaultTableModel.addRow(new Object[]{name, isDir, BitCount.ToString(size), last});
    }

    public FileInfo getRow(int index) {
        var name = (String) table.getValueAt(index, 0);
        var isDir = table.getValueAt(index, 1).equals("DIR");
        var last = (String) table.getValueAt(index, 3);
        try {
            return new FileInfo(name, isDir, 0L, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(last).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getSelectedIndex() {
        return table.getSelectedRow();
    }

    public void clear() {
        defaultTableModel.setRowCount(0);
    }

    public void addTableMouseEvent(MouseListener listener) {
        this.table.addMouseListener(listener);
    }
}
