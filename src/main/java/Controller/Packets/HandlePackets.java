package Controller.Packets;

import Model.Enums.PacketType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;
import Model.Player.PlayerState;

import javax.swing.*;
import java.util.List;

public class HandlePackets {

    private List<ArrivedPackets> arrivedPackets;
    private List<Packet> lostPackets;

    // thresholds
    private final float maxSpeed = 500f;

    public HandlePackets(List<ArrivedPackets> arrivedPackets, List<Packet> lostPackets) {
        this.arrivedPackets = arrivedPackets;
        this.lostPackets = lostPackets;
    }

    public void Handle (List<BlockSystem> blockSystems, List<Connection> connections, int lostPacketsCount) {
        for (ArrivedPackets arrivedPacket : arrivedPackets) {
            Packet packet = arrivedPacket.getPacket();
            if (!blockSystems.get(packet.getToBlockIdx()).isActive()) {
                packet.startOnWire(packet.getConnectionIdx(), packet.getToBlockIdx(), packet.getToPort(), packet.getFromBlockIdx(), packet.getFromPort());
            } else {
                if (packet.getSpeed() >= maxSpeed) {
                    deActiveDestinationSystem(blockSystems, arrivedPacket.getDestinationBlockSystemId());
                }
                freeLine(packet, connections);

                // enqueue to destination block
                packet.parkInBlock(arrivedPacket.getDestinationBlockSystemId());
                blockSystems.get(arrivedPacket.getDestinationBlockSystemId()).addPacket(packet.getId());

                // coin rules
                if (packet.getType() == PacketType.MESSENGER_2) {
                    PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 1);
                } else {
                    PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 2);
                }
            }
        }
        arrivedPackets.clear();

        for (Packet packet : lostPackets) {
            freeLine(packet, connections);
            packet.markLost();
            lostPacketsCount++;
        }
    }

    public void deActiveDestinationSystem(List<BlockSystem> blockSystems, int blockSystemId) {
        blockSystems.get(blockSystemId).setActive(false);
        Timer timer = new Timer(5 * 1000, e -> {
            blockSystems.get(blockSystemId).setActive(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void freeLine(Packet p, List<Connection> connections) {
        int idx = p.getConnectionIdx();
        for (Connection connection : connections) {
            if (connection.getId() == idx) {
                connection.setPacketOnLine(false);
            }
        }
        p.setConnectionIdx(-1);
    }

    public List<ArrivedPackets> getArrivedPackets() {
        return arrivedPackets;
    }

    public void setArrivedPackets(List<ArrivedPackets> arrivedPackets) {
        this.arrivedPackets = arrivedPackets;
    }
}

