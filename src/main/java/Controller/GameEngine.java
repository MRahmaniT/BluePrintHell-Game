package Controller;

public class GameEngine {
    private final TimeController timeController;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameEngine(TimeController timeController) {
        this.timeController = timeController;
    }

    public void setLeftPressed(boolean pressed) {
        this.leftPressed = pressed;
    }

    public void setRightPressed(boolean pressed) {
        this.rightPressed = pressed;
    }

    public void update() {
        timeController.update(leftPressed, rightPressed);
    }

    public String getFormattedTime() {
        return timeController.getFormattedTime();
    }

    public void reset() {
        timeController.reset();
        leftPressed = false;
        rightPressed = false;
    }

}
