package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.thirst.SetThirst;
import com.ibexmc.epidemic.thirst.Thirst;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.dependencies.papi.PAPIValues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

    /**
     * Called when a player respawns.  Used to reset the player thirst level
     * @param event PlayerRespawnEvent
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Logging.debug(
                "PlayerRespawn",
                "onRespawn",
                "Player Respawn: " + event.getPlayer()
        );
        if (Epidemic.instance().config.isEnableThirst()) {
            int lvl = Epidemic.instance().config.getRespawnThirstLevel();
            Thirst thirst = Epidemic.instance().data().getThirstBar().get(event.getPlayer().getUniqueId());
            if (thirst != null) {
                if (thirst.getThirstAmount() < 1 || Epidemic.instance().config.getRespawnResetThirst()) {
                    Logging.debug(
                            "PlayerRespawn",
                            "onRespawn",
                            "Player thirst amount is less than 1 - setting to default of " + lvl
                    );
                    SetThirst.set(event.getPlayer(), lvl);
                }
            }
        }
        PAPIValues.update(event.getPlayer());
    }
}
