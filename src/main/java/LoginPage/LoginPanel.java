package LoginPage;

import Main.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    MainFrame mainFrame;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenSizeX = screenSize.width;
    int screenSizeY = screenSize.height;
    int fontSize = screenSizeX / 100;

    public LoginPanel(){
        setLayout(null);

        //Add Label
        JLabel username = new JLabel("Username:");
        username.setHorizontalTextPosition(0);
        username.setBounds(screenSizeX / 2 - 100, screenSizeY / 2 - 35, 100, 30);
        username.setFont(new Font("Arial", Font.BOLD, fontSize));

        //Add Textfield
        JTextField usernameField = new JTextField(10);
        usernameField.setBounds(screenSizeX / 2, screenSizeY / 2 - 35, 100, 30);

        //Add Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(screenSizeX / 2 - 50, screenSizeY / 2, 100, 30);
        loginButton.setFont(new Font("Arial", Font.BOLD, fontSize));

        //Add All
        add(username);
        add(usernameField);
        add(loginButton);

        loginButton.addActionListener(e -> mainFrame.showMenu());
    }
}
