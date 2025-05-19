package LevelPage;

import Main.MainFrame;
import Player.PlayerState;

import javax.swing.*;
import java.awt.*;

public class LevelPanel extends JPanel {

    public LevelPanel() {
        setLayout(null);
        setBackground(Color.DARK_GRAY);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screen.width;
        int screenHeight = screen.height;
        int fontSize = screenWidth / 80;

        JLabel title = new JLabel("Select a Level");
        title.setFont(new Font("Arial", Font.BOLD, fontSize * 2));
        title.setForeground(Color.WHITE);
        title.setBounds(screenWidth / 2 - 200, 100, 400, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Level Buttons (example: 6 levels in 2 rows)
        int cols = 3;
        int rows = 2;
        int spacingX = 200;
        int spacingY = 100;
        int baseX = screenWidth / 2 - (cols * spacingX) / 2;
        int baseY = 200;

        for (int i = 0; i < 6; i++) {
            int level = i + 1;
            JButton levelBtn = new JButton("Level " + level);
            levelBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
            int col = i % cols;
            int row = i / cols;
            levelBtn.setBounds(baseX + col * spacingX, baseY + row * spacingY, 150, 50);
            PlayerState.getPlayer().setLevelNumber(level);
            levelBtn.addActionListener(e -> MainFrame.startGame()); // You implement level handling
            add(levelBtn);
        }

        // Back Button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        backBtn.setBounds(screenWidth / 2 - 75, screenHeight - 100, 150, 40);
        backBtn.addActionListener(e -> MainFrame.showMenu());
        add(backBtn);
    }
}
