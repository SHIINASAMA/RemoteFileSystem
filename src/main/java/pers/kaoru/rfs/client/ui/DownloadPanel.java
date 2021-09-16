package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.client.ui.control.TaskTable;

import javax.swing.*;
import java.awt.*;

public class DownloadPanel extends JPanel {

    public final TaskTable table = new TaskTable();
    public final JButton backButton = new JButton("back");

    public DownloadPanel() {
        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(backButton, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
