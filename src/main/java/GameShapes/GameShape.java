package GameShapes;

import java.awt.*;
import java.awt.geom.Path2D;

public interface GameShape {

    void setPosition(Point point);

    Point getPosition();

    void draw(Graphics2D g2d);

    void setColor(Color color);

    int getShapeModel(int i);

    void setConnection(int i, boolean b);

    boolean getConnection(int i);

    Path2D.Float getPortPath(int shapeNumber);

    Path2D.Float getTopPath();
}
