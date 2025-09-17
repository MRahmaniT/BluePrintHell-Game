package Client.View.GamePage.State;

import javax.swing.*;
import java.awt.*;

public class SavePanel extends JPanel {

    public SavePanel(Runnable save, Runnable discard, Runnable back) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180)); // semi-transparent black

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int fontSize = screenWidth / 80;

        // Title
        JLabel title = new JLabel("Save Your Game!");
        title.setFont(new Font("Arial", Font.BOLD, fontSize * 2));
        title.setForeground(Color.WHITE);
        title.setBounds(screenWidth / 2 - 200, screenHeight / 4, 400, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Continue Button
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        saveButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 60, 200, 40);
        saveButton.addActionListener(e -> save.run());
        add(saveButton);

        // Retry Button
        JButton discardButton = new JButton("Discard Changes");
        discardButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        discardButton.setBounds(screenWidth / 2 - 100, screenHeight / 2, 200, 40);
        discardButton.addActionListener(e -> discard.run());
        add(discardButton);

        // Menu Button
        JButton backButton = new JButton("Back to Game");
        backButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        backButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 60, 200, 40);
        backButton.addActionListener(e -> back.run());
        add(backButton);
    }

}
