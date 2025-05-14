package Shape;

import java.awt.*;
import java.awt.geom.Path2D;

public class LineShape implements GameShape {

    private final float x1, y1, x2, y2;
    private Color color;

    public LineShape(float x1, float y1, float x2, float y2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
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
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

    }

    @Override
    public Path2D.Float getPortPath(int i) {
        return null;
    }

    @Override
    public Path2D.Float getTopPath() {
        return null;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getShapeModel(int i) {
        return 0;
    }

    @Override
    public void setConnection(int i, boolean b) {}

    @Override
    public boolean getConnection(int i) {
        return false;
    }
}
