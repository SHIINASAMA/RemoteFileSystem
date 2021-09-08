package pers.kaoru.rfs.client;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private final JTextField hostTextBox;
    private final JTextField portTextBox;
    private final JTextField nameTextBox;
    private final JPasswordField passwordTextBox;

    public LoginForm() {

        setTitle("Connect and login");
        setSize(400, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
            panel.add(yes);
            JButton no = new JButton("Cancel");
            panel.add(no);

            add(panel);
        }

        setVisible(true);
    }
}
