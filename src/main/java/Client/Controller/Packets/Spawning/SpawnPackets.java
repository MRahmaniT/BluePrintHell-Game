package Client.Controller.Packets.Spawning;

import Client.Model.Enums.PortType;
import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;
import Client.Storage.Facade.StorageFacade;

import java.util.*;

public class SpawnPackets implements Runnable{

    public SpawnPackets() {}

    @Override
    public void run(){
        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
        List<Connection> connections = StorageFacade.loadConnections();
        List<Packet> packets = StorageFacade.loadPackets();

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
            packets.get(blockSystem.peekNextPacketId()).startOnWire(connectionChoice.getId(),
                    connectionChoice.getFromSystemId(),connectionChoice.getFromPortId(),
                    connectionChoice.getToSystemId(), connectionChoice.getToPortId());

            // check speed conditions
            spawnConditions.CheckSpeedConditions();

            connectionChoice.setPacketOnLine(true);
            blockSystem.pollNextPacketId();

            //save
            StorageFacade.saveBlockSystems(blockSystems);
            StorageFacade.saveConnections(connections);
            StorageFacade.savePackets(packets);
        }
    }

    public void addPacketToBlock(int blockId, Packet packet) {

        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
        List<Packet> packets = StorageFacade.loadPackets();

        packet.parkInBlock(blockId);
        blockSystems.get(blockId).addPacket(packet.getId());
        StorageFacade.saveBlockSystems(blockSystems);

        if (!packets.contains(packet)) {
            packets.add(packet);
            StorageFacade.savePackets(packets);
        }
    }
}
