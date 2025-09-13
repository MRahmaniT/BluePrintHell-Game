package View.GamePage;

import Controller.GameLoop;
import Controller.Packets.PacketManager;
import Controller.Packets.Spawning.SpawnPackets;
import Controller.Timing.GameEngine;
import Controller.Timing.TimeController;
import Controller.Wiring.WiringManager;
import Controller.Systems.BlockManager;
import Controller.Systems.ChangeBlocksLight;
import Model.Enums.PacketType;
import Model.GameEntities.Impact;
import Model.GameEntities.Packet;

import Model.GameEntities.Wire.Wire;
import Storage.ConnectionStorage;
import Storage.PacketStorage;
import Storage.Snapshots.PacketSnapshots;
import Storage.WireStorage;
import View.GameEnvironment.Background.BuildBackground;
import View.GamePage.State.GameOverPanel;
import View.GamePage.State.PausePanel;
import View.GamePage.State.WinPanel;
import Controller.Levels.BuildLevel1;
import Controller.Levels.BuildLevel2;
import View.GameEnvironment.Options.HUDPanel;
import View.GameEnvironment.Options.ShopPanel;

import View.Render.GameShapes.System.GameShape;

import View.Main.MainFrame;
import Model.Player.PlayerState;
import View.Render.GameShapes.Wire.WireShape;
import View.Render.GameShapes.Packet.PacketRenderer;

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

    //For Wires
    private final WiringManager wiringManager = new WiringManager();
    private final double MAX_WIRE_LENGTH = 2000;
    private int mousePointX;
    private int mousePointY;

    //For HUD
    private final HUDPanel hudPanel;

    //For Timer
    Timer gameTimer;
    private final TimeController timeController = new TimeController();
    private boolean isRunning = false;

    //For Engine
    private final GameEngine gameEngine = new GameEngine(timeController);

    //For Packet
    private PacketManager packetManager;
    private SpawnPackets spawnPacket;
    private final int totalPackets = 10;
    private int generatedPackets = 0;
    private int lostPackets = 0;

    //For Impact
    private final List<Impact> impacts = new ArrayList<>();

    //For Shop
    private final ShopPanel shopPanel;
    private int coins;

    //For Pause
    private PausePanel pausePanel;
    private boolean isPause = false;

    //For GameOver
    private final GameOverPanel gameOverPanel;

    //For Win
    private final WinPanel winPanel;

    public GamePanel(){
        setLayout(null);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("Resources/background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BuildBackground.buildBackground(screenSizeX, screenSizeY, shapes);

        // PreLoading
        wiringManager.setConnections(ConnectionStorage.LoadConnections());
        wiringManager.setWires(WireStorage.LoadWires());

        //Build Level
        int levelOnGoing = PlayerState.getPlayer().getLevelNumber();
        switch (levelOnGoing) {
            case 1 -> {
                BuildLevel1.buildLevel1(screenSizeX, blockShapes);
            }
            case 2 ->{
                BuildLevel2.buildLevel2(screenSizeX, blockShapes);
            }
        }

        // Loading
        List<WireShape> wireShapes = new ArrayList<>();
        for (Wire wire : wiringManager.getWires()) {
            WireShape wireShape = new WireShape(blockShapes, wire);
            wireShapes.add(wireShape);
        }
        wiringManager.setWireShapes(wireShapes);

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

        //Add Pause
        pausePanel = new PausePanel(
                () -> {
                    pausePanel.setVisible(false);
                    gameTimer.start();
                    isPause = false;
                    isRunning = true; },
                this::retryLevel,
                this::returnToMenu
        );
        pausePanel.setBounds(0, 0, screenSizeX, screenSizeY);
        pausePanel.setVisible(false);
        add(pausePanel);
        setComponentZOrder(pausePanel, 0);

        //Add GameOver
        gameOverPanel = new GameOverPanel(
                this::retryLevel,
                this::returnToMenu
        );
        gameOverPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        gameOverPanel.setVisible(false);
        add(gameOverPanel);
        setComponentZOrder(gameOverPanel, 0);

        //Add Win
        winPanel = new WinPanel(
                this::proceedToNextLevel,
                this::retryLevel,
                this::returnToMenu
        );
        winPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        winPanel.setVisible(false);
        add(winPanel);
        setComponentZOrder(winPanel, 0);

        //
        spawnPacket = new SpawnPackets();
        packetManager = new PacketManager(blockShapes, wiringManager, spawnPacket);

        //Timing
        gameTimer = new Timer(10, _ -> {

            GameLoop.Start(gameTimer, hudPanel, winPanel, gameOverPanel,
                    packetManager, wiringManager,
                    timeController, gameEngine,
                    blockShapes,
                    MAX_WIRE_LENGTH, totalPackets);

            lostPackets = packetManager.getLostPacketsCount();
            coins = PlayerState.getPlayer().getGoldCount();

            if (isRunning) {
                PacketSnapshots.SavePacketSnapshot(PacketStorage.LoadPackets(), timeController.getTime());
            }
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
                if (e.getKeyChar() == 'p') {
                    List<Packet> packets = PacketStorage.LoadPackets();
                    if (packets.isEmpty()) {
                        for (int i = 0; i < 3; i++) {
                            spawnPacket.addPacketToBlock(0, new Packet(generatedPackets, PacketType.MESSENGER_2));
                            generatedPackets++;
                        }
                        for (int i = 3; i < 7; i++) {
                            spawnPacket.addPacketToBlock(0, new Packet(generatedPackets, PacketType.MESSENGER_3));
                            generatedPackets++;
                        }
                        for (int i = 7; i < 10; i++) {
                            spawnPacket.addPacketToBlock(0, new Packet(generatedPackets, PacketType.MESSENGER_1));
                            generatedPackets++;
                        }
                    }
                    isRunning = true;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> gameEngine.setLeftPressed(true);
                    case KeyEvent.VK_RIGHT -> gameEngine.setRightPressed(true);
                    case KeyEvent.VK_TAB -> hudPanel.setVisible(true);
                    case KeyEvent.VK_ESCAPE -> {
                        pausePanel.setVisible(true);
                        gameTimer.stop();
                        isRunning = false;
                        isPause = true;
                    }
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
                if (!isRunning) {
                    wiringManager.handleMousePress(blockShapes, mousePointX, mousePointY);
                    blockManager.handleMousePress(blockShapes, mousePointX, mousePointY);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {

                wiringManager.handleMouseRelease(blockShapes, mousePointX, mousePointY, wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH));
                blockManager.handleMouseRelease(mousePointX, mousePointY);
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePointX = e.getX() - getWidth() / 2;
                mousePointY = e.getY() - getHeight() / 2;
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

    public void disableImpactWaves(int seconds) {
        packetManager.disableWaveForSeconds(seconds);
    }

    public void disableCollisions(int seconds) {
        packetManager.disableImpactForSeconds(seconds);
    }

    public void resetAllNoise() {
        List<Packet> packets = PacketStorage.LoadPackets();
        for (Packet packet : packets){
            packet.resetNoise();
            PacketStorage.SavePackets(packets);
        }
    }

    public void resumeGame() {
        shopPanel.setVisible(false);
        gameTimer.start();
    }

    public void proceedToNextLevel() {
        PlayerState.getPlayer().setLevelNumber(PlayerState.getPlayer().getLevelNumber() + 1);
        MainFrame.startGame();
        winPanel.setVisible(false);
        gameTimer.start();
    }

    private void retryLevel() {
        MainFrame.startGame();
        pausePanel.setVisible(false);
        isRunning = true;
        isPause = false;
    }

    private void returnToMenu() {
        MainFrame.showMenu();
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
        for (WireShape line : wiringManager.getWireShapes()) {
            line.draw(g2d);
        }

        if (wiringManager.isDragging()) {
            wiringManager.drawDrag(g2d, new Point(mousePointX, mousePointY));
        }
        if (wiringManager.isFilleting()) {
            wiringManager.drawFillet(new Point(mousePointX, mousePointY));
        }
        if (blockManager.isDragging()) {
            blockManager.drawDrag(mousePointX, mousePointY);
        }

        //Packet
        List<Packet> packets = PacketStorage.LoadPackets();
        for (Packet p : packets) {
            if (!p.isOnWire()) continue;
            PacketRenderer.draw(g2d,p);
        }

        g2d.dispose();
    }


}
