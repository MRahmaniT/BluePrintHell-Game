package Storage;

import Model.GameEntities.Connection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionStorage {
    private static final String CONNECTIONS_FILE = "connection.json";

    public static List<Connection> LoadConnections() {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CONNECTIONS_FILE);

        if (!file.exists()) return new ArrayList<>();

        try {
            return objectMapper.readValue(file, new TypeReference<List<Connection>>() {});
        } catch (IOException e) {
            System.out.println(1);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void SaveConnections(List<Connection> connections) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(CONNECTIONS_FILE), connections);
            //System.out.println("All Connections saved.");
        } catch (IOException e) {
            System.out.println(2);
            e.printStackTrace();
        }
    }
}
