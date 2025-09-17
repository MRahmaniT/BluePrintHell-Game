package Client.View.GamePage;

import Client.Controller.GameLoop;
import Client.Controller.Packets.PacketManager;
import Client.Controller.Packets.Spawning.SpawnPackets;
import Client.Controller.Timing.GameEngine;
import Client.Controller.Timing.TimeController;
import Client.Controller.Wiring.WiringManager;
import Client.Controller.Systems.BlockManager;
import Client.Controller.Systems.ChangeBlocksLight;
import Client.Model.Enums.PacketType;
import Client.Model.GameEntities.Impact;
import Client.Model.GameEntities.Packet;

import Client.Model.GameEntities.Wire.Wire;
import Client.Model.Player.Player;
import Client.Storage.Facade.StorageFacade;
import Client.Storage.Player.PlayerStorage;
import Client.Storage.RealTime.GameEnvironment.ClearSaves;
import Client.Storage.RealTime.Snapshots.PacketSnapshots;
import Client.Storage.RealTime.Snapshots.ClearSnapshots;
import Client.View.GameEnvironment.Background.BuildBackground;
import Client.View.GamePage.State.*;
import Client.Controller.Levels.BuildLevel1;
import Client.Controller.Levels.BuildLevel2;
import Client.View.GameEnvironment.Options.HUDPanel;
import Client.View.GameEnvironment.Options.ShopPanel;

import Client.View.Render.GameShapes.System.GameShape;

