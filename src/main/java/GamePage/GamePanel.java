package GamePage;

import Shape.GameShape;
import Shape.RectangleShape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private Image backgroundImage;

    RectangleShape rectangleShape;
    private final List<GameShape> shapes = new ArrayList<>();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenSizeX = screenSize.width;
    int screenSizeY = screenSize.height;

    int fontSize = screenSizeX / 80;
    int buttonsWidth = 300;
    int buttonsHeight = 35;
    int buttonSpace = 10;

    public GamePanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("background2.jpg")); // put your real image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add Parameters
        //shape 1
        rectangleShape = new RectangleShape(0,-(screenSizeY-(0.15f*screenSizeY))/2,screenSizeX,0.15f*screenSizeY,Color.LIGHT_GRAY);
        shapes.add(rectangleShape);
        //shape 2
        rectangleShape = new RectangleShape(-0.95f*(screenSizeX-(0.1f*screenSizeX))/2,-(screenSizeY-(0.15f*screenSizeY))/2,0.1f*screenSizeX,0.1f*screenSizeY,Color.DARK_GRAY);
        shapes.add(rectangleShape);
        //shape 3
        rectangleShape = new RectangleShape(-(screenSizeX-(0.2f*screenSizeX))/2,(0.15f*screenSizeY)/2,0.2f*screenSizeX,screenSizeY-(0.15f*screenSizeY),Color.GRAY);
        shapes.add(rectangleShape);
        //shape 4
        rectangleShape = new RectangleShape(-(screenSizeX-(0.2f*screenSizeX))/2,-((screenSizeY-(0.05f*screenSizeY))/2-0.15f*screenSizeY),0.2f*screenSizeX,0.05f*screenSizeY,Color.DARK_GRAY);
        shapes.add(rectangleShape);
        //shape 5
        rectangleShape = new RectangleShape(0,0,0.2f*screenSizeX,0.05f*screenSizeY,Color.DARK_GRAY);
        shapes.add(rectangleShape);

        //Add Label
        JLabel welcomeLabel = new JLabel("Welcome Mohammad!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2d.translate(cx, cy);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        for(GameShape gameShape : shapes){
            gameShape.draw(g2d);
        }
        g2d.dispose();
    }
}
