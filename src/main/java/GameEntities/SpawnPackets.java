package GameEntities;

import GameLogic.Connection;
import GameLogic.PortManager;
import GameShapes.GameShape;

import java.util.List;

public class SpawnPackets {

    public void SpawnPacket(GameShape startBlock, PortManager portManager, List<Packet> packets, int shapeModel) {
        for (int portNumber = 3; portNumber<=4; portNumber++){
            if (startBlock.getConnection(portNumber)) {
                for (Connection connection : portManager.getConnections()) {

                    boolean isStartPort = (connection.blockA == startBlock && connection.portA == portNumber);
                    boolean isEndPort = (connection.blockB == startBlock && connection.portB == portNumber);

                    if (isStartPort) {
                        if (connection.packetOnLine) return;
                        Packet packet = new Packet(portManager, connection, connection.blockA, connection.portA, connection.blockB, connection.portB, shapeModel);
                        packets.add(packet);
                        connection.packetOnLine = true;
                        if(shapeModel == 1){
                            startBlock.setSquarePacketCount(startBlock.getSquarePacketCount()-1);
                        } else if (shapeModel == 2){
                            startBlock.setTrianglePacketCount(startBlock.getTrianglePacketCount()-1);
                        }
                        return;
                    } else if (isEndPort){
                        if (connection.packetOnLine) return;
                        Packet packet = new Packet(portManager, connection, connection.blockB, connection.portB, connection.blockA, connection.portA, shapeModel);
                        packets.add(packet);
                        connection.packetOnLine = true;
                        if(shapeModel == 1){
                            startBlock.setSquarePacketCount(startBlock.getSquarePacketCount()-1);
                        } else if (shapeModel == 2){
                            startBlock.setTrianglePacketCount(startBlock.getTrianglePacketCount()-1);
                        }
                        return;
                    }
                }
            }
        }
    }


}

