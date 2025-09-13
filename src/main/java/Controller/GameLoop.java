package Controller;

import Controller.Packets.PacketManager;
import Controller.Timing.GameEngine;
import Controller.Timing.TimeController;
import Controller.Wiring.WiringManager;
import Model.GameEntities.BlockSystem;
import Model.Player.PlayerState;
import Storage.BlockSystemStorage;
import View.GameEnvironment.Options.HUDPanel;
import View.GamePage.GamePanel;
import View.GamePage.State.GameOverPanel;
import View.GamePage.State.WinPanel;
import View.Main.MainFrame;
import View.Render.GameShapes.System.GameShape;
import View.Render.GameShapes.Wire.WireShape;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameLoop {
    public static void Start (Timer gameTimer, HUDPanel hudPanel, WinPanel winPanel, GameOverPanel gameOverPanel,
                              PacketManager packetManager, WiringManager wiringManager,
                              TimeController timeController, GameEngine gameEngine,
                              List<GameShape> blockShapes,
                              double MAX_WIRE_LENGTH, int totalPackets,
                              boolean isRunning) {
        //start
        if (gameEngine.isLeftPressed() || gameEngine.isRightPressed()) GamePanel.interrupted = true;
        if (isRunning && !GamePanel.interrupted){
            timeController.update(false,true);
        }
        gameEngine.update();

        if (!GamePanel.interrupted){
            packetManager.manageMovement();
        }


        int lostPackets = packetManager.getLostPacketsCount();
        int coins = PlayerState.getPlayer().getGoldCount();

        hudPanel.update(
                Math.max(0, wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH)),
                timeController.getFormattedTime(),
                lostPackets,
                totalPackets,
                coins
        );

        if(wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH) < 0){
            for (WireShape line : wiringManager.getWireShapes()) {
                line.setColor(Color.RED);
            }
        }else {
            for (WireShape line : wiringManager.getWireShapes()) {
                line.setColor(Color.CYAN);
            }
        }

        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        if (!blockShapes.isEmpty() && totalPackets == blockSystems.getLast().queueCount()) {
            gameTimer.stop();
            MainFrame.audioManager.playSoundEffect("Resources/win.wav");
            winPanel.updateStats(
                    blockSystems.getLast().queueCount(),
                    totalPackets,
                    timeController.getFormattedTime(),
                    coins
            );
            winPanel.setVisible(true);
            BlockSystemStorage.SaveBlockSystems(blockSystems);
        }

        if (packetManager.getLostPacketsCount() >= totalPackets / 2) {
            gameTimer.stop();
            gameOverPanel.updateStats(
                    packetManager.getLostPacketsCount(),
                    totalPackets,
                    timeController.getFormattedTime()
            );
            gameOverPanel.setVisible(true);
        }
    }
}
