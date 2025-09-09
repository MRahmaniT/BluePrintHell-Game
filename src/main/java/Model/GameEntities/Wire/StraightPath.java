package Model.GameEntities.Wire;

import java.awt.Shape;
import java.awt.geom.Line2D;
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
        return path;
    }

    @Override public Shape toShape() {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(startPoint.x, startPoint.y);
        path.lineTo(endPoint.x, endPoint.y);
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

    @Override public Nearest nearestTo(Point2D.Float point) {

        float apx = point.x - startPoint.x, apy = point.y - startPoint.y;
        float abx = endPoint.x - startPoint.x, aby = endPoint.y - startPoint.y;
        float ab2 = abx*abx + aby*aby;
        float t = (ab2 <= 1e-12f) ? 0f : (apx*abx + apy*aby) / ab2;
        t = Math.max(0f, Math.min(1f, t));
        Point2D.Float q = pointAt(t);
        float d = (float)Math.hypot(q.x - point.x, q.y - point.y);
        return new Nearest(q, t, d);
    }
}
