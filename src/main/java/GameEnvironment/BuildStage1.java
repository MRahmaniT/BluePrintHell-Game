package GameEnvironment;

import Shape.GameShape;
import Shape.BlockShape2Stairs;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuildStage1 {
    public static void buildStage1(int screenSizeX, int screenSizeY,
                                       BlockShape2Stairs blockShape2Stairs, List<GameShape> blockShapes){
        ArrayList<Integer> forDrawBlocks = new ArrayList<>();
        forDrawBlocks.add(1);
        forDrawBlocks.add(0);
        forDrawBlocks.add(2);
        forDrawBlocks.add(1);
        blockShape2Stairs = new BlockShape2Stairs(0,0,0.1f*screenSizeX,0.1f*screenSizeX, Color.RED, forDrawBlocks);
        blockShapes.add(blockShape2Stairs);
    }
}
