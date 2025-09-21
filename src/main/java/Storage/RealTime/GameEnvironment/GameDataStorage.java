package Storage.RealTime.GameEnvironment;

import MVC.Model.GameEntities.GameData;
import Storage.RealTime.StorageLocks;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameDataStorage {
    private static final String GAMEDATA_FILE = "Resources/Saves/Realtime/gameData.json";

    public static GameData LoadGameData() {
        synchronized (StorageLocks.IOLock) {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(GAMEDATA_FILE);

            if (!file.exists()) return new GameData();

            try {
                return objectMapper.readValue(file, new TypeReference<GameData>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
                return new GameData();
            }
        }
    }

    public static void SaveGameData(GameData gameData) {
        synchronized (StorageLocks.IOLock) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(GAMEDATA_FILE), gameData);
                //System.out.println("All Connections saved.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
