package com.ibexmc.epidemic.server;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ServerManager {

    private static File getFile() {
        Epidemic instance = Epidemic.instance();
        File dir = new File(instance.getDataFolder() + File.separator + "config");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File serverFile = new File(
                Epidemic.instance().getDataFolder() +
                        File.separator +
                        "config" + File.separator +
                        "server.yml"
        );
        if (!serverFile.exists()) {
            try {
                serverFile.createNewFile();
            } catch (IOException e) {
                // Error
            }
        }
        return serverFile;
    }

    public static void load() {
        File serverFile = getFile();
        if (serverFile.exists()) {
            ConfigParser configParser = new ConfigParser(Epidemic.instance(), serverFile);
            ConfigParser.ConfigReturn crDay = configParser.getIntValue(
                    "day",
                    0,
                    false
            );
            if (crDay.isSuccess()) {
                String worldName = "";
                ConfigParser.ConfigReturn crWorld = configParser.getStringValue("main_world", "world", false);
                if (crWorld.isSuccess()) {
                    worldName = crWorld.getString();
                }
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    long time = world.getTime();
                    Logging.debug("ServerManager", "load", "Setting day to " + crDay.getInt() + " and current time to " + time);
                    Epidemic.instance().gameData().day().set(time, crDay.getInt());
                }
            }
        }
    }
    public static void save() {
        Epidemic instance = Epidemic.instance();
        File serverFile = getFile();

        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(serverFile);
        fileConfig.set("day", instance.gameData().day().get());
        fileConfig.set("main_world", instance.gameData().day().getWorld().getName());
        try {
            fileConfig.save(serverFile);
            if (serverFile.length() == 0) {
                //Logging.log("Blank file found", customYml.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
