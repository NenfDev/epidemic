package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;

public class Hurt {
    /**
     * Applies damage to a player based on affliction
     * @param player Player to damage
     * @param afflicted Affliction causing damage
     */
    public static void damagePlayer(Player player, Afflicted afflicted) {
        if (player != null)
        {
            Logging.debug(
                    "Hurt",
                    "damagePlayer(Player, Afflicted)",
                    "Player: " + player.getDisplayName() + ", " +
                            "Affliction: " + afflicted
            );
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                double damageAmount = afflicted.getAilment().getDamage();
                hurt(player, damageAmount, afflicted.getAilment().isFatal());
            } else {
                Logging.debug(
                        "Hurt",
                        "damagePlayer(Player, Afflicted)",
                        "Player: " + player.getDisplayName() + " has invincible permission or is in creative mode"
                );
            }
        }
    }

    /**
     * Hurts a player
     * @param player Player to hurt
     * @param amount Amount of damage to deal (0-20)
     * @param fatal If true, player health can drop to 0
     */
    public static void hurt(Player player, double amount, boolean fatal) {
        Logging.debug(
                "Hurt",
                "hurt(Player, double, boolean)",
                "Player: " + player.getDisplayName() + ", " +
                        "Amount: " + amount + ", Fatal?: " + fatal
        );
        try {
            if (amount <= 0 || amount > 20) {
                // Exit out if the damage amount is out of range
                Logging.debug(
                        "Hurt",
                        "hurt(Player, double, boolean)",
                        "Not hurting player, amount is out of range"
                );
                return;
            }
            if (!fatal) {
                if (player.getHealth() <= amount) {
                    player.setHealth(Math.max(0.5, player.getHealth() - 0.5)); // Reduce slowly but don't kill
                    return;
                }
            }
            player.damage(amount);
        }
        catch (Exception ex) {
            Logging.debug(
                    "Hurt",
                    "hurt(Player, double, boolean)",
                    "Unexpected error - " + ex.getMessage()
            );
            return;
        }
    }

}
