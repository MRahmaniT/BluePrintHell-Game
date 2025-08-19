package Controller.Systems;

import View.Render.GameShapes.GameShape;

import java.awt.*;
import java.util.List;

public class ChangeBlocksLight {
    public static void changeBlocksLight (List<GameShape> blockShapes) {
        for (GameShape gameShape : blockShapes) {
            boolean allConnected = true;
            for (int i = 1; i <= 4; i++) {
                if (!gameShape.getConnection(i)) {
                    allConnected = false;
                    break;
                }
            }
            gameShape.setColor(allConnected ? Color.CYAN : Color.RED);
        }
    }
}
