package Modes.Client;

import MVC.Controller.Packets.Spawning.SpawnPackets;
import MVC.Controller.Timing.GameEngine;
import MVC.Controller.Wiring.WiringManager;
import Modes.InputSink;
import MVC.View.GamePage.GamePanel;
import MVC.View.GameEnvironment.Options.HUDPanel;
import MVC.View.GamePage.State.PausePanel;

import javax.swing.*;

public class LocalInputSink implements InputSink {

    private final GamePanel gamePanel;
    private final WiringManager wiring;
    private final GameEngine engine;
    private final SpawnPackets spawner;
    private final HUDPanel hud;
    private final PausePanel pause;
    private final Timer gameTimer;

    private int mousePointX;
    private int mousePointY;

    public LocalInputSink(GamePanel gamePanel,
                          WiringManager wiring,
                          GameEngine engine,
                          SpawnPackets spawner,
                          HUDPanel hud,
                          PausePanel pause,
                          Timer gameTimer) {
        this.gamePanel = gamePanel;
        this.wiring = wiring;
        this.engine = engine;
        this.spawner = spawner;
        this.hud = hud;
        this.pause = pause;
        this.gameTimer = gameTimer;
    }

    @Override
    public void keyTyped(char ch, int keyCode) {
        if (ch == 'p' && !gamePanel.getGameLogic().isIntersected()) {
            gamePanel.getGameLogic().handleP();
        }
    }

    @Override
    public void keyPressed(int keyCode, String keyName) {
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_LEFT  -> engine.setLeftPressed(true);
            case java.awt.event.KeyEvent.VK_RIGHT -> engine.setRightPressed(true);
            case java.awt.event.KeyEvent.VK_TAB   -> hud.setVisible(true);
            case java.awt.event.KeyEvent.VK_ESCAPE -> gamePanel.handleEscape();
        }
    }

    @Override
    public void keyReleased(int keyCode, String keyName) {
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_LEFT  -> engine.setLeftPressed(false);
            case java.awt.event.KeyEvent.VK_RIGHT -> engine.setRightPressed(false);
            case java.awt.event.KeyEvent.VK_TAB   -> hud.setVisible(false);
        }
    }

    @Override
    public void mouseDown(int button, int x, int y) {
        gamePanel.handleMousePress(button, x, y);
    }

    @Override
    public void mouseUp(int button, int x, int y) {
        gamePanel.handleMouseRelease(button, x, y);
    }

    @Override
    public void mouseClick(int button, int x, int y) {
        gamePanel.handleMouseClicked(button, x, y);
    }

    @Override
    public void mouseMove(int x, int y) {
        mousePointX = x;
        mousePointY = y;
    }

    @Override
    public void mouseDrag(int button, int x, int y) {
        mousePointX = x;
        mousePointY = y;
    }

    @Override
    public void uiAction(String action, String payloadJson) {
        if ("OPEN_SHOP".equals(action)) {
            gamePanel.handleOpenShop();
        }
    }
}
