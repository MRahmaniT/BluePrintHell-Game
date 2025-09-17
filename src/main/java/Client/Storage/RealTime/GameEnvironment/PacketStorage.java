package Client.Storage.RealTime.GameEnvironment;

import Client.Model.GameEntities.Packet;
import Client.Storage.RealTime.StorageLocks;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacketStorage {
    private static String PACKETS_FILE = "Resources/Saves/Realtime/packet.json";

    public static List<Packet> LoadPackets() {
        synchronized (StorageLocks.IOLock) {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(PACKETS_FILE);

            if (!file.exists()) return new ArrayList<>();

            try {
                return objectMapper.readValue(file, new TypeReference<List<Packet>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    public static synchronized void SavePackets(List<Packet> packets) {
        synchronized (StorageLocks.IOLock) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(PACKETS_FILE), packets);
                //System.out.println("All Packets saved.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
