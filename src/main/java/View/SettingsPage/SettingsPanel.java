package View.SettingsPage;

import Services.AudioManager;
import View.Main.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SettingsPanel extends JPanel {
    private Image backgroundImage;

    public SettingsPanel(AudioManager audioManager) {
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("Resources/background2.jpg")); // put your real image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width/2;
        int screenHeight = screenSize.height/2;
        int fontSize = screenWidth / 50;

        JLabel volumeLabel = new JLabel("Music Volume:");
        volumeLabel.setBounds(screenWidth / 2 - 150, screenHeight / 2 - 100, 200, 30);
        volumeLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        volumeLabel.setForeground(Color.WHITE);
        add(volumeLabel);

        JSlider volumeSlider = new JSlider(0, 100, 50); // default 50%
        volumeSlider.setBounds(screenWidth / 2 - 150, screenHeight / 2 - 60, 300, 40);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100f;
            audioManager.setVolume(volume);
        });
        add(volumeSlider);

        JButton backButton = new JButton("Back to Menu");
        backButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 10, 200, 40);
        backButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        backButton.addActionListener(_ -> MainFrame.showMenu());
        add(backButton);
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
