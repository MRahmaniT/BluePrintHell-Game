package Client.Model.GameEntities;

import Client.Model.Enums.PacketType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class Packet implements Serializable {

    public int getDistributeVolume() {
        return distributeVolume;
    }

    public void setDistributeVolume(int distributeVolume) {
        this.distributeVolume = distributeVolume;
    }

    public int getMergeVolume() {
        return mergeVolume;
    }

    public void setMergeVolume(int mergeVolume) {
        this.mergeVolume = mergeVolume;
    }

    public enum Location { IN_BLOCK, ON_WIRE, LOST, DISTRIBUTED }

    // Identity
    private int id;
    private int bulkId = -1;
    private int distributeVolume = -1;
    private int mergeVolume = -1;
    private PacketType packetType;
    private PacketType firstType;
    private boolean doNotFindCompatible = false;
    private int protectedBy = -1;

    // Where it is
    private Location location = Location.IN_BLOCK;

    // When IN_BLOCK
    private int blockIdx = -1;
    private long enqueuedAt = 0;
    private boolean isMoved = false;

    // When ON_WIRE
    // Index in WiringManager.getConnections()
    private int connectionIdx = -1;
    private int fromBlockIdx = -1, fromPort = -1;
    private int toBlockIdx   = -1, toPort   = -1;

    // Kinematics (for drawing/snapshots); physics sets/updates these.
    private float x, y;        // current position
    private float xDirection, yDirection;      // facing (normalized direction used for rotation)
    private float progress;    // [0..1] geometric progress along the wire (physics maintains)
    private float xImpactDirection, yImpactDirection;  // deviation from impact waves
    private float speed;       // constant movement magnitude (physics uses this each update)
    private float acceleration;       // per-update additive to speed (can be 0)
    private float accelerationChanger;

    // status
    private float noise;
    public static final float NOISE_THRESHOLD = 15f;
    public final float baseSpeed = 50f;

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
        this.speed = 0;
        this.acceleration = 0f;
    }

    public void startOnWire(int connectionIdx,
                            int fromBlockIdx, int fromPort,
                            int toBlockIdx,   int toPort) {
        this.location = Location.ON_WIRE;
        this.connectionIdx = connectionIdx;

        this.fromBlockIdx = fromBlockIdx;
        this.fromPort     = fromPort;
        this.toBlockIdx   = toBlockIdx;
        this.toPort       = toPort;

        this.progress = 0f;
        this.speed = baseSpeed;
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
    public PacketType getPacketType() { return packetType; }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public void setFirstType(PacketType firstType) {
        this.firstType = firstType;
    }

    public PacketType getFirstType () { return firstType; }

    public Location getLocation() { return location; }
    @JsonIgnore public boolean isOnWire()  { return location == Location.ON_WIRE; }
    @JsonIgnore public boolean isInBlock() { return location == Location.IN_BLOCK; }
    @JsonIgnore public boolean isLost()    { return location == Location.LOST; }
    @JsonIgnore public boolean isDistributed () { return location == Location.DISTRIBUTED; }
    public void setLocation(Location location) {
        this.location = location;
    }

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

    public float getXDirection() { return xDirection; }
    public float getYDirection() { return yDirection; }
    public void setXDirection(float xDirection) { this.xDirection = xDirection; }
    public void setYDirection(float yDirection) { this.yDirection = yDirection; }

    public float getProgress() { return progress; }
    public void setProgress(float progress) { this.progress = progress; }

    public float getXImpactDirection() { return xImpactDirection; }
    public float getYImpactDirection() { return yImpactDirection; }
    public void setXImpactDirection(float xImpactDirection) { this.xImpactDirection = xImpactDirection; }
    public void setYImpactDirection(float yImpactDirection) { this.yImpactDirection = yImpactDirection; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public float getAcceleration() { return acceleration; }
    public void setAcceleration(float acceleration) { this.acceleration = acceleration; }

    public float getAccelerationChanger() {
        return accelerationChanger;
    }

    public void setAccelerationChanger(float accelerationChanger) {
        this.accelerationChanger = accelerationChanger;
    }

    public float getNoise() { return noise; }

    public boolean isMoved() {
        return isMoved;
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }

    public boolean isDoNotFindCompatible() {
        return doNotFindCompatible;
    }

    public void setDoNotFindCompatible(boolean doNotFindCompatible) {
        this.doNotFindCompatible = doNotFindCompatible;
    }

    public int getProtectedBy() {
        return protectedBy;
    }

    public void setProtectedBy(int protectedBy) {
        this.protectedBy = protectedBy;
    }

    public int getBulkId() {
        return bulkId;
    }

    public void setBulkId(int bulkId) {
        this.bulkId = bulkId;
    }
}
