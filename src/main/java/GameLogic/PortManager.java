package GameLogic;

import Main.MainFrame;
import GameShapes.GameShape;
import GameShapes.LineShape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.awt.geom.Point2D;

public class PortManager {
    private boolean dragging = false;
    private Point startPoint;
    private GameShape sourceBlock;
    private int sourcePort;
    private boolean sourceIsEntrance;

    private final List<Connection> connections = new ArrayList<>();

    public void handleMousePress(List<GameShape> blockShapes, int mouseX, int mouseY) {
        for (GameShape block : blockShapes) {
            for (int i = 1; i <= 4; i++) {
                Path2D.Float port = block.getPortPath(i);
                if (port != null && port.contains(mouseX, mouseY)) {
                    dragging = true;
                    Rectangle2D bounds = port.getBounds2D();
                    startPoint = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
                    sourceBlock = block;
                    sourcePort = i;
                    sourceIsEntrance = i <= 2;
                    return;
                }
            }
        }
    }

    public void handleMouseRelease(List<GameShape> blockShapes, int mouseX, int mouseY) {
        if (!dragging) return;
        dragging = false;

        for (GameShape targetBlock : blockShapes) {
            for (int i = 1; i <= 4; i++) {
                Path2D.Float port = targetBlock.getPortPath(i);
                if (port != null && port.contains(mouseX, mouseY)) {

                    boolean sameBlock = sourceBlock == targetBlock;
                    boolean alreadyConnected = targetBlock.getConnection(i) ||
                            sourceBlock.getConnection(sourcePort);
                    boolean sameModel = sourceBlock.getShapeModel(sourcePort) == targetBlock.getShapeModel(i);
                    boolean targetIsEntrance = i <= 2;

                    if (sameBlock && sourceBlock.getConnection(i) && sourcePort == i) {

                        for (Connection connection : connections) {
                            if (connection.contains(sourceBlock, sourcePort)) {
                                connection.blockA.setConnection(connection.portA, false);
                                connection.blockB.setConnection(connection.portB, false);
                                connection.blockA.setPacket(connection.portA, false);
                                connection.blockB.setPacket(connection.portB, false);
                                connections.remove(connection);
                                break;
                            }
                        }

                    }

                    if (!sameBlock && !alreadyConnected && sameModel && (sourceIsEntrance ^ targetIsEntrance)) {

                        MainFrame.audioManager.playSoundEffect("Resources/connection.wav");
                        LineShape line = new LineShape(sourceBlock, sourcePort, targetBlock, i, Color.CYAN);
                        Connection connection = new Connection(sourceBlock, sourcePort, targetBlock, i, line);
                        connections.add(connection);
                        sourceBlock.setConnection(sourcePort, true);
                        targetBlock.setConnection(i, true);

                    }
                }
            }
        }
    }

    public Point2D.Float getPortCenter(GameShape block, int portNumber) {
        Path2D.Float portPath = block.getPortPath(portNumber);
        if (portPath == null) return null;

        Rectangle2D bounds = portPath.getBounds2D();
        return new Point2D.Float((float) bounds.getCenterX(), (float) bounds.getCenterY());
    }

    public void drawDrag(Graphics2D g2d, Point mousePoint) {
        if (!dragging || startPoint == null || mousePoint == null) return;
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawLine(startPoint.x, startPoint.y, mousePoint.x, mousePoint.y);
    }

    public double getUsedWireLength() {
        double total = 0;
        for (Connection c : connections) {
            Path2D.Float pathA = c.blockA.getPortPath(c.portA);
            Path2D.Float pathB = c.blockB.getPortPath(c.portB);
            if (pathA != null && pathB != null) {
                Rectangle2D boundsA = pathA.getBounds2D();
                Rectangle2D boundsB = pathB.getBounds2D();
                double dx = boundsA.getCenterX() - boundsB.getCenterX();
                double dy = boundsA.getCenterY() - boundsB.getCenterY();
                total += Math.sqrt(dx * dx + dy * dy);
            }
        }
        return total;
    }

    public double getRemainingWireLength(double MAX_WIRE_LENGTH) {
        return MAX_WIRE_LENGTH - getUsedWireLength();
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public boolean isDragging() {
        return dragging;
    }
}
