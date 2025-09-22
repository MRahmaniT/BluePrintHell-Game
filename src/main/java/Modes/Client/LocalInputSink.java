package Modes.Client;

import Modes.InputSink;
import MVC.View.GamePage.GamePanel;

import javax.swing.*;

public class LocalInputSink implements InputSink {

    private final GamePanel gamePanel;

    private int mousePointX;
    private int mousePointY;

    public LocalInputSink(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(char ch, int keyCode) {
        if (ch == 'p' && !gamePanel.getGameLogic().isIntersected()) {
            gamePanel.getGameLogic().handleP();
        }
    }

    @Override
    public void keyPressed(int keyCode, String keyName) {
        gamePanel.getGameLogic().handleKeyPressed(keyCode, keyName);
    }

    @Override
    public void keyReleased(int keyCode, String keyName) {
        gamePanel.getGameLogic().handleKeyReleased(keyCode, keyName);
    }

    @Override
    public void mouseDown(int button, int x, int y) {
        gamePanel.getGameLogic().handleMousePress(button, x, y);
    }

    @Override
    public void mouseUp(int button, int x, int y) {
        gamePanel.getGameLogic().handleMouseRelease(button, x, y);
    }

    @Override
    public void mouseClick(int button, int x, int y) {
        gamePanel.getGameLogic().handleMouseClicked(button, x, y);
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
            gamePanel.getGameLogic().handleOpenShop();
        }
    }
}
