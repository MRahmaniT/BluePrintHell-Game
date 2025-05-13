package GameEnvironment;

import Shape.GameShape;

import java.awt.*;
import java.util.List;

public class ChangeBlocksLight {
    public static void changeBlocksLight (List<GameShape> blockShapes) {
        for (GameShape gameShape : blockShapes) {
            boolean checkPortsForLight;
            for (int i = 1; i < 5; i++) {
                checkPortsForLight = gameShape.getConnection(i);
                if (!checkPortsForLight) {
                    gameShape.setColor(Color.RED);
                    return;
                }
            }
            gameShape.setColor(Color.cyan);
        }
    }
}
