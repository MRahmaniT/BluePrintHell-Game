package Shape;

import java.awt.*;
import java.awt.geom.Path2D;

public class LineShape implements GameShape {

    private final float x1, y1, x2, y2;
    private final Color color;

    public LineShape(float x1, float y1, float x2, float y2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g2d) {

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

    }

    @Override
    public Path2D.Float getPath(int i) {
        return null;
    }
}
