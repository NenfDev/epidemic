package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.thirst.Thirst;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport implements Listener {

    /**
     * Called any time a player tries to enter a bed.  Used for insomnia checks.
     * @param event PlayerBedEnterEvent
     */
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Logging.debug(
                "PlayerTeleport",
                "onEnter",
                "Event Fired"
        );
        Bukkit.getScheduler().runTaskLater(Epidemic.instance(), () -> {
            if (!event.getFrom().getWorld().getUID().equals(event.getTo().getWorld().getUID())) {
                if (event.getPlayer() != null) {
                    boolean apply = true;

                    if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
                        apply = false;
                    }
                    if (Permission.inBypass(event.getPlayer())) {
                        apply = false;
                    }

                    if (apply) {
                        Thirst.applyThirst(event.getPlayer());
                    }
                }
            }
        }, 20);

    }
}
