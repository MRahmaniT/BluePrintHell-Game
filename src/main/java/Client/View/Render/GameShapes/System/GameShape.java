package Client.View.Render.GameShapes.System;

import Client.Model.Enums.PortType;
import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Packet;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public interface GameShape {

    void setPosition(Point point);

    Point getPosition();

    void draw(Graphics2D g2d);

    void setColor(Color color);

    PortType getPortType(int i);

    void setConnection(int i, boolean b);

    boolean getConnection(int i);

    void setSquarePacketCount(int i);

    int getSquarePacketCount();

    void setTrianglePacketCount(int i);

    int getTrianglePacketCount();

    void addBlockPackets(Packet packet);

    void releaseBlockPackets(Packet packet);

    ArrayList<Packet> getBlockPackets();

    Path2D.Float getPortPath(int shapeNumber);

    Path2D.Float getTopPath();

    BlockSystem getBlockSystem();
}
