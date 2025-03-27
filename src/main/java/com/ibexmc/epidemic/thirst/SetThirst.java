package com.ibexmc.epidemic.thirst;

import com.ibexmc.epidemic.Epidemic;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class SetThirst {
    /**
     * Sets the thirst for a player
     * @param player Player to set
     * @param amount Amount to set to
     */
    public static void set(Player player, double amount) {
        Thirst thirst = Epidemic.instance().data().getThirstBar().get(player.getUniqueId());
        if (thirst != null) {
            thirst.setThirstAmount(amount);

            if (Epidemic.instance().config.useThirstBar()) {
                BossBar thirstBar = thirst.getThirstBar();
                thirstBar.setProgress(thirst.getThirstAmount() / 100);
                thirst.setThirstAmount(thirst.getThirstAmount());
                thirst.setThirstBar(thirstBar);
                Epidemic.instance().data().setThirstBar(player.getUniqueId(), thirst);
            }

        }
    }

    /**
     * Adds hydration to a player
     * @param player Player to add hydration to
     * @param amount Amount to add
     */
    public static void add(Player player, double amount) {
        Thirst thirst = Epidemic.instance().data().getThirstBar().get(player.getUniqueId());
        double newAmount = 0;
        if (thirst != null) {
            newAmount = thirst.getThirstAmount() + amount;
        }
        if (newAmount < 0) {
            newAmount = 0;
        }
        if (newAmount > 100) {
            newAmount = 100;
        }
        set(player, newAmount);
    }
}
