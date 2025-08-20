package Controller.Packets;

import Controller.Wiring.LinePath;
import Controller.Wiring.WiringManager;
import Model.GameEntities.Packet;
import View.Render.GameShapes.GameShape;
import View.Main.MainFrame;

import java.awt.geom.Point2D;
import java.util.List;

public class PacketPhysics {

    public static class ArrivedPackets {
        public final Packet packet;
        public final int destBlockSystemId;
        public ArrivedPackets(Packet p, int destBlockSystemId) { this.packet = p; this.destBlockSystemId = destBlockSystemId; }
    }

    private final List<GameShape> blocks;
    private final WiringManager wiringManager;      // to compute port centers

    // thresholds
    private final float arriveEps = 4f;         // how close to B counts as arrived
    private final float offTargetLossEps = 12f; // if progress>=1 but still far -> lost

    public PacketPhysics(List<GameShape> blocks,
                         WiringManager portManager) {
        this.blocks = blocks;
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
            float distToB = (float) Math.hypot(destinationPoint.x - packet.getX(), destinationPoint.y - packet.getY());
            if (packet.getProgress() >= 1f) {
                if (distToB <= arriveEps) {
                    arrivedPackets.add(new ArrivedPackets(packet, packet.getToBlockIdx()));
                } else if (distToB > offTargetLossEps) {
                    lostPackets.add(packet);
                    MainFrame.audioManager.playSoundEffect("Resources/lose.wav");                }
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
        if (id < 0 || id >= blocks.size()) return null;
        return blocks.get(id);
    }
}
