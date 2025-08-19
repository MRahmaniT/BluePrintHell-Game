package Controller.Packets;

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
            float dx = (destinationPoint.x - startPoint.x);
            float dy = (destinationPoint.y - startPoint.y);
            float len = (float) Math.hypot(dx, dy);
            float nx = (len > 0f) ? dx / len : 0f;
            float ny = (len > 0f) ? dy / len : 0f;
            packet.setVx(nx + packet.getDevX());
            packet.setVy(ny + packet.getDevY());

            // speed (+ optional accel)
            float speed = Math.max(0f, packet.getSpeed() + packet.getAccel());
            packet.setSpeed(speed);
            packet.setX(packet.getX() + packet.getVx() * speed * dt);
            packet.setY(packet.getY() + packet.getVy() * speed * dt);

            // Update progress
            float px = packet.getX() - startPoint.x, py = packet.getY() - startPoint.y;
            float pLen = (float) Math.hypot(px, py);
            float progress = pLen/len;
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

        p.setDevX(p.getDevX() + (dx * attenuation) / 10f);
        p.setDevY(p.getDevY() + (dy * attenuation) / 10f);
    }

    private GameShape findBlockShape(int id) {
        if (id < 0 || id >= blocks.size()) return null;
        return blocks.get(id);
    }
}
