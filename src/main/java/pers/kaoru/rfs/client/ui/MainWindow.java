package pers.kaoru.rfs.client.ui;

import pers.kaoru.rfs.client.ClientUtils;
import pers.kaoru.rfs.client.Config;
import pers.kaoru.rfs.client.Router;
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

public class MainWindow extends JFrame {

    private final CardLayout layout = new CardLayout();

    private final LoginPanel loginPanel = new LoginPanel();
    private final ViewPanel viewPanel = new ViewPanel();
    private final DownloadPanel downloadPanel = new DownloadPanel();
    private String token = "";

    private final JPopupMenu viewMenu = new JPopupMenu();

    private String host = "";
    private int port = 0;
    private Boolean flushState = false;
    private final Router router = new Router();

    private Config config;

    public MainWindow() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/icon.png")));
        setIconImage(icon.getImage());
        setTitle("RFS Client");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(layout);

        initMenu();
        initLoginPanel();
        initViewPanel();
        initDownloadPanel();

        setVisible(true);

        try {
            config = Config.ConfigBuilder("client.json");
        } catch (IOException e) {
            e.printStackTrace();
            config = new Config("", 8080, "", "/Downloads");
        }

        loginPanel.hostTextBox.setText(config.getLastHost());
        loginPanel.portTextBox.setText(String.valueOf(config.getLastPort()));
        loginPanel.nameTextBox.setText(config.getLastName());

