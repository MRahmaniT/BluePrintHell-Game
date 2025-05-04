package Shape;

import java.awt.*;
import java.awt.geom.Path2D;

public class BlockShape2Stairs implements GameShape {
    private float x, y, width, height;
    private int shapeModel; //1 for square, 2 for triangle
    private Color color;

    public BlockShape2Stairs(float x, float y,
                             float width, float height,
                             int shapeModel, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shapeModel = shapeModel;
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g) {
        //Draw base
        g.setColor(Color.lightGray);
        g.fillRect((int)x, (int)y, (int)width, (int)height);

        //Draw mid
        g.setColor(Color.gray);
        g.fillRect((int)(x + 0.25*width), (int)(y+0.2*height),
                   (int)(0.5*width), (int)(0.8*height));

        //Draw top
        g.setColor(Color.darkGray);
        g.fillRect((int)x, (int)y, (int)width, (int)(0.2*height));

        //Draw light
        g.setColor(color);
        g.fillRoundRect((int)(x + 0.05*width), (int)(y + 0.05*height),
                        (int)(0.4*width), (int)(0.1*height),
                        (int)(0.1*width), (int)(0.2*height));
    }

    @Override
    public Path2D.Float getPath() {
        return null;
    }

    @Override
    public void update() {
        // Blocks are static for now — no update needed
    }
}
