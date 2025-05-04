package MenuPage;

import Main.MainFrame;
import Player.PlayerState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private Image backgroundImage;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int screenSizeX = screenSize.width;
    private final int screenSizeY = screenSize.height;
    private final int fontSize = screenSizeX / 80;
    private final int buttonsWidth = 300;
    private final int buttonsHeight = 35;
    private final int buttonSpace = 10;

    public MenuPanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("background2.jpg")); // put your real image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add Label
        JLabel welcomeLabel = new JLabel();
        if (PlayerState.isPlayerLoggedIn()) {
            welcomeLabel.setText("Welcome " + PlayerState.getPlayer().getUsername() + "!");
        } else {
            welcomeLabel.setText("<html><a href=''>Please log in</a></html>");
            welcomeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            welcomeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    MainFrame.showLogin();
                }
            });
        }
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBounds(screenSizeX / 2 - buttonsWidth/2, screenSizeY / 2 - 4*(buttonsHeight+buttonSpace), buttonsWidth, buttonsHeight);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));

        //Add Button Start
        JButton startButton = new JButton("Start");
        startButton.setBounds(screenSizeX / 2 - buttonsWidth/2, screenSizeY / 2 - 3*(buttonsHeight+buttonSpace), buttonsWidth, buttonsHeight);
        startButton.setFont(new Font("Arial", Font.BOLD, fontSize));

        //Add Button Levels
        JButton levelsButton = new JButton("Levels");
        levelsButton.setBounds(screenSizeX / 2 - buttonsWidth/2, screenSizeY / 2 - 2*(buttonsHeight+buttonSpace), buttonsWidth, buttonsHeight);
        levelsButton.setFont(new Font("Arial", Font.BOLD, fontSize));

        //Add Button Settings
        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(screenSizeX / 2 - buttonsWidth/2, screenSizeY / 2 - (buttonsHeight+buttonSpace), buttonsWidth, buttonsHeight);
        settingsButton.setFont(new Font("Arial", Font.BOLD, fontSize));

        //Add Button Exit
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(screenSizeX / 2 - buttonsWidth/2, screenSizeY / 2, buttonsWidth, buttonsHeight);
        exitButton.setFont(new Font("Arial", Font.BOLD, fontSize));

        //Add All
        add(welcomeLabel);
        add(startButton);
        add(levelsButton);
        add(settingsButton);
        add(exitButton);

        startButton.addActionListener(_ -> {
            if(!PlayerState.isPlayerLoggedIn()){
                welcomeLabel.setFont(new Font("Arial", Font.BOLD, (int)(1.1*fontSize)));
                return;
            }
            MainFrame.startGame();
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
