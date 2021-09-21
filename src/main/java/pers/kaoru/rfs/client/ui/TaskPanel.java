package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.client.ui.control.TaskTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Objects;

public class TaskPanel extends JPanel {

    public final TaskTable table = new TaskTable();
    public final JButton backButton = new JButton("back");

    public final JPopupMenu menu = new JPopupMenu();
    public JMenuItem pauseMenu;
    public JMenuItem resumeMenu;
    public JMenuItem cancelMenu;

    public TaskPanel() {
        setLayout(new BorderLayout());
        initMenu();

        add(table, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(backButton, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initMenu() {
        ImageIcon pauseIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/pause.png")));
        pauseMenu = new JMenuItem("pause", pauseIcon);
        menu.add(pauseMenu);

        ImageIcon resumeIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/resume.png")));
        resumeMenu = new JMenuItem("resume", resumeIcon);
        menu.add(resumeMenu);

        ImageIcon cancelIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/remove.png")));
        cancelMenu = new JMenuItem("cancel", cancelIcon);
        menu.add(cancelMenu);
    }

    public void updateTable() {
        table.updateUI();
    }

    public void clear() {
        table.clear();
    }

    public void addTableMouseListener(MouseListener listener){
        table.addMouseListener(listener);
    }
}
