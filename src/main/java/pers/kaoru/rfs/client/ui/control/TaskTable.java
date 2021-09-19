package pers.kaoru.rfs.client.ui.control;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TaskTable extends JScrollPane {

    private final DefaultTableModel defaultTableModel;
    private final JTable table;
    private final TaskTableCellRenderer taskTableCellRenderer = new TaskTableCellRenderer();

    public TaskTable() {
        super();

        String[] columnNames = {"name", "type", "progress", "speed", "state"};
        Object[][] data = {};
        defaultTableModel = new DefaultTableModel(data, columnNames);

        table = new JTable(defaultTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(0).setCellRenderer(taskTableCellRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(taskTableCellRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(taskTableCellRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(taskTableCellRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(taskTableCellRenderer);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(29);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setVisible(true);

        setViewportView(table);
        setVisible(true);

    }

    public void clear() {
        defaultTableModel.setRowCount(0);
    }
}
