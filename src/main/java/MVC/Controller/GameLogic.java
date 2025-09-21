package MVC.Controller;

import MVC.Controller.Packets.PacketManager;
import MVC.Controller.Packets.Spawning.SpawnPackets;
import MVC.Controller.Timing.GameEngine;
import MVC.Controller.Timing.TimeController;
import MVC.Controller.Wiring.WiringManager;
import MVC.Controller.Systems.BlockManager;
import MVC.Model.Enums.PacketType;
import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.GameData;
import MVC.Model.GameEntities.Packet;

import MVC.Model.GameEntities.Wire.Wire;
import MVC.Model.Player.Player;
import MVC.View.GamePage.GamePanel;
import MVC.View.GamePage.State.*;
import Modes.InputSink;
import Storage.Facade.StorageFacade;
import Storage.Player.PlayerStorage;
import Storage.RealTime.GameEnvironment.ClearSaves;
import Storage.RealTime.Snapshots.PacketSnapshots;
import Storage.RealTime.Snapshots.ClearSnapshots;
import MVC.Controller.Levels.BuildLevel1;
import MVC.Controller.Levels.BuildLevel2;

import MVC.View.Render.GameShapes.System.GameShape;

import MVC.View.Main.MainFrame;
import MVC.Model.Player.PlayerState;
import MVC.View.Render.GameShapes.Wire.WireShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameLogic {
    //
    private GamePanel gamePanel;
    private final InputSink input;
    private boolean online;

    //For Blocks
    private final List<GameShape> blockShapes = new ArrayList<>();
    private BlockManager blockManager;

    private boolean youCanMoveBlock = false;
    private boolean notGoodPosition = false;

    //For Wires
    private WiringManager wiringManager;
    private final double MAX_WIRE_LENGTH = 1000;

    private boolean youCanDisableAcceleration = false;
    private boolean youCanDisableMissAlignment = false;

    //intersect
    private boolean isIntersected = false;

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

    //Player Data
    private int levelOnGoing = 1;
    private int coins = 0;
    private boolean madeDecision;

    public GameLogic (GamePanel gamePanel, InputSink input, boolean online) {
        this.gamePanel = gamePanel;
        this.input = input;
        this.online = online;
    }

    public void Run () {
        blockManager = new BlockManager(gamePanel);
        wiringManager = new WiringManager(gamePanel);

        //Player Data
        madeDecision = false;
        if (PlayerState.getPlayer() != null) {
            levelOnGoing = PlayerState.getPlayer().getLevelNumber();
            coins = PlayerState.getPlayer().getGoldCount();
            timeController.setTime(PlayerState.getPlayer().getTimePlayed());
            madeDecision = PlayerState.getPlayer().isMadeDecision();
        }

        // PreLoading
        wiringManager.setConnections(StorageFacade.loadConnections());
        wiringManager.setWires(StorageFacade.loadWires());

        //Build Level
        switch (levelOnGoing) {
            case 1 -> {
                BuildLevel1.buildLevel1(gamePanel.getWidth(), blockShapes);
            }
            case 2 ->{
                BuildLevel2.buildLevel2(gamePanel.getWidth(), blockShapes);
            }
        }

        // Loading
        List<WireShape> wireShapes = new ArrayList<>();
        for (Wire wire : wiringManager.getWires()) {
            WireShape wireShape = new WireShape(blockShapes, wire);
            wireShapes.add(wireShape);
        }
        wiringManager.setWireShapes(wireShapes);

        spawnPacket = new SpawnPackets();
        packetManager = new PacketManager(blockShapes, wiringManager, spawnPacket);

        //Timing
        gameTimer = new Timer(10, _ -> {

            if (isRunning && !interrupted) {
                PacketSnapshots.SavePacketSnapshot(StorageFacade.loadPackets(), timeController.getTime());
            }

            if (!online) {
                GameLoop.Start(packetManager, wiringManager, timeController, gameEngine,
                        blockShapes, MAX_WIRE_LENGTH, totalPackets, isRunning);
            }

            GameData gameData = StorageFacade.loadGameData();
            gamePanel.getHudPanel().update(gameData.getRemainingWireLength(), gameData.getFormatedTime(), gameData.getLostPackets(),
                    gameData.getTotalPackets(), gameData.getCoins());

            //Win or Loos
            List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
            if (gameData.getLostPackets() >= gameData.getTotalPackets() / 2) {
                gameTimer.stop();
                gamePanel.getGameOverPanel().updateStats(gameData.getLostPackets(), gameData.getTotalPackets(),gameData.getFormatedTime());
                gamePanel.getGameOverPanel().setVisible(true);
            }else if (!blockShapes.isEmpty() && !blockSystems.isEmpty() && gameData.getTotalPackets() == blockSystems.getLast().queueCount()) {
                gameTimer.stop();
                MainFrame.audioManager.playSoundEffect("Resources/win.wav");
                gamePanel.getWinPanel().updateStats(gameData.getDeliveredPackets(), gameData.getTotalPackets(), gameData.getFormatedTime(),
                        gameData.getCoins());
                gamePanel.getWinPanel().setVisible(true);
                StorageFacade.saveBlockSystems(blockSystems);
            }

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

            if (!online) {
                gamePanel.getPainter().run();
            }

            if (isIntersected || wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH) < 0) {
                notGoodPosition = true;
            } else {
                notGoodPosition = false;
            }
        });
        gameTimer.start();
    }

    // Handlers
    public void handleP () {
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

    public void handleEscape () {
        gamePanel.getPausePanel().setVisible(true);
        gameTimer.stop();
        isRunning = false;
    }

    public void handleMouseClicked(int button, int mousePointX, int mousePointY) {
        wiringManager.handleMouseClick(blockShapes, mousePointX, mousePointY);
    }

    public void handleMousePress (int button, int mousePointX, int mousePointY) {
        if (!isRunning && !youCanDisableAcceleration && !youCanDisableMissAlignment) {
            wiringManager.handleMousePress(blockShapes, mousePointX, mousePointY);
            if (youCanMoveBlock) {
                blockManager.handleMousePress(blockShapes, mousePointX, mousePointY);
            }
        }
    }

    public void handleMouseRelease (int button, int mousePointX, int mousePointY) {
        if (!isRunning) {
            wiringManager.handleMouseRelease(blockShapes, mousePointX, mousePointY, wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH));
            blockManager.handleMouseRelease(mousePointX, mousePointY);
            gamePanel.getPainter().run();
        }
    }

    public void handleOpenShop () {
        gamePanel.getShopPanel().setVisible(true);
        gameTimer.stop();
    }


    public int getCoins() { return coins; }

    public void spendCoins(int amount) { PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() - amount); }

    public void disableImpactWaves(int seconds) {
        packetManager.disableWaveForSeconds(seconds);
    }

    public void disableCollisions(int seconds) {
        packetManager.disableImpactForSeconds(seconds);
    }

    public void disableAcceleration(int seconds, Point2D.Float point) {
        packetManager.disableAccelerationForSeconds(seconds, point);
    }

    public void disableMissAlignment(int seconds, Point2D.Float point) {
        packetManager.disableMissAlignmentForSeconds(seconds, point);
    }

    public void resetAllNoise() {
        List<Packet> packets = StorageFacade.loadPackets();
        for (Packet packet : packets){
            packet.resetNoise();
            StorageFacade.savePackets(packets);
        }
    }

    public void resumeGame() {
        gamePanel.getShopPanel().setVisible(false);
        gameTimer.start();
    }

    public void proceedToNextLevel() {
        PlayerState.getPlayer().setLevelNumber(PlayerState.getPlayer().getLevelNumber() + 1);
        MainFrame.startGame();
        gamePanel.getWinPanel().setVisible(false);
        gameTimer.start();
    }

    public void retryLevel() {
        ClearSaves.clearSnapshotFolder("Resources/Saves/Realtime");
        ClearSnapshots.clearSnapshotFolder("Resources/Saves/Snapshot");
        madeDecision = true;
        PlayerState.getPlayer().setMadeDecision(true);
        MainFrame.startGame();
        gamePanel.getPausePanel().setVisible(false);
        isRunning = true;
        interrupted = false;
        generatedPackets = 0;
    }

    public void showSavePanel() {
        gamePanel.getPausePanel().setVisible(false);
        gamePanel.getSavePanel().setVisible(true);
    }

    public void saveGame() {
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

    public void returnToMenu() {
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


    public boolean getYouCanDisableAcceleration() {
        return youCanDisableAcceleration;
    }

    public void setYouCanDisableAcceleration(boolean youCanDisableAcceleration) {
        this.youCanDisableAcceleration = youCanDisableAcceleration;
    }

    public boolean getYouCanDisableMissAlignment() {
        return youCanDisableMissAlignment;
    }

    public void setYouCanDisableMissAlignment(boolean youCanDisableMissAlignment) {
        this.youCanDisableMissAlignment = youCanDisableMissAlignment;
    }

    public void setYouCanMoveBlock (boolean b) {
        youCanMoveBlock = b;
    }

    public boolean getNotGoodPosition () {
        return notGoodPosition;
    }

    public boolean isIntersected () {
        return isIntersected;
    }

    public void setIntersected (boolean b) {
        this.isIntersected = b;
    }

    public boolean isInterrupted () {
        return interrupted;
    }

    public void setInterrupted (boolean b) {
        interrupted = b;
    }
    public Timer getGameTimer () {
        return gameTimer;
    }

    public boolean isRunning () {
        return isRunning;
    }

    public void setRunning (boolean b) {
        this.isRunning = b;
    }

    public boolean isMadeDecision() {
        return madeDecision;
    }

    public TimeController getTimeController () {
        return timeController;
    }

    public List<GameShape> getBlockShapes () {
        return blockShapes;
    }

    public WiringManager getWiringManager () {
        return wiringManager;
    }

    public BlockManager getBlockManager () {
        return blockManager;
    }
}

