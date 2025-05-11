package GameEnvironment;

import Shape.GameShape;
import Shape.BlockShape2Stairs;
import Shape.BlockShapeStart;
import Shape.BlockShapeEnd;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuildStage1 {
    public static void buildStage1(int screenSizeX, List<GameShape> blockShapes){

        //Block Start
        ArrayList<Integer> forDrawBlockStart = new ArrayList<>();
        forDrawBlockStart.add(0);
        forDrawBlockStart.add(0);
        forDrawBlockStart.add(0);
        forDrawBlockStart.add(1);
        BlockShapeStart blockStart = new BlockShapeStart(-300, -100,
                0.1f * screenSizeX, 0.6f * 0.1f * screenSizeX, Color.RED,
                forDrawBlockStart);
        blockShapes.add(blockStart);

        //Block 1
        ArrayList<Integer> forDrawBlock1 = new ArrayList<>();
        ArrayList<Boolean> forPortBlock1 = new ArrayList<>();
        forDrawBlock1.add(1);
        forPortBlock1.add(false);
        forDrawBlock1.add(0);
        forPortBlock1.add(false);
        forDrawBlock1.add(2);
        forPortBlock1.add(false);
        forDrawBlock1.add(1);
        forPortBlock1.add(false);

        BlockShape2Stairs block1 = new BlockShape2Stairs(-100, -100,
                0.1f * screenSizeX, 0.1f * screenSizeX, Color.RED,
                forDrawBlock1, forPortBlock1);
        blockShapes.add(block1);

        //Block 2
        ArrayList<Integer> forDrawBlock2 = new ArrayList<>();
        ArrayList<Boolean> forPortBlock2 = new ArrayList<>();
        forDrawBlock2.add(2);
        forPortBlock2.add(false);
        forDrawBlock2.add(1);
        forPortBlock2.add(false);
        forDrawBlock2.add(0);
        forPortBlock2.add(false);
        forDrawBlock2.add(1);
        forPortBlock2.add(false);
        BlockShape2Stairs block2 = new BlockShape2Stairs(+100,+100,
                0.1f*screenSizeX,0.1f*screenSizeX, Color.RED,
                forDrawBlock2, forPortBlock2);
        blockShapes.add(block2);

        //Block End
        ArrayList<Integer> forDrawBlockEnd = new ArrayList<>();
        forDrawBlockEnd.add(1);
        BlockShapeEnd blockEnd = new BlockShapeEnd(+300, +100,
                0.1f * screenSizeX, 0.6f * 0.1f * screenSizeX, Color.RED,
                forDrawBlockEnd);
        blockShapes.add(blockEnd);
    }
}
