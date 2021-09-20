package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.client.ui.control.TaskTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class TaskPanel extends JPanel {

    public final TaskTable table = new TaskTable();
    public final JButton backButton = new JButton("back");

    private final JPopupMenu menu = new JPopupMenu();
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

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    menu.show(getParent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void updateTable() {
        table.updateUI();
    }

    public void clear() {
        table.clear();
    }
}
