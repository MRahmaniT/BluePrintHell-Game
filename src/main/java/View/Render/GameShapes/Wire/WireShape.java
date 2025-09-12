package View.Render.GameShapes.Wire;

import Model.Enums.WireType;
import Model.GameEntities.Wire.*;
import View.Render.GameShapes.System.GameShape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class WireShape {
    private Wire wire;
    private WireType wireType;
    private final ArrayList<Point2D.Float> midPoints;
    private final GameShape blockA;
    private final int portA;
    private final GameShape blockB;
    private final int portB;
    private Color color;

    public WireShape(List<GameShape> blockShapes, Wire wire) {
        this.wire = wire;
        this.wireType = wire.getWireType();
        this.midPoints = wire.getMidPoints();
        this.blockA = blockShapes.get(wire.getStartBlockId());
        this.portA = wire.getStartPortId();
        this.blockB = blockShapes.get(wire.getEndBlockId());
        this.portB = wire.getEndPortId();
        this.color = Color.cyan;
    }


    public void draw(Graphics2D g) {
        Path2D.Float pathA = blockA.getPortPath(portA);
        Path2D.Float pathB = blockB.getPortPath(portB);
        if (pathA == null || pathB == null) return;

        Rectangle2D boundsA = pathA.getBounds2D();
        Rectangle2D boundsB = pathB.getBounds2D();

        Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
        Point2D.Float endPoint = new Point2D.Float((float) boundsB.getCenterX(), (float) boundsB.getCenterY());
        WirePath wirePath;
        if (wireType == WireType.STRAIGHT) {
            wirePath = new StraightPath(startPoint, endPoint);
        } else if (wireType == WireType.CURVE1) {
            wirePath = new OneFilletPath(startPoint, midPoints.get(0), endPoint);
        } else if (wireType == WireType.CURVE2) {
            wirePath = new TwoFilletPath(startPoint, midPoints.get(0), midPoints.get(1), endPoint);
        } else if (wireType == WireType.CURVE3) {
            wirePath = new ThreeFilletPath(startPoint, midPoints.get(0), midPoints.get(1), midPoints.get(2), endPoint);
        } else {
            wirePath = null;
        }
        g.setColor(color);
        g.setStroke(new BasicStroke(4f));
        if (wirePath != null){
            g.draw(wirePath.toShape());
        }
    }

    public Wire getWire () {return this.wire;}
    public Path2D.Float getWirePath () {
        Path2D.Float pathA = blockA.getPortPath(portA);
        Path2D.Float pathB = blockB.getPortPath(portB);
        if (pathA == null || pathB == null) return null;

        Rectangle2D boundsA = pathA.getBounds2D();
        Rectangle2D boundsB = pathB.getBounds2D();

        Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
        Point2D.Float endPoint = new Point2D.Float((float) boundsB.getCenterX(), (float) boundsB.getCenterY());

        StraightPath straightPath = new StraightPath(startPoint, endPoint);

        return straightPath.getPath();
    }

    public GameShape getBlockA() {
        return blockA;
    }

    public int getPortA() {
        return portA;
    }

    public GameShape getBlockB() {
        return blockB;
    }

    public int getPortB() {
        return portB;
    }

    public void setColor(Color color) {this.color = color;}

    public boolean isNear(Point2D.Float point, int distance) {
        Path2D.Float pathA = blockA.getPortPath(portA);
        Path2D.Float pathB = blockB.getPortPath(portB);

        Rectangle2D boundsA = pathA.getBounds2D();
        Rectangle2D boundsB = pathB.getBounds2D();

        Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
        Point2D.Float endPoint = new Point2D.Float((float) boundsB.getCenterX(), (float) boundsB.getCenterY());

        if (wireType == WireType.STRAIGHT) {
            StraightPath straightPath = new StraightPath(startPoint, endPoint);
            return straightPath.nearestTo(point, straightPath.length(), 0).distance < distance;
        } else if (wireType == WireType.CURVE1) {
            OneFilletPath oneFilletPath = new OneFilletPath(startPoint, wire.getMidPoints().get(0), endPoint);
            return oneFilletPath.nearestTo(point, oneFilletPath.length(), 0).distance < distance;
        } else if (wireType == WireType.CURVE2) {
            TwoFilletPath twoFilletPath = new TwoFilletPath(startPoint, wire.getMidPoints().get(0), wire.getMidPoints().get(1), endPoint);
            return twoFilletPath.nearestTo(point, twoFilletPath.length(), 0).distance < distance;
        } else if (wireType == WireType.CURVE3) {
            ThreeFilletPath threeFilletPath = new ThreeFilletPath(startPoint, wire.getMidPoints().get(0), wire.getMidPoints().get(1),
                    wire.getMidPoints().get(2), endPoint);
            //return threeFilletPath.nearestTo(point, threeFilletPath.length(), 0).distance < 5;
            return false;
        }  else {
            return false;
        }
    }

    public Point2D.Float getStartPoint () {
        Path2D.Float pathA = blockA.getPortPath(portA);
        if (pathA == null) return null;

        Rectangle2D boundsA = pathA.getBounds2D();

        Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
         return startPoint;
    }

    public Point2D.Float getEndPoint () {
        Path2D.Float pathB = blockB.getPortPath(portB);
        if (pathB == null) return null;

        Rectangle2D boundsA = pathB.getBounds2D();

        Point2D.Float endPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
        return endPoint;
    }

    public void setWireType (WireType wireType) {
        this.wireType = wireType;
    }
    public void addMidPoint (Point2D.Float point) {
        this.midPoints.add(point);
    }
    public void editLastMidPoint ( Point2D.Float point) {
        this.midPoints.removeLast();
        this.midPoints.add(point);
    }
    public ArrayList<Point2D.Float> getMidPoints () {
        return this.midPoints;
    }
}
