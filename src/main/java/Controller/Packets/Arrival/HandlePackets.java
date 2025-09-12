package Controller.Packets.Arrival;

import Model.Enums.PacketType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;
import Model.Player.PlayerState;
import Storage.BlockSystemStorage;
import Storage.ConnectionStorage;

import javax.swing.*;
import java.util.List;

public class HandlePackets {

    private List<Packet> packets;
    private List<ArrivedPackets> arrivedPackets;
    private List<Packet> lostPackets;

    // thresholds
    private final float maxSpeed = 500f;

    public HandlePackets(List<Packet> packets, List<ArrivedPackets> arrivedPackets, List<Packet> lostPackets) {
        this.packets = packets;
        this.arrivedPackets = arrivedPackets;
        this.lostPackets = lostPackets;
    }

    public void Handle (int lostPacketsCount) {

        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        List<Connection> connections = ConnectionStorage.LoadConnections();

        for (ArrivedPackets arrivedPacket : arrivedPackets) {
            Packet packet = arrivedPacket.getPacket();
            if (!blockSystems.get(packet.getToBlockIdx()).isActive()) {
                packet.startOnWire(packet.getConnectionIdx(), packet.getToBlockIdx(), packet.getToPort(), packet.getFromBlockIdx(), packet.getFromPort());
            } else {
                if (packet.getSpeed() >= maxSpeed) {
                    deActiveDestinationSystem(blockSystems, arrivedPacket.getDestinationBlockSystemId());
                    for (Packet packet1 : packets) {
                        if (packet1.getPacketType() == PacketType.PROTECTED) {
                            if (packet1.getProtectedBy() == arrivedPacket.getDestinationBlockSystemId()) {
                                packet1.setPacketType(packet1.getFirstType());
                            }
                        }
                    }
                }
                freeLine(packet, connections);

                // enqueue to destination block
                packet.parkInBlock(arrivedPacket.getDestinationBlockSystemId());
                blockSystems.get(arrivedPacket.getDestinationBlockSystemId()).addPacket(packet.getId());

                // coin rules
                switch (packet.getPacketType()) {
                    case MESSENGER_1 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 1);
                    case MESSENGER_2 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 2);
                    case MESSENGER_3 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 3);
                    case PROTECTED -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 5);
                    case PRIVATE_4 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 3);
                    case PRIVATE_6 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 4);
                    case BULKY_8 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 8);
                    case BULKY_10 -> PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 10);
                }
            }
        }
        arrivedPackets.clear();

        for (Packet packet : lostPackets) {
            freeLine(packet, connections);
            packet.markLost();
            lostPacketsCount++;
        }

        BlockSystemStorage.SaveBlockSystems(blockSystems);
        ConnectionStorage.SaveConnections(connections);
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

