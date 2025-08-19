package Controller.Wiring;

import java.awt.geom.Point2D;

public class LinePath implements WirePath {

    private final float xDirection, yDirection, length;

    public LinePath(Point2D.Float startPoint, Point2D.Float destinationPoint) {
        this.xDirection = destinationPoint.x - startPoint.x;
        this.yDirection = destinationPoint.y - startPoint.y;
        this.length = (float) Math.hypot(xDirection, yDirection);
    }

    @Override public Point2D.Float tangent() {
        return new Point2D.Float(xDirection/length, yDirection/length);
    }

    @Override public float length() { return length; }

    public float getXDirection() {
        return xDirection;
    }

    public float getYDirection() {
        return yDirection;
    }

    public float getLength() {
        return length;
    }
}
