package Client.Controller;

import Client.Controller.Packets.PacketManager;
import Client.Controller.Timing.GameEngine;
import Client.Controller.Timing.TimeController;
import Client.Controller.Wiring.WiringManager;
import Client.Model.GameEntities.BlockSystem;
import Client.Model.Player.PlayerState;
import Client.Storage.Facade.StorageFacade;
import Client.View.GameEnvironment.Options.HUDPanel;
import Client.View.GamePage.GamePanel;
import Client.View.GamePage.State.GameOverPanel;
import Client.View.GamePage.State.WinPanel;
import Client.View.Main.MainFrame;
import Client.View.Render.GameShapes.Packet.PacketRenderer;
import Client.View.Render.GameShapes.System.GameShape;
import Client.View.Render.GameShapes.Wire.WireShape;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
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
        int coins = 0;
        if (PlayerState.getPlayer() != null) {
            coins = PlayerState.getPlayer().getGoldCount();
        }

        hudPanel.update(
                Math.max(0, wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH)),
                timeController.getFormattedTime(),
                lostPackets,
                totalPackets,
                coins
        );

        if(wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH) < 0){
            for (WireShape wireShape : wiringManager.getWireShapes()) {
                wireShape.setColor(Color.RED);
            }
        }else {
            for (WireShape wireShape : wiringManager.getWireShapes()) {
                wireShape.setColor(Color.CYAN);
            }
        }

        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
        if (!blockShapes.isEmpty() && !blockSystems.isEmpty() && totalPackets == blockSystems.getLast().queueCount()) {
            gameTimer.stop();
            MainFrame.audioManager.playSoundEffect("Resources/win.wav");
            winPanel.updateStats(
                    blockSystems.getLast().queueCount(),
                    totalPackets,
                    timeController.getFormattedTime(),
                    coins
            );
            winPanel.setVisible(true);
            StorageFacade.saveBlockSystems(blockSystems);
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

        // wire on system
        for (GameShape blockShape : blockShapes) {
            for (WireShape wireShape : wiringManager.getWireShapes()) {
                Shape s1 = blockShape.getShape();
                Shape s2 = wireShape.getWirePath();
                if (s1 == null || s2 == null) continue;

                Area a1 = new Area(s1);
                a1.intersect(new Area(s2));
            }
        }
    }
}
