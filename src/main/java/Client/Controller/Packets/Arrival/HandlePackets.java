package Client.Controller.Packets.Arrival;

import Client.Model.Enums.PacketType;
import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;
import Client.Model.Player.PlayerState;
import Client.Storage.Facade.StorageFacade;

import javax.swing.*;
import java.util.List;

public class HandlePackets {

    private List<ArrivedPackets> arrivedPackets;

    // thresholds
    private final float maxSpeed = 500f;

    public HandlePackets() {}

    public void Handle (List<ArrivedPackets> arrivedPackets, List<Packet> lostPackets, int lostPacketsCount) {

        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();

        for (ArrivedPackets arrivedPacket : arrivedPackets) {

            int packetId = arrivedPacket.getPacketId();
            boolean isActive = true;
            Packet arrrivedPacket = null;

            List<Packet> packets = StorageFacade.loadPackets();
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
                        StorageFacade.savePackets(packets);
                    }
                }
            } else {
                assert arrrivedPacket != null;
                if (arrrivedPacket.getSpeed() >= maxSpeed) {
                    deActiveDestinationSystem(arrivedPacket.getDestinationBlockSystemId());

                    List<Packet> packets2 = StorageFacade.loadPackets();
                    for (Packet packet : packets2) {
                        if (packet.getPacketType() == PacketType.PROTECTED) {
                            if (packet.getProtectedBy() == arrivedPacket.getDestinationBlockSystemId()) {
                                packet.setPacketType(packet.getFirstType());
                                StorageFacade.savePackets(packets2);
                            }
                        }
                    }
                }

                List<Packet> packets3 = StorageFacade.loadPackets();
                for (Packet packet : packets3) {
                    if (packet.getId() == packetId) {
                        //free line
                        freeLine(packet);

                        // enqueue to destination block
                        packet.parkInBlock(arrivedPacket.getDestinationBlockSystemId());
                        blockSystems.get(arrivedPacket.getDestinationBlockSystemId()).addPacket(packet.getId());

                        StorageFacade.savePackets(packets3);
                        StorageFacade.saveBlockSystems(blockSystems);
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

        List<Packet> packets = StorageFacade.loadPackets();
        for (Packet packet : lostPackets) {
            freeLine(packet);
            for (Packet packet1 : packets) {
                if (packet1.getId() == packet.getId()) {
                    packet1.markLost();
                    packet.markLost();
                    StorageFacade.savePackets(packets);
                }
            }
            lostPacketsCount++;
        }

        StorageFacade.saveBlockSystems(blockSystems);
    }

    public void deActiveDestinationSystem(int blockSystemId) {
        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
        blockSystems.get(blockSystemId).setActive(false);
        StorageFacade.saveBlockSystems(blockSystems);

        Timer timer = new Timer(5 * 1000, e -> {
            List<BlockSystem> blockSystems1 = StorageFacade.loadBlockSystems();
            blockSystems1.get(blockSystemId).setActive(true);
            StorageFacade.saveBlockSystems(blockSystems);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void freeLine(Packet p) {
        List<Connection> connections = StorageFacade.loadConnections();
        int idx = p.getConnectionIdx();
        for (Connection connection : connections) {
            if (connection.getId() == idx) {
                connection.setPacketOnLine(false);
                StorageFacade.saveConnections(connections);
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

