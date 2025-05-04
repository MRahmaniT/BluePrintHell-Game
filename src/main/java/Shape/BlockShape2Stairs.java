package Shape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class BlockShape2Stairs implements GameShape {
    private float x, y, width, height;
    private ArrayList<Integer> shapeModel = new ArrayList<>(); //1 for square, 2 for triangle
    private Color color;

    public BlockShape2Stairs(float x, float y,
                             float width, float height,
                             Color color, ArrayList<Integer> shapeModel) {
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

        //Draw line
        g.setColor(Color.darkGray);
        g.fillRect((int)x, (int)(y+0.59*height), (int)width, (int)(0.01*height));

        //Draw light
        g.setColor(color);
        g.fillRoundRect((int)(x + 0.05*width), (int)(y + 0.05*height),
                        (int)(0.4*width), (int)(0.1*height),
                        (int)(0.1*width), (int)(0.2*height));

        //Draw ports
        if (shapeModel.get(0) == 1){
            g.setColor(Color.GREEN);
            g.fillRect((int)(x - 0.08*width), (int)(y+0.4*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        } else if (shapeModel.get(0) == 2){
            g.setColor(Color.YELLOW);
            g.fillOval((int)(x - 0.08*width), (int)(y+0.4*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        }
        if (shapeModel.get(1) == 1){
            g.setColor(Color.GREEN);
            g.fillRect((int)(x - 0.08*width), (int)(y+0.8*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        } else if (shapeModel.get(1) == 2){
            g.setColor(Color.YELLOW);
            g.fillOval((int)(x - 0.08*width), (int)(y+0.8*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        }
        if (shapeModel.get(2) == 1){
            g.setColor(Color.GREEN);
            g.fillRect((int)(x + 0.92*width), (int)(y+0.4*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        } else if (shapeModel.get(2) == 2){
            g.setColor(Color.YELLOW);
            g.fillOval((int)(x + 0.92*width), (int)(y+0.4*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        }
        if (shapeModel.get(3) == 1){
            g.setColor(Color.GREEN);
            g.fillRect((int)(x + 0.92*width), (int)(y+0.8*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        } else if (shapeModel.get(3) == 2){
            g.setColor(Color.YELLOW);
            g.fillOval((int)(x + 0.92*width), (int)(y+0.8*height-0.08*width),
                    (int)(0.16*width), (int)(0.16*width));
        }
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
