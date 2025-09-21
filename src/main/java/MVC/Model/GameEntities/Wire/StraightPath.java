package MVC.Model.GameEntities.Wire;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public final class StraightPath implements WirePath {
    private final Point2D.Float startPoint;
    private final Point2D.Float endPoint;
    private final float length;
    private final Point2D.Float direction; // unit AB

    public StraightPath(Point2D.Float startPoint, Point2D.Float endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        float dx = endPoint.x - startPoint.x, dy = endPoint.y - startPoint.y;
        float L = (float)Math.hypot(dx, dy);
        this.length = Math.max(L, 1e-6f);
        this.direction = new Point2D.Float(dx/ length, dy/ length);
    }

    @Override
    public Path2D.Float getPath() {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        path.closePath();
        return path;
    }

    @Override public Shape toShape() {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
        path.closePath();
        return path;
    }

    @Override public Point2D.Float pointAt(float t) {
        t = Math.max(0, Math.min(1, t));
        return new Point2D.Float(startPoint.x + t*(endPoint.x - startPoint.x),
                startPoint.y + t*(endPoint.y - startPoint.y));
    }

    @Override public Point2D.Float tangentAt(float t) {
        return new Point2D.Float(direction.x, direction.y);
    }

    @Override public float length() { return length; }

    @Override public Nearest nearestTo(Point2D.Float point, float totalLength, float pastLength) {
        Point2D.Float nearestPoint;
        float t;
        float distance;

        float a = endPoint.x - startPoint.x;
        float b = endPoint.y - startPoint.y;
        float c = endPoint.y - startPoint.y;
        float d = startPoint.x - endPoint.x;

        float determinant  = 1/(a*d - b*c);

        float aPrime = determinant * d;
        float bPrime = determinant * b * (-1);
        float cPrime = determinant + c * (-1);
        float dPrime = determinant * a;

        float xPrime = point.x - startPoint.x;
        float yPrime = point.y - startPoint.y;

        float alpha = aPrime * xPrime + bPrime * yPrime;
        float betta = cPrime * xPrime + dPrime * yPrime;

        if (alpha <= 0) nearestPoint = startPoint;
        else if (alpha >= 1) nearestPoint = endPoint;
        else {
            nearestPoint = new Point2D.Float(alpha * a + startPoint.x, alpha * c + startPoint.y);
        }

        t = ((float) Math.hypot(nearestPoint.x - startPoint.x, nearestPoint.y - startPoint.y) + pastLength) / totalLength;

        distance = (float)Math.hypot(nearestPoint.x - point.x, nearestPoint.y - point.y);

        return new Nearest(nearestPoint, t, distance, alpha);
    }
}
