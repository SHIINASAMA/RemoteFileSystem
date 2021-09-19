package pers.kaoru.rfs.client.ui.control;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TaskTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            ((Component) value).setBackground(new Color(184, 207, 229));
        } else {
            ((Component) value).setBackground(Color.white);
        }

        if (column == 2) {
            return (JProgressBar) value;
        } else {
            var label = (JLabel) value;
            label.setOpaque(true);
            return label;
        }
    }
}
