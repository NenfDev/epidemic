package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.temperature.Temperature;

public class CheckTemp {
    /**
     * Called by the temperature scheduler.  Modifies a players temperature based on their surroundings on
     * a timed basis
     */
    public static void Run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null) {
                Logging.debug("CheckTemp", "Run", "Applying Temperature for " + player.getName());
                Temperature temp = new Temperature(player);
                temp.applyTemperature();
            }
        }
    }
}
