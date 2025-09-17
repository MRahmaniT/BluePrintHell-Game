package Client.View.GameEnvironment.Options;

import Client.View.GamePage.GamePanel;

import javax.swing.*;
import java.awt.*;

public class ShopPanel extends JPanel {

    public ShopPanel(GamePanel gamePanel) {

        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 200));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int fontSize = screenWidth / 100;

        JLabel title = new JLabel("Game Shop");
        title.setFont(new Font("Arial", Font.BOLD, fontSize * 2));
        title.setForeground(Color.WHITE);
        title.setBounds(screenWidth / 2 - 100, 50, 200, 40);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // --- O' Atar ---
        JButton btnAtar = new JButton("O' Atar - 3 Coins");
        btnAtar.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btnAtar.setBounds(screenWidth / 2 - 100, 150, 200, 40);
        btnAtar.addActionListener(e -> {
            if (gamePanel.getCoins() >= 3) {
                gamePanel.spendCoins(3);
                gamePanel.disableImpactWaves(10);
                gamePanel.resumeGame();
            }else {
                System.out.println("f you");
            }
        });
        add(btnAtar);

        // --- O' Airyaman ---
        JButton btnAiryaman = new JButton("O' Airyaman - 4 Coins");
        btnAiryaman.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btnAiryaman.setBounds(screenWidth / 2 - 100, 210, 200, 40);
        btnAiryaman.addActionListener(e -> {
            if (gamePanel.getCoins() >= 4) {
                gamePanel.spendCoins(4);
                gamePanel.disableCollisions(5);
                gamePanel.resumeGame();
            }
        });
        add(btnAiryaman);

        // --- O' Anahita ---
        JButton btnAnahita = new JButton("O' Anahita - 5 Coins");
        btnAnahita.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btnAnahita.setBounds(screenWidth / 2 - 100, 270, 200, 40);
        btnAnahita.addActionListener(e -> {
            if (gamePanel.getCoins() >= 5) {
                gamePanel.spendCoins(5);
                gamePanel.resetAllNoise();
                gamePanel.resumeGame();
            }
        });
        add(btnAnahita);

        // --- Back ---
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        backBtn.setBounds(screenWidth / 2 - 75, screenHeight - 100, 150, 40);
        backBtn.addActionListener(e -> gamePanel.resumeGame());
        add(backBtn);
    }
}
