package GamePage;

import GameEnvironment.BuildBackground;
import GameEnvironment.BuildStage1;
import Shape.GameShape;
import Shape.RectangleShape;
import Shape.BlockShape2Stairs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    //For Resolution
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenSizeX = screenSize.width;
    int screenSizeY = screenSize.height;
    int fontSize = screenSizeX / 80;
    int buttonsWidth = 300;
    int buttonsHeight = 35;
    int buttonSpace = 10;

    //For Background
    private Image backgroundImage;
    RectangleShape rectangleShape;
    private final List<GameShape> shapes = new ArrayList<>();

    //For Blocks
    BlockShape2Stairs blockShape2Stairs;
    private final List<GameShape> blockShapes = new ArrayList<>();

    public GamePanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("background2.jpg")); // put your real image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        BuildBackground.buildBackground(screenSizeX,screenSizeY, rectangleShape, shapes);
        BuildStage1.buildStage1(screenSizeX, screenSizeY, blockShape2Stairs, blockShapes);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int mouseX = p.x - screenSizeX / 2;
                int mouseY = p.y - screenSizeY / 2;
                for (GameShape gameShape : blockShapes){
                    for (int i = 1; i < 5; i++){
                        Path2D.Float port = gameShape.getPath(i);
                        if (port.contains(mouseX,mouseY)){
                            System.out.println("Port clicked at: ");
                            Rectangle2D bounds = port.getBounds2D();
                            double centerX = bounds.getCenterX();
                            double centerY = bounds.getCenterY();
                            System.out.println(centerX+","+centerY);
                            System.out.println(i);
                        }
                    }
                }


            }
        });
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
        for(GameShape gameShape : blockShapes){
            gameShape.draw(g2d);
        }
        g2d.dispose();
    }

}
