package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.MD5Utils;
import pers.kaoru.rfs.core.web.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainWindow extends JFrame {

    private final CardLayout layout;
    private final LoginPanel loginPanel;
    private String token;

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

        ViewPanel viewPanel = new ViewPanel();
        add(viewPanel, "view");

        setVisible(true);
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
                    if (token != null) {
                        layout.show(getContentPane(), "view");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground() throws Exception {
                var host = loginPanel.hostTextBox.getText();
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
                var port = Integer.parseInt(portStr);
                var pwdMd5 = MD5Utils.GenerateMD5(new String(pwd));

                Request request = new Request();
                request.setMethod(RequestMethod.VERIFY);
                request.setHeader("username", name);
                request.setHeader("password", pwdMd5);

                String token;
                try {
                    Socket socket = new Socket(host, port);
                    WebUtils.WriteRequest(socket, request);
                    Response response = WebUtils.ReadResponse(socket);
                    if (response.getCode() == ResponseCode.OK) {
                        token = response.getHeader("token");
                        return token;
                    } else {
                        JOptionPane.showMessageDialog(getContentPane(), "Login failed, please check you info");
                        return null;
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), exception.getMessage(), "Info", JOptionPane.INFORMATION_MESSAGE);
                    return null;
                }
            }
        }.execute();
    }
}
