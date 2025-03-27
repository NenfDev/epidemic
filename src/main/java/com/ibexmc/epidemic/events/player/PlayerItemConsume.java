package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.remedy.RemedyManager;
import com.ibexmc.epidemic.thirst.ThirstManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsume implements Listener {
    /**
     * Called any time the player consumes food or drink.  Used for remedy usage, and water
     * source usage.
     * @param event PlayerItemConsumeEvent
     */
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (event.getItem() == null) {
            Logging.debug(
                    "PlayerItemConsume",
                    "onItemConsume",
                    "Player: " + event.getPlayer().getName() + " - null item"
            );
        } else {
            Logging.debug(
                    "PlayerItemConsume",
                    "onItemConsume",
                    "Player: " + event.getPlayer().getName() + " - " + event.getItem().getType().name()
            );
        }

        if (RemedyManager.onPlayerItemConsume(event)) {
            event.setCancelled(true);
        }

        if (ThirstManager.onPlayerConsume(event)) {
            event.setCancelled(true);
        }

    }
}
