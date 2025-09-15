package View.LoginPage;

import View.Main.MainFrame;
import Model.Player.PlayerState;
import Model.Player.Player;
import Storage.Player.PlayerStorage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LoginPanel extends JPanel {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int screenSizeX = screenSize.width/2;
    private final int screenSizeY = screenSize.height/2;
    int buttonsWidth = screenSizeX / 4;
    int buttonsHeight = screenSizeY / 10;
    int buttonSpace = screenSizeY / 90;

    private Image backgroundImage;

    public LoginPanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("Resources/background2.jpg")); // put your real image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add Error Label
        JLabel error = new JLabel(" ");
        error.setBounds(screenSizeX / 2 - buttonsWidth , screenSizeY / 2 - 2*(buttonsHeight + buttonSpace), 2*buttonsWidth, buttonsHeight);
        error.setHorizontalAlignment(SwingConstants.CENTER);
        error.setForeground(Color.RED);
        int fontSize = screenSizeX / 50;
        error.setFont(new Font("Arial", Font.BOLD, (int)(1.2* fontSize)));
        add(error);

        //Add Username Label
        JLabel username = new JLabel("Username:");
        username.setBounds(screenSizeX / 2 - buttonsWidth, screenSizeY / 2 - buttonsHeight - buttonSpace, buttonsWidth, buttonsHeight);
        username.setHorizontalAlignment(SwingConstants.CENTER);
        username.setForeground(Color.WHITE);
        username.setFont(new Font("Arial", Font.BOLD, fontSize));
        add(username);

        //Add Password Label
        JLabel password = new JLabel("Password:");
        password.setBounds(screenSizeX / 2 - buttonsWidth, screenSizeY / 2, buttonsWidth, buttonsHeight);
        password.setHorizontalAlignment(SwingConstants.CENTER);
        password.setForeground(Color.WHITE);
        password.setFont(new Font("Arial", Font.BOLD, fontSize));
        add(password);

        //Add Username Text field
        JTextField usernameField = new JTextField(10);
        usernameField.setBounds(screenSizeX / 2, screenSizeY / 2 - buttonsHeight - buttonSpace, buttonsWidth, buttonsHeight);
        add(usernameField);

        //Add Password Text field
        JPasswordField passwordField = new JPasswordField(10);
        passwordField.setBounds(screenSizeX / 2, screenSizeY / 2, buttonsWidth, buttonsHeight);
        add(passwordField);

        //Add Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(screenSizeX / 2 - buttonsWidth, screenSizeY / 2 + buttonsHeight + buttonSpace, (int)(0.99*buttonsWidth), buttonsHeight);
        registerButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        registerButton.setBackground(Color.DARK_GRAY);
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        add(registerButton);

        registerButton.addActionListener(_ -> {
            String usernameText = usernameField.getText().trim();
            String passwordText = new String(passwordField.getPassword()).trim();

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                error.setText("Enter both username and password.");
                return;
            }

            Player existing = PlayerStorage.findPlayer(usernameText);
            if (existing != null) {
                error.setText("You have an account. Please log in.");
                return;
            } else {
                Player newPlayer = new Player(usernameText, passwordText);
                PlayerStorage.setLogin(newPlayer);
                PlayerStorage.saveNewPlayer(newPlayer);
                PlayerState.setPlayer(newPlayer);
            }
            MainFrame.showMenu();
        });

        //Add Login Button
        JButton loginButton = new JButton("Log in");
        loginButton.setBounds(screenSizeX / 2 , screenSizeY / 2 + buttonsHeight + buttonSpace, (int)(0.99*buttonsWidth), buttonsHeight);
        loginButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        add(loginButton);

        loginButton.addActionListener(_ -> {
            String usernameText = usernameField.getText().trim();
            String passwordText = new String(passwordField.getPassword()).trim();

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                error.setText("Enter both username and password.");
                return;
            }

            Player existing = PlayerStorage.findPlayer(usernameText);
            if (existing != null) {
                if (Objects.equals(existing.getPassword(), passwordText)){
                    PlayerStorage.setLogin(existing);
                    PlayerState.setPlayer(existing);
                } else {
                    error.setText("Wrong username or password!");
                    return;
                }
            } else {
                error.setText("Please register.");
                return;
            }
            MainFrame.showMenu();
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2d.translate(cx, cy);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g2d.dispose();
    }
}
