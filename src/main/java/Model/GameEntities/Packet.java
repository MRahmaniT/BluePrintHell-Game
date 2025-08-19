package Model.GameEntities;

import Model.Enums.PacketType;

import java.io.Serializable;

public class Packet implements Serializable {

    public enum Type { SQUARE, TRIANGLE, MESSENGER, PROTECTED, PRIVET4, PRIVET6, BULKY8, BULKY10}
    public enum Location { IN_BLOCK, ON_WIRE, LOST }

    // Identity
    private int id;
    private PacketType packetType;

    // Where it is
    private Location location = Location.IN_BLOCK;

    // When IN_BLOCK
    private int blockIdx = -1;
    private long enqueuedAt = 0;

    // When ON_WIRE
    // Index in WiringManager.getConnections()
    private int connectionIdx = -1;
    private int fromBlockIdx = -1, fromPort = -1;
    private int toBlockIdx   = -1, toPort   = -1;

    // Kinematics (for drawing/snapshots); physics sets/updates these.
    private float x, y;        // current position
    private float vx, vy;      // facing (normalized direction used for rotation)
    private float progress;    // [0..1] geometric progress along the wire (physics maintains)
    private float devX, devY;  // deviation from impact waves
    private float speed;       // constant movement magnitude (physics uses this each update)
    private float speedFactor;
    private float accel;       // per-update additive to speed (can be 0)

    // status
    private float noise;
    public static final float NOISE_THRESHOLD = 15f;
    public final float baseSpeed = 100f;

    public Packet() { /* for JSON */ }

    public Packet(int id, PacketType packetType) {
        this.id = id;
        this.packetType = packetType;
        this.speed = baseSpeed;
    }

    /* ---------------- State transitions (no physics here) ---------------- */

    public void parkInBlock(int blockIdx) {
        this.location = Location.IN_BLOCK;
        this.blockIdx = blockIdx;

        this.connectionIdx = -1;
        this.progress = 0f;
        this.speedFactor = 0f;
        this.accel = 0f;
    }

    public void startOnWire(int connectionIdx,
                            int fromBlockIdx, int fromPort,
                            int toBlockIdx,   int toPort,
                            float speedFactor, float accel) {
        this.location = Location.ON_WIRE;
        this.connectionIdx = connectionIdx;

        this.fromBlockIdx = fromBlockIdx;
        this.fromPort     = fromPort;
        this.toBlockIdx   = toBlockIdx;
        this.toPort       = toPort;

        this.progress = 0f;
        this.speed = baseSpeed * speedFactor;
        this.accel = accel;
    }

    public void markArrivedToBlock(int destBlockIdx) {
        parkInBlock(destBlockIdx);
    }

    public void markLost() {
        this.location = Location.LOST;
        this.connectionIdx = -1;
    }

    /* ---------------- Small helpers ---------------- */

    public void addNoise(float amount) { this.noise += amount; }
    public void resetNoise()           { this.noise = 0f; }

    /* ---------------- Getters / Setters ---------------- */

    public int getId() { return id; }
    public PacketType getType() { return packetType; }

    public Location getLocation() { return location; }
    public boolean isOnWire()  { return location == Location.ON_WIRE; }
    public boolean isInBlock() { return location == Location.IN_BLOCK; }
    public boolean isLost()    { return location == Location.LOST; }

    public int  getBlockIdx()   { return blockIdx; }
    public long getEnqueuedAt() { return enqueuedAt; }

    public int getConnectionIdx() { return connectionIdx; }
    public void setConnectionIdx(int idx) { this.connectionIdx = idx; }

    public int getFromBlockIdx() { return fromBlockIdx; }
    public int getFromPort()     { return fromPort; }
    public int getToBlockIdx()   { return toBlockIdx; }
    public int getToPort()       { return toPort; }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }

    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }

    public float getProgress() { return progress; }
    public void setProgress(float progress) { this.progress = progress; }

    public float getDevX() { return devX; }
    public float getDevY() { return devY; }
    public void setDevX(float devX) { this.devX = devX; }
    public void setDevY(float devY) { this.devY = devY; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public float getAccel() { return accel; }
    public void setAccel(float accel) { this.accel = accel; }

    public float getNoise() { return noise; }
}
