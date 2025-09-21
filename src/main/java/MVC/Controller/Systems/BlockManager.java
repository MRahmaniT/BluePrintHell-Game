package MVC.Controller.Systems;

import MVC.Controller.GameLogic;
import MVC.Model.GameEntities.BlockSystem;
import Storage.Facade.StorageFacade;
import MVC.View.GamePage.GamePanel;
import MVC.View.Render.GameShapes.System.GameShape;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class BlockManager {
    private boolean dragging = false;
    private Point startPoint;
    private Point sourcePosition;
    private GameShape sourceBlock;
    private int blockId;

    public BlockManager () {
    }

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
        List<BlockSystem> blockSystems = StorageFacade.loadBlockSystems();
        if (!dragging) return;
        dragging = false;
        Point move = new Point(mouseX - startPoint.x + sourcePosition.x,
                                mouseY - startPoint.y + sourcePosition.y);
        sourceBlock.setPosition(move);
        blockSystems.get(blockId).setX(move.x);
        blockSystems.get(blockId).setY(move.y);

        if (GameLogic.notGoodPosition) {
            Point moveBack = new Point(sourcePosition.x, sourcePosition.y);
            sourceBlock.setPosition(moveBack);
            blockSystems.get(blockId).setX(moveBack.x);
            blockSystems.get(blockId).setY(moveBack.y);
        }

        StorageFacade.saveBlockSystems(blockSystems);

        GameLogic.youCanMoveBlock = false;
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
