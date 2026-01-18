package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.Hotspot;
import com.ibexmc.epidemic.util.functions.ParticleFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.AilmentManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;

public class Afflict {
    /**
     * Called by the afflict scheduler.  Applies ailments to players on a timed basis
     */
    public static void Run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AilmentManager.timed(player);
        }
        // Hotspot check
        for (Map.Entry<Location, Hotspot> entry : Epidemic.instance().gameData().hotspots().get().entrySet()) {
            Hotspot hotspot = entry.getValue();
            // If any players within 50 blocks of the hotspot, show the particles
            Collection<Entity> within50 = entry.getKey().getWorld().getNearbyEntities(entry.getKey(), 50, 50, 50);
            if (within50 != null) {
                for (Entity entity : within50) {
                    if (entity instanceof Player) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Epidemic.instance(), () -> {
                            new BukkitRunnable() {
                                private int step = 0;
                                public void run() {
                                    step++;
                                    Location location = entry.getKey().clone().add(0.5, 0, 0.5);
                                    ParticleFunctions.displayParticleDust(entry.getKey(), hotspot.getCount(), hotspot.getRed(), hotspot.getGreen(), hotspot.getBlue(), hotspot.getSize());

                                    if (step >= Epidemic.instance().config.getAfflictedTime() - 1) {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Epidemic.instance(), 0, 20);
                        }, 3);

                    }
                }
            }
            int distance = hotspot.getDistance();
            Collection<Entity> nearby = entry.getKey().getWorld().getNearbyEntities(entry.getKey(), distance, distance, distance);
            if (nearby != null) {
                for (Entity entity : nearby) {
                    if (entity instanceof Player) {
                        Ailment ailment = AilmentManager.getAilmentByInternalName(hotspot.getAilment());
                        if (ailment != null) {
                            Player player = (Player) entity;
                            AilmentManager.hotspotAfflictCheck(player, ailment, hotspot);
                        }
                    }
                }
            }
        }

        // MythicMobs Proximity check
        if (Epidemic.instance().dependencies().hasMythicMobs()) {
            java.util.Map<String, com.ibexmc.epidemic.dependencies.MythicMobDisease> diseases = Epidemic.instance().config.getMythicMobDiseases();
            if (!diseases.isEmpty()) {
                double maxRadius = 10.0;
                for (com.ibexmc.epidemic.dependencies.MythicMobDisease mmDisease : diseases.values()) {
                    if (mmDisease.isOnProximity() && mmDisease.getProximityRadius() > maxRadius) {
                        maxRadius = mmDisease.getProximityRadius();
                    }
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Entity nearby : player.getNearbyEntities(maxRadius, maxRadius, maxRadius)) {
                        if (com.ibexmc.epidemic.dependencies.MythicMobsHook.isMythicMob(nearby)) {
                            String mobType = com.ibexmc.epidemic.dependencies.MythicMobsHook.getMythicMobType(nearby);
                            if (mobType != null && diseases.containsKey(mobType)) {
                                com.ibexmc.epidemic.dependencies.MythicMobDisease mmDisease = diseases.get(mobType);
                                if (mmDisease.isOnProximity()) {
                                    double distance = player.getLocation().distance(nearby.getLocation());
                                    if (distance <= mmDisease.getProximityRadius()) {
                                        Ailment ailment = AilmentManager.getAilmentByInternalName(mmDisease.getAilmentName());
                                        if (ailment != null) {
                                            if (new java.util.Random().nextInt(100) < mmDisease.getChance()) {
                                                ailment.afflict(player, null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
