package View.GamePage.State;

import javax.swing.*;
import java.awt.*;

public class PausePanel extends JPanel {

    public PausePanel(Runnable onContinue, Runnable onRetry, Runnable onMenu) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180)); // semi-transparent black

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int fontSize = screenWidth / 80;

        // Title
        JLabel title = new JLabel("Game Paused");
        title.setFont(new Font("Arial", Font.BOLD, fontSize * 2));
        title.setForeground(Color.WHITE);
        title.setBounds(screenWidth / 2 - 200, screenHeight / 4, 400, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Continue Button
        JButton continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        continueBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 60, 200, 40);
        continueBtn.addActionListener(e -> onContinue.run());
        add(continueBtn);

        // Retry Button
        JButton retryBtn = new JButton("Retry");
        retryBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        retryBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2, 200, 40);
        retryBtn.addActionListener(e -> onRetry.run());
        add(retryBtn);

        // Menu Button
        JButton menuBtn = new JButton("Back to Menu");
        menuBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        menuBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 60, 200, 40);
        menuBtn.addActionListener(e -> onMenu.run());
        add(menuBtn);
    }
}
