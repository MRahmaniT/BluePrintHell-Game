package MVC.View.GameEnvironment.Options;

import MVC.View.GamePage.GamePanel;

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

        //Warning
        JLabel warning = new JLabel("Coins : " + gamePanel.getCoins());
        warning.setFont(new Font("Arial", Font.BOLD, fontSize));
        warning.setForeground(Color.WHITE);
        warning.setBounds(screenWidth / 2 - 200, screenHeight - 160, 400, 40);
        warning.setHorizontalAlignment(SwingConstants.CENTER);
        add(warning);


        // --- O' Atar ---
        JButton btnAtar = new JButton("O' Atar - 3 Coins");
        btnAtar.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btnAtar.setBounds(screenWidth / 2 - 150, 150, 300, 40);
        btnAtar.addActionListener(e -> {
            if (gamePanel.getCoins() >= 3) {
                gamePanel.spendCoins(3);
                gamePanel.disableImpactWaves(10);
                gamePanel.resumeGame();
            }else {
                warning.setText("You don't have enough money!");
                Timer timer = new Timer(2000, _ -> warning.setText("Coins : " + gamePanel.getCoins()));
                timer.setRepeats(false);
                timer.start();
            }
        });
        add(btnAtar);

        // --- O' Airyaman ---
        JButton btnAiryaman = new JButton("O' Airyaman - 4 Coins");
        btnAiryaman.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btnAiryaman.setBounds(screenWidth / 2 - 150, 210, 300, 40);
        btnAiryaman.addActionListener(e -> {
            if (gamePanel.getCoins() >= 4) {
                gamePanel.spendCoins(4);
                gamePanel.disableCollisions(5);
                gamePanel.resumeGame();
            }
            else {
                warning.setText("You don't have enough money!");
                Timer timer = new Timer(2000, _ -> warning.setText("Coins : " + gamePanel.getCoins()));
                timer.setRepeats(false);
                timer.start();
            }
        });
        add(btnAiryaman);

        // --- O' Anahita ---
        JButton btnAnahita = new JButton("O' Anahita - 5 Coins");
        btnAnahita.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btnAnahita.setBounds(screenWidth / 2 - 150, 270, 300, 40);
        btnAnahita.addActionListener(e -> {
            if (gamePanel.getCoins() >= 5) {
                gamePanel.spendCoins(5);
                gamePanel.resetAllNoise();
                gamePanel.resumeGame();
            } else {
                warning.setText("You don't have enough money!");
                Timer timer = new Timer(2000, _ -> warning.setText("Coins : " + gamePanel.getCoins()));
                timer.setRepeats(false);
                timer.start();
            }
        });
        add(btnAnahita);

        // --- SoA ---
        JButton buttonSoA = new JButton("Scroll of Aergia - 10 Coins");
        buttonSoA.setFont(new Font("Arial", Font.PLAIN, fontSize));
        buttonSoA.setBounds(screenWidth / 2 - 150, 330, 300, 40);
        buttonSoA.addActionListener(e -> {
            if (gamePanel.getCoins() >= 10) {
                gamePanel.spendCoins(10);
                gamePanel.setYouCanDisableAcceleration(true);
                gamePanel.resumeGame();
            }else {
                warning.setText("You don't have enough money!");
                Timer timer = new Timer(2000, _ -> warning.setText("Coins : " + gamePanel.getCoins()));
                timer.setRepeats(false);
                timer.start();
            }
        });
        add(buttonSoA);

        // --- SoS ---
        JButton buttonSoS = new JButton("Scroll of Sisyphus - 15 Coins");
        buttonSoS.setFont(new Font("Arial", Font.PLAIN, fontSize));
        buttonSoS.setBounds(screenWidth / 2 - 150, 390, 300, 40);
        buttonSoS.addActionListener(e -> {
            if (gamePanel.getCoins() >= 15) {
                gamePanel.spendCoins(15);
                gamePanel.setYouCanMoveBlock(true);
                gamePanel.resumeGame();
            }else {
                warning.setText("You don't have enough money!");
                Timer timer = new Timer(2000, _ -> warning.setText("Coins : " + gamePanel.getCoins()));
                timer.setRepeats(false);
                timer.start();
            }
        });
        add(buttonSoS);

        // --- SoE ---
        JButton buttonSoE = new JButton("Scroll of Eliphas - 20 Coins");
        buttonSoE.setFont(new Font("Arial", Font.PLAIN, fontSize));
        buttonSoE.setBounds(screenWidth / 2 - 150, 450, 300, 40);
        buttonSoE.addActionListener(e -> {
            if (gamePanel.getCoins() >= 20) {
                gamePanel.spendCoins(20);
                gamePanel.setYouCanDisableMissAlignment(true);
                gamePanel.resumeGame();
            }else {
                warning.setText("You don't have enough money!");
                Timer timer = new Timer(2000, _ -> warning.setText("Coins : " + gamePanel.getCoins()));
                timer.setRepeats(false);
                timer.start();
            }
        });
        add(buttonSoE);

        // --- Back ---
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, fontSize));
        backBtn.setBounds(screenWidth / 2 - 75, screenHeight - 100, 150, 40);
        backBtn.addActionListener(e -> gamePanel.resumeGame());
        add(backBtn);
    }
}
