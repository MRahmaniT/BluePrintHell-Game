package MVC.Model.GameEntities.Wire;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class OneFilletPath implements WirePath {
    private final ArrayList<StraightPath> straightPathList = new ArrayList<>();

    public OneFilletPath(Point2D.Float startPoint, Point2D.Float midPoint, Point2D.Float endPoint) {
        straightPathList.add(new StraightPath(startPoint, midPoint));
        straightPathList.add(new StraightPath(midPoint, endPoint));
    }

    @Override public float length() {
        return straightPathList.get(0).length() + straightPathList.get(1).length();
    }

    @Override
    public Path2D.Float getPath() {
        Path2D.Float path = new Path2D.Float();
        path.append(straightPathList.get(0).getPath(),false);
        path.append(straightPathList.get(1).getPath(), false);
        return path;
    }

    @Override public Point2D.Float pointAt(float t) {
        if (t <= straightPathList.get(0).length()/ length()) {
            return straightPathList.get(0).pointAt(t);
        } else {
            return straightPathList.get(1).pointAt(t);
        }
    }

    @Override public Point2D.Float tangentAt(float t) {
        if (t < straightPathList.get(0).length()/ length()) {
            return straightPathList.get(0).tangentAt(t);
        } else {
            return straightPathList.get(1).tangentAt(t);
        }
    }

    @Override public Nearest nearestTo(Point2D.Float point, float totalLength, float pastLength) {
        Nearest nearestTo1 = straightPathList.get(0).nearestTo(point, totalLength, 0);
        Nearest nearestTo2 = straightPathList.get(1).nearestTo(point, totalLength, straightPathList.get(0).length());
        if (nearestTo1.distance >= nearestTo2.distance) {
            return nearestTo2;
        } else {
            return nearestTo1;
        }
    }

    @Override public Shape toShape() {
        Path2D.Float path = new Path2D.Float();
        path.append(straightPathList.get(0).getPath(),false);
        path.append(straightPathList.get(1).getPath(), false);
        return path;
    }
}
