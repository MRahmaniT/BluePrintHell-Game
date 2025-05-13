package Shape;

import java.awt.*;
import java.awt.geom.Path2D;

public interface GameShape {

    void update();

    void draw(Graphics2D g2d);

    Path2D.Float getPath(int shapeNumber);

    void setColor(Color color);

    int getShapeModel(int i);

    void setConnection(int i, boolean b);

    boolean getConnection(int i);
}
