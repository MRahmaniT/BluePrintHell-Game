package Controller.Wiring;

import java.awt.*;
import java.awt.geom.Point2D;

public interface Wire {

    Point2D.Float pointAt(float t);

    Point2D.Float tangentAt(float t);

    float approxLength();

    void draw(Graphics2D g);
}
