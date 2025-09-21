package MVC.View.Render.GameShapes.System;

import MVC.Model.Enums.PortType;
import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.Packet;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class EndSystem implements GameShape {
    private final BlockSystem blockSystem;
    private float x;
    private float y;
    private final float width;
    private final float height;
    private final ArrayList<PortType> portType; //1 for square, 2 for triangle
    private final ArrayList<Boolean> portConnection;
    private final ArrayList<Packet> blockPackets = new ArrayList<>();
    private int squarePacketCount, trianglePacketCount;
    Path2D.Float port1, port2, port3, port4;
    private Color color;

    public EndSystem(BlockSystem blockSystem, float width, float height) {
        this.blockSystem = blockSystem;
        this.x = blockSystem.getX();
        this.y = blockSystem.getY();
        this.width = width;
        this.height = height;
        this.portType = blockSystem.getPortsType();
        this.portConnection = blockSystem.getPortsConnection();
        this.color = Color.red;
    }

    @Override
    public void draw(Graphics2D g) {
        //Draw base
        g.setColor(Color.lightGray);
        g.fillRect((int)x, (int)y, (int)width, (int) (0.6*height));

        //Draw mid
        g.setColor(Color.gray);
        g.fillRect((int)(x + 0.25*width), (int)(y+0.2*height),
                   (int)(0.5*width), (int)(0.4*height));

        //Draw top
        g.setColor(Color.darkGray);
        g.fillRect((int)x, (int)y, (int)width, (int)(0.2*height));

        //Draw light
        g.setColor(color);
        g.fillRoundRect((int)(x + 0.05*width), (int)(y + 0.05*height),
                        (int)(0.4*width), (int)(0.1*height),
                        (int)(0.1*width), (int)(0.09*height));

        //Draw ports
        if (portType.getFirst() == PortType.MESSENGER_2){
            g.setColor(Color.GREEN);
            port1 = new Path2D.Float();
            port1.moveTo((int)(x - 0.12*width), (int)(y+0.4*height-0.08*width));
            port1.lineTo((int)(x + 0.04*width), (int)(y+0.4*height-0.08*width));
            port1.lineTo((int)(x + 0.04*width), (int)(y+0.4*height+0.08*width));
            port1.lineTo((int)(x - 0.12*width), (int)(y+0.4*height+0.08*width));
            g.fill(port1);
        } else if (portType.getFirst() == PortType.MESSENGER_3){
            g.setColor(Color.YELLOW);
            port1 = new Path2D.Float();
            port1.moveTo((int)(x - 0.12*width), (int)(y+0.4*height-0.08*width));
            port1.lineTo((int)(x + 0.04*width), (int)(y+0.4*height));
            port1.lineTo((int)(x - 0.12*width), (int)(y+0.4*height+0.08*width));
            g.fill(port1);
        }

    }


    @Override
    public Path2D.Float getPortPath(int portNumber) {
        return switch (portNumber) {
            case 1 -> port1;
            case 2 -> port2;
            case 3 -> port3;
            case 4 -> port4;
            default -> null;
        };
    }

    @Override
    public Path2D.Float getTopPath() {
        Path2D.Float path = new Path2D.Float();
        Rectangle2D.Float rect = new Rectangle2D.Float((int)x, (int)y, (int)width, (int)(0.2*height));
        path.append(rect, false);
        return path;
    }

    @Override
    public Shape getShape() {
        return new Rectangle2D.Float((int)x, (int)y, (int)width, (int) (0.6*height));
    }

    @Override
    public BlockSystem getBlockSystem() {
        return blockSystem;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setPosition(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public Point getPosition() {
        return new Point((int) x, (int) y);
    }

    @Override
    public PortType getPortType(int i) {
        return portType.get(i-1);
    }

    @Override
    public void setConnection(int i, boolean b) {
        portConnection.set(i-1,b);
    }

    @Override
    public boolean getConnection(int i) {
        return portConnection.get(i-1);
    }

    @Override
    public void setSquarePacketCount(int i) {
        this.squarePacketCount = i;
    }

    @Override
    public int getSquarePacketCount() {
        return this.squarePacketCount;
    }

    @Override
    public void setTrianglePacketCount(int i) {
        this.trianglePacketCount = i;
    }

    @Override
    public int getTrianglePacketCount() {
        return this.trianglePacketCount;
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
