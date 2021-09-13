package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.core.FileInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class ViewPanel extends JPanel {

    public final JTextField pathTextBox = new JTextField();
    public final JButton backButton = new JButton("back");

    public final FileTable table;

    public final JButton flushButton = new JButton("flush");
    public final JButton mkdirButton = new JButton("new directory");
    public final JButton removeButton = new JButton("remove");
    public final JButton moveButton = new JButton("move");
    public final JButton copyButton = new JButton("copy");
    public final JButton uploadButton = new JButton("upload");
    public final JButton downloadButton = new JButton("download");
    public final JButton taskViewButton = new JButton("task view");

    public ViewPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(pathTextBox, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        table = new FileTable();
        add(table, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1,7,3,1));
        buttonPanel.add(flushButton);
        buttonPanel.add(mkdirButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(taskViewButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}
