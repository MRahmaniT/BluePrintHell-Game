package Client.View.GamePage.State;

import javax.swing.*;
import java.awt.*;

public class LoadPanel extends JPanel {

    public LoadPanel(Runnable load, Runnable newGame, Runnable back) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180)); // semi-transparent black

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int fontSize = screenWidth / 80;

        // Title
        JLabel title = new JLabel("Do You Want to Load Your Saved Data?");
        title.setFont(new Font("Arial", Font.BOLD, fontSize));
        title.setForeground(Color.WHITE);
        title.setBounds(screenWidth / 2 - 300, screenHeight / 4, 600, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Continue Button
        JButton loadButton = new JButton("Load Data");
        loadButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        loadButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 60, 200, 40);
        loadButton.addActionListener(e -> load.run());
        add(loadButton);

        // Retry Button
        JButton newGameButton = new JButton("Start New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        newGameButton.setBounds(screenWidth / 2 - 100, screenHeight / 2, 200, 40);
        newGameButton.addActionListener(e -> newGame.run());
        add(newGameButton);

        // Menu Button
        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        backButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 60, 200, 40);
        backButton.addActionListener(e -> back.run());
        add(backButton);
    }
}
