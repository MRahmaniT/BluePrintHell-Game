package MVC.View.Render.GameShapes.Packet;

import MVC.Model.Enums.PacketType;
import MVC.Model.GameEntities.Packet;

import java.awt.*;
import java.awt.geom.*;

public final class PacketRenderer {
    private PacketRenderer() {}

    public static Shape getShape(Packet packet) {
        int size = 12;
        double angle = Math.atan2(packet.getYDirection(), packet.getXDirection());

        AffineTransform at = AffineTransform.getTranslateInstance(packet.getX(), packet.getY());
        at.rotate(angle);

        if (packet.getPacketType() == PacketType.MESSENGER_1) {

            // s shape
            Path2D.Float sShape = getSShape(size);
            return at.createTransformedShape(sShape);

        } else if (packet.getPacketType() == PacketType.MESSENGER_2) {

            // square shape
            return at.createTransformedShape(new Rectangle2D.Float(-size/2f, -size/2f, size, size));

        } else if (packet.getPacketType() == PacketType.MESSENGER_3){

            // triangle shape
            Path2D.Float triangle = new Path2D.Float();
            triangle.moveTo(-size/2f, -size/2f);
            triangle.lineTo( size/2f,        0f);
            triangle.lineTo(-size/2f,  size/2f);
            triangle.closePath();
            return at.createTransformedShape(triangle);

        } else if (packet.getPacketType() == PacketType.PROTECTED) {

            // lock shape
            Shape body = new Rectangle2D.Float(-size/2f, -size/6f, size, size);
            Area lock = new Area(body);
            Shape arc = new Arc2D.Double(-size/2f, -size/2f, size, size, 0, 180, Arc2D.OPEN);
            lock.add(new Area(arc));
            return at.createTransformedShape(lock);

        } else if (packet.getPacketType() == PacketType.PRIVATE_4) {

            // circle shape
            int Size = (int) (1.2 * size);
            return at.createTransformedShape(new Ellipse2D.Double(-Size/2f, -Size/2f, Size, Size));

        } else if (packet.getPacketType() == PacketType.PRIVATE_6) {

            // circle shape
            int Size = (int) (1.2 * size);
            return at.createTransformedShape(new Ellipse2D.Double(-Size/2f, -Size/2f, Size, Size));

        } else if (packet.getPacketType() == PacketType.BULKY_8) {

            // hexagon shape
            int sides = 6;
            int[] xs = new int[sides];
            int[] ys = new int[sides];

            for (int i = 0; i < sides; i++) {
                double angle1 = Math.toRadians(60 * i);
                xs[i] = (int) (0 + size * Math.cos(angle1));
                ys[i] = (int) (0 + size * Math.sin(angle1));
            }

            return at.createTransformedShape(new Polygon(xs, ys, sides));

        } else if (packet.getPacketType() == PacketType.BULKY_10) {

            // hexagon shape
            int sides = 8;
            int[] xs = new int[sides];
            int[] ys = new int[sides];

            for (int i = 0; i < sides; i++) {
                double angle1 = Math.toRadians(45 * i);
                xs[i] = (int) (0 + size * Math.cos(angle1));
                ys[i] = (int) (0 + size * Math.sin(angle1));
            }

            return at.createTransformedShape(new Polygon(xs, ys, sides));

        } else {
            return null;
        }
    }

    private static Path2D.Float getSShape(int size) {
        float sSize = size /2f;
        Path2D.Float sShape = new Path2D.Float();
        sShape.moveTo(0,0);
        sShape.lineTo( -sSize/2f,        -sSize*Math.sqrt(3)/2);
        sShape.lineTo( -sSize/2f-sSize,        -sSize*Math.sqrt(3)/2);
        sShape.lineTo( -2*sSize,        0);
        sShape.lineTo( -sSize/2f-sSize,        sSize*Math.sqrt(3)/2);
        sShape.lineTo( -sSize/2f,        sSize*Math.sqrt(3)/2);
        sShape.lineTo( sSize/2f,        -sSize*Math.sqrt(3)/2);
        sShape.lineTo( sSize/2f+sSize,        -sSize*Math.sqrt(3)/2);
        sShape.lineTo( 2*sSize,        0);
        sShape.lineTo( sSize/2f+sSize,        sSize*Math.sqrt(3)/2);
        sShape.lineTo( sSize/2f,        sSize*Math.sqrt(3)/2);
        sShape.closePath();
        return sShape;
    }

    public static void draw(Graphics2D g, Packet p) {
        Shape s = getShape(p);

        switch (p.getPacketType()) {
            case MESSENGER_1 -> g.setColor(Color.WHITE);
            case MESSENGER_2 -> g.setColor(Color.GREEN);
            case MESSENGER_3 -> g.setColor(Color.YELLOW);
            case PROTECTED -> g.setColor(Color.ORANGE);
            case PRIVATE_4 -> g.setColor(Color.GRAY);
            case PRIVATE_6 -> g.setColor(Color.BLUE);
            case BULKY_8 -> g.setColor(Color.BLUE);
            case BULKY_10 -> g.setColor(Color.GRAY);
        }

        g.fill(s);
    }
}
