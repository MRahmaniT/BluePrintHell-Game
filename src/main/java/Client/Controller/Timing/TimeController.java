package Client.Controller.Timing;

public class TimeController {
    private double time;
    private static double t;

    public TimeController() {
        this.time = 0.0;
    }

    public void update(boolean leftPressed, boolean rightPressed) {
        if (leftPressed && !rightPressed) {
            time = Math.max(0, time - 0.01);
        } else if (rightPressed && !leftPressed) {
            time += 0.01;
        }
        t = time;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public static double giveTime () {
        return t;
    }
    public String getFormattedTime() {
        return String.format("Time: %.1f", time);
    }

    public void reset() {
        time = 0.0;
    }
}
