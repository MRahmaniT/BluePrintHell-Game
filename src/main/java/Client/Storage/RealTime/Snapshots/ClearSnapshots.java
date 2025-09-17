package Client.Storage.RealTime.Snapshots;

import java.io.File;

public class ClearSnapshots {

    public static void clearSnapshotFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        f.delete();
                    }
                }
            }
        }
    }
}

