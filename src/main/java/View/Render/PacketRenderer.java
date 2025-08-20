package View.Render;

import Model.Enums.PacketType;
import Model.GameEntities.Packet;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public final class PacketRenderer {
    private PacketRenderer() {}

    public static Shape getShape(Packet packet) {
        int size = 12;
        double angle = Math.atan2(packet.getYDirection(), packet.getXDirection());

        AffineTransform at = AffineTransform.getTranslateInstance(packet.getX(), packet.getY());
        at.rotate(angle);

        if (packet.getType() == PacketType.MESSENGER_1) {

            // s shape
            Path2D.Float sShape = getSShape(size);
            return at.createTransformedShape(sShape);

        } else if (packet.getType() == PacketType.MESSENGER_2) {

            // square shape
            return at.createTransformedShape(new Rectangle2D.Float(-size/2f, -size/2f, size, size));

        } else if (packet.getType() == PacketType.MESSENGER_3){

            // triangle shape
            Path2D.Float triangle = new Path2D.Float();
            triangle.moveTo(-size/2f, -size/2f);
            triangle.lineTo( size/2f,        0f);
            triangle.lineTo(-size/2f,  size/2f);
            triangle.closePath();
            return at.createTransformedShape(triangle);

        } else if (packet.getType() == PacketType.PROTECTED) {

            // lock shape
            return at.createTransformedShape(new Rectangle2D.Float(-size/2f, -size/2f, size, size));

        } else if (packet.getType() == PacketType.PRIVATE_4) {

            // circle shape
            return at.createTransformedShape(new Rectangle2D.Float(-size/2f, -size/2f, size, size));

        } else if (packet.getType() == PacketType.PRIVATE_6) {

            // circle shape
            return at.createTransformedShape(new Rectangle2D.Float(-size/2f, -size/2f, size, size));

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
        if (p.getType() == PacketType.MESSENGER_1) {
            g.setColor(Color.WHITE);
        } else if (p.getType() == PacketType.MESSENGER_2) {
            g.setColor(Color.GREEN);
        } else if (p.getType() == PacketType.MESSENGER_3) {
            g.setColor(Color.YELLOW);
        }
        g.fill(s);
    }
}
