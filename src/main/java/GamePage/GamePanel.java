package GamePage;

import GameEntities.Packet;
import GameEntities.SpawnPackets;

import GameEnvironment.BuildBackground;
import GameEnvironment.BuildLevel1;
import GameEnvironment.HUDPanel;
import GameEnvironment.ShopPanel;

import GameLogic.*;

import GameShapes.GameShape;

import Player.PlayerState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private final BlockManager blockManager = new BlockManager();

    //For Ports
    private final PortManager portManager = new PortManager();

    //For Lines
    private int mousePointX;
    private int mousePointY;

    //For HUD
    private final HUDPanel hudPanel;

    //For Timer
    Timer gameTimer;
    private final TimeController timeController = new TimeController();

    //For Engine
    private final GameEngine gameEngine = new GameEngine(timeController);

    //For Packet
    private final PacketManager packetManager = new PacketManager();
    private final SpawnPackets spawnPacket = new SpawnPackets();
    private final List<Packet> packets = new ArrayList<>();
    private final int totalPackets = 5;
    private int lostPackets;

    //For Impact
    private final List<Impact> impacts = new ArrayList<>();
    private Impact impact;

    //For Shop
    private ShopPanel shopPanel;
    private int coins;


    public GamePanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("Resources/background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BuildBackground.buildBackground(screenSizeX, screenSizeY, shapes);

        //Build Level 1
        BuildLevel1.buildStage1(screenSizeX, blockShapes);
        final double MAX_WIRE_LENGTH = 2000;

        //Add Shop Button
        JButton shopButton = new JButton("Shop");
        shopButton.setBounds(screenSizeX - buttonsWidth - buttonSpace,
                             screenSizeY - buttonsHeight - buttonSpace,
                                buttonsWidth, buttonsHeight);
        shopButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        shopButton.setFocusable(false);
        add(shopButton);


        JLabel timeLabel = new JLabel(timeController.getFormattedTime());
        timeLabel.setBounds((int) (0.025*screenSizeX),
                            (int) (0.025*screenSizeY),
                            (int) (0.1f*screenSizeX),
                            (int) (0.1f*screenSizeY));
        timeLabel.setFont(new Font("Arial", Font.BOLD, (int) (1.5*fontSize)));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setText("DAY 1");
        add(timeLabel);

        /*
        JLabel wireLabel = new JLabel();
        wireLabel.setBounds(0,
                (int) (0.15 * screenSizeY),
                (int) (0.2f * screenSizeX),
                (int) (0.05f * screenSizeY));
        wireLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        wireLabel.setForeground(Color.WHITE);
        wireLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(wireLabel);
        */

        //Add HUD
        hudPanel = new HUDPanel(screenSizeX, screenSizeY, fontSize);
        hudPanel.setVisible(false);
        add(hudPanel);
        setComponentZOrder(hudPanel, 0);

        //Add Shop
        shopPanel = new ShopPanel(this);
        shopPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        shopPanel.setVisible(false);
        add(shopPanel);
        setComponentZOrder(shopPanel, 0);

        //Timing
        gameTimer = new Timer(10, _ -> {
            gameEngine.update();

            packetManager.manageMovement(blockShapes, portManager, packets, spawnPacket, impacts);
            packetManager.manageImpact(impacts,  packets);

            coins = PlayerState.getPlayer().getGoldCount();
            hudPanel.update(
                    portManager.getRemainingWireLength(MAX_WIRE_LENGTH),
                    timeController.getFormattedTime(),
                    lostPackets,
                    totalPackets,
                    coins
            );
            repaint();
        });
        gameTimer.start();

        shopButton.addActionListener(_ -> {
            shopPanel.setVisible(true);
            gameTimer.stop();
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'p' -> {
                        blockShapes.getFirst().setSquarePacketCount(totalPackets);
                        for (int i = 1; i <= totalPackets; i++){
                            spawnPacket.addPacketToStartBlock(blockShapes.getFirst(),portManager,blockShapes.getFirst().getShapeModel(4));
                        }
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> gameEngine.setLeftPressed(true);
                    case KeyEvent.VK_RIGHT -> gameEngine.setRightPressed(true);
                    case KeyEvent.VK_TAB -> hudPanel.setVisible(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> gameEngine.setLeftPressed(false);
                    case KeyEvent.VK_RIGHT -> gameEngine.setRightPressed(false);
                    case KeyEvent.VK_TAB -> hudPanel.setVisible(false);
                }
            }

        });

        //Wiring
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                portManager.handleMousePress(blockShapes, mousePointX, mousePointY);
                blockManager.handleMousePress(blockShapes, mousePointX, mousePointY);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                portManager.handleMouseRelease(blockShapes, mousePointX, mousePointY);
                blockManager.handleMouseRelease(mousePointX, mousePointY);
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
        setFocusTraversalKeysEnabled(false);
    }
    public int getCoins() { return coins; }

    public void spendCoins(int amount) { PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() - amount); }

    public List<Packet> getPackets() { return packets; }

    public void disableImpactWaves(int seconds) {
        packetManager.disableWaveForSeconds(seconds);
    }

    public void disableCollisions(int seconds) {
        packetManager.disableImpactForSeconds(seconds);
    }

    public void resumeGame() {
        shopPanel.setVisible(false);
        gameTimer.start();  // restart main game loop
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2d.translate(cx, cy);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        //Shapes
        for(GameShape gameShape : shapes){
            gameShape.draw(g2d);
        }

        //Block Shapes
        ChangeBlocksLight.changeBlocksLight(blockShapes);
        for(GameShape gameShape : blockShapes){
            gameShape.draw(g2d);
        }

        //For Lines
        for (Connection c : portManager.getConnections()) {
            c.line.draw(g2d);
        }

        if (portManager.isDragging()) {
            portManager.drawDrag(g2d, new Point(mousePointX, mousePointY));
        }
        if (blockManager.isDragging()) {
            blockManager.drawDrag(mousePointX, mousePointY);
        }

        //Packet
        for (Packet p : packets) {
            p.draw(g2d);
        }

        g2d.dispose();
    }


}
