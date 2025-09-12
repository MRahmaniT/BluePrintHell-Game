package Controller.Packets.Spawning;

import Model.Enums.PortType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;
import Storage.BlockSystemStorage;
import Storage.ConnectionStorage;
import Storage.Snapshots.PacketStorage;

import java.util.*;

public class SpawnPackets {

    public SpawnPackets() {}

    public void spawnFromBlocks() {

        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        List<Connection> connections = ConnectionStorage.LoadConnections();
        List<Packet> packets = PacketStorage.LoadPackets();

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

        BlockSystemStorage.SaveBlockSystems(blockSystems);
        ConnectionStorage.SaveConnections(connections);
        PacketStorage.SavePackets(packets);
    }

    public void addPacketToBlock(int blockId, Packet packet) {

        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        List<Packet> packets = PacketStorage.LoadPackets();

        packet.parkInBlock(blockId);
        blockSystems.get(blockId).addPacket(packet.getId());

        if (!packets.contains(packet)) {
            packets.add(packet);
        }

        BlockSystemStorage.SaveBlockSystems(blockSystems);
        PacketStorage.SavePackets(packets);
    }
}
