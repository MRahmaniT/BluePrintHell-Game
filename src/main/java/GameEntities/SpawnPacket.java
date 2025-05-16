package GameEntities;

import GameLogic.Connection;
import GameLogic.PortManager;
import GameShapes.GameShape;

import java.awt.geom.Point2D;
import java.util.List;

public class SpawnPacket {
    private Point2D.Float position;
    private Point2D.Float direction;
    private float speed;
    private float noise;
    private boolean lost;

    // Constants
    public static final float NOISE_THRESHOLD = 100f;
    public static final float MAX_DISTANCE_FROM_WIRE = 20f;


    public void SpawnPacket(GameShape startBlock, PortManager portManager, List<Packet> packets) {
        for (int portNumber = 3; portNumber<=4; portNumber++){
            if (startBlock.getConnection(portNumber)) {
                for (Connection connection : portManager.getConnections()) {
                    boolean isStartPort = (connection.blockA == startBlock && connection.portA == portNumber);
                    boolean isEndPort = (connection.blockB == startBlock && connection.portB == portNumber);
                    int shapeModel;
                    if (isStartPort) {
                        shapeModel = connection.blockA.getShapeModel(connection.portA);
                        Packet packet = new Packet(portManager, connection.blockA, connection.portA, connection.blockB, connection.portB, shapeModel);
                        packets.add(packet);
                    } else if (isEndPort){
                        shapeModel = connection.blockB.getShapeModel(connection.portB);
                        Packet packet = new Packet(portManager, connection.blockB, connection.portB, connection.blockA, connection.portA, shapeModel);
                        packets.add(packet);
                    }
                }
            }
        }
    }


}

