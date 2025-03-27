package com.ibexmc.epidemic.events.entity;

import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {
    /**
     * Called when an entity is damaged by another entity.  Used for entity damage by another entity
     * in AilmentManager to decide if a player should receive an ailment
     * @param event EntityDamageByEntityEvent
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Logging.debug(
                "EntityDamageByEntity",
                "onEntityDamageByEntity",
                "Started"
        );
        if (event.isCancelled()) {
            Logging.debug(
                    "EntityDamageByEntity",
                    "onEntityDamageByEntity",
                    "Cancelled - event was already cancelled before Epidemic got to it"
            );
            return;
        } else {
            Logging.debug("EntityDamageByEntity", "onEntityDamageByEntity",
                    "DETAILS: " +
                            "Victim type: " + event.getEntity().getType().name() + "\n" +
                            "Damager type: " + event.getDamager().getType().name() + "\n" +
                            "Cause: " + event.getCause().name() + "\n" +
                            "Damage: " + event.getDamage() + "\n" +
                            "Final Damage: " + event.getFinalDamage()
            );
        }
        AilmentManager.onEntityDamageByEntity(event);
    }
}
