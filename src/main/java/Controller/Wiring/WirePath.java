package Controller.Wiring;

import java.awt.geom.Point2D;

public interface WirePath {

    Point2D.Float tangent();

    float length();
}
