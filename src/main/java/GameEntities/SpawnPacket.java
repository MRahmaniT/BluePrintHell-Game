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
                for (Connection conn : portManager.getConnections()) {
                    boolean isStartPort = (conn.blockA == startBlock && conn.portA == portNumber);
                    boolean isEndPort = (conn.blockB == startBlock && conn.portB == portNumber);
                    if (isStartPort) {
                        Packet packet = new Packet(portManager, conn.blockA, conn.portA, conn.blockB, conn.portB);
                        packets.add(packet);
                    } else if (isEndPort){
                        Packet packet = new Packet(portManager, conn.blockB, conn.portB, conn.blockA, conn.portA);
                        packets.add(packet);
                    }
                }
            }
        }
    }


}

