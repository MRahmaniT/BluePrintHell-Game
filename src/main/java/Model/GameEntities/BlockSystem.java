package Model.GameEntities;

import Model.Enums.BlockSystemType;
import Model.Enums.PortType;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class BlockSystem implements Serializable {

    private int id;
    private BlockSystemType type;
    private float x, y;
    private boolean isActive;
    private int capacity;

    private List<Port> ports = new ArrayList<>();
    private Deque<Integer> queueOfPackets = new ArrayDeque<>();

    public BlockSystem() {}

    public BlockSystem(int id, BlockSystemType type, List<Port> ports, float x, float y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.ports = ports;

        this.capacity = 5;
        this.isActive = true;
    }

    public int getId() { return id; }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public BlockSystemType getType() { return type; }

    public List<Port> getPorts() { return ports; }
    public Port getPort(int index) { return ports.get(index-1); }
    public ArrayList<PortType> getPortsType () {
        ArrayList<PortType> portsType = new ArrayList<>();
        for (Port p : ports) {
            portsType.add(p.getType());
        }
        return portsType;
    }
    public ArrayList<Boolean> getPortsConnection () {
        ArrayList<Boolean> portsConnection = new ArrayList<>();
        for (Port p : ports) {
            portsConnection.add(p.isConnected());
        }
        return portsConnection;
    }

    public void addPacket (int packetId) { this.queueOfPackets.add(packetId); }
    public Integer peekNextPacketId() { return queueOfPackets.peekFirst(); }
    public Integer pollNextPacketId() { return queueOfPackets.pollFirst(); }
    public int queueCount() { return queueOfPackets.size(); }

    public Deque<Integer> getQueueOfPackets() {
        return queueOfPackets;
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean activation) { this.isActive = activation; }
}
