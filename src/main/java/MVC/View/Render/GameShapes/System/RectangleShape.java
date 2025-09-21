package MVC.View.Render.GameShapes.System;

import MVC.Model.Enums.PortType;
import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.Packet;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class RectangleShape implements GameShape {

    private final float x, y, width, height;
    private Color color;

    public RectangleShape(float x, float y, float width, float height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void setPosition(Point point) {
    }

    @Override
    public Point getPosition() {
        return null;
    }

    @Override
    public void draw(Graphics2D g2d) {

        g2d.setColor(color);
        g2d.fill(getPortPath(1));

    }

    @Override
    public Path2D.Float getPortPath(int i) {
        Path2D.Float rectangle = new Path2D.Float();

        float x1 = x - width/2;
        float x2 = x + width/2;
        float y1 = y - height/2;
        float y2 = y + height/2;


        rectangle.moveTo(x1, y1);
        rectangle.lineTo(x1, y2);
        rectangle.lineTo(x2, y2);
        rectangle.lineTo(x2, y1);
        rectangle.closePath();

        return rectangle;
    }

    @Override
    public Path2D.Float getTopPath() {
        return null;
    }

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public BlockSystem getBlockSystem() {
        return null;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public PortType getPortType(int i) {
        return null;
    }

    @Override
    public void setConnection(int i, boolean b) {}

    @Override
    public boolean getConnection(int i) {
        return false;
    }

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
