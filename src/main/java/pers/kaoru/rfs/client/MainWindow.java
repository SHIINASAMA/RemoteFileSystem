package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.web.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainWindow extends JFrame {

    private final CardLayout layout = new CardLayout();

    private final LoginPanel loginPanel = new LoginPanel();
    private final ViewPanel viewPanel = new ViewPanel();
    private String token = "";

    private String host = "";
    private int port = 0;
    private Boolean flushState = false;
    private final Router router = new Router();

    public MainWindow() {
        String iconPath = Objects.requireNonNull(getClass().getResource("/icon.png")).getPath();
        setIconImage(new ImageIcon(iconPath).getImage());
        setTitle("RFS Client");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(layout);

        initLoginPanel();
        initViewPanel();

        setVisible(true);

        /// 测试用参数
        loginPanel.hostTextBox.setText("localhost");
        loginPanel.portTextBox.setText("8080");
        loginPanel.nameTextBox.setText("root");
        loginPanel.pwdTextBox.setText("123");
    }

    private void initLoginPanel() {
        loginPanel.loginButton.addActionListener(func -> login());
        add(loginPanel, "login");
    }

    private void initViewPanel() {
        viewPanel.backButton.addActionListener(func -> back());

        viewPanel.pathTextBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    jump(viewPanel.pathTextBox.getText());
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        viewPanel.table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int row = viewPanel.table.getSelectedRow();
                    String type = (String) viewPanel.table.getValueAt(row, 1);
                    if (type.equals("Dir")) {
                        String name = (String) viewPanel.table.getValueAt(row, 0);
                        onward(name);
                    }
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

        viewPanel.flushButton.addActionListener(func -> onward("/"));
        viewPanel.mkdirButton.addActionListener(func -> newDir());
        viewPanel.removeButton.addActionListener(func -> remove());

        add(viewPanel, "view");
    }

    private void login() {

        loginPanel.loginButton.setEnabled(false);
        loginPanel.cancelButton.setEnabled(false);

        new SwingWorker<String, Void>() {

            @Override
            protected void done() {
                loginPanel.loginButton.setEnabled(true);
                loginPanel.cancelButton.setEnabled(true);
                try {
                    token = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (token != null) {
                    layout.show(getContentPane(), "view");
                    jump("/");
                }
            }

            @Override
            protected String doInBackground() throws Exception {
                host = loginPanel.hostTextBox.getText();
                var portStr = loginPanel.portTextBox.getText();
                var name = loginPanel.nameTextBox.getText();
                var pwd = loginPanel.pwdTextBox.getPassword();

                if (host.isEmpty() || portStr.isEmpty() || name.isEmpty() || pwd.length == 0) {
                    JOptionPane.showMessageDialog(getContentPane(), "Please enter complete information", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return null;
                }

                for (char c : portStr.toCharArray()) {
                    if (!(c >= '0' && c <= '9')) {
                        return null;
                    }
                }
                port = Integer.parseInt(portStr);


                String token;
                Response response;
                try {
                    response = ClientUtils.Verify(host, port, name, new String(pwd));
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), exception.getMessage());
                    return null;
                }

                if (response.getCode() == ResponseCode.OK) {
                    token = response.getHeader("token");
                    return token;
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                    return null;
                }
            }
        }.execute();
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

        viewPanel.pathTextBox.setEnabled(false);
        viewPanel.backButton.setEnabled(false);
        viewPanel.table.setFocusable(false);

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    viewPanel.pathTextBox.setEnabled(true);
                    viewPanel.backButton.setEnabled(true);
                    viewPanel.table.setFocusable(true);
                    flushState = false;
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }

                if (response.getCode() == ResponseCode.OK) {
                    viewPanel.clear();
                    var list = FileInfo.FileInfosBuild(response.getHeader("list"));
                    for (var item : list) {
                        viewPanel.addRow(item);
                    }
                    router.reset(path);
                    viewPanel.pathTextBox.setText(router.toString());
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }

                viewPanel.pathTextBox.setEnabled(true);
                viewPanel.backButton.setEnabled(true);
                viewPanel.table.setFocusable(true);
                flushState = false;
            }

            @Override
            protected Response doInBackground() throws Exception {
                return ClientUtils.ListShow(host, port, path, token);
            }
        }.execute();

    }

    /**
     * 刷新 JTable
     *
     * @param isBack  为 true 时 path 参数可为 null
     * @param subName 子目录名称
     */
    private void flush(Boolean isBack, String subName) {
        if (flushState) {
            return;
        }
        flushState = true;

        viewPanel.pathTextBox.setEnabled(false);
        viewPanel.backButton.setEnabled(false);
        viewPanel.table.setFocusable(false);

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    viewPanel.pathTextBox.setEnabled(true);
                    viewPanel.backButton.setEnabled(true);
                    viewPanel.table.setFocusable(true);
                    flushState = false;
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }


                if (response.getCode() == ResponseCode.OK) {
                    viewPanel.clear();
                    var list = FileInfo.FileInfosBuild(response.getHeader("list"));
                    for (var item : list) {
                        viewPanel.addRow(item);
                    }

                    if (isBack) {
                        router.back();
                    } else {
                        router.enter(subName);
                    }
                    viewPanel.pathTextBox.setText(router.toString());
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }

                viewPanel.pathTextBox.setEnabled(true);
                viewPanel.backButton.setEnabled(true);
                viewPanel.table.setFocusable(true);
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
        }.

                execute();

    }

    private void newDir() {
        String source = JOptionPane.showInputDialog(getContentPane(), "make a new directory");
        if(source == null) return;
        char[] chars = {'\"', '*', '?', '<', '>', '|'};
        for (char c : chars) {
            if (source.indexOf(c) != -1) {
                JOptionPane.showMessageDialog(getContentPane(), "illegal name");
                return;
            }
        }

        source = router + source;
        Response response = null;
        try {
            response = ClientUtils.MakeDirectory(host, port, source, token);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
            return;
        }

        if (response.getCode() == ResponseCode.OK) {
            flush(false, "/");
        } else {
            JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
        }
    }

    private void remove() {
        int index = viewPanel.table.getSelectedRow();
        if (index == -1) {
            return;
        }

        boolean isDir = viewPanel.table.getValueAt(index, 1).equals("Dir");
        String source = router + (String) viewPanel.table.getValueAt(index, 0);
        var opt = JOptionPane.showConfirmDialog(getContentPane(), "are you sure remove this " + (isDir ? "directory" : "file"), "remove operate", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            Response response = null;
            try {
                response = ClientUtils.Remove(host, port, source, token);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                return;
            }

            if (response.getCode() == ResponseCode.OK) {
                flush(false, "/");
            } else {
                JOptionPane.showMessageDialog(getContentPane(), "remove fail");
            }
        }
    }

}
