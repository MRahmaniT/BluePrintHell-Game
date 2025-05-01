package Main;

import LoginPage.LoginPanel;
import MenuPage.MenuPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public static CardLayout cardLayout;
    public static JPanel mainPanel;
    public static LoginPanel loginPanel;
    public static MenuPanel menuPanel;

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
        loginPanel = new LoginPanel();
        mainPanel.add(loginPanel, "Login");

        menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, "Menu");

        //Show Frame
        showLogin();

        //Add Main Frame to Main Panel
        add(mainPanel);
    }

    public static void showLogin(){
        mainPanel.remove(loginPanel);
        LoginPanel loginPanel = new LoginPanel();
        mainPanel.add(loginPanel, "Login");
        cardLayout.show(mainPanel, "Login");
    }

    public static void showMenu(){
        mainPanel.remove(menuPanel);
        MenuPanel menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, "Menu");
        cardLayout.show(mainPanel, "Menu");
    }
}
