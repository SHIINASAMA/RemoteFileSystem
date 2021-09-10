package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.MD5Utils;
import pers.kaoru.rfs.core.web.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
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
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(layout);

        initLoginPanel();
        initViewPanel();

        setVisible(true);

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
                    flush(viewPanel.pathTextBox.getText(), true);
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
                        flush(name, false);
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
                    flush(router.toString(), true);
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
                var pwdMd5 = MD5Utils.GenerateMD5(new String(pwd));

                Request request = new Request();
                request.setMethod(RequestMethod.VERIFY);
                request.setHeader("username", name);
                request.setHeader("password", pwdMd5);

                String token;
                Response response;
                try {
                    Socket socket = new Socket(host, port);
                    WebUtils.WriteRequest(socket, request);
                    response = WebUtils.ReadResponse(socket);
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
        router.back();
        flush(router.toString(), true);
    }

    /// 返回也属于 Jump
    private void flush(String path, Boolean isJump) {
        assert !token.isEmpty();

        if (flushState) {
            return;
        }
        flushState = true;

        var table = viewPanel.table;
        var pathTextBox = viewPanel.pathTextBox;

        new SwingWorker<LinkedList<FileInfo>, Void>() {
            @Override
            protected void done() {
                flushState = false;
                LinkedList<FileInfo> list = null;
                try {
                    list = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (list != null) {
                    viewPanel.clear();
                    if (isJump) {
                        router.reset(path);
                    } else {
                        router.enter(path);
                    }
                    pathTextBox.setText(router.toString());
                    for (var item : list) {
                        viewPanel.addRow(item);
                    }
                }
            }

            @Override
            protected LinkedList<FileInfo> doInBackground() throws Exception {
                Request request = new Request();
                request.setMethod(RequestMethod.LIST_SHOW);
                request.setHeader("token", token);
                if (isJump) {
                    request.setHeader("source", path);
                } else {
                    request.setHeader("source", router.toString() + path);
                }

                Response response;
                try {
                    Socket socket = new Socket(host, port);
                    WebUtils.WriteRequest(socket, request);
                    response = WebUtils.ReadResponse(socket);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), exception.getMessage());
                    return null;
                }

                if (response.getCode() == ResponseCode.OK) {
                    String list = response.getHeader("list");
                    return FileInfo.FileInfosBuild(list);
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), response.getHeader("error"));
                    return null;
                }
            }
        }.execute();

    }
}
