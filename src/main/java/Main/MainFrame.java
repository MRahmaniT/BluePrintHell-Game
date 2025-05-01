package Main;

import LoginPage.LoginPanel;
import MenuPage.MenuPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;

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

        MenuPanel menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, "Menu");

        //Show Frame
        cardLayout.show(mainPanel, "Login");

        //Add Main Frame to Main Panel
        add(mainPanel);
    }

    public void showLogin(){
        mainPanel.removeAll();
        LoginPanel loginPanel = new LoginPanel();
        mainPanel.add(loginPanel, "Login");
        cardLayout.show(mainPanel, "Login");
    }

    public void showMenu(){
        mainPanel.removeAll();
        MenuPanel menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, "Menu");
        cardLayout.show(mainPanel, "Menu");
    }
}
