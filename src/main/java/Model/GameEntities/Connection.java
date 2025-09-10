package Model.GameEntities;

import View.Render.GameShapes.GameShape;

public class Connection implements java.io.Serializable {
    private int id;
    private int fromSystemId;
    private int fromPortId;
    private int toSystemId;
    private int toPortId;
    private int wireId;
    private boolean packetOnLine;

    public Connection() {}

    public Connection(int id, int fromSystemId, int fromPortId, int toSystemId, int toPortId, int wireId) {
        this.id = id;
        this.fromSystemId = fromSystemId;
        this.fromPortId = fromPortId;
        this.toSystemId = toSystemId;
        this.toPortId = toPortId;
        this.wireId = wireId;
        this.packetOnLine = false;
    }

    public boolean contains(int block, int port) {
        return (this.fromSystemId == block && fromPortId == port) || (toSystemId == block && toPortId == port);
    }

    public int getId() {
        return id;
    }

    public int getFromSystemId() {
        return fromSystemId;
    }

    public int getFromPortId() {
        return fromPortId;
    }

    public int getToSystemId() {
        return toSystemId;
    }

    public int getToPortId() {
        return toPortId;
    }

    public boolean isPacketOnLine() {
        return packetOnLine;
    }

    public void setPacketOnLine(boolean packetOnLine) {
        this.packetOnLine = packetOnLine;
    }
}
