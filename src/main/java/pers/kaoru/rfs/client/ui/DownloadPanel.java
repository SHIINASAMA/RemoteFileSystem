package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.client.ui.control.TaskTable;

import javax.swing.*;
import java.awt.*;

public class DownloadPanel extends JPanel {

    public final TaskTable table = new TaskTable();
    public final JButton pauseButton = new JButton("pause");
    public final JButton removeButton = new JButton("remove task");
    public final JButton backButton = new JButton("back");

    public DownloadPanel() {
        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(removeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
