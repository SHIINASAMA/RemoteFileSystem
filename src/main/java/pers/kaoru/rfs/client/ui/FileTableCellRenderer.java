package pers.kaoru.rfs.client.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class FileTableCellRenderer extends DefaultTableCellRenderer {

    private final ImageIcon dirIcon;
    private final ImageIcon fileIcon;

    public FileTableCellRenderer() {
        dirIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/dir.png")));
        fileIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/file.png")));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = null;
        if (column == 0) {
            if (table.getValueAt(row, 1).equals("DIR")) {
                label = new JLabel((String) value, dirIcon, JLabel.LEFT);
            } else {
                label = new JLabel((String) value, fileIcon, JLabel.LEFT);
            }
        } else {
            label = new JLabel((String) value);
        }

        label.setOpaque(true);
        if (isSelected) {
            label.setBackground(new Color(184, 207, 229));
        } else {
            label.setBackground(Color.white);
        }
        return label;
    }
}
