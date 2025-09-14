package Storage.RealTime.GameEnvironment;

import java.io.File;

public class ClearSaves {

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

