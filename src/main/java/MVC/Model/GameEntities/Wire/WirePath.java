package MVC.Model.GameEntities.Wire;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public interface WirePath {

    Path2D.Float getPath ();

    Point2D.Float pointAt(float t);

    Point2D.Float tangentAt(float t);

    float length();

    Shape toShape();

    Nearest nearestTo(Point2D.Float p, float totalLength, float pastLength);

    final class Nearest {
        public final float t;                 // parameter [0..1] of closest point
        public final Point2D.Float point;     // closest point on the path
        public final float distance;          // distance to p
        public final float alpha;
        public Nearest(Point2D.Float point, float t, float distance, float alpha) {
            this.t = t; this.point = point; this.distance = distance; this.alpha = alpha;
        }
    }
}