        /// 测试用
        loginPanel.pwdTextBox.setText("123");
    }

    private void initLoginPanel() {
        loginPanel.loginButton.addActionListener(func -> login());
        add(loginPanel, "login");
    }

    private void initMenu() {
        ImageIcon refreshIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/refresh.png")));
        JMenuItem refreshMenu = new JMenuItem("refresh", refreshIcon);
        refreshMenu.addActionListener(func -> refresh(false, "/"));
        viewMenu.add(refreshMenu);

        viewMenu.add(new JSeparator());

        ImageIcon mkdirIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/newdir.png")));
        JMenuItem mkdirMenu = new JMenuItem("new directory", mkdirIcon);
        mkdirMenu.addActionListener(func -> newDir());
        viewMenu.add(mkdirMenu);

        ImageIcon moveIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/move.png")));
        JMenuItem moveMenu = new JMenuItem("move", moveIcon);
        moveMenu.addActionListener(func -> move());
        viewMenu.add(moveMenu);

        ImageIcon copyIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/copy.png")));
        JMenuItem copyMenu = new JMenuItem("copy", copyIcon);
        copyMenu.addActionListener(func -> copy());
        viewMenu.add(copyMenu);

        ImageIcon removeIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/remove.png")));
        JMenuItem removeMenu = new JMenuItem("remove", removeIcon);
        removeMenu.addActionListener(func -> remove());
        viewMenu.add(removeMenu);

        viewMenu.add(new JSeparator());

        ImageIcon uploadIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/upload.png")));
        JMenuItem uploadMenu = new JMenuItem("upload", uploadIcon);
//        uploadMenu.addActionListener();
        viewMenu.add(uploadMenu);

        ImageIcon downloadIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/download.png")));
        JMenuItem downloadMenu = new JMenuItem("download", downloadIcon);
//        downloadMenu.addActionListener();
        viewMenu.add(downloadMenu);

        viewMenu.add(new JSeparator());

        ImageIcon tasksIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/res/tasks.png")));
        JMenuItem tasksMenu = new JMenuItem("task view", tasksIcon);
        tasksMenu.addActionListener(func -> layout.show(getContentPane(), "download"));
        viewMenu.add(tasksMenu);
    }

    private void initViewPanel() {

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
        viewPanel.backButton.addActionListener(func -> back());
        viewPanel.table.addTableMouseEvent(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() >= 2) {
                        int row = viewPanel.table.getSelectedIndex();
                        var file = viewPanel.table.getRow(row);
                        if (file.isDirectory()) {
                            String name = file.getName();
                            onward(name);
                        }
                    }
                } else {
                    viewMenu.show(getContentPane(), e.getX(), e.getY());
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

//        viewPanel.refreshButton.addActionListener(func -> onward("/"));
//        viewPanel.mkdirButton.addActionListener(func -> newDir());
//        viewPanel.removeButton.addActionListener(func -> remove());
//        viewPanel.moveButton.addActionListener(func -> move());
//        viewPanel.copyButton.addActionListener(func -> copy());
//        viewPanel.taskViewButton.addActionListener(func -> {
//            layout.show(getContentPane(), "download");
//        });

        add(viewPanel, "view");
    }

    private void initDownloadPanel() {
        downloadPanel.backButton.addActionListener(func -> layout.show(getContentPane(), "view"));
        add(downloadPanel, "download");
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

                    config.setLastHost(loginPanel.hostTextBox.getText());
                    config.setLastPort(Integer.parseInt(loginPanel.portTextBox.getText()));
                    config.setLastName(loginPanel.nameTextBox.getText());
                    Config.ConfigStore(config);
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
            refresh(true, null);
        }
    }

    private void onward(String subName) {
        refresh(false, subName);
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
                    viewPanel.table.clear();
                    var list = FileInfo.FileInfosBuild(response.getHeader("list"));
                    for (var item : list) {
                        viewPanel.table.addRow(item);
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
            protected Response doInBackground() throws IOException {
                return ClientUtils.ListShow(host, port, path, token);
            }
        }.execute();

    }

    private void refresh(Boolean isBack, String subName) {
        if (flushState) {
            return;
        }
        flushState = true;

        viewPanel.pathTextBox.setEnabled(false);
        viewPanel.backButton.setEnabled(false);
        viewPanel.table.setFocusable(false);

        String path;
        if (isBack) {
            path = router.preback();
        } else {
            path = router.toString() + subName;
        }

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
                    viewPanel.table.clear();
                    var list = FileInfo.FileInfosBuild(response.getHeader("list"));
                    for (var item : list) {
                        viewPanel.table.addRow(item);
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
            protected Response doInBackground() throws Exception {
                return ClientUtils.ListShow(host, port, path, token);
            }
        }.execute();

    }

    private void newDir() {
        String source = JOptionPane.showInputDialog(getContentPane(), "make a new directory");
        if (source == null) return;
        char[] chars = {'\"', '*', '?', '<', '>', '|'};
        for (char c : chars) {
            if (source.indexOf(c) != -1) {
                JOptionPane.showMessageDialog(getContentPane(), "illegal name");
                return;
            }
        }

        source = router + source;
        String finalSource = source;

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }

                if (response.getCode() == ResponseCode.OK) {
                    refresh(false, "/");
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }
            }

            @Override
            protected Response doInBackground() throws Exception {
                return ClientUtils.MakeDirectory(host, port, finalSource, token);
            }
        }.execute();
    }

    private void remove() {
        int index = viewPanel.table.getSelectedIndex();
        if (index == -1) {
            return;
        }

        var file = viewPanel.table.getRow(index);
        String source = router + file.getName();
        var opt = JOptionPane.showConfirmDialog(getContentPane(), "are you sure remove this " + (file.isDirectory() ? "directory" : "file"), "remove operate", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            new SwingWorker<Response, Void>() {
                @Override
                protected void done() {
                    Response response = null;
                    try {
                        response = get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                        return;
                    }

                    if (response.getCode() == ResponseCode.OK) {
                        refresh(false, "/");
                    } else {
                        JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                    }
                }

                @Override
                protected Response doInBackground() throws Exception {
                    return ClientUtils.Remove(host, port, source, token);
                }
            }.execute();
        }
    }

    private void move() {
        int index = viewPanel.table.getSelectedIndex();
        if (index == -1) {
            return;
        }

        var window = new SelectWindow(this, host, port, token);
        if (!window.isSelected()) {
            return;
        }

        var file = viewPanel.table.getRow(index);
        String source = router + file.getName();
        String destination = window.getPath();

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }

                if (response.getCode() == ResponseCode.OK) {
                    refresh(false, "/");
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }
            }

            @Override
            protected Response doInBackground() throws Exception {
                return ClientUtils.Move(host, port, source, destination, token);
            }
        }.execute();
    }

    public void copy() {
        int index = viewPanel.table.getSelectedIndex();
        if (index == -1) {
            return;
        }

        var window = new SelectWindow(this, host, port, token);
        if (!window.isSelected()) {
            return;
        }

        var file = viewPanel.table.getRow(index);
        String source = router + file.getName();
        String destination = window.getPath();

        new SwingWorker<Response, Void>() {
            @Override
            protected void done() {
                Response response = null;
                try {
                    response = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), e.getMessage());
                    return;
                }

                if (response.getCode() == ResponseCode.OK) {
                    refresh(false, "/");
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                }
            }

            @Override
            protected Response doInBackground() throws Exception {
                return ClientUtils.Copy(host, port, source, destination, token);
            }
        }.execute();
    }
}