import Client.View.Main.MainFrame;
import Client.Model.Player.PlayerState;
import Client.View.Render.GameShapes.Wire.WireShape;
import Client.View.Render.GameShapes.Packet.PacketRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public static boolean interrupted = false;

    //For Engine
    private final GameEngine gameEngine = new GameEngine(timeController);

    //For Packet
    private PacketManager packetManager;
    private SpawnPackets spawnPacket;
    private final int totalPackets = 10;
    public static int generatedPackets = 0;
    private int lostPackets = 0;

    //For Impact
    private final List<Impact> impacts = new ArrayList<>();

    //For Shop
    private final ShopPanel shopPanel;

    //For Pause
    private PausePanel pausePanel;
    private boolean isPause = false;

    //For GameOver
    private final GameOverPanel gameOverPanel;

    //For Win
    private final WinPanel winPanel;

    //For Save and Load
    private SavePanel savePanel;
    private LoadPanel loadPanel;

    //Player Data
    private int levelOnGoing = 1;
    private int coins = 0;
    private boolean madeDecision;

    public GamePanel(){
        setLayout(null);

        //Player Data
        madeDecision = false;
        if (PlayerState.getPlayer() != null) {
            levelOnGoing = PlayerState.getPlayer().getLevelNumber();
            coins = PlayerState.getPlayer().getGoldCount();
            timeController.setTime(PlayerState.getPlayer().getTimePlayed());
            madeDecision = PlayerState.getPlayer().isMadeDecision();
        }

        //Add Save and Load
        savePanel = new SavePanel(
                this::saveGame,
                this::returnToMenu,
                () -> {
                    savePanel.setVisible(false);
                    gameTimer.start();
                    isPause = false;
                    isRunning = true; }
        );
        savePanel.setBounds(0, 0, screenSizeX, screenSizeY);
        savePanel.setVisible(false);
        add(savePanel);
        setComponentZOrder(savePanel, 0);

        loadPanel = new LoadPanel(
                () -> {
                    loadPanel.setVisible(false);
                },
                this::retryLevel,
                this::returnToMenu
        );
        loadPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        loadPanel.setVisible(false);
        add(loadPanel);
        setComponentZOrder(loadPanel, 0);


        if (!madeDecision) {
            loadPanel.setVisible(true);
        }

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("Resources/background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BuildBackground.buildBackground(screenSizeX, screenSizeY, shapes);

        // PreLoading
        wiringManager.setConnections(StorageFacade.loadConnections());
        wiringManager.setWires(StorageFacade.loadWires());

        //Build Level
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
                this::showSavePanel
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

        //
        Thread paint = new Thread(this::repaint);

        //Timing
        gameTimer = new Timer(10, _ -> {

            if (isRunning && !interrupted) {
                PacketSnapshots.SavePacketSnapshot(StorageFacade.loadPackets(), timeController.getTime());
            }

            GameLoop.Start(gameTimer, hudPanel, winPanel, gameOverPanel,
                    packetManager, wiringManager,
                    timeController, gameEngine,
                    blockShapes,
                    MAX_WIRE_LENGTH, totalPackets,
                    isRunning);

            lostPackets = packetManager.getLostPacketsCount();
            if (PlayerState.getPlayer() != null) {
                coins = PlayerState.getPlayer().getGoldCount();
                PlayerState.getPlayer().setTimePlayed(timeController.getTime());

                 if (madeDecision && MainFrame.gamePanel.isVisible()){
                    madeDecision = false;
                    assert PlayerState.getPlayer() != null;
                    PlayerState.getPlayer().setMadeDecision(false);
                }

                List<Player> players = PlayerStorage.loadAllPlayers();
                for (Player player : players) {
                    if (Objects.equals(player.getUsername(), PlayerState.getPlayer().getUsername())) {
                        player.setMadeDecision(PlayerState.getPlayer().isMadeDecision());
                        player.setGoldCount(coins);
                        player.setTimePlayed(timeController.getTime());
                        PlayerStorage.saveAllPlayers(players);
                    }
                }

            }

            paint.run();

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
                    List<Packet> packets = StorageFacade.loadPackets();
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
        List<Packet> packets = StorageFacade.loadPackets();
        for (Packet packet : packets){
            packet.resetNoise();
            StorageFacade.savePackets(packets);
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
        ClearSaves.clearSnapshotFolder("Resources/Saves/Realtime");
        ClearSnapshots.clearSnapshotFolder("Resources/Saves/Snapshot");
        madeDecision = true;
        PlayerState.getPlayer().setMadeDecision(true);
        MainFrame.startGame();
        pausePanel.setVisible(false);
        isRunning = true;
        isPause = false;
        interrupted = false;
        generatedPackets = 0;
    }

    private void showSavePanel() {
        pausePanel.setVisible(false);
        savePanel.setVisible(true);
    }

    private void saveGame() {
        ClearSnapshots.clearSnapshotFolder("Resources/Saves/Snapshot");
        madeDecision = true;
        PlayerState.getPlayer().setMadeDecision(madeDecision);
        List<Player> players = PlayerStorage.loadAllPlayers();
        for (Player player : players) {
            if (Objects.equals(player.getUsername(), PlayerState.getPlayer().getUsername())) {
                player.setMadeDecision(PlayerState.getPlayer().isMadeDecision());
                player.setGoldCount(coins);
                player.setTimePlayed(timeController.getTime());
                PlayerStorage.saveAllPlayers(players);
            }
        }
        MainFrame.showMenu();
    }

    private void returnToMenu() {
        ClearSaves.clearSnapshotFolder("Resources/Saves/Realtime");
        ClearSnapshots.clearSnapshotFolder("Resources/Saves/Snapshot");
        madeDecision = true;
        PlayerState.getPlayer().setMadeDecision(madeDecision);
        List<Player> players = PlayerStorage.loadAllPlayers();
        for (Player player : players) {
            if (Objects.equals(player.getUsername(), PlayerState.getPlayer().getUsername())) {
                player.setMadeDecision(PlayerState.getPlayer().isMadeDecision());
                player.setGoldCount(coins);
                player.setTimePlayed(timeController.getTime());
                PlayerStorage.saveAllPlayers(players);
            }
        }
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
        if (!interrupted) {
            List<Packet> packets = StorageFacade.loadPackets();
            for (Packet p : packets) {
                if (!p.isOnWire()) continue;
                PacketRenderer.draw(g2d, p);
            }
        } else {
            List<Packet> packets = PacketSnapshots.LoadPacketSnapshots(timeController.getTime());
            for (Packet p : packets) {
                if (!p.isOnWire()) continue;
                PacketRenderer.draw(g2d, p);
            }
        }

        g2d.dispose();
    }


}
