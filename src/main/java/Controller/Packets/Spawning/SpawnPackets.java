package Controller.Packets.Spawning;

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

            // find wire
            FindWire findWire = new FindWire(blockSystems, connections, blockSystem, firstPacketInQueue);
            Connection connectionChoice = findWire.pickFreeOutgoingConnection();
            if (connectionChoice == null) {
                continue;
            }

            // check spawn condition
            PortType portType = blockSystem.getPort(connectionChoice.getFromPortId()).getType();
            SpawnConditions spawnConditions = new SpawnConditions(blockSystems,firstPacketInQueue, portType);
            spawnConditions.CheckSpawnCondition();

            // put the packet on the wire
            firstPacketInQueue.startOnWire(connectionChoice.getId(),
                    connectionChoice.getFromSystemId(),connectionChoice.getFromPortId(),
                    connectionChoice.getToSystemId(), connectionChoice.getToPortId());

            // check speed conditions
            spawnConditions.CheckSpeedConditions();

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
}
