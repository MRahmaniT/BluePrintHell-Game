package Shape;

import java.awt.*;
import java.awt.geom.Path2D;

public interface GameShape {

    void update();

    void draw(Graphics2D g2d);

    Path2D.Float getPath(int shapeNumber);

    int getShapeModel(int i);

    void setConnection(boolean b);

    boolean getConnection();
}
