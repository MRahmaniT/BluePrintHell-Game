package LoginPage;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(){
        setLayout(new FlowLayout());
        JTextField usernameField = new JTextField(10);
        JButton loginButton = new JButton("Login");

        add(new JLabel("Username:"));
        add(usernameField);
        add(loginButton);

        loginButton.addActionListener(e -> {
            System.exit(0);
        });
    }
}
