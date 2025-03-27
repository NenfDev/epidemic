package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileFunctions {
    /**
     * Checks if a file exists
     * @param filePath Full file path to check
     * @return If true, file exists
     */
    private static boolean fileExists(String filePath) {
        File myfile = new File(filePath);
        return myfile.exists();
    }

    /**
     * Saves a resource file if it does not exist
     * @param filePath Full file path to check
     */
    public static void saveResourceIfNotExists(String filePath) {
        if (!fileExists(filePath)) {
            Epidemic.instance().saveResource(filePath, false);
        }
    }

    /**
     * Gets the config.yml file - If not found, will try to create from resources, if not found there
     * will create a new blank file
     * @return Config File object
     */
    public static File getConfigYml() {
        File configFile = new File(
                Epidemic.instance().getDataFolder() +
                        File.separator +
                        "config.yml"
        );
        if (!configFile.exists()) {
            if (Epidemic.instance().getResource("config.yml") != null) {
                Epidemic.instance().saveResource("config.yml", false);
            } else {
                try {
                    configFile.createNewFile();
                    FileConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
                    try {
                        configuration.save(configFile);
                    } catch (IOException e) {
                        Error.save(
                                "gCY1",
                                "FileFunctions",
                                "getConfigYml",
                                "Unexpected Error getting Config",
                                e.getMessage(),
                                Error.Severity.URGENT,
                                e.getStackTrace()
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configFile;
    }
}
