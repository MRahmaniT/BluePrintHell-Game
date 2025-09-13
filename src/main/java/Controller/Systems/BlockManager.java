package Controller.Systems;

import Model.GameEntities.BlockSystem;
import Storage.BlockSystemStorage;
import View.Render.GameShapes.System.GameShape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class BlockManager {
    private boolean dragging = false;
    private Point startPoint;
    private Point sourcePosition;
    private GameShape sourceBlock;
    private int blockId;

    public void handleMousePress(List<GameShape> blockShapes, int mouseX, int mouseY) {
        for (GameShape currentBlock : blockShapes) {
            Path2D.Float top = currentBlock.getTopPath();
            if (top != null && top.contains(mouseX, mouseY)) {
                dragging = true;
                sourceBlock = currentBlock;
                sourcePosition = sourceBlock.getPosition();
                startPoint = new Point(mouseX, mouseY);
                blockId = currentBlock.getBlockSystem().getId();
                return;
            }
        }
    }

    public void handleMouseRelease(int mouseX, int mouseY) {
        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        if (!dragging) return;
        dragging = false;
        Point move = new Point(mouseX - startPoint.x + sourcePosition.x,
                                mouseY - startPoint.y + sourcePosition.y);
        sourceBlock.setPosition(move);
        blockSystems.get(blockId).setX(move.x);
        blockSystems.get(blockId).setY(move.y);

        BlockSystemStorage.SaveBlockSystems(blockSystems);
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
