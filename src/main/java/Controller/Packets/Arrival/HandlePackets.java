package Controller.Packets.Arrival;

import Model.Enums.PacketType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;
import Model.Player.PlayerState;
import Storage.BlockSystemStorage;
import Storage.ConnectionStorage;
import Storage.Snapshots.PacketStorage;

import javax.swing.*;
import java.util.List;

public class HandlePackets {

    private List<ArrivedPackets> arrivedPackets;

    // thresholds
    private final float maxSpeed = 500f;

    public HandlePackets() {}

    public void Handle (List<ArrivedPackets> arrivedPackets, List<Packet> lostPackets, int lostPacketsCount) {

        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();

        for (ArrivedPackets arrivedPacket : arrivedPackets) {

            int packetId = arrivedPacket.getPacketId();
            boolean isActive = true;
            Packet arrrivedPacket = null;

            List<Packet> packets = PacketStorage.LoadPackets();
            for (Packet packet : packets) {
                if (packet.getId() == packetId) {
                    isActive = blockSystems.get(packet.getToBlockIdx()).isActive();
                    arrrivedPacket = packet;
                }
            }

            if (!isActive) {
                for (Packet packet : packets) {
                    if (packet.getId() == packetId) {
                        packet.startOnWire(packet.getConnectionIdx(), packet.getToBlockIdx(), packet.getToPort(), packet.getFromBlockIdx(), packet.getFromPort());
                        PacketStorage.SavePackets(packets);
                    }
                }
            } else {
                assert arrrivedPacket != null;
                if (arrrivedPacket.getSpeed() >= maxSpeed) {
                    deActiveDestinationSystem(arrivedPacket.getDestinationBlockSystemId());

                    List<Packet> packets2 = PacketStorage.LoadPackets();
                    for (Packet packet : packets2) {
                        if (packet.getPacketType() == PacketType.PROTECTED) {
                            if (packet.getProtectedBy() == arrivedPacket.getDestinationBlockSystemId()) {
                                packet.setPacketType(packet.getFirstType());
                                PacketStorage.SavePackets(packets2);
                            }
                        }
                    }
                }

                List<Packet> packets3 = PacketStorage.LoadPackets();
                for (Packet packet : packets3) {
                    if (packet.getId() == packetId) {
                        //free line
                        freeLine(packet);

                        // enqueue to destination block
                        packet.parkInBlock(arrivedPacket.getDestinationBlockSystemId());
                        blockSystems.get(arrivedPacket.getDestinationBlockSystemId()).addPacket(packet.getId());

                        PacketStorage.SavePackets(packets3);
                        BlockSystemStorage.SaveBlockSystems(blockSystems);
                    }
                }



                // coin rules
                switch (arrrivedPacket.getPacketType()) {
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
            freeLine(packet);
            packet.markLost();
            lostPacketsCount++;
        }

        BlockSystemStorage.SaveBlockSystems(blockSystems);
    }

    public void deActiveDestinationSystem(int blockSystemId) {
        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        blockSystems.get(blockSystemId).setActive(false);
        BlockSystemStorage.SaveBlockSystems(blockSystems);

        Timer timer = new Timer(5 * 1000, e -> {
            List<BlockSystem> blockSystems1 = BlockSystemStorage.LoadBlockSystems();
            blockSystems1.get(blockSystemId).setActive(true);
            BlockSystemStorage.SaveBlockSystems(blockSystems1);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void freeLine(Packet p) {
        List<Connection> connections = ConnectionStorage.LoadConnections();
        int idx = p.getConnectionIdx();
        for (Connection connection : connections) {
            if (connection.getId() == idx) {
                connection.setPacketOnLine(false);
                ConnectionStorage.SaveConnections(connections);
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

