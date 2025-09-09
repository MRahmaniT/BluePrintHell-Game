package Controller.Packets;

import Controller.Wiring.WiringManager;
import Model.Enums.PacketType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import Model.GameEntities.Impact;
import Model.GameEntities.Packet;
import View.Render.GameShapes.GameShape;
import Model.Player.PlayerState;
import View.Render.PacketRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PacketManager {

    // dependencies
    private final List<BlockSystem> blockSystems;
    private final List<GameShape> blockShapes;
    private final List<Connection> connections;
    private final List<Packet> packets;
    private final PacketPhysics physics;
    private final SpawnPackets spawnPackets;

    // shop toggles
    private boolean impactIsDisabled = false; // disable collision detection
    private boolean waveIsDisabled   = false; // disable outward impact wave

    // thresholds
    private final float maxSpeed = 500f;

    // counters
    private int lostPacketsCount = 0;

    // constants (kept from your old logic)
    private static final float NOISE_PER_HIT = 5f;

    // impacts
    private List<Impact> impacts = new ArrayList<>();
    private List<Impact> managedImpacts = new ArrayList<>();

    public PacketManager(List<BlockSystem> blockSystems,
                         List<GameShape> blockShapes,
                         WiringManager wiringManager,
                         List<Connection> connections,
                         List<Packet> packets,
                         SpawnPackets spawnPackets) {
        this.blockSystems = blockSystems;
        this.blockShapes = blockShapes;
        this.connections = connections;
        this.packets = packets;
        this.physics = new PacketPhysics(blockShapes, wiringManager);
        this.spawnPackets = spawnPackets;
    }

    public void manageMovement() {

        // 0) spawn from blocks
        spawnPackets.spawnFromBlocks();

        // 1) move (physics update)
        List<PacketPhysics.ArrivedPackets> arrivedPackets = new ArrayList<>();
        List<Packet> lostPackets = new ArrayList<>();
        physics.update(packets, 0.01f, arrivedPackets, lostPackets);

        // 2) find impacts

        for (Packet p : packets) {
            if (!p.isOnWire()) continue;
            findImpact(packets, p);
        }

        //3) apply impacts
        manageImpact(packets);

        // 4) handle arrivedPackets & lostPackets
        for (PacketPhysics.ArrivedPackets arrivedPacket : arrivedPackets) {
            Packet packet = arrivedPacket.packet;
            if (!blockSystems.get(packet.getToBlockIdx()).isActive()) {
                packet.startOnWire(packet.getConnectionIdx(), packet.getToBlockIdx(), packet.getToPort(), packet.getFromBlockIdx(), packet.getFromPort());
            } else {
                if (packet.getSpeed() >= maxSpeed) {
                    deActiveDestinationSystem(arrivedPacket.destinationBlockSystemId);
                }
                freeLine(packet);

                // enqueue to destination block
                packet.parkInBlock(arrivedPacket.destinationBlockSystemId);
                blockSystems.get(arrivedPacket.destinationBlockSystemId).addPacket(packet.getId());

                // coin rules
                if (packet.getType() == PacketType.MESSENGER_2) {
                    PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 1);
                } else {
                    PlayerState.getPlayer().setGoldCount(PlayerState.getPlayer().getGoldCount() + 2);
                }
            }
        }

        for (Packet packet : lostPackets) {
            freeLine(packet);
            packet.markLost();
            lostPacketsCount++;
        }

    }

    private void findImpact(List<Packet> packets, Packet packet1) {
        if (impactIsDisabled) return;

        for (Packet packet2 : packets) {
            if (packet2 == packet1) continue;
            if (!packet1.isOnWire() || !packet2.isOnWire()) continue;

            Shape s1 = PacketRenderer.getShape(packet1);
            Shape s2 = PacketRenderer.getShape(packet2);
            if (s1 == null || s2 == null) continue;

            Area a1 = new Area(s1);
            a1.intersect(new Area(s2));
            if (!a1.isEmpty()) {
                Rectangle2D r = a1.getBounds2D();
                Point point = new Point((int) r.getCenterX(), (int) r.getCenterY());

                boolean firstImpact = true;
                for (Impact impact : managedImpacts) {
                    if (impact.contains(packet1, packet2)) {
                        System.out.println("redundant");
                        firstImpact = false; break; }
                }
                if (firstImpact) {
                    impacts.add(new Impact(packet1, packet2, point));
                }
            } else {
                for (Impact impact : managedImpacts) {
                    if (impact.contains(packet1, packet2)) {
                        managedImpacts.remove(impact); break; }
                }
            }
        }
    }

    private void manageImpact(List<Packet> packets) {
        for (Impact impact : impacts) {
            for (Packet p : packets) {
                if (!p.isOnWire()) continue;

                if (p == impact.packet1 || p == impact.packet2) {
                    p.addNoise(NOISE_PER_HIT);
                } else if (!waveIsDisabled) {
                    physics.applyImpact(p, impact.point.x, impact.point.y);
                }
            }
            managedImpacts.add(impact);
        }
        impacts.clear();
    }

    private void freeLine(Packet p) {
        int idx = p.getConnectionIdx();
        for (Connection connection : connections) {
            if (connection.getId() == idx) {
                connection.setPacketOnLine(false);
            }
        }
        p.setConnectionIdx(-1);
    }

    public void disableImpactForSeconds(int seconds) {
        impactIsDisabled = true;
        Timer timer = new Timer(seconds * 1000, e -> impactIsDisabled = false);
        timer.setRepeats(false);
        timer.start();
    }
    public boolean isImpactIsDisabled() { return impactIsDisabled; }

    public void disableWaveForSeconds(int seconds) {
        waveIsDisabled = true;
        Timer timer = new Timer(seconds * 1000, e -> waveIsDisabled = false);
        timer.setRepeats(false);
        timer.start();
    }
    public boolean isWaveIsDisabled() { return waveIsDisabled; }

    public void deActiveDestinationSystem(int blockSystemId) {
        blockSystems.get(blockSystemId).setActive(false);
        Timer timer = new Timer(5 * 1000, e -> {
            blockSystems.get(blockSystemId).setActive(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    public int getLostPacketsCount() { return lostPacketsCount; }
}
