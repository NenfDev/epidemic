package com.ibexmc.epidemic.symptoms;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.NumberFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.bukkit.entity.Player;

public class SuddenDeath {
    /**
     * Kills the player
     * @param player Player to kill
     * @param message Message to send to the player
     */
    public static void kill(Player player, String message) {
        if (!StringFunctions.isNullOrEmpty(message)) {
            SendMessage.Player(
                "na",
                message,
                player,
                true,
                null
                    );
        }
        player.setHealth(0);
    }

    /**
     * Checks if sudden death is appropriate, if so, kills the player
     * @param player Player to kill
     * @param afflicted Affliction to check
     */
    public static void applySuddenDeath(Player player, Afflicted afflicted) {
        if (player != null && afflicted != null) {
            if (afflicted.getAilment() != null) {
                int suddenDeathChance = afflicted.getAilment().getSuddenDeathChance();
                if (suddenDeathChance > 0) {
                    int random = NumberFunctions.random(1000);
                    Logging.debug(
                            "SuddenDeath",
                            "applySuddenDeath",
                            "Ailment = " + afflicted.getAilment().getDisplayName() +
                                    " Random chance = " + random +
                                    " Ailment Sudden Death Chance: " + suddenDeathChance
                    );
                    if (!afflicted.getAilment().invincible(player)) {
                        if (random < suddenDeathChance) {
                            kill(player, afflicted.getAilment().suddenDeathMessage());
                        }
                    }
                }
            }
        }
    }
}
