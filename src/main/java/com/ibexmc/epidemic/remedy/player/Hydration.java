package com.ibexmc.epidemic.remedy.player;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.thirst.Thirst;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Hydration {
    /**
     * Adjusts a players hydration
     * @param player Player to adjust hydration for
     * @param amount Amount to adjust hydration by (-100 to 100)
     */
    public static void adjust(Player player, int amount) {
        Epidemic plugin = Epidemic.instance();
        if (plugin.config.isEnableThirst()) {
            if (plugin.data().getThirstBar().containsKey(player.getUniqueId())) {
                Thirst thirst = Epidemic.instance().data().getThirstBar().get(player.getUniqueId());
                if (thirst != null) {
                    // Used for both adding and removing hydration
                    double newThirstAmount = thirst.getThirstAmount() + amount;
                    if (newThirstAmount > 100) {
                        newThirstAmount = 100;
                    }
                    if (newThirstAmount < 0) {
                        newThirstAmount = 0;
                    }
                    thirst.setThirstAmount(newThirstAmount);
                    BossBar thirstBar = thirst.getThirstBar();
                    if (thirstBar != null) {
                        thirstBar.setProgress(thirst.getThirstAmount() / 100);
                        thirst.setThirstAmount(thirst.getThirstAmount());
                        thirst.setThirstBar(thirstBar);
                        Epidemic.instance().data().getThirstBar().put(player.getUniqueId(), thirst);
                    }
                }
            }
        }
    }
}
