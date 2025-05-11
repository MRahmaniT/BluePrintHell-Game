package Shape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class BlockShape2Stairs implements GameShape {
    private final float x, y, width, height;
    private final ArrayList<Integer> shapeModel; //1 for square, 2 for triangle
    private final ArrayList<Boolean> portConnection;
    Path2D.Float port1, port2, port3, port4;
    private Color color;

    public BlockShape2Stairs(float x, float y,
                             float width, float height,
                             Color color, ArrayList<Integer> shapeModel,
                             ArrayList<Boolean> portConnection) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shapeModel = shapeModel;
        this.color = color;
        this.portConnection = portConnection;
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
                        (int)(0.1*width), (int)(0.09*height));

        //Draw ports
        if (shapeModel.get(0) == 1){
            g.setColor(Color.GREEN);
            port1 = new Path2D.Float();
            port1.moveTo((int)(x - 0.08*width), (int)(y+0.4*height-0.08*width));
            port1.lineTo((int)(x + 0.08*width), (int)(y+0.4*height-0.08*width));
            port1.lineTo((int)(x + 0.08*width), (int)(y+0.4*height+0.08*width));
            port1.lineTo((int)(x - 0.08*width), (int)(y+0.4*height+0.08*width));
            g.fill(port1);
        } else if (shapeModel.get(0) == 2){
            g.setColor(Color.YELLOW);
            port1 = new Path2D.Float();
            port1.moveTo((int)(x - 0.08*width), (int)(y+0.4*height-0.08*width));
            port1.lineTo((int)(x + 0.08*width), (int)(y+0.4*height));
            port1.lineTo((int)(x - 0.08*width), (int)(y+0.4*height+0.08*width));
            g.fill(port1);
        }
        if (shapeModel.get(1) == 1){
            g.setColor(Color.GREEN);
            port2 = new Path2D.Float();
            port2.moveTo((int)(x - 0.08*width), (int)(y+0.8*height-0.08*width));
            port2.lineTo((int)(x + 0.08*width), (int)(y+0.8*height-0.08*width));
            port2.lineTo((int)(x + 0.08*width), (int)(y+0.8*height+0.08*width));
            port2.lineTo((int)(x - 0.08*width), (int)(y+0.8*height+0.08*width));
            g.fill(port2);
        } else if (shapeModel.get(1) == 2){
            g.setColor(Color.YELLOW);
            port2 = new Path2D.Float();
            port2.moveTo((int)(x - 0.08*width), (int)(y+0.8*height-0.08*width));
            port2.lineTo((int)(x + 0.08*width), (int)(y+0.8*height));
            port2.lineTo((int)(x - 0.08*width), (int)(y+0.8*height+0.08*width));
            g.fill(port2);
        }
        if (shapeModel.get(2) == 1){
            g.setColor(Color.GREEN);
            port3 = new Path2D.Float();
            port3.moveTo((int)(x - 0.08*width + width), (int)(y+0.4*height-0.08*width));
            port3.lineTo((int)(x + 0.08*width + width), (int)(y+0.4*height-0.08*width));
            port3.lineTo((int)(x + 0.08*width + width), (int)(y+0.4*height+0.08*width));
            port3.lineTo((int)(x - 0.08*width + width), (int)(y+0.4*height+0.08*width));
            g.fill(port3);
        } else if (shapeModel.get(2) == 2){
            g.setColor(Color.YELLOW);
            port3 = new Path2D.Float();
            port3.moveTo((int)(x - 0.08*width + width), (int)(y+0.4*height-0.08*width));
            port3.lineTo((int)(x + 0.08*width + width), (int)(y+0.4*height));
            port3.lineTo((int)(x - 0.08*width + width), (int)(y+0.4*height+0.08*width));
            g.fill(port3);
        }
        if (shapeModel.get(3) == 1){
            g.setColor(Color.GREEN);
            port4 = new Path2D.Float();
            port4.moveTo((int)(x - 0.08*width + width), (int)(y+0.8*height-0.08*width));
            port4.lineTo((int)(x + 0.08*width + width), (int)(y+0.8*height-0.08*width));
            port4.lineTo((int)(x + 0.08*width + width), (int)(y+0.8*height+0.08*width));
            port4.lineTo((int)(x - 0.08*width + width), (int)(y+0.8*height+0.08*width));
            g.fill(port4);
        } else if (shapeModel.get(3) == 2){
            g.setColor(Color.YELLOW);
            port4 = new Path2D.Float();
            port4.moveTo((int)(x - 0.08*width + width), (int)(y+0.8*height-0.08*width));
            port4.lineTo((int)(x + 0.08*width + width), (int)(y+0.8*height));
            port4.lineTo((int)(x - 0.08*width + width), (int)(y+0.8*height+0.08*width));
            g.fill(port4);
        }
    }


    @Override
    public Path2D.Float getPath(int portNumber) {
        switch (portNumber){
            case 1 : return port1;
            case 2 : return port2;
            case 3 : return port3;
            case 4 : return port4;
        }
        return null;
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
