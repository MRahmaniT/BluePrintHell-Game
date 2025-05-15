package GameEntities;

import GameLogic.PortManager;
import GameShapes.GameShape;

import java.awt.*;
import java.awt.geom.Point2D;

public class Packet {
    private final PortManager portManager;
    private final GameShape startBlock;
    private final GameShape endBlock;
    private final int startPort;
    private final int endPort;
    private Point2D.Float startPosition, endPosition, currentPosition, direction, destinationDistance; // normalized vector
    private float speed;
    private float movementPercentage;
    private float noise;
    private boolean lost;

    // Constants
    public static final float NOISE_THRESHOLD = 100f;
    public static final float MAX_DISTANCE_FROM_WIRE = 20f;

    public Packet(PortManager portManager, GameShape startBlock, int startPort, GameShape endBlock, int endPort) {
        this.startBlock = startBlock;
        this.endBlock = endBlock;
        this.startPort = startPort;
        this.endPort = endPort;
        this.portManager = portManager;
        this.currentPosition = portManager.getPortCenter(startBlock, startPort);
        this.movementPercentage = 0;
        this.noise = 0;
        this.lost = false;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fillOval((int) currentPosition.x - 5, (int) currentPosition.y - 5,10,10);
    }

    public void update(Point2D.Float nearestWirePoint) {
        startPosition = portManager.getPortCenter(startBlock, startPort);
        endPosition = portManager.getPortCenter(endBlock, endPort);
        destinationDistance = new Point2D.Float(endPosition.x - startPosition.x, endPosition.y - startPosition.y);
        if (movementPercentage >= 1) {
            return;
        }

        //Move
        direction = normalize(new Point2D.Float(destinationDistance.x, destinationDistance.y));
        speed = (float) (5.0 / (startPosition.distance(endPosition)));
        movementPercentage += speed;
        currentPosition.x = (startPosition.x + movementPercentage * destinationDistance.x);
        currentPosition.y = (startPosition.y + movementPercentage * destinationDistance.y);

        //Check off-wire
        double distance = startPosition.distance(nearestWirePoint);
        if (distance > MAX_DISTANCE_FROM_WIRE) {
            markLost();
        }

        //Check noise
        if (noise >= NOISE_THRESHOLD) {
            markLost();
        }
    }

    public boolean isArrived (){
        if (movementPercentage >= 1) {
            return true;
        }else {
            return false;
        }
    }

    public void applyImpact(Point2D.Float forceVector, float distanceFromImpact) {
        float attenuation = 1.0f - Math.min(1.0f, distanceFromImpact / 100f);
        direction.x += forceVector.x * attenuation;
        direction.y += forceVector.y * attenuation;
        direction = normalize(direction);

        // Add noise
        noise += 10f * attenuation;
    }

    public boolean collidesWith(Packet other) {
        return !this.lost && !other.lost &&
                this.startPosition.distance(other.startPosition) < 12;
    }

    public boolean isLost() {
        return lost;
    }

    public void markLost() {
        this.lost = true;
    }

    public Point2D.Float getPosition() {
        return startPosition;
    }

    public float getNoise() {
        return noise;
    }

    private Point2D.Float normalize(Point2D.Float vector) {
        float len = (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        return len == 0 ? new Point2D.Float(0, 0) : new Point2D.Float(vector.x / len, vector.y / len);
    }
    public GameShape getEndBlock(){
        return this.endBlock;
    }
    public int getEndPort(){
        return this.endPort;
    }
}
