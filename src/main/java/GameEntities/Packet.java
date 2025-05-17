package GameEntities;

import GameLogic.Connection;
import GameLogic.PortManager;
import GameShapes.GameShape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Packet {
    private final PortManager portManager;
    private final GameShape startBlock;
    private final GameShape endBlock;
    private final Connection connection;
    private Point2D.Float startPosition, endPosition, currentPosition, direction, destinationDistance;
    private final int shapeModel; //1 for square, 2 for triangle
    private Path2D triangle;
    private Path2D rectangle;
    private final int startPort;
    private final int endPort;
    private float speed, speedChanger, acceleration, accelerationChanger;
    private float movementPercentage;
    private float noise;
    private boolean lost;

    // Constants
    public static final float NOISE_THRESHOLD = 100f;
    public static final float MAX_DISTANCE_FROM_WIRE = 20f;

    public Packet(PortManager portManager, Connection connection, GameShape startBlock, int startPort, GameShape endBlock, int endPort, int shapeModel, float speedChanger, float accelerationChanger) {
        this.connection = connection;
        this.startBlock = startBlock;
        this.endBlock = endBlock;
        this.startPort = startPort;
        this.endPort = endPort;
        this.portManager = portManager;
        this.shapeModel = shapeModel;
        this.currentPosition = portManager.getPortCenter(startBlock, startPort);
        this.speedChanger = speedChanger;
        this.accelerationChanger = accelerationChanger;
        this.direction = new Point2D.Float(0,0);
        this.movementPercentage = 0;
        this.noise = 0;
        this.lost = false;
    }

    public void draw(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g.create();

        if (shapeModel == 1) {
            g2d.setColor(Color.GREEN);
            g2d.fill(getPath());
        } else if (shapeModel == 2) {
            g2d.setColor(Color.YELLOW);
            g2d.fill(getPath());
        }

        g2d.dispose();
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
        acceleration = (float) (accelerationChanger / (startPosition.distance(endPosition)));
        speed = (float) ((5.0 / (startPosition.distance(endPosition))) + acceleration + (5.0 / (startPosition.distance(endPosition)) * speedChanger));
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

    public void applyImpact(Point pointOfImpact) {
        double distanceFromImpact = currentPosition.distance(pointOfImpact);
        float attenuation = (float) (1.0f - Math.min(1.0f, distanceFromImpact / 100f));

        Point2D.Float forceVector = new Point2D.Float(currentPosition.x - pointOfImpact.x, currentPosition.y - pointOfImpact.y);

        direction.x += forceVector.x * attenuation * 100000000f;
        direction.y += forceVector.y * attenuation * 100000000f;
        direction = normalize(direction);
    }

    public boolean collidesWith(Packet other) {
        return !this.lost && !other.lost &&
                this.startPosition.distance(other.startPosition) < 12;
    }

    private Point2D.Float normalize(Point2D.Float vector) {
        float len = (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        return len == 0 ? new Point2D.Float(0, 0) : new Point2D.Float(vector.x / len, vector.y / len);
    }

    //Setters and Getters
    public boolean isLost() {
        return lost;
    }

    public void markLost() {
        this.lost = true;
    }

    public Point2D.Float getPosition() {
        return startPosition;
    }

    public void resetNoise() {
        this.noise = 0;
    }

    public float getNoise() {
        return noise;
    }

    public void increaseNoise(float noise) {
        this.noise += noise;
    }


    public GameShape getEndBlock(){
        return this.endBlock;
    }

    public int getEndPort(){
        return this.endPort;
    }

    public int getShapeModel() {
        return shapeModel;
    }

    public Connection getConnection(){return this.connection;}

    public Path2D getPath(){
        AffineTransform transform = new AffineTransform();

        double angle = Math.atan2(direction.y, direction.x);
        transform.translate(currentPosition.x, currentPosition.y);
        transform.rotate(angle);

        int size = 12;

        if (shapeModel == 1) {
            rectangle = new Path2D.Float();
            rectangle.moveTo(- (double) size / 2, - (double) size / 2);
            rectangle.lineTo(+ (double) size / 2, (double) -size / 2);
            rectangle.lineTo(+ (double) size / 2, (double) +size / 2);
            rectangle.lineTo(- (double) size / 2, (double) +size / 2);
            rectangle.closePath();
            return (Path2D) rectangle.createTransformedShape(transform);
        } else if (shapeModel == 2) {
            triangle = new Path2D.Float();
            triangle.moveTo((double) -size / 2, (double) -size / 2);
            triangle.lineTo((double) +size / 2, 0);
            triangle.lineTo((double) -size / 2, (double) +size / 2);
            triangle.closePath();
            return (Path2D) triangle.createTransformedShape(transform);
        }
        return null;
    }
}
