package Storage.Snapshots;

import Model.GameEntities.Packet;
import Storage.StorageLocks;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacketSnapshots {
    private static final String PACKETS_SNAPSHOTS_FILE = "Resources/Saves/Snapshot/packets@";

    public static List<Packet> LoadPacketSnapshots(double time) {
        synchronized (StorageLocks.IOLock) {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(PACKETS_SNAPSHOTS_FILE + Math.round(time * 100.0) / 100.0 + ".json");

            if (!file.exists()) return new ArrayList<>();

            try {
                return objectMapper.readValue(file, new TypeReference<List<Packet>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    public static synchronized void SavePacketSnapshot(List<Packet> packets, double time) {
        synchronized (StorageLocks.IOLock) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(PACKETS_SNAPSHOTS_FILE + Math.round(time * 100.0) / 100.0 + ".json"), packets);
                //System.out.println("All Packets saved.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
