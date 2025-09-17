package Client.Controller.Packets.Arrival;

public class ArrivedPackets {

    private final int packetId;
    private final int destinationBlockSystemId;

    public ArrivedPackets(int packetId, int destinationBlockSystemId) {
        this.packetId = packetId;
        this.destinationBlockSystemId = destinationBlockSystemId;
    }

    public int getPacketId() {
        return packetId;
    }

    public int getDestinationBlockSystemId() {
        return destinationBlockSystemId;
    }
}
