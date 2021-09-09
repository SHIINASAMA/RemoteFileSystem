package pers.kaoru.rfs.client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;

public class ViewPanel extends JPanel {

    public ViewPanel() {
        setLayout(new GridLayout(1,1));

        String[] columnNames = {"name", "attributes", "size", "last modified"};
        Object[][] data = {};

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }
}
