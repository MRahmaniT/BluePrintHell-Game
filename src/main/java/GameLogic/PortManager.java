package GameLogic;

import Shape.GameShape;
import Shape.LineShape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class PortManager {
    private boolean dragging = false;
    private Point startPoint;
    private GameShape sourceBlock;
    private int sourcePort;
    private boolean sourceIsEntrance;

    private final ArrayList<LineShape> lines = new ArrayList<>();
    private final Map<Point, LineShape> lineByStartPoint = new HashMap<>();
    private final Map<Point, Point> startPointByEndPoint = new HashMap<>();
    private final Map<Point, Point> endPointByStartPoint = new HashMap<>();

    public Map.Entry<GameShape, Integer> findPort(List<GameShape> blockShapes, Point point) {
        for (GameShape block : blockShapes) {
            for (int i = 1; i <= 4; i++) {
                Path2D.Float port = block.getPortPath(i);
                if (port != null) {
                    Rectangle2D bounds = port.getBounds2D();
                    Point portPoint = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
                    if (portPoint.equals(point)){
                           return Map.entry(block, i);
                    }
                }
            }
        }
        return null;
    }

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

                    if (sameBlock && sourceBlock.getConnection(i)) {

                        Rectangle2D bounds = port.getBounds2D();
                        Point pointToRemove = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
                        Point startOfLine = null;
                        Point endOfLine = null;

                        if(endPointByStartPoint.get(pointToRemove) != null &&
                                startPointByEndPoint.get(pointToRemove) == null){

                            startOfLine = pointToRemove;
                            endOfLine = endPointByStartPoint.get(pointToRemove);

                        } else if(startPointByEndPoint.get(pointToRemove) != null &&
                                endPointByStartPoint.get(pointToRemove) == null){

                            endOfLine = pointToRemove;
                            startOfLine = startPointByEndPoint.get(pointToRemove);

                        }
                        if (startOfLine != null){
                            LineShape lineToRemove = lineByStartPoint.get(startOfLine);
                            lines.remove(lineToRemove);
                            lineByStartPoint.remove(pointToRemove);
                            startPointByEndPoint.remove(endOfLine);
                            endPointByStartPoint.remove(startOfLine);

                            Map.Entry<GameShape, Integer> endResult = findPort(blockShapes, endOfLine);
                            Map.Entry<GameShape, Integer> startResult = findPort(blockShapes, startOfLine);
                            endResult.getKey().setConnection(endResult.getValue(), false);
                            startResult.getKey().setConnection(startResult.getValue(), false);
                        }
                        return;
                    }

                    if (!sameBlock && !alreadyConnected && sameModel && (sourceIsEntrance ^ targetIsEntrance)) {
                        Rectangle2D bounds = port.getBounds2D();
                        Point endPoint = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());

                        LineShape line = new LineShape(startPoint.x, startPoint.y, endPoint.x, endPoint.y, Color.CYAN);
                        lines.add(line);
                        lineByStartPoint.put(startPoint, line);
                        startPointByEndPoint.put(endPoint, startPoint);
                        endPointByStartPoint.put(startPoint, endPoint);

                        sourceBlock.setConnection(sourcePort, true);
                        targetBlock.setConnection(i, true);
                    }
                }
            }
        }
    }

    public void drawDrag(Graphics2D g2d, Point mousePoint) {
        if (!dragging || startPoint == null || mousePoint == null) return;
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawLine(startPoint.x, startPoint.y, mousePoint.x, mousePoint.y);
    }

    public List<LineShape> getLines() {
        return lines;
    }

    public boolean isDragging() {
        return dragging;
    }
}
