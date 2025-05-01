package MenuPage;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenSizeX = screenSize.width;
    int screenSizeY = screenSize.height;
    int fontSize = screenSizeX / 80;
    int buttonsWidth = 300;
    int buttonsHeight = 35;
    int buttonSpace = 10;

    public MenuPanel(){
        setLayout(null);

        //Add Background
        setBackground(Color.RED);

        //Add Label
        JLabel welcomeLabel = new JLabel("Welcome Mohammad!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBounds(screenSizeX / 2 - buttonsWidth/2, screenSizeY / 2 - 4*(buttonsHeight+buttonSpace), buttonsWidth, buttonsHeight);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, fontSize));

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

        startButton.addActionListener(_ -> System.exit(0));
    }
}
