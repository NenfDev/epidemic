package com.ibexmc.epidemic.events.entity;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentCauseType;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.equipment.InfectedSplashPotion;
import com.ibexmc.epidemic.util.Logging;

import com.ibexmc.epidemic.util.functions.WorldFunctions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

public class ProjectileEvents implements Listener {

    @EventHandler
    public void onProjectileLaunch(EntityShootBowEvent event) {
        Epidemic plugin = Epidemic.instance();
        if (event.getEntity() instanceof Player) {
            // We only care if a player has launched the projectile
            Player player = (Player) event.getEntity();
            Entity projectile = event.getProjectile();
            if (event.getConsumable() != null) {
                // If the projectile fired is an epidemic projectile, set the persistent data values to the entity fired
                if (plugin.persistentData().hasString(event.getConsumable(), PersistentData.Key.EQUIPMENT)) {
                    String equipmentType = plugin.persistentData().getString(event.getConsumable(), PersistentData.Key.EQUIPMENT);
                    Logging.debug(
                            "ProjectileEvents",
                            "onProjectileLaunch",
                            "Launched consumable by " + player.getName() + " is epidemic equipment (" + equipmentType + "), setting data to entity"
                    );
                    plugin.persistentData().setString(projectile, PersistentData.Key.EQUIPMENT, equipmentType);
                }
                if (plugin.persistentData().hasString(event.getConsumable(), PersistentData.Key.AILMENT)) {
                    String launchedAilment = plugin.persistentData().getString(event.getConsumable(), PersistentData.Key.AILMENT);
                    Logging.debug(
                            "ProjectileEvents",
                            "onProjectileLaunch",
                            "Launched consumable by " + player.getName() + " has epidemic ailment (" + launchedAilment + "), setting data to entity"
                    );
                    plugin.persistentData().setString(projectile, PersistentData.Key.AILMENT, launchedAilment);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Epidemic plugin = Epidemic.instance();
        if (event.getHitEntity() != null) {
            if (event.getHitEntity() instanceof Player) {
                Player player = (Player) event.getHitEntity();

                // Check if it is epidemic equipment
                if (plugin.persistentData().hasString(event.getEntity(), PersistentData.Key.EQUIPMENT)) {
                    String playerHitEquipment = plugin.persistentData().getString(event.getEntity(), PersistentData.Key.EQUIPMENT);
                    Logging.debug(
                            "ProjectileEvents",
                            "onProjectileHit",
                            player.getName() + " hit with a projectile that is epidemic equipment (" + playerHitEquipment + ")"
                    );
                    if (plugin.persistentData().hasString(event.getEntity(), PersistentData.Key.AILMENT)) {
                        String playerHitAilment = plugin.persistentData().getString(event.getEntity(), PersistentData.Key.AILMENT);
                        Logging.debug(
                                "ProjectileEvents",
                                "onProjectileHit",
                                player.getName() + " hit with a projectile that has epidemic ailment (" + playerHitAilment + ")"
                        );
                        // Get the ailment and check if the player should be afflicted
                        Ailment ailment = AilmentManager.getAilmentByInternalName(playerHitAilment);
                        if (ailment != null) {
                            AilmentManager.afflictCheck(player, AilmentCauseType.PROJECTILE, ailment);
                        }
                    }
                }
            }
        }
        /*
        // This code removed for now - I like the idea of a splash from a near miss, but more thought needs to go into
        // if it has the same impact as a projectile or splash potion, or something else completely
        // Use AilmentCauseType PROJECTILE_NEAR
        if (event.getHitBlock() != null) {
            if (plugin.persistentData().hasString(event.getEntity(), PersistentData.Key.AILMENT)) {
                String blockHitAilment = plugin.persistentData().getString(event.getEntity(), PersistentData.Key.AILMENT);
                // If we hit a block with epidemic equipment that has an ailment, possibly infect those nearby (within 1 block)
                for (Entity entity : WorldFunctions.nearbyEntities(event.getEntity().getLocation(), 1, 1, 1)) {
                    if (entity instanceof Player) {
                        Player nearPlayer = (Player) entity;
                        Logging.debug(
                                "ProjectileEvents",
                                "onProjectileHit",
                                nearPlayer.getName() + " was close to the block that a projectile that has epidemic ailment (" +
                                        blockHitAilment + ") and has a chance to catch it"
                        );
                        // Add in Chance of catching here
                    }
                }
            }
        }
         */
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        Epidemic plugin = Epidemic.instance();
        ItemStack item = event.getEntity().getItem();
        if (plugin.persistentData().hasString(item, PersistentData.Key.EQUIPMENT) && plugin.persistentData().hasString(item, PersistentData.Key.AILMENT)) {
            int range = 3;
            String splashEquipment = plugin.persistentData().getString(item, PersistentData.Key.EQUIPMENT);
            String splashAilment = plugin.persistentData().getString(item, PersistentData.Key.AILMENT);
            InfectedSplashPotion splashPotion = (InfectedSplashPotion)  Epidemic.instance().data().equipment.get(splashEquipment);
            if (splashPotion != null) {
                range = splashPotion.range();
            }
            Logging.debug(
                    "ProjectileEvents",
                    "onPotionSplash",
                    "Epidemic equipment (" + splashEquipment + ") potion splash that is infected with " + splashAilment +
                        " at " +  WorldFunctions.simpleReadable((event.getEntity().getLocation()))
            );
            // Check for nearby entities
            for (Entity entity : WorldFunctions.nearbyEntities(event.getEntity().getLocation(), range, range, range)) {
                if (entity instanceof Player) {
                    Player nearPlayer = (Player) entity;
                    Logging.debug(
                            "ProjectileEvents",
                            "onPotionSplash",
                            "Player " + nearPlayer.getName() + " is close to the " + splashAilment + " splash"
                    );
                    // Get the ailment and check if the player should be afflicted
                    Ailment ailment = AilmentManager.getAilmentByInternalName(splashAilment);
                    if (ailment != null) {
                        AilmentManager.afflictCheck(nearPlayer, AilmentCauseType.SPLASH_POTION, ailment);
                    }
                }
            }
        }
    }

}
