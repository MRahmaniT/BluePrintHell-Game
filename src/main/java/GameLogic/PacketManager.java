package GameLogic;

import GameEntities.Packet;
import GameEntities.SpawnPackets;
import GameShapes.GameShape;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PacketManager {
        public void manageSpawn(List<GameShape> blockShapes, PortManager portManager, List<Packet> packets, SpawnPackets spawnPacket){
            List<Packet> packetsToAdd = new ArrayList<>();
            List<Packet> packetsToRemove = new ArrayList<>();
            for (Packet p : packets) {
                p.update(new Point2D.Float(0,0));
                if (p.isArrived()) {
                    packetsToRemove.add(p);
                    int shapeModel = p.getShapeModel();
                    if (shapeModel == 1){
                        p.getEndBlock().setSquarePacketCount(p.getEndBlock().getSquarePacketCount()+1);
                    }else {
                        p.getEndBlock().setTrianglePacketCount(p.getEndBlock().getTrianglePacketCount()+1);
                    }
                    p.getConnection().packetOnLine = false;
                }
            }
            for (GameShape blockShape : blockShapes){
                if (blockShape.getSquarePacketCount() > 0){
                    spawnPacket.SpawnPacket(blockShape, portManager, packetsToAdd, 1);
                }
                if (blockShape.getTrianglePacketCount() > 0){
                    spawnPacket.SpawnPacket(blockShape, portManager, packetsToAdd, 2);
                }
            }
            packets.removeAll(packetsToRemove);
            packets.addAll(packetsToAdd);
            packetsToRemove.clear();
            packetsToAdd.clear();
        }

}
