package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.FileInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class ViewPanel extends JPanel {

    public final JTable table;
    private final DefaultTableModel defaultTableModel;

    public ViewPanel() {
        setLayout(new GridLayout(1, 1));

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

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    public void addRow(FileInfo info) {
        var name = info.getName();
        var isDir = info.isDirectory() ? "Dir" : "File";
        var size = info.getSize();
        var last = new Date(info.getLast());
        defaultTableModel.addRow(new Object[]{name, isDir, size, last.toString()});
    }

    public void clear(){
        defaultTableModel.setRowCount(0);
    }
}
