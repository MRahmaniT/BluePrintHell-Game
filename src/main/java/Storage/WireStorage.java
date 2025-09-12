package Storage;

import Model.GameEntities.Connection;
import Model.GameEntities.Wire.Wire;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WireStorage {
    private static final String WIRES_FILE = "Resources/Saves/wire.json";

    public static List<Wire> LoadWires() {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(WIRES_FILE);

        if (!file.exists()) return new ArrayList<>();

        try {
            return objectMapper.readValue(file, new TypeReference<List<Wire>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void SaveWires(List<Wire> wires) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(WIRES_FILE), wires);
            //System.out.println("All Wires saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
