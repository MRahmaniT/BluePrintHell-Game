package GameEntities;

import GameLogic.Connection;
import GameLogic.PortManager;
import GameShapes.GameShape;

import java.util.List;

public class SpawnPackets {

    public void SpawnPacket(GameShape startBlock, PortManager portManager, List<Packet> packets, int shapeModel) {
        boolean isSpawned = false;
        if (startBlock.getShapeModel(3) == shapeModel && startBlock.getConnection(3)){
            for (Connection connection : portManager.getConnections()) {
                boolean isStartPort = (connection.blockA == startBlock && connection.portA == 3);
                boolean isEndPort = (connection.blockB == startBlock && connection.portB == 3);
                if (isStartPort || isEndPort) {
                    if (!connection.packetOnLine){
                        Spawn(startBlock, portManager, packets, shapeModel, 3, connection);
                        isSpawned = true;
                    }
                }
            }
        }
        if(startBlock.getShapeModel(4) == shapeModel && startBlock.getConnection(4) && !isSpawned){
            for (Connection connection : portManager.getConnections()) {
                boolean isStartPort = (connection.blockA == startBlock && connection.portA == 4);
                boolean isEndPort = (connection.blockB == startBlock && connection.portB == 4);
                if (isStartPort || isEndPort) {
                    if (!connection.packetOnLine){
                        Spawn(startBlock, portManager, packets, shapeModel, 4, connection);
                        isSpawned = true;
                    }
                }
            }
        }
        if (!isSpawned){
            for (int i = 3; i <= 4; i++){
                for (Connection connection : portManager.getConnections()) {
                    boolean isStartPort = (connection.blockA == startBlock && connection.portA == i);
                    boolean isEndPort = (connection.blockB == startBlock && connection.portB == i);
                    if (isStartPort || isEndPort) {
                        if (connection.packetOnLine) return;
                        Spawn(startBlock, portManager, packets, shapeModel, i, connection);
                    }
                }
            }
        }

    }

    public void Spawn(GameShape startBlock, PortManager portManager, List<Packet> packets, int shapeModel, int portNumber, Connection connection){
        boolean isStartPort = (connection.blockA == startBlock && connection.portA == portNumber);
        boolean isEndPort = (connection.blockB == startBlock && connection.portB == portNumber);

        if (isStartPort) {
            float speedChanger = 0;
            float acceleration = 0;
            if (connection.blockB.getShapeModel(connection.portB) == 2 && shapeModel == 1){
                speedChanger = 1;
            } else if (connection.blockB.getShapeModel(connection.portB) == 1 && shapeModel == 2){
                acceleration = 1;
            }
            Packet packet = new Packet(portManager, connection, connection.blockA, connection.portA, connection.blockB, connection.portB, shapeModel, speedChanger, acceleration);
            packets.add(packet);
            connection.packetOnLine = true;
            if(shapeModel == 1){
                startBlock.setSquarePacketCount(startBlock.getSquarePacketCount()-1);
            } else if (shapeModel == 2){
                startBlock.setTrianglePacketCount(startBlock.getTrianglePacketCount()-1);
            }
        } else if (isEndPort){
            float speedChanger = 0;
            float acceleration = 0;
            if (connection.blockB.getShapeModel(connection.portB) == 2 && shapeModel == 1){
                speedChanger = 1;
            } else if (connection.blockB.getShapeModel(connection.portB) == 1 && shapeModel == 2){
                acceleration = 1;
            }

            Packet packet = new Packet(portManager, connection, connection.blockB, connection.portB, connection.blockA, connection.portA, shapeModel, speedChanger, acceleration);
            packets.add(packet);
            connection.packetOnLine = true;
            if(shapeModel == 1){
                startBlock.setSquarePacketCount(startBlock.getSquarePacketCount()-1);
            } else if (shapeModel == 2){
                startBlock.setTrianglePacketCount(startBlock.getTrianglePacketCount()-1);
            }
        }
    }

}

