package Controller.Systems;

import Model.GameEntities.BlockSystem;
import View.Render.GameShapes.GameShape;

import java.awt.*;
import java.util.List;

public class ChangeBlocksLight {
    public static void changeBlocksLight (List<BlockSystem> blockSystems, List<GameShape> blockShapes) {
        for (int i = 0; i < blockSystems.size(); i++){
            boolean allConnected = true;
            for (int j = 1; j <= 4; j++) {
                if (!blockSystems.get(i).getPort(j).isConnected()) {
                    allConnected = false;
                    break;
                }
            }
            blockShapes.get(i).setColor(allConnected ? Color.CYAN : Color.RED);
        }
    }
}
