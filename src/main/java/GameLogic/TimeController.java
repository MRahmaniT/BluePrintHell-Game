package GameLogic;

public class TimeController {
    private double time;

    public TimeController() {
        this.time = 0.0;
    }

    public void update(boolean leftPressed, boolean rightPressed) {
        if (leftPressed && !rightPressed) {
            time -= 0.1;
        } else if (rightPressed && !leftPressed) {
            time += 0.1;
        }
    }

    public double getTime() {
        return time;
    }

    public String getFormattedTime() {
        return String.format("Time: %.1f", time);
    }

    public void reset() {
        time = 0.0;
    }
}
