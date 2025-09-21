package Modes;

public interface InputSink {
    default void keyTyped(char ch, int keyCode) {}
    default void keyPressed(int keyCode, String keyName) {}
    default void keyReleased(int keyCode, String keyName) {}

    default void mouseDown(int button, int x, int y) {}
    default void mouseUp(int button, int x, int y) {}
    default void mouseClick(int button, int x, int y) {}
    default void mouseDrag(int button, int x, int y) {}
    default void mouseMove(int x, int y) {}

    default void uiAction(String action, String payloadJson) {} // e.g. "OPEN_SHOP"
}
