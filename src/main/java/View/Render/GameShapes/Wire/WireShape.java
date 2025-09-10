package View.Render.GameShapes.Wire;

import Model.Enums.WireType;
import Model.GameEntities.Wire.OneFilletPath;
import Model.GameEntities.Wire.StraightPath;
import Model.GameEntities.Wire.Wire;
import View.Render.GameShapes.GameShape;

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
        this.color = wire.getColor();
    }


    public void draw(Graphics2D g) {
        if (wireType == WireType.STRAIGHT) {
            Path2D.Float pathA = blockA.getPortPath(portA);
            Path2D.Float pathB = blockB.getPortPath(portB);
            if (pathA == null || pathB == null) return;

            Rectangle2D boundsA = pathA.getBounds2D();
            Rectangle2D boundsB = pathB.getBounds2D();

            Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
            Point2D.Float endPoint = new Point2D.Float((float) boundsB.getCenterX(), (float) boundsB.getCenterY());

            StraightPath straightPath = new StraightPath(startPoint, endPoint);

            g.setColor(color);
            g.setStroke(new BasicStroke(4f));
            g.draw(straightPath.toShape());
        } else if (wireType == WireType.CURVE1) {
            Path2D.Float pathA = blockA.getPortPath(portA);
            Path2D.Float pathB = blockB.getPortPath(portB);
            if (pathA == null || pathB == null) return;

            Rectangle2D boundsA = pathA.getBounds2D();
            Rectangle2D boundsB = pathB.getBounds2D();

            Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
            Point2D.Float endPoint = new Point2D.Float((float) boundsB.getCenterX(), (float) boundsB.getCenterY());

            OneFilletPath oneFilletPath = new OneFilletPath(startPoint, midPoints.get(0), endPoint);
            g.setColor(color);
            g.setStroke(new BasicStroke(4f));
            g.draw(oneFilletPath.toShape());
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

    public boolean isNear(Point2D.Float point) {
        Path2D.Float pathA = blockA.getPortPath(portA);
        Path2D.Float pathB = blockB.getPortPath(portB);

        Rectangle2D boundsA = pathA.getBounds2D();
        Rectangle2D boundsB = pathB.getBounds2D();

        Point2D.Float startPoint = new Point2D.Float((float) boundsA.getCenterX(), (float) boundsA.getCenterY());
        Point2D.Float endPoint = new Point2D.Float((float) boundsB.getCenterX(), (float) boundsB.getCenterY());

        StraightPath straightPath = new StraightPath(startPoint, endPoint);

        return straightPath.nearestTo(point).distance < 3;
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
}
