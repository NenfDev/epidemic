package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.thirst.Thirst;

public class ApplyThirst {
    /**
     * Called by the thirst scheduler.  Modifies a players thirst and applies thirst symptoms
     */
    public static void Run() {
        if (!Epidemic.instance().config.isEnableThirst()) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null) {
                boolean apply = true;

                Material m = player.getLocation().getBlock().getType();
                if (m == Material.WATER) {
                    apply = false;
                }
                if (player.getLocation().getWorld().hasStorm()) {
                    apply = false;
                }
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                    apply = false;
                }
                if (Permission.inBypass(player)) {
                    apply = false;
                }
                if (apply) {
                    Thirst.applyThirst(player);
                }
            }
        }
    }
}
