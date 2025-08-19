package View.Render;

import Model.Enums.PacketType;
import Model.GameEntities.Packet;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public final class PacketRenderer {
    private PacketRenderer() {}

    public static Shape getShape(Packet packet) {
        int size = 12;
        double angle = Math.atan2(packet.getYDirection(), packet.getXDirection());

        AffineTransform at = AffineTransform.getTranslateInstance(packet.getX(), packet.getY());
        at.rotate(angle);

        if (packet.getType() == PacketType.MESSENGER_2) {
            return at.createTransformedShape(new Rectangle2D.Float(-size/2f, -size/2f, size, size));
        } else if (packet.getType() == PacketType.MESSENGER_3){
            Path2D.Float tri = new Path2D.Float();
            tri.moveTo(-size/2f, -size/2f);
            tri.lineTo( size/2f,        0f);
            tri.lineTo(-size/2f,  size/2f);
            tri.closePath();
            return at.createTransformedShape(tri);
        }
        else {
            return null;
        }
    }

    public static void draw(Graphics2D g, Packet p) {
        Shape s = getShape(p);
        g.setColor(p.getType() == PacketType.MESSENGER_2 ? Color.GREEN : Color.YELLOW);
        g.fill(s);
    }
}
