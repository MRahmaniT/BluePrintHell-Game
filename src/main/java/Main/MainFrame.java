package Main;

import LoginPage.LoginPanel;
import MenuPage.MenuPanel;
import GamePage.GamePanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public static CardLayout cardLayout;
    public static JPanel mainPanel;
    public static LoginPanel loginPanel;
    public static MenuPanel menuPanel;
    public static GamePanel gamePanel;

    public static final String LOGIN = "Login";
    public static final String MENU = "Menu";
    public static final String GAME = "Game";

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
        mainPanel.add(loginPanel, LOGIN);

        menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, MENU);

        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, GAME);

        //Show Frame
        showLogin();

        //Add Main Frame to Main Panel
        add(mainPanel);
    }

    public static void showLogin(){
        mainPanel.remove(loginPanel);
        LoginPanel loginPanel = new LoginPanel();
        mainPanel.add(loginPanel, LOGIN);
        cardLayout.show(mainPanel, LOGIN);
    }

    public static void showMenu(){
        cardLayout.show(mainPanel, MENU);
    }

    public static void startGame(){
        mainPanel.remove(gamePanel);
        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, GAME);
        cardLayout.show(mainPanel, GAME);
    }
}
