package Main;

import LoginPage.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenSizeX = screenSize.width;
    int screenSizeY = screenSize.height;

    public MainFrame(){
        //Base Frame
        setResizable(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //Full Screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }


        //Card Layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        //Add Pages
        LoginPanel loginPanel = new LoginPanel();
        mainPanel.add(loginPanel, "Login");

        //Show Frame
        cardLayout.show(mainPanel, "Login");

        //Add Main Frame to Main Panel
        add(mainPanel);
    }
}
