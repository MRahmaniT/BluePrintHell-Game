package Model.GameEntities.Wire;

import Model.Enums.WireType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Wire {
    private WireType wireType;
    private ArrayList<Point2D.Float> midPoints;
    private int startBlockId;
    private int startPortId;
    private int endBlockId;
    private int endPortId;
    private int id;

    public Wire () {}

    public Wire(WireType wireType, ArrayList<Point2D.Float> midPoints, int blockA, int portA, int blockB, int portB, int id) {
        this.wireType = wireType;
        this.midPoints = midPoints;
        this.startBlockId = blockA;
        this.startPortId = portA;
        this.endBlockId = blockB;
        this.endPortId = portB;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
