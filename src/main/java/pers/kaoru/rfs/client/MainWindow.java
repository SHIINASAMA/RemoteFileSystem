package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.MD5Utils;
import pers.kaoru.rfs.core.web.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainWindow extends JFrame {

    private final CardLayout layout;
    private final LoginPanel loginPanel;
    private final ViewPanel viewPanel;
    private String token = "";

    private String host = "";
    private int port = 0;
    private Boolean flushState = false;

    public MainWindow() {
        String iconPath = Objects.requireNonNull(getClass().getResource("/icon.png")).getPath();
        setIconImage(new ImageIcon(iconPath).getImage());
        setTitle("RFS Client");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        layout = new CardLayout();
        setLayout(layout);

        loginPanel = new LoginPanel();
        loginPanel.loginButton.addActionListener(func -> login());
        add(loginPanel, "login");

        viewPanel = new ViewPanel();
        add(viewPanel, "view");

        setVisible(true);

        loginPanel.hostTextBox.setText("localhost");
        loginPanel.portTextBox.setText("8080");
        loginPanel.nameTextBox.setText("root");
        loginPanel.pwdTextBox.setText("123");
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
                    flush();
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

    private void flush() {
        assert !token.isEmpty();

        var table = viewPanel.table;

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
                    for(var item : list){
                        viewPanel.addRow(item);
                    }
                }else{
                    JOptionPane.showMessageDialog(getContentPane(), "request fail");
                }
            }

            @Override
            protected LinkedList<FileInfo> doInBackground() throws Exception {
                Request request = new Request();
                request.setMethod(RequestMethod.LIST_SHOW);
                request.setHeader("token", token);
                request.setHeader("source", "/");

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
