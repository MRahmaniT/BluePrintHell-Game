package Controller.Packets;

import Controller.Wiring.LinePath;
import Controller.Wiring.WiringManager;
import Model.GameEntities.Packet;
import View.Render.GameShapes.GameShape;
import View.Main.MainFrame;
import View.Render.PacketRenderer;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.List;

public class PacketPhysics {

    public static class ArrivedPackets {
        public final Packet packet;
        public final int destBlockSystemId;
        public ArrivedPackets(Packet p, int destBlockSystemId) { this.packet = p; this.destBlockSystemId = destBlockSystemId; }
    }

    private final List<GameShape> blockShapes;
    private final WiringManager wiringManager;      // to compute port centers

    public PacketPhysics(List<GameShape> blocks,
                         WiringManager portManager) {
        this.blockShapes = blocks;
        this.wiringManager = portManager;
    }

    public void update(List<Packet> packets, float dt,
                       List<ArrivedPackets> arrivedPackets,
                       List<Packet> lostPackets) {

        for (Packet packet : packets) {
            if (!packet.isOnWire()) continue;

            GameShape fromBlock = findBlockShape(packet.getFromBlockIdx());
            GameShape toBlock   = findBlockShape(packet.getToBlockIdx());
            if (fromBlock == null || toBlock == null) continue;

            Point2D.Float startPoint = wiringManager.getPortCenter(fromBlock, packet.getFromPort());
            Point2D.Float destinationPoint = wiringManager.getPortCenter(toBlock,   packet.getToPort());
            if (startPoint == null || destinationPoint == null) continue;

            // Initialize x,y on first step for this segment
            if (packet.getProgress() == 0f) {
                packet.setX(startPoint.x); packet.setY(startPoint.y);
            }

            // Direction to target + deviation
            LinePath linePath = new LinePath(startPoint, destinationPoint);
            packet.setXDirection(linePath.tangent().x + packet.getXImpactDirection());
            packet.setYDirection(linePath.tangent().y + packet.getYImpactDirection());

            // speed (+ optional accel)
            packet.setAcceleration(packet.getAcceleration()+packet.getAccelerationChanger());
            float speed = packet.getSpeed() + packet.getAcceleration();
            packet.setSpeed(speed);
            packet.setX(packet.getX() + packet.getXDirection() * speed * dt);
            packet.setY(packet.getY() + packet.getYDirection() * speed * dt);

            // Update progress
            float progressX = packet.getX() - startPoint.x;
            float progressY = packet.getY() - startPoint.y;
            float progressLength = (float) Math.hypot(progressX, progressY);
            float progress = progressLength / linePath.getLength();
            if (progress <= 0){
                packet.setProgress(0);
            }else if (progress >= 1){
                packet.setProgress(1);
            } else {
                packet.setProgress(progress);
            }

            // Loss checks
            if (packet.getNoise() > Packet.NOISE_THRESHOLD) {
                lostPackets.add(packet);
                MainFrame.audioManager.playSoundEffect("Resources/lose.wav");
                continue;
            }

            // Arrival / off-target after 100% progress
            //float distToB = (float) Math.hypot(destinationPoint.x - packet.getX(), destinationPoint.y - packet.getY());
            if (!isLost(packet)) {
                arrivedPackets.add(new ArrivedPackets(packet, packet.getToBlockIdx()));
            } else if (packet.getProgress() >= 1f) {
                lostPackets.add(packet);
                MainFrame.audioManager.playSoundEffect("Resources/lose.wav");
            }
        }
    }

    public void applyImpact(Packet p, float impactX, float impactY) {
        float dx = p.getX() - impactX;
        float dy = p.getY() - impactY;
        float dist = (float) Math.hypot(dx, dy);

        // 0..1 falloff with 500px range
        float attenuation = 1f - Math.min(1f, dist / 500f);
        if (attenuation <= 0f) return;

        p.setXImpactDirection(p.getXImpactDirection() + (dx * attenuation) / 10f);
        p.setYImpactDirection(p.getYImpactDirection() + (dy * attenuation) / 10f);
    }

    private GameShape findBlockShape(int id) {
        if (id < 0 || id >= blockShapes.size()) return null;
        return blockShapes.get(id);
    }

    private boolean isLost (Packet packet) {
        Shape shape1 = PacketRenderer.getShape(packet);
        Shape shape2 = blockShapes.get(packet.getToBlockIdx()).getPortPath(packet.getToPort());

        Area a1 = new Area(shape1);
        a1.intersect(new Area(shape2));

        return a1.isEmpty();
    }
}
