package Controller.Wiring;

import Model.Enums.PortRole;
import Model.Enums.WireType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Connection;
import View.Main.MainFrame;
import View.Render.GameShapes.GameShape;
import View.Render.GameShapes.Wire.Wire;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.awt.geom.Point2D;

public class WiringManager {

    private boolean dragging = false;
    private Point startPoint;

    private GameShape fromBlockShape;
    private BlockSystem fromBlockSystem;
    private int fromBlockSystemId;
    private int fromPortId;
    private boolean sourceIsEntrance;

    private final List<Connection> connections = new ArrayList<>();
    private int connectionIdCounter = 0;

    private final List<Wire> lines = new ArrayList<>();

    public void handleMousePress(List<GameShape> blockShapes, int mouseX, int mouseY) {
        for (GameShape block : blockShapes) {
            for (int i = 1; i <= 4; i++) {
                Path2D.Float port = block.getPortPath(i);
                if (port != null && port.contains(mouseX, mouseY)) {
                    dragging = true;
                    Rectangle2D bounds = port.getBounds2D();
                    startPoint = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
                    fromBlockShape = block;
                    fromBlockSystem = block.getBlockSystem();
                    fromBlockSystemId = block.getBlockSystem().getId();
                    fromPortId = i;
                    sourceIsEntrance = block.getBlockSystem().getPort(i).getRole() == PortRole.IN;
                    return;
                }
            }
        }
    }

    public void handleMouseRelease(List<BlockSystem> blockSystems, List<GameShape> blockShapes,int mouseX, int mouseY, double remainingWireLength) {
        if (!dragging) return;
        dragging = false;

        for (GameShape targetBlock : blockShapes) {
            for (int i = 1; i <= 4; i++) {
                Path2D.Float port = targetBlock.getPortPath(i);
                if (port != null && port.contains(mouseX, mouseY)) {

                    boolean sameBlock = fromBlockSystemId == targetBlock.getBlockSystem().getId();
                    boolean alreadyConnected = targetBlock.getBlockSystem().getPort(i).isConnected() ||
                            fromBlockSystem.getPort(fromPortId).isConnected();
                    boolean sameModel = fromBlockSystem.getPort(fromPortId).getType() == targetBlock.getPortType(i);
                    boolean targetIsEntrance = targetBlock.getBlockSystem().getPort(i).getRole() == PortRole.IN;;

                    if (sameBlock && fromBlockSystem.getPort(fromPortId).isConnected() && fromPortId == i) {

                        removeLine(fromBlockShape, fromPortId);

                        for (Connection connection : connections) {
                            if (connection.contains(fromBlockSystemId, fromPortId)) {
                                getBlockSystem(blockSystems,connection.getFromSystemId()).getPort(connection.getFromPortId()).setConnected(false);
                                getBlockSystem(blockSystems,connection.getToSystemId()).getPort(connection.getToPortId()).setConnected(false);
                                connections.remove(connection);
                                break;
                            }
                        }

                    }

                    if (!sameBlock && !alreadyConnected && sameModel && (sourceIsEntrance ^ targetIsEntrance)) {

                        Rectangle2D boundsA = fromBlockShape.getPortPath(fromPortId).getBounds2D();
                        Rectangle2D boundsB = targetBlock.getPortPath(i).getBounds2D();
                        double dx = boundsA.getCenterX() - boundsB.getCenterX();
                        double dy = boundsA.getCenterY() - boundsB.getCenterY();
                        if (remainingWireLength - Math.sqrt(dx * dx + dy * dy) < 0)return;

                        MainFrame.audioManager.playSoundEffect("Resources/connection.wav");

                        Wire line = new Wire(WireType.STRAIGHT, fromBlockShape, fromPortId, targetBlock, i, Color.CYAN);
                        lines.add(line);

                        connectionIdCounter++;
                        Connection connection;
                        if (blockSystems.get(fromBlockSystemId).getPort(fromPortId).getRole() == PortRole.OUT) {
                            connection = new Connection(connectionIdCounter, fromBlockSystemId, fromPortId, targetBlock.getBlockSystem().getId(), i);
                        } else {
                            connection = new Connection(connectionIdCounter, targetBlock.getBlockSystem().getId(), i, fromBlockSystemId, fromPortId);
                        }
                        connections.add(connection);

                        getBlockSystem(blockSystems,fromBlockSystemId).getPort(fromPortId).setConnected(true);
                        getBlockSystem(blockSystems,targetBlock.getBlockSystem().getId()).getPort(i).setConnected(true);

                    }
                }
            }
        }
    }

    private BlockSystem getBlockSystem (List<BlockSystem> blockSystems, int id) {
        return blockSystems.get(id);
    }
    private void removeLine (GameShape blockSystem, int port){
        for (Wire line : lines) {
            if ((line.getBlockA() == blockSystem && line.getPortA() == port) || (line.getBlockB() == blockSystem && line.getPortB() == port )){
                lines.remove(line);
                break;
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
        for (Wire line : lines) {
            Path2D.Float pathA = line.getBlockA().getPortPath(line.getPortA());
            Path2D.Float pathB = line.getBlockB().getPortPath(line.getPortB());
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

    public List<Wire> getlines() {
        return lines;
    }

    public boolean isDragging() {
        return dragging;
    }
}
