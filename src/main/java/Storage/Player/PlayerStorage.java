package Storage.Player;

import Model.Player.Player;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerStorage {

    private static final String PLAYERS_FILE = "Resources/Saves/players.json";

    public static List<Player> loadAllPlayers() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(PLAYERS_FILE);

        if (!file.exists()) return new ArrayList<>();

        try {
            return mapper.readValue(file, new TypeReference<List<Player>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveAllPlayers(List<Player> players) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(PLAYERS_FILE), players);
            System.out.println("All players saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveNewPlayer(Player newPlayer) {
        List<Player> all = loadAllPlayers();
        for (Player p : all) {
            if (p.getUsername().equalsIgnoreCase(newPlayer.getUsername())) {
                System.out.println("Player already exists. Skipping save.");
                return;
            }
        }

        all.add(newPlayer);
        saveAllPlayers(all);
    }

    public static Player findPlayer(String username) {
        for (Player p : loadAllPlayers()) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return p;
            }
        }
        return null;
    }

    public static Player whoIsLogin() {
        for (Player p : loadAllPlayers()) {
            if (p.isLogin()) {
                return p;
            }
        }
        return null;
    }

    public static void setLogin(Player player) {
        List<Player> all = loadAllPlayers();
        for (Player p : all) {
            if (p.getUsername().equals(player.getUsername())) {
                p.setLogin(true);
                break;
            }
        }
        saveAllPlayers(all);
    }
}
