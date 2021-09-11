package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.FileInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class ViewPanel extends JPanel {

    public final JTextField pathTextBox = new JTextField();
    public final JButton backButton = new JButton("back");
    public final JTable table;

    public final JButton flushButton = new JButton("flush");
    public final JButton mkdirButton = new JButton("new directory");
    public final JButton removeButton = new JButton("remove");
    public final JButton moveButton = new JButton("move");
    public final JButton copyButton = new JButton("copy");
    public final JButton uploadButton = new JButton("upload");
    public final JButton downloadButton = new JButton("download");
    public final JButton taskViewButton = new JButton("task view");

    private final DefaultTableModel defaultTableModel;

    public ViewPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(pathTextBox, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

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
        table.setRowHeight(26);

        JPanel buttonPanel = new JPanel(new GridLayout(1,7,3,1));
        buttonPanel.add(flushButton);
        buttonPanel.add(mkdirButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(taskViewButton);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addRow(FileInfo info) {
        var name = info.getName();
        var isDir = info.isDirectory() ? "Dir" : "File";
        var size = info.getSize();
        var last = new Date(info.getLast());
        defaultTableModel.addRow(new Object[]{name, isDir, size, last.toString()});
    }

    public void clear() {
        defaultTableModel.setRowCount(0);
    }
}
