package Controller.Systems;

import View.Render.GameShapes.GameShape;

import java.awt.*;
import java.awt.geom.Path2D;

public class BlockManager {
    private boolean dragging = false;
    private Point startPoint;
    private Point sourcePosition;
    private GameShape sourceBlock;

    public void handleMousePress(java.util.List<GameShape> blockShapes, int mouseX, int mouseY) {
        for (GameShape currentBlock : blockShapes) {
            Path2D.Float top = currentBlock.getTopPath();
            if (top != null && top.contains(mouseX, mouseY)) {
                dragging = true;
                sourceBlock = currentBlock;
                sourcePosition = sourceBlock.getPosition();
                startPoint = new Point(mouseX, mouseY);
                return;
            }
        }
    }

    public void handleMouseRelease(int mouseX, int mouseY) {
        if (!dragging) return;
        dragging = false;
        Point move = new Point(mouseX - startPoint.x + sourcePosition.x,
                                mouseY - startPoint.y + sourcePosition.y);
        sourceBlock.setPosition(move);
    }

    public void drawDrag(int mouseX, int mouseY) {
        if (!dragging || startPoint == null) return;
        Point move = new Point(mouseX - startPoint.x + sourcePosition.x,
                mouseY - startPoint.y + sourcePosition.y);
        sourceBlock.setPosition(move);
    }

    public boolean isDragging() {
        return dragging;
    }
}
