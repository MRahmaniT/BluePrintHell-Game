package Client.Controller.Packets;

import Client.Controller.Packets.Arrival.ArrivedPackets;
import Client.Controller.Packets.Arrival.HandlePackets;
import Client.Controller.Packets.Spawning.SpawnPackets;
import Client.Controller.Wiring.WiringManager;
import Client.Model.GameEntities.Impact;
import Client.Model.GameEntities.Packet;
import Client.Storage.Facade.StorageFacade;
import Client.View.Render.GameShapes.System.GameShape;
import Client.View.Render.GameShapes.Packet.PacketRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PacketManager {

    // dependencies
    private List<Packet> packets;
    private final PacketPhysics physics;
    private final SpawnPackets spawnPackets;

    // shop toggles
    private boolean impactIsDisabled = false; // disable collision detection
    private boolean waveIsDisabled   = false; // disable outward impact wave

    // counters
    private int lostPacketsCount = 0;

    // constants
    private static final float NOISE_PER_HIT = 5f;

    // impacts
    private final List<Impact> impacts = new ArrayList<>();
    private final List<Impact> managedImpacts = new ArrayList<>();

    // handle arrived packets
    private final List<ArrivedPackets> arrivedPackets = new ArrayList<>();
    private final List<Packet> lostPackets = new ArrayList<>();
    private final HandlePackets handlePackets;
    private Thread spawnThread;

    public PacketManager(List<GameShape> blockShapes,
                         WiringManager wiringManager,
                         SpawnPackets spawnPackets) {
        packets = StorageFacade.loadPackets();
        this.handlePackets = new HandlePackets();
        this.physics = new PacketPhysics(blockShapes, wiringManager, arrivedPackets, lostPackets);
        this.spawnPackets = spawnPackets;
        spawnThread = new Thread(spawnPackets);
    }

    public void manageMovement() {
        // 0) spawn from blocks
        spawnThread.run();

        // 1) move (physics update)
        physics.run();

        // 2) find impacts
        packets = StorageFacade.loadPackets();
        for (Packet p : packets) {
            if (!p.isOnWire()) continue;
            findImpact(packets, p);
        }

        //3) apply impacts
        packets = StorageFacade.loadPackets();
        manageImpact(packets);

        // 4) handle arrivedPackets & lostPackets
        handlePackets.Handle(arrivedPackets, lostPackets, lostPacketsCount);

    }

    private void findImpact(List<Packet> packets, Packet packet1) {
        if (impactIsDisabled) return;

        for (Packet packet2 : packets) {
            if (packet2.getId() == packet1.getId()) continue;
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
                        firstImpact = false; break; }
                }
                for (Impact impact : impacts) {
                    if (impact.contains(packet1, packet2)) {
                        firstImpact = false; break; }
                }
                if (firstImpact) {
                    impacts.add(new Impact(packet1, packet2, point));
                }
            } else {
                for (Impact impact : managedImpacts) {
                    if (impact.contains(packet1, packet2)) {
                        managedImpacts.remove(impact);
                        break;
                    }
                }
            }
        }
    }

    private void manageImpact(List<Packet> packets) {
        for (Impact impact : impacts) {
            managedImpacts.add(impact);
            for (Packet p : packets) {
                if (!p.isOnWire()) continue;

                if (p.getId() == impact.packet1.getId() || p.getId() == impact.packet2.getId()) {
                    p.addNoise(NOISE_PER_HIT);
                    StorageFacade.savePackets(packets);
                } else if (!waveIsDisabled) {
                    physics.applyImpact(p, impact.point.x, impact.point.y);
                    StorageFacade.savePackets(packets);
                }
            }
        }
        impacts.clear();
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

    public int getLostPacketsCount() { return lostPacketsCount; }

    public List<ArrivedPackets> getArrivedPackets () {
        return arrivedPackets;
    }


}
