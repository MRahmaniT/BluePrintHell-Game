package Controller.Packets;

import Controller.Packets.Arrival.ArrivedPackets;
import Controller.Wiring.WiringManager;
import Model.Enums.WireType;
import Model.GameEntities.Packet;
import Model.GameEntities.Wire.*;
import Storage.Snapshots.PacketStorage;
import View.Render.GameShapes.System.GameShape;
import View.Main.MainFrame;
import View.Render.GameShapes.Wire.WireShape;
import View.Render.GameShapes.Packet.PacketRenderer;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.List;

public class PacketPhysics {

    private final List<GameShape> blockShapes;
    private final WiringManager wiringManager;
    private final List<ArrivedPackets> arrivedPackets;

    public PacketPhysics(List<GameShape> blocks,
                         WiringManager wiringManager,
                         List<ArrivedPackets> arrivedPackets) {
        this.blockShapes = blocks;
        this.wiringManager = wiringManager;
        this.arrivedPackets = arrivedPackets;
    }

    public void update(float dt, List<Packet> lostPackets) {

        List<Packet> packets = PacketStorage.LoadPackets();

        for (Packet packet : packets) {
            if (!packet.isOnWire()) continue;

            GameShape fromBlock = findBlockShape(packet.getFromBlockIdx());
            GameShape toBlock   = findBlockShape(packet.getToBlockIdx());
            if (fromBlock == null || toBlock == null) continue;

            Point2D.Float startPoint = wiringManager.getPortCenter(fromBlock, packet.getFromPort());
            Point2D.Float destinationPoint = wiringManager.getPortCenter(toBlock,   packet.getToPort());
            List<WireShape> wireShapes = wiringManager.getWireShapes();

            WireShape packetWireShape = null;
            for (WireShape wireShape : wireShapes) {
                if (packet.getConnectionIdx() == wireShape.getWire().getId()) {
                    packetWireShape = wireShape;
                    break;
                }
            }

            if (startPoint == null || destinationPoint == null) continue;

            // Initialize x,y on first step for this segment
            if (packet.getProgress() == 0f) {
                packet.setX(startPoint.x); packet.setY(startPoint.y);
            }

            // Path Type
            assert packetWireShape != null;
            WirePath wirePath = getWirePath(packetWireShape, startPoint, destinationPoint);

            // Direction to target + deviation
            packet.setXDirection(wirePath.tangentAt(packet.getProgress()).x + packet.getXImpactDirection());
            packet.setYDirection(wirePath.tangentAt(packet.getProgress()).y + packet.getYImpactDirection());


            // speed (+ optional accel)
            packet.setAcceleration(packet.getAcceleration()+packet.getAccelerationChanger());
            if (packet.getAcceleration() <= 0) packet.setAcceleration(0);
            float speed = packet.getSpeed() + packet.getAcceleration();
            packet.setSpeed(speed);
            packet.setX(packet.getX() + packet.getXDirection() * speed * dt);
            packet.setY(packet.getY() + packet.getYDirection() * speed * dt);

            // Update progress
            WirePath.Nearest nearest = wirePath.nearestTo(new Point2D.Float(packet.getX(), packet.getY()), wirePath.length(), 0);
            float newProgress = nearest.t;
            if (newProgress <= 0){
                packet.setProgress(0);
            }else if (newProgress >= 1){
                packet.setProgress(1);
            } else {
                packet.setProgress(newProgress);
            }

            // Loss checks
            if (packet.getNoise() > Packet.NOISE_THRESHOLD) {
                lostPackets.add(packet);
                MainFrame.audioManager.playSoundEffect("Resources/lose.wav");
                continue;
            }

            // Arrival / off-target after 100% progress
            if (isDelivered(packet)) {
                arrivedPackets.add(new ArrivedPackets(packet.getId(), packet.getToBlockIdx()));
            } else if (packet.getProgress() >= 1f) {
                lostPackets.add(packet);
                MainFrame.audioManager.playSoundEffect("Resources/lose.wav");
            }
        }

        if (!packets.isEmpty()) {
            PacketStorage.SavePackets(packets);
        }
    }

    private static WirePath getWirePath(WireShape packetWireShape, Point2D.Float startPoint, Point2D.Float destinationPoint) {
        WirePath wirePath;
        if (packetWireShape.getWire().getWireType() == WireType.STRAIGHT) {
            wirePath = new StraightPath(startPoint, destinationPoint);
        } else if (packetWireShape.getWire().getWireType() == WireType.CURVE1) {
            wirePath = new OneFilletPath(startPoint, packetWireShape.getWire().getMidPoints().getFirst(), destinationPoint);
        } else if (packetWireShape.getWire().getWireType() == WireType.CURVE2) {
            wirePath = new TwoFilletPath(startPoint, packetWireShape.getWire().getMidPoints().get(0),
                    packetWireShape.getWire().getMidPoints().get(1), destinationPoint);
        } else if (packetWireShape.getWire().getWireType() == WireType.CURVE3) {
            wirePath = new ThreeFilletPath(startPoint, packetWireShape.getWire().getMidPoints().get(0),
                    packetWireShape.getWire().getMidPoints().get(1), packetWireShape.getWire().getMidPoints().get(2), destinationPoint);
        } else  {
            wirePath = null;
        }

        assert wirePath != null;
        return wirePath;
    }

    public void applyImpact(Packet p, float impactX, float impactY) {
        float dx = p.getX() - impactX;
        float dy = p.getY() - impactY;
        float dist = (float) Math.hypot(dx, dy);

        // 0..1 falloff with 500px range
        float attenuation = 1f - Math.min(1f, dist / 500f);
        if (attenuation <= 0f) return;

        p.setXImpactDirection(p.getXImpactDirection() + (dx * attenuation) / 1000f);
        p.setYImpactDirection(p.getYImpactDirection() + (dy * attenuation) / 1000f);
    }

    private GameShape findBlockShape(int id) {
        if (id < 0 || id >= blockShapes.size()) return null;
        return blockShapes.get(id);
    }

    private boolean isDelivered(Packet packet) {
        Shape shape1 = PacketRenderer.getShape(packet);
        Shape shape2 = blockShapes.get(packet.getToBlockIdx()).getPortPath(packet.getToPort());

        assert shape1 != null;
        Area a1 = new Area(shape1);
        a1.intersect(new Area(shape2));

        return !a1.isEmpty();
    }
}
