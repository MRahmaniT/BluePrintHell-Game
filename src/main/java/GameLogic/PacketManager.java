package GameLogic;

import GameEntities.Packet;
import GameEntities.SpawnPackets;
import GameShapes.GameShape;

import Player.PlayerState;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PacketManager {
    public void manageMovement(List<GameShape> blockShapes, PortManager portManager, List<Packet> packets,
                               SpawnPackets spawnPacket, List<Impact> impacts){
        List<Packet> packetsToAdd = new ArrayList<>();
        List<Packet> packetsToRemove = new ArrayList<>();
        for (Packet p : packets) {
            p.update(new Point2D.Float(0,0));
            findImpact(impacts, packets, p);
            if (p.isArrived()) {
                packetsToRemove.add(p);
                int shapeModel = p.getShapeModel();
                if (shapeModel == 1){
                    p.getEndBlock().setSquarePacketCount(p.getEndBlock().getSquarePacketCount()+1);
                    PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount()+2);
                }else {
                    p.getEndBlock().setTrianglePacketCount(p.getEndBlock().getTrianglePacketCount()+1);
                    PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount()+3);
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

    public void findImpact(List<Impact> impacts, List<Packet> packets, Packet packet1){
        for (Packet packet2 : packets) {
            if (packet2 == packet1 || packet1.getPath() == null || packet2.getPath() == null){
                return;
            }
            Area area1 = new Area(packet1.getPath());
            Area area2 = new Area(packet2.getPath());
            area1.intersect(area2);

            boolean firstImpact = true;
            for (Impact i : impacts){
                if((i.contains(packet1, packet2))){
                    firstImpact = false;
                }
            }
            if (!area1.isEmpty() && firstImpact){
                Rectangle2D boundsArea1 = area1.getBounds2D();
                Point point = new Point((int) boundsArea1.getX(), (int) boundsArea1.getY());
                Impact impact = new Impact(packet1, packet2, point);
                impacts.add(impact);
            }
        }
    }

    public void manageImpact(List<Impact> impacts, List<Packet> packets){
        List<Impact> impactsToRemove;
        for (Impact impact : impacts){
            for (Packet packet : packets) {
                if (impact.packet1 != packet && impact.packet2 != packet){
                    packet.applyImpact(impact.point);
                    System.out.println("Bang");
                }
            }

        }
    }
}
