package GamePage;

import GameEnvironment.BuildBackground;
import GameEnvironment.BuildStage1;
import Shape.GameShape;
import Shape.RectangleShape;
import Shape.BlockShape2Stairs;
import Shape.LineShape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
    private RectangleShape rectangleShape;
    private final List<GameShape> shapes = new ArrayList<>();

    //For Blocks
    private BlockShape2Stairs blockShape2Stairs;
    private final List<GameShape> blockShapes = new ArrayList<>();
    private int firstBlockShape2Stairs;
    private int firstShapeModel;

    //For Ports
    private double centerX1;
    private double centerY1;
    private double centerX2;
    private double centerY2;
    private boolean isEntrancePort;

    //For Lines
    private LineShape lineShape;
    private final List<GameShape> lineShapes = new ArrayList<>();
    private int mousePointX;
    private int mousePointY;
    private boolean dragging = false;

    public GamePanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BuildBackground.buildBackground(screenSizeX,screenSizeY, rectangleShape, shapes);
        BuildStage1.buildStage1(screenSizeX, blockShapes);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int mouseX = p.x - screenSizeX / 2;
                int mouseY = p.y - screenSizeY / 2;
                for (GameShape gameShape : blockShapes){
                    for (int i = 1; i < 5; i++){
                        Path2D.Float port = gameShape.getPath(i);
                        if (port != null && port.contains(mouseX,mouseY)){
                            dragging = true;
                            Rectangle2D bounds = port.getBounds2D();
                            centerX1 = bounds.getCenterX();
                            centerY1 = bounds.getCenterY();
                            firstShapeModel = blockShapes.get(firstBlockShape2Stairs).getShapeModel(i);
                            if (i <= 2) {
                                isEntrancePort = true;
                            }
                        }
                    }
                }


            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
                Point p = e.getPoint();
                int mouseX = p.x - screenSizeX / 2;
                int mouseY = p.y - screenSizeY / 2;
                for (GameShape gameShape : blockShapes){
                    for (int i = 1; i < 5; i++){
                        Path2D.Float port = gameShape.getPath(i);
                        if (port != null && port.contains(mouseX,mouseY) &&
                            blockShapes.get(firstBlockShape2Stairs) != gameShape &&
                            firstShapeModel == gameShape.getShapeModel(i) &&
                            !gameShape.getConnection() &&
                            !blockShapes.get(firstBlockShape2Stairs).getConnection()){
                            if (isEntrancePort ^ (i <= 2)){
                                Rectangle2D bounds = port.getBounds2D();
                                centerX2 = bounds.getCenterX();
                                centerY2 = bounds.getCenterY();
                                lineShape = new LineShape((float) centerX1, (float) centerY1,
                                        (float) centerX2, (float) centerY2,
                                        Color.cyan);
                                lineShapes.add(lineShape);
                                blockShapes.get(firstBlockShape2Stairs).setConnection(true);
                                gameShape.setConnection(true);
                            }
                        }
                    }
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePointX = e.getX() - getWidth() / 2;
                mousePointY = e.getY() - getHeight() / 2;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
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
        for(GameShape gameShape : lineShapes){
            gameShape.draw(g2d);
        }
        if (dragging){
            g2d.setColor(Color.CYAN);
            g2d.setStroke(new BasicStroke(4f));
            g2d.drawLine((int) centerX1, (int) centerY1, mousePointX, mousePointY);
        }
        g2d.dispose();
    }

}
