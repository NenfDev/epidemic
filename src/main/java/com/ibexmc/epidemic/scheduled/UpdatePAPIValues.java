package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.dependencies.papi.PAPIValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UpdatePAPIValues {
    /**
     * Called by the update papi scheduler - Used to update the PAPI values for each player
     */
    public static void Run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PAPIValues.update(player);
        }
    }
}
