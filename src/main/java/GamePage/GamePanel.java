package GamePage;

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
    private Image backgroundImage;

    RectangleShape rectangleShape;
    private final List<GameShape> shapes = new ArrayList<>();

    BlockShape2Stairs blockShape2Stairs;
    private final List<GameShape> blockShapes = new ArrayList<>();

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
        ArrayList<Integer> forDrawBlocks = new ArrayList<>();
        forDrawBlocks.add(2);
        forDrawBlocks.add(0);
        forDrawBlocks.add(2);
        forDrawBlocks.add(1);
        blockShape2Stairs = new BlockShape2Stairs(0,0,0.1f*screenSizeX,0.1f*screenSizeX, Color.RED, forDrawBlocks);
        blockShapes.add(blockShape2Stairs);
        blockShape2Stairs.setColor(Color.cyan);
        blockShapes.add(blockShape2Stairs);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int mouseX = p.x - screenSizeX / 2;
                int mouseY = p.y - screenSizeY / 2;
                Path2D.Float port1 = blockShape2Stairs.getPath(1);
                if (port1.contains(mouseX,mouseY)){
                    System.out.println("Port clicked at: ");
                    Rectangle2D bounds = port1.getBounds2D();
                    double centerX = bounds.getCenterX();
                    double centerY = bounds.getCenterY();
                    System.out.println(centerX+","+centerY);
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
        Path2D.Float alo = blockShape2Stairs.getPath(1);
        g2d.setColor(Color.RED);
        g2d.fill(alo);
        g2d.dispose();
    }

}
