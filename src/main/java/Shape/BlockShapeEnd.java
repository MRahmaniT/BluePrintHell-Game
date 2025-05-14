package Shape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class BlockShapeEnd implements GameShape {
    private final float x, y, width, height;
    private final ArrayList<Integer> shapeModel; //1 for square, 2 for triangle
    private final ArrayList<Boolean> portConnection;
    Path2D.Float port1, port2, port3, port4;
    private Color color;

    public BlockShapeEnd(float x, float y,
                         float width, float height,
                         Color color, ArrayList<Integer> shapeModel,
                         ArrayList<Boolean> portConnection) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shapeModel = shapeModel;
        this.portConnection = portConnection;
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
        g.fillRect((int)x, (int)y, (int)width, (int)(height/3));

        //Draw light
        g.setColor(color);
        g.fillRoundRect((int)(x + 0.05*width), (int)(y + 0.05*height),
                        (int)(0.4*width), (int)(0.1*height),
                        (int)(0.1*width), (int)(0.09*height));

        //Draw ports
        if (shapeModel.getFirst() == 1){
            g.setColor(Color.GREEN);
            port1 = new Path2D.Float();
            port1.moveTo((int)(x - 0.08*width), (int)(y+2*height/3-0.08*width));
            port1.lineTo((int)(x + 0.08*width), (int)(y+2*height/3-0.08*width));
            port1.lineTo((int)(x + 0.08*width), (int)(y+2*height/3+0.08*width));
            port1.lineTo((int)(x - 0.08*width), (int)(y+2*height/3+0.08*width));
            g.fill(port1);
        } else if (shapeModel.getFirst() == 2){
            g.setColor(Color.YELLOW);
            port1 = new Path2D.Float();
            port1.moveTo((int)(x - 0.08*width), (int)(y+2*height/3-0.08*width));
            port1.lineTo((int)(x + 0.08*width), (int)(y+2*height/3));
            port1.lineTo((int)(x - 0.08*width), (int)(y+2*height/3+0.08*width));
            g.fill(port1);
        }

    }


    @Override
    public Path2D.Float getPath(int portNumber) {
        return switch (portNumber) {
            case 1 -> port1;
            case 2 -> port2;
            case 3 -> port3;
            case 4 -> port4;
            default -> null;
        };
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void update() {
        // Blocks are static for now — no update needed
    }

    @Override
    public int getShapeModel(int i) {
        return shapeModel.get(i-1);
    }

    @Override
    public void setConnection(int i, boolean b) {
        portConnection.set(i-1,b);
    }

    @Override
    public boolean getConnection(int i) {
        return portConnection.get(i-1);
    }
}
