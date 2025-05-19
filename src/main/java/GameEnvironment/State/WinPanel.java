package GameEnvironment.State;

import javax.swing.*;
import java.awt.*;

public class WinPanel extends JPanel {

    private final JLabel statsLabel;

    public WinPanel(Runnable onContinue, Runnable onRetry, Runnable onMenu) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 128, 0, 180)); // translucent green

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int fontSize = screenWidth / 80;

        // Title
        JLabel title = new JLabel("Victory!");
        title.setFont(new Font("Arial", Font.BOLD, fontSize * 2));
        title.setForeground(Color.WHITE);
        title.setBounds(screenWidth / 2 - 200, screenHeight / 4, 400, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Stats label
        statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setBounds(screenWidth / 2 - 200, screenHeight / 2 - 80, 400, 30);
        add(statsLabel);

        // Continue button
        JButton continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        continueBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 10, 200, 40);
        continueBtn.addActionListener(e -> onContinue.run());
        add(continueBtn);

        // Retry button
        JButton retryBtn = new JButton("Retry");
        retryBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        retryBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 50, 200, 40);
        retryBtn.addActionListener(e -> onRetry.run());
        add(retryBtn);

        // Menu button
        JButton menuBtn = new JButton("Back to Menu");
        menuBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        menuBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 110, 200, 40);
        menuBtn.addActionListener(e -> onMenu.run());
        add(menuBtn);
    }

    public void updateStats(int delivered, int total, String time, int coinsEarned) {
        statsLabel.setText("Time: " + time + " | Delivered: " + delivered + "/" + total + " | + " + coinsEarned + " coins");
    }
}
