package pers.kaoru.rfs.client;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public final JTextField hostTextBox;
    public final JTextField portTextBox;

    public final JTextField nameTextBox;
    public final JPasswordField pwdTextBox;

    public final JButton loginButton;
    public final JButton cancelButton;

    public LoginPanel() {
        var layout = new GridBagLayout();
        setLayout(layout);

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 5, 5, 5);

        {
            JLabel label = new JLabel("Connect your server");
            label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            add(label, constraints);
            constraints.gridwidth = 1;
        }

        {
            JLabel label = new JLabel("Host: ");
            label.setHorizontalAlignment(JLabel.RIGHT);
            constraints.gridx = 0;
            constraints.gridy = 1;
            add(label, constraints);
            hostTextBox = new JTextField(16);
            constraints.gridx = 1;
            constraints.gridy = 1;
            add(hostTextBox, constraints);
        }

        {
            JLabel label = new JLabel("Port: ");
            label.setHorizontalAlignment(JLabel.RIGHT);
            constraints.gridx = 0;
            constraints.gridy = 2;
            add(label, constraints);
            portTextBox = new JTextField(16);
            constraints.gridx = 1;
            constraints.gridy = 2;
            add(portTextBox, constraints);
        }

        {
            JLabel label = new JLabel("Verify your account");
            label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 2;
            add(label, constraints);
            constraints.gridwidth = 1;
        }

        {
            JLabel label = new JLabel("Name: ");
            label.setHorizontalAlignment(JLabel.RIGHT);
            constraints.gridx = 0;
            constraints.gridy = 4;
            add(label, constraints);
            nameTextBox = new JTextField(16);
            constraints.gridx = 1;
            constraints.gridy = 4;
            add(nameTextBox, constraints);
        }

        {
            JLabel label = new JLabel("Password: ");
            label.setHorizontalAlignment(JLabel.RIGHT);
            constraints.gridx = 0;
            constraints.gridy = 5;
            add(label, constraints);
            pwdTextBox = new JPasswordField(16);
            constraints.gridx = 1;
            constraints.gridy = 5;
            add(pwdTextBox, constraints);
        }

        {
            cancelButton = new JButton("Cancel");
            constraints.gridx = 0;
            constraints.gridy = 6;
            add(cancelButton, constraints);
            loginButton = new JButton("Login");
            constraints.gridx = 1;
            constraints.gridy = 6;
            add(loginButton, constraints);
        }
    }
}
