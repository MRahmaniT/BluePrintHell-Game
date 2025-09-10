package Model.GameEntities.Wire;

import Model.Enums.WireType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Wire {
    private WireType wireType;
    private final ArrayList<Point2D.Float> midPoints;
    private final int startBlockId;
    private final int startPortId;
    private final int endBlockId;
    private final int endPortId;
    private Color color;
    private int id;

    public Wire(WireType wireType, ArrayList<Point2D.Float> midPoints, int blockA, int portA, int blockB, int portB, Color color, int id) {
        this.wireType = wireType;
        this.midPoints = midPoints;
        this.startBlockId = blockA;
        this.startPortId = portA;
        this.endBlockId = blockB;
        this.endPortId = portB;
        this.color = color;
        this.id = id;
    }

    public WireType getWireType() {
        return wireType;
    }

    public void setWireType(WireType wireType) {
        this.wireType = wireType;
    }

    public ArrayList<Point2D.Float> getMidPoints() {
        return midPoints;
    }

    public int getStartBlockId() {
        return startBlockId;
    }

    public int getStartPortId() {
        return startPortId;
    }

    public int getEndBlockId() {
        return endBlockId;
    }

    public int getEndPortId() {
        return endPortId;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
