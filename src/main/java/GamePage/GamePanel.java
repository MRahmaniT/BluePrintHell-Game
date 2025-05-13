package GamePage;

import GameEnvironment.BuildBackground;
import GameEnvironment.BuildStage1;
import Shape.GameShape;
import Shape.LineShape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    int buttonsWidth = screenSizeX / 10;
    int buttonsHeight = screenSizeY / 20;
    int buttonSpace = 10;

    //For Background
    private Image backgroundImage;
    private final List<GameShape> shapes = new ArrayList<>();

    //For Blocks
    private final List<GameShape> blockShapes = new ArrayList<>();
    private int firstBlockShape2Stairs;
    private int firstShapeModel;
    private int firstPortNumber;

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

    private double timeCounter = 0;
    private boolean leftPressed;
    private boolean rightPressed;

    public GamePanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BuildBackground.buildBackground(screenSizeX, screenSizeY, shapes);
        BuildStage1.buildStage1(screenSizeX, blockShapes);

        //Add Shop Button
        JButton shopButton = new JButton("Shop");
        shopButton.setBounds(screenSizeX - buttonsWidth - buttonSpace,
                             screenSizeY - buttonsHeight - buttonSpace,
                                buttonsWidth, buttonsHeight);
        shopButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        shopButton.addActionListener(_ -> System.exit(0));
        shopButton.setFocusable(false);
        add(shopButton);

        //Add Time Counter
        JLabel timeLabel = new JLabel("Time: " + timeCounter);
        timeLabel.setBounds((int) (0.025*screenSizeX),
                            (int) (0.025*screenSizeY),
                            (int) (0.1f*screenSizeX),
                            (int) (0.1f*screenSizeY));
        timeLabel.setFont(new Font("Arial", Font.BOLD, (int) (1.5*fontSize)));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timeLabel);

        //Lights
        for (GameShape gameShape : blockShapes){
            boolean checkPortsForLight = true;
            for (int i = 1; i < 5; i++){
                checkPortsForLight = blockShapes.get(firstBlockShape2Stairs).getConnection(i);
            }
            if (checkPortsForLight){
                gameShape.s
            }
        }
        //Timing
        //For Timing
        Timer gameTimer = new Timer(100, _ -> {
            if (leftPressed && !rightPressed) {
                timeCounter = timeCounter - 0.1;
            } else if (rightPressed && !leftPressed) {
                timeCounter = timeCounter + 0.1;
            }
            timeLabel.setText("Time : " + timeCounter);
        });
        leftPressed = false;
        rightPressed = false;
        gameTimer.start();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_LEFT:
                        leftPressed = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_LEFT:
                        leftPressed = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = false;
                        break;
                }
            }
        });

        //Wiring
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
                            firstBlockShape2Stairs = blockShapes.indexOf(gameShape);
                            firstShapeModel = blockShapes.get(firstBlockShape2Stairs).getShapeModel(i);
                            firstPortNumber = i;
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
                            !gameShape.getConnection(i) &&
                            !blockShapes.get(firstBlockShape2Stairs).getConnection(firstPortNumber)){
                            if (isEntrancePort ^ (i <= 2)){
                                Rectangle2D bounds = port.getBounds2D();
                                centerX2 = bounds.getCenterX();
                                centerY2 = bounds.getCenterY();
                                lineShape = new LineShape((float) centerX1, (float) centerY1,
                                        (float) centerX2, (float) centerY2,
                                        Color.cyan);
                                lineShapes.add(lineShape);
                                blockShapes.get(firstBlockShape2Stairs).setConnection(
                                        firstPortNumber,true);
                                gameShape.setConnection(i,true);
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
        setFocusable(true);
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
