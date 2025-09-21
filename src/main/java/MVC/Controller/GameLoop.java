package MVC.Controller;

import MVC.Controller.Packets.PacketManager;
import MVC.Controller.Timing.GameEngine;
import MVC.Controller.Timing.TimeController;
import MVC.Controller.Wiring.WiringManager;
import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.GameData;
import MVC.Model.Player.PlayerState;
import Storage.Facade.StorageFacade;
import MVC.View.GameEnvironment.Options.HUDPanel;
import MVC.View.GamePage.GamePanel;
import MVC.View.GamePage.State.GameOverPanel;
import MVC.View.GamePage.State.WinPanel;
import MVC.View.Main.MainFrame;
import MVC.View.Render.GameShapes.System.GameShape;
import MVC.View.Render.GameShapes.Wire.WireShape;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

public class GameLoop {
    public static void Start (PacketManager packetManager, WiringManager wiringManager,
                              TimeController timeController, GameEngine gameEngine,
                              List<GameShape> blockShapes,
                              double MAX_WIRE_LENGTH, int totalPackets,
                              boolean isRunning) {
        //start
        if (gameEngine.isLeftPressed() || gameEngine.isRightPressed()) GameLogic.interrupted = true;
        if (isRunning && !GameLogic.interrupted){
            timeController.update(false,true);
        }
        gameEngine.update();

        if (!GameLogic.interrupted){
            packetManager.manageMovement();
        }

        int coins = 0;
        if (PlayerState.getPlayer() != null) {
            coins = PlayerState.getPlayer().getGoldCount();
        }

        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
        GameData gameData = new GameData(wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH),
                timeController.getFormattedTime(), blockSystems.get(blockSystems.size()-1).queueCount(), packetManager.getLostPacketsCount(),
                totalPackets, coins);
        StorageFacade.saveGameData(gameData);

        if(wiringManager.getRemainingWireLength(MAX_WIRE_LENGTH) < 0){
            for (WireShape wireShape : wiringManager.getWireShapes()) {
                wireShape.setColor(Color.RED);
            }
        }else {
            for (WireShape wireShape : wiringManager.getWireShapes()) {
                wireShape.setColor(Color.CYAN);
            }
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
