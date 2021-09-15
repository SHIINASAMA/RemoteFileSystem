package pers.kaoru.rfs.client.ui.control;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TaskTable extends JScrollPane {

    private final DefaultTableModel defaultTableModel;
    private final JTable table;

    public TaskTable() {
        super();

        String[] columnNames = {"name", "type", "progress", "speed"};
        Object[][] data = {};
        defaultTableModel = new DefaultTableModel(data, columnNames);

        table = new JTable(defaultTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setCellRenderer(new TaskTableCellRenderer());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(29);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setVisible(true);

        setViewportView(table);
        setVisible(true);

    }
}
