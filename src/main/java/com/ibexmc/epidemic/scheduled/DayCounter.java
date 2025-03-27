package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.server.ServerManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class DayCounter {
    /**
     * Called by the day counter scheduler.  Applies ailments to players on a timed basis
     */
    public static void Run() {
        Epidemic instance = Epidemic.instance();
        World world = instance.gameData().day().getWorld();
        if (world != null) {
            long time = world.getTime();
            if (instance.gameData().day().isNewDay(time)) {
                int day = instance.gameData().day().get();
                Logging.debug("DayCounter", "Run", "It's a new day! - Welcome to day " + day);
                if (day - instance.gameData().day().getLastSavedDay() >= 1) {
                    // If we've gone 3 days, save the server info
                    Logging.debug("DayCounter", "Run", "Saving server information..");
                    ServerManager.save();
                }
            } else {
                Logging.debug("DayCounter", "Run", "Not a new day: " + time);
            }
        }
    }
}
