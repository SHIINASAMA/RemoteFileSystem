package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.client.ClientUtils;
import pers.kaoru.rfs.client.Router;
import pers.kaoru.rfs.client.ui.control.FileTable;
import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.web.Response;
import pers.kaoru.rfs.core.web.ResponseCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SelectWindow extends JDialog {

    private final JButton flushButton = new JButton("flush");
    private final JButton backButton = new JButton("back");
    private final FileTable table = new FileTable();
    private final JTextField pathTextBox = new JTextField();
    private final JTextField nameTextBox = new JTextField();
    private final JButton selectButton = new JButton("select");

    private final Router router = new Router();

    private Boolean flushState = false;

    private final String host;
    private final int port;
    private final String token;

    private Boolean isSelected = false;

    public SelectWindow(JFrame parent, String host, int port, String token) {
        super(parent);
        this.host = host;
        this.port = port;
        this.token = token;

        setModal(true);
        String iconPath = Objects.requireNonNull(getClass().getResource("/res/icon.png")).getPath();
        setIconImage(new ImageIcon(iconPath).getImage());
        setTitle("select a location");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        pathTextBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    jump(pathTextBox.getText());
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        backButton.addActionListener(func -> back());
        flushButton.addActionListener(func -> flush(false, "/"));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(flushButton, BorderLayout.EAST);
        topPanel.add(pathTextBox, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        selectButton.addActionListener(func -> {
            isSelected = true;
            dispose();
        });
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(nameTextBox, BorderLayout.CENTER);
        bottomPanel.add(selectButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        table.addTableMouseEvent(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int row = table.getSelectedIndex();
                    var file = table.getRow(row);
                    if (file.isDirectory()) {
                        String name = file.getName();
                        onward(name);
                        nameTextBox.setText("");
                    }
                } else {
                    int row = table.getSelectedIndex();
                    var file = table.getRow(row);
                    nameTextBox.setText(file.getName());
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
        add(table, BorderLayout.CENTER);

        flush(false, "/");
        setVisible(true);
    }

    public String getPath() {
        return router + nameTextBox.getText();
    }

    public Boolean isSelected() {
        return isSelected;
    }

    private void back() {
        if (!router.isEmpty()) {
            flush(true, null);
        }
    }

    private void onward(String subName) {
        flush(false, subName);
    }

    private void jump(String path) {
        if (flushState) {
            return;
        }
        flushState = true;

        pathTextBox.setEnabled(false);
        backButton.setEnabled(false);
        table.setFocusable(false);

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    pathTextBox.setEnabled(true);
                    backButton.setEnabled(true);
                    table.setFocusable(true);
                    flushState = false;
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }

                if (response.getCode() == ResponseCode.OK) {
                    table.clear();
                    var list = FileInfo.FileInfosBuild(response.getHeader("list"));
                    for (var item : list) {
                        table.addRow(item);
                    }
                    router.reset(path);
                    pathTextBox.setText(router.toString());
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }

                pathTextBox.setEnabled(true);
                backButton.setEnabled(true);
                table.setFocusable(true);
                flushState = false;
            }

            @Override
            protected Response doInBackground() throws Exception {
                return ClientUtils.ListShow(host, port, path, token);
            }
        }.execute();
    }

    private void flush(Boolean isBack, String subName) {
        if (flushState) {
            return;
        }
        flushState = true;

        pathTextBox.setEnabled(false);
        backButton.setEnabled(false);
        table.setFocusable(false);

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    pathTextBox.setEnabled(true);
                    backButton.setEnabled(true);
                    table.setFocusable(true);
                    flushState = false;
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }


                if (response.getCode() == ResponseCode.OK) {
                    table.clear();
                    var list = FileInfo.FileInfosBuild(response.getHeader("list"));
                    for (var item : list) {
                        table.addRow(item);
                    }

                    if (isBack) {
                        router.back();
                    } else {
                        router.enter(subName);
                    }
                    pathTextBox.setText(router.toString());
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }

                pathTextBox.setEnabled(true);
                backButton.setEnabled(true);
                table.setFocusable(true);
                flushState = false;
            }

            @Override
            protected Response doInBackground() throws IOException {
                String path;
                if (isBack) {
                    path = router.preback();
                } else {
                    path = router.toString() + subName;
                }

                return ClientUtils.ListShow(host, port, path, token);
            }
        }.execute();
    }
}
