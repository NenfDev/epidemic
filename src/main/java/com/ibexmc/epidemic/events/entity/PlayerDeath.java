package com.ibexmc.epidemic.events.entity;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    /**
     * Called when a player dies.  Used for clearing player afflictions and lowering immunities.
     * @param event PlayerDeathEvent
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Logging.debug(
                "PlayerDeath",
                "onPlayerDeath",
                "Event Fired"
        );
        try {
            Player player = event.getEntity();
            if (player != null) {
                Logging.debug(
                        "PlayerDeath",
                        "onPlayerDeath",
                        "Entity Death - " + player.getDisplayName()
                );
                if (Epidemic.instance().config.isClearAilmentOnDeath()) {
                    Logging.debug(
                            "PlayerDeath",
                            "onPlayerDeath",
                            "Clear ailment on death = true"
                    );
                    //Epidemic.Affliction.remove(player.getUniqueId());
                    Epidemic.instance().data().clearPlayerAfflictions(player.getUniqueId());
                } else {
                    Logging.debug(
                            "PlayerDeath",
                            "onPlayerDeath",
                            "Clear ailment on death = false"
                    );
                }
                Epidemic.instance().data().lowerImmunities(player.getUniqueId());
            }
        } catch (Exception ex) {
            Error.save(
                    "PlayerDeath.onPlayerDeath.001",
                    "PlayerDeath",
                    "onPlayerDeath()",
                    "Player Death Event",
                    "Unexpected error during Player Death event",
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }
}