package LoginPage;

import Main.MainFrame;
import Player.GameState;
import Player.Player;
import Player.PlayerStorage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LoginPanel extends JPanel {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int screenSizeX = screenSize.width;
    private final int screenSizeY = screenSize.height;
    private final int fontSize = screenSizeX / 100;
    int buttonsWidth = screenSizeX / 8;
    int buttonsHeight = screenSizeY / 20;
    int buttonSpace = screenSizeY / 180;

    private Image backgroundImage;

    public LoginPanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("background2.jpg")); // put your real image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add Error Label
        JLabel error = new JLabel(" ");
        error.setBounds(screenSizeX / 2 - buttonsWidth , screenSizeY / 2 - 2*(buttonsHeight + buttonSpace), 2*buttonsWidth, buttonsHeight);
        error.setHorizontalAlignment(SwingConstants.CENTER);
        error.setForeground(Color.RED);
        error.setFont(new Font("Arial", Font.BOLD, (int)(1.2*fontSize)));
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

        //Add Username Textfield
        JTextField usernameField = new JTextField(10);
        usernameField.setBounds(screenSizeX / 2, screenSizeY / 2 - buttonsHeight - buttonSpace, buttonsWidth, buttonsHeight);
        add(usernameField);

        //Add Password Textfield
        JTextField passwordField = new JTextField(10);
        passwordField.setBounds(screenSizeX / 2, screenSizeY / 2, buttonsWidth, buttonsHeight);
        add(passwordField);

        //Add Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds((screenSizeX - 2*buttonsWidth) / 2, screenSizeY / 2 + buttonsHeight + buttonSpace, 2*buttonsWidth, buttonsHeight);
        loginButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton);

        loginButton.addActionListener(_ -> {
            String usernameText = usernameField.getText().trim();
            String passwordText = passwordField.getText().trim();

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                error.setText("Enter both username and password.");
                return;
            }

            Player existing = PlayerStorage.findPlayer(usernameText);
            if (existing != null) {
                if (Objects.equals(existing.getPassword(), passwordText)){
                    GameState.setPlayer(existing);
                } else {
                    error.setText("Wrong username or password!");
                    return;
                }
            } else {
                Player newPlayer = new Player(usernameText, passwordText);
                PlayerStorage.saveNewPlayer(newPlayer);
                GameState.setPlayer(newPlayer);
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
