package com.ibexmc.epidemic.events.entity;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.dependencies.MythicMobDisease;
import com.ibexmc.epidemic.dependencies.MythicMobsHook;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;

public class MythicMobsEvents implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMythicMobHit(EntityDamageByEntityEvent event) {
        if (!Epidemic.instance().dependencies().hasMythicMobs()) return;

        if (event.getEntity() instanceof Player && MythicMobsHook.isMythicMob(event.getDamager())) {
            Player player = (Player) event.getEntity();
            String mobType = MythicMobsHook.getMythicMobType(event.getDamager());
            
            if (mobType != null) {
                Map<String, MythicMobDisease> diseases = Epidemic.instance().config.getMythicMobDiseases();
                if (diseases.containsKey(mobType)) {
                    MythicMobDisease mmDisease = diseases.get(mobType);
                    if (mmDisease.isOnHit()) {
                        Ailment ailment = AilmentManager.getAilmentByInternalName(mmDisease.getAilmentName());
                        if (ailment != null) {
                            if (new java.util.Random().nextInt(100) < mmDisease.getChance()) {
                                Logging.debug("MythicMobsEvents", "onMythicMobHit", "Player " + player.getName() + " hit by MythicMob " + mobType + ". Afflicting with " + ailment.getDisplayName());
                                ailment.afflict(player, null);
                            }
                        }
                    }
                }
            }
        }
    }
}
