package GameEnvironment;

import GameShapes.GameShape;
import GameShapes.BlockShape2Stairs;
import GameShapes.BlockShapeStart;
import GameShapes.BlockShapeEnd;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuildLevel1 {
    public static void buildStage1(int screenSizeX, List<GameShape> blockShapes){

        //Block Start
        ArrayList<Integer> forDrawBlockStart = new ArrayList<>();
        ArrayList<Boolean> forPortBlockStart = new ArrayList<>();
        ArrayList<Boolean> forPortPacketBlockStart = new ArrayList<>();
        forDrawBlockStart.add(0);
        forPortBlockStart.add(true);
        forPortPacketBlockStart.add(true);
        forDrawBlockStart.add(0);
        forPortBlockStart.add(true);
        forPortPacketBlockStart.add(true);
        forDrawBlockStart.add(0);
        forPortBlockStart.add(true);
        forPortPacketBlockStart.add(true);
        forDrawBlockStart.add(1);
        forPortBlockStart.add(false);
        forPortPacketBlockStart.add(false);
        BlockShapeStart blockStart = new BlockShapeStart(-300, -100,
                0.1f * screenSizeX, 0.6f * 0.1f * screenSizeX, Color.RED,
                forDrawBlockStart, forPortBlockStart, forPortPacketBlockStart);
        blockShapes.add(blockStart);

        //Block 1
        ArrayList<Integer> forDrawBlock1 = new ArrayList<>();
        ArrayList<Boolean> forPortBlock1 = new ArrayList<>();
        ArrayList<Boolean> forPortPacketBlock1 = new ArrayList<>();
        forDrawBlock1.add(1);
        forPortBlock1.add(false);
        forPortPacketBlock1.add(false);
        forDrawBlock1.add(0);
        forPortBlock1.add(true);
        forPortPacketBlock1.add(true);
        forDrawBlock1.add(2);
        forPortBlock1.add(false);
        forPortPacketBlock1.add(false);
        forDrawBlock1.add(1);
        forPortBlock1.add(false);
        forPortPacketBlock1.add(false);

        BlockShape2Stairs block1 = new BlockShape2Stairs(-100, -100,
                0.1f * screenSizeX, 0.1f * screenSizeX, Color.RED,
                forDrawBlock1, forPortBlock1);
        blockShapes.add(block1);

        //Block 2
        ArrayList<Integer> forDrawBlock2 = new ArrayList<>();
        ArrayList<Boolean> forPortBlock2 = new ArrayList<>();
        ArrayList<Boolean> forPortPacketBlock2 = new ArrayList<>();
        forDrawBlock2.add(2);
        forPortBlock2.add(false);
        forPortPacketBlock2.add(false);
        forDrawBlock2.add(1);
        forPortBlock2.add(false);
        forPortPacketBlock2.add(false);
        forDrawBlock2.add(0);
        forPortBlock2.add(true);
        forPortPacketBlock2.add(true);
        forDrawBlock2.add(1);
        forPortBlock2.add(false);
        forPortPacketBlock2.add(false);
        BlockShape2Stairs block2 = new BlockShape2Stairs(+100,+100,
                0.1f*screenSizeX,0.1f*screenSizeX, Color.RED,
                forDrawBlock2, forPortBlock2);
        blockShapes.add(block2);

        //Block End
        ArrayList<Integer> forDrawBlockEnd = new ArrayList<>();
        ArrayList<Boolean> forPortBlockEnd = new ArrayList<>();
        ArrayList<Boolean> forPortPacketBlockEnd = new ArrayList<>();
        forDrawBlockEnd.add(1);
        forPortBlockEnd.add(false);
        forPortPacketBlockEnd.add(false);
        forDrawBlockEnd.add(0);
        forPortBlockEnd.add(true);
        forPortPacketBlockEnd.add(true);
        forDrawBlockEnd.add(0);
        forPortBlockEnd.add(true);
        forPortPacketBlockEnd.add(true);
        forDrawBlockEnd.add(0);
        forPortBlockEnd.add(true);
        forPortPacketBlockEnd.add(true);
        BlockShapeEnd blockEnd = new BlockShapeEnd(+300, +100,
                0.1f * screenSizeX, 0.6f * 0.1f * screenSizeX, Color.RED,
                forDrawBlockEnd, forPortBlockEnd, forPortPacketBlockEnd);
        blockShapes.add(blockEnd);
    }
}
