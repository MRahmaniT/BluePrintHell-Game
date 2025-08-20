package View.Render.GameShapes.Wire;

import Controller.Wiring.Wire;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

public class QuadBezierWire implements Wire {
    private final Point2D.Float a, c, b; // start, control, end
    private final Color color;

    public QuadBezierWire(Point2D.Float a, Point2D.Float control, Point2D.Float b, Color color) {
        this.a = a; this.c = control; this.b = b; this.color = color;
    }

    @Override public Point2D.Float pointAt(float t) {
        t = Math.max(0, Math.min(1, t));
        float u = 1 - t;
        float x = u*u*a.x + 2*u*t*c.x + t*t*b.x;
        float y = u*u*a.y + 2*u*t*c.y + t*t*b.y;
        return new Point2D.Float(x, y);
    }

    @Override public Point2D.Float tangentAt(float t) {
        t = Math.max(0, Math.min(1, t));
        float x = 2*(1 - t)*(c.x - a.x) + 2*t*(b.x - c.x);
        float y = 2*(1 - t)*(c.y - a.y) + 2*t*(b.y - c.y);
        float len = (float)Math.hypot(x, y);
        if (len == 0) return new Point2D.Float(1, 0);
        return new Point2D.Float(x/len, y/len);
    }

    @Override public float approxLength() {
        // simple 10-sample polyline estimate (good enough for speed-to-dt)
        Point2D.Float prev = pointAt(0f);
        float sum = 0;
        for (int i = 1; i <= 10; i++) {
            Point2D.Float p = pointAt(i/10f);
            sum += (float) prev.distance(p);
            prev = p;
        }
        return sum;
    }

    @Override public void draw(Graphics2D g) {
        QuadCurve2D.Float curve = new QuadCurve2D.Float(a.x, a.y, c.x, c.y, b.x, b.y);
        g.setColor(color);
        g.setStroke(new BasicStroke(4f));
        g.draw(curve);
    }
}
