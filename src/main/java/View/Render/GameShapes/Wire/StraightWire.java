package View.Render.GameShapes.Wire;

import Model.Enums.PortType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Packet;
import Model.GameEntities.Wire.StraightPath;
import View.Render.GameShapes.GameShape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class StraightWire implements GameShape {
    private final GameShape blockA;
    private final int portA;
    private final GameShape blockB;
    private final int portB;
    private Color color;

    public StraightWire(GameShape blockA, int portA, GameShape blockB, int portB, Color color) {
        this.blockA = blockA;
        this.portA = portA;
        this.blockB = blockB;
        this.portB = portB;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g) {
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

    @Override public void setColor(Color color) {this.color = color;}
    @Override public void setPosition(Point point) {}
    @Override public Point getPosition() { return null; }
    @Override public Path2D.Float getTopPath() { return null; }

    @Override
    public BlockSystem getBlockSystem() {
        return null;
    }

    @Override public Path2D.Float getPortPath(int i) { return null; }
    @Override public PortType getPortType(int i) { return null; }
    @Override public void setConnection(int i, boolean b) {}
    @Override public boolean getConnection(int i) { return false; }

    @Override
    public void setSquarePacketCount(int i) {

    }

    @Override
    public int getSquarePacketCount() {
        return 0;
    }

    @Override
    public void setTrianglePacketCount(int i) {

    }

    @Override
    public int getTrianglePacketCount() {
        return 0;
    }

    @Override
    public void addBlockPackets(Packet packet) {

    }

    @Override
    public void releaseBlockPackets(Packet packet) {

    }

    @Override
    public ArrayList<Packet> getBlockPackets() {
        return null;
    }
}
