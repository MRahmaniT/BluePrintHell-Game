package Controller.Packets.Spawning;

import Model.Enums.BlockSystemType;
import Model.Enums.PacketType;
import Model.Enums.PortType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Packet;
import Model.GameEntities.Wire.Wire;
import Storage.Facade.StorageFacade;
import Storage.RealTime.GameEnvironment.ConnectionStorage;
import Storage.RealTime.GameEnvironment.PacketStorage;
import Storage.RealTime.GameEnvironment.WireStorage;
import View.GamePage.GamePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnConditions {

    private final List<BlockSystem> blockSystems;
    private final Packet packet;
    private final PortType portType;

    public SpawnConditions(List<BlockSystem> blockSystems, Packet packet, PortType portType) {
        this.blockSystems = blockSystems;
        this.packet = packet;
        this.portType = portType;
    }

    public void CheckSpawnCondition(){
        PacketType packetType = packet.getPacketType();
        BlockSystemType blockSystemType = blockSystems.get(packet.getBlockIdx()).getType();
        switch (blockSystemType){
            case SPYING -> {
                switch (packetType){
                    case PROTECTED -> {
                        packet.setPacketType(packet.getFirstType());
                    }
                    case PRIVATE_4, PRIVATE_6 -> {
                        packet.markLost();
                    }
                    case MESSENGER_1, MESSENGER_2, MESSENGER_3, BULKY_8, BULKY_10 -> {
                        if (packet.isMoved()) return;
                        Random rand = new Random();
                        ArrayList<Integer> ids = new ArrayList<>();
                        for (BlockSystem blockSystem : blockSystems){
                            if (blockSystem.getType() == BlockSystemType.SPYING) ids.add(blockSystem.getId());
                        }
                        packet.parkInBlock(ids.get(rand.nextInt(ids.size())));
                        packet.setMoved(true);
                    }
                }
            }
            case DDOS -> {
                switch (packetType){
                    case PROTECTED -> {
                        packet.setPacketType(packet.getFirstType());
                    }
                    case MESSENGER_1, MESSENGER_2, MESSENGER_3, BULKY_8, BULKY_10, PRIVATE_4, PRIVATE_6 -> {
                        packet.setDoNotFindCompatible(true);
                        if (packet.getNoise() == 0) packet.addNoise(1);
                        double random = Math.random();
                        if (random < 0.5) {
                            packet.setFirstType(packetType);
                            packet.setPacketType(PacketType.TROJAN);
                        }
                    }
                }
            }
            case VPN -> {
                switch (packetType){
                    case MESSENGER_1, MESSENGER_2, MESSENGER_3 -> {
                        packet.setFirstType(packetType);
                        packet.setPacketType(PacketType.PROTECTED);
                        packet.setProtectedBy(packet.getBlockIdx());
                    }
                    case PRIVATE_4 -> {
                        packet.setFirstType(packetType);
                        packet.setPacketType(PacketType.PRIVATE_6);
                    }
                }
            }
            case ANTIVIRUS -> {
                if (packetType == PacketType.TROJAN) {
                    packet.setPacketType(packet.getFirstType());
                    blockSystems.get(packet.getBlockIdx()).setActive(false);
                    Timer timer = new Timer(5 * 1000, e -> {
                        blockSystems.get(packet.getBlockIdx()).setActive(true);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
            case DISTRIBUTE -> {
                switch (packetType){
                    case BULKY_8 -> {
                        List<Packet> packets = StorageFacade.loadPackets();
                        for (int packetId : blockSystems.get(packet.getBlockIdx()).getQueueOfPackets()) {
                            for (Packet packet1 : packets) {
                                if (packet1.getId() == packetId && packetId != packet.getId()) {
                                    blockSystems.get(packet.getBlockIdx()).getQueueOfPackets().remove(packetId);
                                    packet1.markLost();
                                    StorageFacade.savePackets(packets);
                                } else if (packetId == packet.getId()){
                                    blockSystems.get(packet.getBlockIdx()).getQueueOfPackets().remove(packetId);
                                    packet.setLocation(Packet.Location.DISTRIBUTED);
                                    StorageFacade.savePackets(packets);
                                }

                            }
                        }
                        SpawnPackets spawnPackets = new SpawnPackets();
                        for (int i = 1; i <= 8; i++) {
                            Packet newPacket = new Packet(GamePanel.generatedPackets, PacketType.MESSENGER_1);
                            newPacket.setBulkId(packet.getId());
                            newPacket.setDistributeVolume(8);
                            newPacket.setMergeVolume(1);
                            spawnPackets.addPacketToBlock(packet.getBlockIdx(), newPacket);
                            GamePanel.generatedPackets++;
                        }
                    }
                    case BULKY_10 -> {
                        List<Packet> packets = StorageFacade.loadPackets();
                        for (int packetId : blockSystems.get(packet.getBlockIdx()).getQueueOfPackets()) {
                            for (Packet packet1 : packets) {
                                if (packet1.getId() == packetId && packetId != packet.getId()) {
                                    blockSystems.get(packet.getBlockIdx()).getQueueOfPackets().remove(packetId);
                                    packet1.markLost();
                                    StorageFacade.savePackets(packets);
                                } else if (packetId == packet.getId()){
                                    blockSystems.get(packet.getBlockIdx()).getQueueOfPackets().remove(packetId);
                                    packet.setLocation(Packet.Location.DISTRIBUTED);
                                    StorageFacade.savePackets(packets);
                                }

                            }
                        }
                        SpawnPackets spawnPackets = new SpawnPackets();
                        for (int i = 1; i <= 10; i++) {
                            Packet newPacket = new Packet(GamePanel.generatedPackets, PacketType.MESSENGER_1);
                            newPacket.setBulkId(packet.getId());
                            newPacket.setDistributeVolume(10);
                            newPacket.setMergeVolume(1);
                            spawnPackets.addPacketToBlock(packet.getBlockIdx(), newPacket);
                            GamePanel.generatedPackets++;
                        }
                    }
                }
            }
            case MERGE -> {
                switch (packetType) {
                    case MESSENGER_1 -> {
                        if (packet.getBulkId() >= 0) {
                            int volume = 0;
                            List<Packet> packets = StorageFacade.loadPackets();
                            for (int packetId : blockSystems.get(packet.getBlockIdx()).getQueueOfPackets()) {
                                for (Packet packet1 : packets) {
                                    if (packet1.getId() == packetId && packet1.getBulkId() == packet.getBulkId()) {
                                        blockSystems.get(packet.getBlockIdx()).getQueueOfPackets().remove(packet1.getId());
                                        packet1.setLocation(Packet.Location.DISTRIBUTED);
                                        volume = volume + packet1.getMergeVolume();
                                        StorageFacade.savePackets(packets);
                                    }
                                }
                            }
                            SpawnPackets spawnPackets = new SpawnPackets();
                            Packet newPacket = new Packet(GamePanel.generatedPackets, PacketType.MESSENGER_1);
                            newPacket.setBulkId(packet.getBulkId());
                            newPacket.setDistributeVolume(packet.getDistributeVolume());
                            newPacket.setMergeVolume(volume);
                            spawnPackets.addPacketToBlock(packet.getBlockIdx(), newPacket);
                            GamePanel.generatedPackets++;
                        }
                    }
                }
            }
        }
        switch (packetType) {
            case BULKY_8, BULKY_10 -> {
                Random rand = new Random();
                PortType[] values = new PortType[]{
                        PortType.MESSENGER_1,
                        PortType.MESSENGER_2,
                        PortType.MESSENGER_3
                };
                PortType portType = values[rand.nextInt(values.length)];
                while (portType == blockSystems.get(packet.getFromBlockIdx()).getPort(packet.getFromPort()).getType()) {
                    portType = values[rand.nextInt(values.length)];
                }
                blockSystems.get(packet.getFromBlockIdx()).getPort(packet.getFromPort()).setType(portType);
                blockSystems.get(packet.getToBlockIdx()).getPort(packet.getToPort()).setType(portType);

                List<Connection> connections = StorageFacade.loadConnections();
                Connection connection = null;
                for (Connection connection1 : connections) {
                    if (packet.getConnectionIdx() == connection1.getId()) {
                        connection = connection1;
                    }
                }
                List<Wire> wires = StorageFacade.loadWires();
                for (Wire wire : wires) {
                    assert connection != null;
                    if (wire.getId() == connection.getWireId()) {
                        wire.setBulkyPassed(wire.getBulkyPassed() + 1);
                        if (wire.getBulkyPassed() == 3) {
                            wire.setLost(true);
                        }
                        StorageFacade.saveWires(wires);
                    }
                }
            }
        }
    }
    public void CheckSpeedConditions(){
        PacketType packetType = packet.getPacketType();
        if (packetType == PacketType.PROTECTED) {
            Random rand = new Random();
            PacketType[] values = new PacketType[]{
                    PacketType.MESSENGER_1,
                    PacketType.MESSENGER_2,
                    PacketType.MESSENGER_3
            };
            packetType = values[rand.nextInt(values.length)];
        }
        switch (packetType){
            case MESSENGER_1 -> {
                packet.setAcceleration(0.1f);
                if (portType != PortType.MESSENGER_1) {
                    packet.setAccelerationChanger(-0.0005f);
                }
            }
            case MESSENGER_2 ->{
                if (portType != PortType.MESSENGER_2) {
                    packet.setSpeed(packet.getSpeed()*2);
                }
            }
            case MESSENGER_3 ->{
                if (portType != PortType.MESSENGER_3){
                    packet.setAccelerationChanger(0.1f);
                }
            }
            case PRIVATE_4 ->{
                if (blockSystems.get(packet.getToBlockIdx()).queueCount() > 0) {
                    packet.setSpeed((float) (packet.getSpeed()*0.25));
                }
            }
            //case PRIVATE_6 ->packet.setAccelerationChanger(0.1f);
            //case BULKY_8 ->packet.setAccelerationChanger(0.1f);
            //case BULKY_10 ->    packet.setAccelerationChanger(0.1f);
        }
    }
}
