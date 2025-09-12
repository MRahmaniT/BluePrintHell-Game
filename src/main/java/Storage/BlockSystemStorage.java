package Storage;

import Model.GameEntities.BlockSystem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockSystemStorage {
    private static final String BLOCK_SYSTEMS_FILE = "blockSystem.json";

    public static List<BlockSystem> LoadBlockSystems() {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(BLOCK_SYSTEMS_FILE);

        if (!file.exists()) return new ArrayList<>();

        try {
            return objectMapper.readValue(file, new TypeReference<List<BlockSystem>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void SaveBlockSystems(List<BlockSystem> blockSystems) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BLOCK_SYSTEMS_FILE), blockSystems);
            //System.out.println("All BlockSystems saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
