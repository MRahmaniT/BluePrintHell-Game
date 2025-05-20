package View.Main;

import View.LoginPage.LoginPanel;
import View.MenuPage.MenuPanel;
import View.GamePage.GamePanel;
import View.LevelPage.LevelPanel;
import Services.AudioManager;
import View.SettingsPage.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    public static MainFrame mainFrame;

    public static AudioManager audioManager = new AudioManager();

    public static CardLayout cardLayout;
    public static JPanel mainPanel;
    public static LoginPanel loginPanel;
    public static MenuPanel menuPanel;
    public static GamePanel gamePanel;
    public static LevelPanel levelPanel;
    public static SettingsPanel settingsPanel;


    public static final String LOGIN = "Login";
    public static final String MENU = "Menu";
    public static final String GAME = "Game";
    public static final String LEVEL = "Level";
    public static final String SETTINGS = "Settings";

    public MainFrame(){

        MainFrame.mainFrame = this;

        //Base Frame
        setResizable(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //Full Screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        //Music
        audioManager.playMusic("Resources/music.wav");
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

        levelPanel = new LevelPanel();
        mainPanel.add(levelPanel, LEVEL);

        settingsPanel = new SettingsPanel(audioManager);
        mainPanel.add(settingsPanel, SETTINGS);

        //Show Frame
        showMenu();

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
        setFullscreenMode(false);
        mainPanel.remove(menuPanel);
        MenuPanel menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, MENU);
        cardLayout.show(mainPanel, MENU);
    }

    public static void startGame(){
        minimizeAllOtherWindows();
        mainPanel.remove(gamePanel);
        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, GAME);
        setFullscreenMode(true);
        cardLayout.show(mainPanel, GAME);
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public static void showLevels() {
        setFullscreenMode(false);
        cardLayout.show(mainPanel, LEVEL);
    }


    public static void showSettings() {
        setFullscreenMode(false);
        cardLayout.show(mainPanel, SETTINGS);
    }

    public static void setFullscreenMode(boolean fullscreen) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenSizeX = screenSize.width;
        int screenSizeY = screenSize.height;

        if (fullscreen) {
            mainFrame.dispose();
            mainFrame.setUndecorated(true);
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(mainFrame);
            } else {
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        } else {
            gd.setFullScreenWindow(null);
            mainFrame.dispose();
            mainFrame.setUndecorated(true);
            mainFrame.setExtendedState(JFrame.NORMAL);
            mainFrame.setSize(screenSizeX/2, screenSizeY/2);
            mainFrame.setLocationRelativeTo(null);
        }

        mainFrame.setVisible(true);
    }

    public static void minimizeAllOtherWindows() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
