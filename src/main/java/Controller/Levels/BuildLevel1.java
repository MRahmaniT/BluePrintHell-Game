package Controller.Levels;

import Model.Enums.BlockSystemType;
import Model.Enums.PortRole;
import Model.Enums.PortType;
import Model.GameEntities.BlockSystem;
import Model.GameEntities.Port;
import Storage.RealTime.GameEnvironment.BlockSystemStorage;
import View.Render.GameShapes.System.EndSystem;
import View.Render.GameShapes.System.GameShape;
import View.Render.GameShapes.System.StartSystem;
import View.Render.GameShapes.System.TwoStairsSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildLevel1 {
    public static void buildLevel1(int screenSizeX, List<GameShape> blockShapes){

        List<BlockSystem> blockSystems = BlockSystemStorage.LoadBlockSystems();
        BlockSystem blockSystem;
        GameShape block;
        Port port1,port2,port3,port4;
        ArrayList<Port> ports;
        int blockSystemId = blockSystems.size();

        if (!blockSystems.isEmpty()) {
            //Block Start
            blockSystem = blockSystems.get(0);
            block = new StartSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);

            //Block 1
            blockSystem = blockSystems.get(1);
            block = new TwoStairsSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);

            //Block 2
            blockSystem = blockSystems.get(2);
            block = new TwoStairsSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);

            //Block End
            blockSystem = blockSystems.get(3);
            block = new EndSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);
        } else {
            //Block Start
            port1 = new Port(1, blockSystemId);
            port2 = new Port(2, blockSystemId);
            port3 = new Port(3, blockSystemId, PortRole.OUT, PortType.MESSENGER_2);
            port4 = new Port(4, blockSystemId);
            ports = new ArrayList<>(Arrays.asList(port1, port2, port3, port4));

            blockSystem = new BlockSystem(blockSystemId, BlockSystemType.START, ports, -300, -100);
            blockSystems.add(blockSystem);

            block = new StartSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);

            blockSystemId++;

            //Block 1
            port1 = new Port(1, blockSystemId, PortRole.IN, PortType.MESSENGER_2);
            port2 = new Port(2, blockSystemId);
            port3 = new Port(3, blockSystemId, PortRole.OUT, PortType.MESSENGER_3);
            port4 = new Port(4, blockSystemId, PortRole.OUT, PortType.MESSENGER_2);
            ports = new ArrayList<>(Arrays.asList(port1, port2, port3, port4));

            blockSystem = new BlockSystem(blockSystemId, BlockSystemType.PROCESSOR, ports, -100, -100);
            blockSystems.add(blockSystem);

            block = new TwoStairsSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);

            blockSystemId++;

            //Block 2
            port1 = new Port(1, blockSystemId, PortRole.IN, PortType.MESSENGER_3);
            port2 = new Port(2, blockSystemId, PortRole.IN, PortType.MESSENGER_2);
            port3 = new Port(3, blockSystemId);
            port4 = new Port(4, blockSystemId, PortRole.OUT, PortType.MESSENGER_2);
            ports = new ArrayList<>(Arrays.asList(port1, port2, port3, port4));

            blockSystem = new BlockSystem(blockSystemId, BlockSystemType.PROCESSOR, ports, 100, 100);
            blockSystems.add(blockSystem);

            block = new TwoStairsSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);

            blockSystemId++;

            //Block End
            port1 = new Port(1, blockSystemId, PortRole.IN, PortType.MESSENGER_2);
            port2 = new Port(2, blockSystemId);
            port3 = new Port(3, blockSystemId);
            port4 = new Port(4, blockSystemId);
            ports = new ArrayList<>(Arrays.asList(port1, port2, port3, port4));

            blockSystem = new BlockSystem(blockSystemId, BlockSystemType.START, ports, 300, 100);
            blockSystem.setCapacity(100);
            blockSystems.add(blockSystem);

            block = new EndSystem(blockSystem,0.1f * screenSizeX, 0.1f * screenSizeX);
            blockShapes.add(block);
        }
        BlockSystemStorage.SaveBlockSystems(blockSystems);
    }
}
