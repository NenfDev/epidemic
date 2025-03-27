package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;

import java.io.File;
import java.util.UUID;

public class FileUtils {
    /**
     * Gets a file by player unique identifier.  This is the data file for the player
     * @param uuid Unique identifier of a player
     * @return File object of the data file for the player
     */
    public static File getFileByUUID(UUID uuid) {
        return new File(
                Epidemic.instance().getDataFolder() +
                        File.separator +
                        "Data" +
                        File.separator +
                        uuid.toString() +
                        ".yml"
        );
    }
}
