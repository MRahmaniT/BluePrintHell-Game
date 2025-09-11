package Controller.Packets.Arrival;

import Model.GameEntities.Packet;

public class ArrivedPackets {

    private final Packet packet;
    private final int destinationBlockSystemId;

    public ArrivedPackets(Packet p, int destinationBlockSystemId) {
        this.packet = p;
        this.destinationBlockSystemId = destinationBlockSystemId;
    }

    public Packet getPacket() {
        return packet;
    }

    public int getDestinationBlockSystemId() {
        return destinationBlockSystemId;
    }
}
