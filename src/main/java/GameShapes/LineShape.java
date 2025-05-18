package GameShapes;

import GameEntities.Packet;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class LineShape implements GameShape {
    private final GameShape blockA;
    private final int portA;
    private final GameShape blockB;
    private final int portB;
    private final Color color;

    public LineShape(GameShape blockA, int portA, GameShape blockB, int portB, Color color) {
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
        int x1 = (int) boundsA.getCenterX();
        int y1 = (int) boundsA.getCenterY();
        int x2 = (int) boundsB.getCenterX();
        int y2 = (int) boundsB.getCenterY();

        g.setColor(color);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(x1, y1, x2, y2);
    }

    @Override public void setColor(Color color) {}
    @Override public void setPosition(Point point) {}
    @Override public Point getPosition() { return null; }
    @Override public Path2D.Float getTopPath() { return null; }
    @Override public Path2D.Float getPortPath(int i) { return null; }
    @Override public int getShapeModel(int i) { return 0; }
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
