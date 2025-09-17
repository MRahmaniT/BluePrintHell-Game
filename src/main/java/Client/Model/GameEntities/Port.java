package Client.Model.GameEntities;

import Client.Model.Enums.PortRole;
import Client.Model.Enums.PortType;

import java.io.Serializable;

public class Port implements Serializable {
    private int id;
    private int systemId;
    private PortRole role;
    private PortType type;
    private boolean connected;

    public Port() {}

    public Port(int id, int systemId,PortRole role, PortType type) {
        this.id = id;
        this.systemId = systemId;
        this.role = role;
        this.type = type;
        this.connected = false;
    }
    public Port(int id, int systemId) {
        this.id = id;
        this.systemId = systemId;
        this.role = null;
        this.type = null;
        this.connected = true;
    }

    public int getId() { return id; }
    public int getSystemId() { return systemId; }
    public PortRole getRole() { return role; }
    public PortType getType() { return type; }

    public void setType(PortType type) {
        this.type = type;
    }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

}
