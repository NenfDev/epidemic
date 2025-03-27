package com.ibexmc.epidemic.events.entity;

import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {

    /**
     * Called when an entity is damaged.  Used for entity damage logic in AilmentManager
     * to decide if a player should receive an ailment
     * @param event EntityDamageEvent
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Logging.debug(
                "EntityDamage",
                "onEntityDamage",
                "Entity Type: " + event.getEntityType() +
                        " Damage: " + event.getCause().name() +
                        " = " + event.getDamage()
        );
        AilmentManager.onEntityDamage(event);
    }
}
