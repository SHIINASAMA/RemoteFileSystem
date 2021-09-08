package pers.kaoru.rfs.client;

import pers.kaoru.rfs.core.MD5Utils;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JDialog {

    private final JTextField hostTextBox;
    private final JTextField portTextBox;
    private final JTextField nameTextBox;
    private final JPasswordField passwordTextBox;
    private boolean status = false;

    public LoginForm() {

        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle("Connect and login");
        setSize(400, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 5, 5));

        {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder("Server"));

            JLabel label1 = new JLabel("Host");
            label1.setToolTipText("example: localhost, 127.0.0.1, 192.168.1.1");
            panel.add(label1);

            hostTextBox = new JTextField(16);
            panel.add(hostTextBox);

            JLabel label2 = new JLabel("Port");
            label2.setToolTipText("example: 8080");
            panel.add(label2);

            portTextBox = new JTextField(7);
            panel.add(portTextBox);

            add(panel);
        }

        {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder("User"));

            JLabel label1 = new JLabel("Name");
            panel.add(label1);

            nameTextBox = new JTextField(7);
            panel.add(nameTextBox);

            JLabel label2 = new JLabel("Password");
            panel.add(label2);

            passwordTextBox = new JPasswordField(13);
            panel.add(passwordTextBox);

            add(panel);
        }

        {
            JPanel panel = new JPanel();

            JButton yes = new JButton("Login ");
            yes.addActionListener(func -> onYes());
            panel.add(yes);
            JButton no = new JButton("Cancel");
            no.addActionListener(func -> onNo());
            panel.add(no);

            add(panel);
        }

        setVisible(true);
    }

    private void onYes() {
        for (Character c : portTextBox.getText().toCharArray()) {
            if (!(c >= '0' && c <= '9')) {
                JOptionPane.showMessageDialog(this, "Port is a number.", "Invalid symbol", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        var host = hostTextBox.getText();
        var port = portTextBox.getText();
        var name = nameTextBox.getText();
        var password = new String(passwordTextBox.getPassword());

        if (host.isEmpty() ||
                port.isEmpty() ||
                name.isEmpty() ||
                password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input all parameters.", "Invalid parameter", JOptionPane.ERROR_MESSAGE);
            return;
        }

        status = true;
        dispose();
    }

    private void onNo() {
        status = false;
        dispose();
    }

    public boolean getState() {
        return status;
    }

    public String getHost() {
        return hostTextBox.getText();
    }

    public int getPort() {
        return Integer.parseInt(portTextBox.getText());
    }

    public String getUserName() {
        return nameTextBox.getText();
    }

    public String getPwdMd5() {
        return MD5Utils.GenerateMD5(new String(passwordTextBox.getPassword()));
    }
}
