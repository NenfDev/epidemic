package com.ibexmc.epidemic.remedy.player;

import org.bukkit.entity.Player;

public class Hunger {
    /**
     * Adjusts a players hunger
     * @param player Player to adjust hunger for
     * @param amount Amount to adjust hunger (0 to 20)
     */
    public static void adjust(Player player, int amount) {
        if (amount > 0) {
            // Increase food level
            player.setFoodLevel(Math.min(player.getFoodLevel() + amount, 20));
        } else {
            // Decrease food level
            player.setFoodLevel(Math.max(player.getFoodLevel() - amount, 0));
        }
    }

    /**
     * Sets a players hunger bar
     * @param player Player to set
     * @param amount Amount to set hunger to (0 to 20)
     */
    public static void set(Player player, int amount) {
        if (amount < 0) {
            amount = 0;
        }
        if (amount > 20) {
            amount = 20;
        }
        player.setFoodLevel(amount);
    }
}
