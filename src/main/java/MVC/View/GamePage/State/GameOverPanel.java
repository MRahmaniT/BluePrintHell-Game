package MVC.View.GamePage.State;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {

    public GameOverPanel(Runnable onRetry, Runnable onMenu) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 200)); // translucent

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int fontSize = screenWidth / 80;

        // Title
        JLabel title = new JLabel("Game Over");
        title.setFont(new Font("Arial", Font.BOLD, fontSize * 2));
        title.setForeground(Color.RED);
        title.setBounds(screenWidth / 2 - 200, screenHeight / 4, 400, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Stats label
        JLabel stats = new JLabel("", SwingConstants.CENTER);
        stats.setFont(new Font("Arial", Font.PLAIN, fontSize));
        stats.setForeground(Color.WHITE);
        stats.setBounds(screenWidth / 2 - 200, screenHeight / 2 - 80, 400, 30);
        add(stats);

        // Retry button
        JButton retryBtn = new JButton("Retry");
        retryBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        retryBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2, 200, 40);
        retryBtn.addActionListener(e -> onRetry.run());
        add(retryBtn);

        // Menu button
        JButton menuBtn = new JButton("Back to Menu");
        menuBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        menuBtn.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 60, 200, 40);
        menuBtn.addActionListener(e -> onMenu.run());
        add(menuBtn);

        // Store the stats label for external update
        this.statsLabel = stats;
    }

    private final JLabel statsLabel;

    public void updateStats(int lost, int total, String time) {
        statsLabel.setText("Time: " + time + "   |   Packets Lost: " + lost + " / " + total);
    }
}
