package Controller.Packets;

import Model.Enums.PacketType;
import Model.Enums.PortRole;
import Model.Enums.PortType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;

import java.util.*;

public class SpawnPackets {

    private final List<BlockSystem> blockSystems;
    private final List<Connection> connections;
    private final List<Packet> packets;

    public SpawnPackets(List<BlockSystem> blockSystems,
                        List<Connection> connections,
                        List<Packet> packets) {
        this.blockSystems = blockSystems;
        this.connections = connections;
        this.packets = packets;
    }

    public void spawnFromBlocks() {

        Packet firstPacketInQueue;

        for (BlockSystem blockSystem : blockSystems) {
            if (blockSystem.queueCount() == 0) {
                continue;
            }

            firstPacketInQueue = packets.get(blockSystem.peekNextPacketId());
            if (firstPacketInQueue == null) {
                continue;
            }

            Connection connectionChoice = pickFreeOutgoingConnection(blockSystem, firstPacketInQueue);
            if (connectionChoice == null) {
                continue;
            }

            // put the packet on the wire
            firstPacketInQueue.startOnWire(connectionChoice.getId(),
                    connectionChoice.getFromSystemId(),connectionChoice.getFromPortId(),
                    connectionChoice.getToSystemId(), connectionChoice.getToPortId());

            // check speed and acceleration
            PortType portType = blockSystem.getPort(connectionChoice.getFromPortId()).getType();
            checkSpeedConditions(firstPacketInQueue, portType);

            connectionChoice.setPacketOnLine(true);
            blockSystem.pollNextPacketId();
        }
    }

    public void addPacketToBlock(int blockId, Packet packet) {
        packet.parkInBlock(blockId);
        blockSystems.get(blockId).addPacket(packet.getId());

        if (!packets.contains(packet)) {
            packets.add(packet);
        }
    }

    /* ------------------- internals ------------------- */
    private Connection pickFreeOutgoingConnection(BlockSystem blockSystem, Packet packet) {

        List<Connection> options = new ArrayList<>();

        for (Connection connection : connections) {

            if (connection.isPacketOnLine()) continue;

            if (connection.getFromSystemId() == blockSystem.getId() &&
                    isOutput(blockSystem, connection.getFromPortId())) {
                if (isCompatible(blockSystem, connection.getFromPortId(), packet)) {
                    return connection;
                } else {
                    options.add(connection);
                }
            }
        }

        if (options.isEmpty()) return null;

        // choose random wire
        Random random = new Random();
        int randomOption = random.nextInt(options.size());
        return options.get(randomOption);
    }

    private void checkSpeedConditions (Packet packet, PortType portType) {
        PacketType packetType = packet.getType();

        if (packetType == PacketType.MESSENGER_1) {
            packet.setAcceleration(10);
            if (portType != PortType.MESSENGER_1) {
                packet.setAccelerationChanger(-0.05f);
            }
        } else if (packetType == PacketType.MESSENGER_2) {
            if (portType == PortType.MESSENGER_2) {
                packet.setSpeed(packet.getSpeed()/2);
            }
        } else if (packetType == PacketType.MESSENGER_3) {
            if (portType != PortType.MESSENGER_3){
                packet.setAccelerationChanger(10);
            }
        }
    }
    private static boolean isOutput(BlockSystem blockSystem, int port) {
        return blockSystem.getPort(port).getRole() == PortRole.OUT;
    }
    private static boolean isCompatible(BlockSystem blockSystem, int port, Packet packet) {
        return Objects.equals(blockSystem.getPort(port).getType().toString(), packet.getType().toString());
    }
}
