package Shape;

import java.awt.*;
import java.awt.geom.Path2D;

public class RectangleShape implements GameShape {

    private final float x, y, width, height;
    private final Color color;

    public RectangleShape(float x, float y, float width, float height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g2d) {

        g2d.setColor(color);
        g2d.fill(getPath());

    }

    @Override
    public Path2D.Float getPath() {
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
}
