package com.ibexmc.epidemic.remedy.player;

import com.ibexmc.epidemic.util.Logging;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class Health {
    /**
     * Modifies a players health
     * @param player Player to modify health of
     * @param amount Amount to change (-20 to 20)
     * @param fatal If true, health can drop to 0
     */
    public static void adjust(Player player, double amount, boolean fatal) {
        try {
            if (amount > 0) {
                // increase health
                AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                double maxHealth = maxHealthAttr.getValue();
                if (player.getHealth() + amount >= maxHealth) {
                    player.setHealth(maxHealth);
                } else {
                    player.setHealth(player.getHealth() + amount);
                }
            } else {
                // decrease health
                if (player.getHealth() <= amount) {
                    if (!fatal) {
                        return;
                    }
                }
                player.damage(amount);
            }
        } catch (Exception ex) {
            Logging.debug("Health", "adjust", "Unexpected error setting health: " + ex.getMessage());
        }
    }
}
