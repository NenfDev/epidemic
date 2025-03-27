package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.equipment.EquipmentManager;
import com.ibexmc.epidemic.remedy.RemedyManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {

    /**
     * Used any time a player interacts with an entity
     *
     * @param event PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteractEntity(PlayerInteractEntityEvent event) {

        if (event.getRightClicked().getType() == EntityType.PLAYER) {
            Logging.debug(
                    "PlayerInteractEntity",
                    "onInteractEntity",
                    "Right clicked entity is a player"
            );
            Player clickedPlayer = (Player) event.getRightClicked();
            Logging.debug(
                    "PlayerInteractEntity",
                    "onInteractEntity",
                    "Right clicked player: " + clickedPlayer.getName()
            );

            // Try and apply empty/full syringes
            if (EquipmentManager.onPlayerInteractEntity(event)) {
                event.setCancelled(true);
                return;
            }

            // Try and apply remedy to other player
            if (RemedyManager.onPlayerInteractEntity(event, clickedPlayer)) {
                event.setCancelled(true);
                return;
            }

            // Chance of ailment via touch
            AilmentManager.touch(clickedPlayer, event.getPlayer());

        }

    }
}

