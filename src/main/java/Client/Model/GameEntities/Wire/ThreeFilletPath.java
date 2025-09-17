package Client.Model.GameEntities.Wire;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ThreeFilletPath implements WirePath {
    private final ArrayList<StraightPath> straightPathList = new ArrayList<>();

    public ThreeFilletPath(Point2D.Float startPoint, Point2D.Float midPoint1, Point2D.Float midPoint2, Point2D.Float midPoint3, Point2D.Float endPoint) {
        float distance1 = (float) Math.hypot(startPoint.x - midPoint1.x, startPoint.y - midPoint1.y);
        float distance2 = (float) Math.hypot(startPoint.x - midPoint2.x, startPoint.y - midPoint2.y);
        float distance3 = (float) Math.hypot(startPoint.x - midPoint3.x, startPoint.y - midPoint3.y);
        if (distance1 <= distance2 && distance2 <= distance3) {
            straightPathList.add(new StraightPath(startPoint, midPoint1));
            straightPathList.add(new StraightPath(midPoint1, midPoint2));
            straightPathList.add(new StraightPath(midPoint2, midPoint3));
            straightPathList.add(new StraightPath(midPoint3, endPoint));
        } else if (distance1 <= distance3 && distance3 <= distance2) {
            straightPathList.add(new StraightPath(startPoint, midPoint1));
            straightPathList.add(new StraightPath(midPoint1, midPoint3));
            straightPathList.add(new StraightPath(midPoint3, midPoint2));
            straightPathList.add(new StraightPath(midPoint2, endPoint));
        } else if (distance2 <= distance1 && distance1 <= distance3) {
            straightPathList.add(new StraightPath(startPoint, midPoint2));
            straightPathList.add(new StraightPath(midPoint2, midPoint1));
            straightPathList.add(new StraightPath(midPoint1, midPoint3));
            straightPathList.add(new StraightPath(midPoint3, endPoint));
        } else if (distance2 <= distance3) {
            straightPathList.add(new StraightPath(startPoint, midPoint2));
            straightPathList.add(new StraightPath(midPoint2, midPoint3));
            straightPathList.add(new StraightPath(midPoint3, midPoint1));
            straightPathList.add(new StraightPath(midPoint1, endPoint));
        } else if (distance1 <= distance2) {
            straightPathList.add(new StraightPath(startPoint, midPoint3));
            straightPathList.add(new StraightPath(midPoint3, midPoint1));
            straightPathList.add(new StraightPath(midPoint1, midPoint2));
            straightPathList.add(new StraightPath(midPoint2, endPoint));
        } else {
            straightPathList.add(new StraightPath(startPoint, midPoint3));
            straightPathList.add(new StraightPath(midPoint3, midPoint2));
            straightPathList.add(new StraightPath(midPoint2, midPoint1));
            straightPathList.add(new StraightPath(midPoint1, endPoint));
        }

    }

    @Override public float length() {
        return straightPathList.get(0).length() + straightPathList.get(1).length() + straightPathList.get(2).length() + straightPathList.get(3).length();
    }

    @Override
    public Path2D.Float getPath() {
        Path2D.Float path = new Path2D.Float();
        path.append(straightPathList.get(0).getPath(),false);
        path.append(straightPathList.get(1).getPath(), false);
        path.append(straightPathList.get(2).getPath(), false);
        path.append(straightPathList.get(3).getPath(), false);

        return path;
    }

    @Override public Point2D.Float pointAt(float t) {
        if (t <= straightPathList.get(0).length()/ length()) {
            return straightPathList.get(0).pointAt(t);
        } else if (t <= (straightPathList.get(0).length() + straightPathList.get(1).length())/ length()){
            return straightPathList.get(1).pointAt(t);
        } else if (t <= (straightPathList.get(0).length() + straightPathList.get(1).length() + straightPathList.get(2).length())/ length()){
            return straightPathList.get(2).pointAt(t);
        } else {
            return straightPathList.get(3).pointAt(t);
        }
    }

    @Override public Point2D.Float tangentAt(float t) {
        if (t < straightPathList.get(0).length()/ length()) {
            return straightPathList.get(0).tangentAt(t);
        } else if (t < (straightPathList.get(0).length() + straightPathList.get(1).length())/ length()){
            return straightPathList.get(1).tangentAt(t);
        } else if (t < (straightPathList.get(0).length() + straightPathList.get(1).length() + straightPathList.get(2).length())/ length()){
            return straightPathList.get(2).tangentAt(t);
        } else {
            return straightPathList.get(3).tangentAt(t);
        }
    }

    @Override public Nearest nearestTo(Point2D.Float point, float totalLength, float pastLength) {
        Nearest nearestTo1 = straightPathList.get(0).nearestTo(point, totalLength, 0);
        Nearest nearestTo2 = straightPathList.get(1).nearestTo(point, totalLength, straightPathList.get(0).length());
        Nearest nearestTo3 = straightPathList.get(2).nearestTo(point, totalLength, straightPathList.get(0).length() + straightPathList.get(1).length());
        Nearest nearestTo4 = straightPathList.get(3).nearestTo(point, totalLength, straightPathList.get(0).length() + straightPathList.get(1).length() + straightPathList.get(2).length());
        if (nearestTo1.distance < nearestTo2.distance) {
            return nearestTo1;
        } else if (nearestTo2.distance < nearestTo3.distance) {
            return nearestTo2;
        } else if (nearestTo3.distance < nearestTo4.distance) {
            return nearestTo3;
        } else {
            return nearestTo4;
        }
    }

    @Override public Shape toShape() {
        Path2D.Float path = new Path2D.Float();
        path.append(straightPathList.get(0).getPath(),false);
        path.append(straightPathList.get(1).getPath(), false);
        path.append(straightPathList.get(2).getPath(), false);
        path.append(straightPathList.get(3).getPath(), false);
        return path;
    }
}
