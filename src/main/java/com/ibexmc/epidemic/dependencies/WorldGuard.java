package com.ibexmc.epidemic.dependencies;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.log.Error;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;


import java.util.List;
import java.util.Objects;

public class WorldGuard {
    /**
     * Checks if WorldGuard will allow a player can build in the location
     * @param player Player to check
     * @param location Location to check
     * @param flag Flag to check
     * @return If true, flag is allowed by player at the location
     */
    public static boolean flagAllowed(Player player, Location location, StateFlag flag) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "flagAllowed",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "flagAllowed",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (flag == null) {
            Error.save(
                    "003",
                    "WorldGuard",
                    "flagAllowed",
                    "Invalid parameters",
                    "Flag is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        try {
            if(Epidemic.instance().dependencies().hasWorldGuard()){
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
                com.sk89q.worldguard.WorldGuard worldGuard = Epidemic.instance().dependencies().getWorldGuard();
                RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
                if (!worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, wgWorld)) {
                    return query.testState(wgLocation, wgPlayer, flag);
                }
            }
        } catch (Exception ex) {
            Error.save(
                    "004",
                    "WorldGuard",
                    "flagAllowed",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return true;
        }
        return true; // If we reach this point, something didn't go well with WorldGuard, so return true
    }

    /**
     * Checks if WorldGuard will allow a player can build in the location
     * @param player Player to check
     * @param location Location to check
     * @return If true, player can build at the location
     */
    public static boolean canBuild(Player player, Location location) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "canBuild",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "canBuild",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        try {
            if(Epidemic.instance().dependencies().hasWorldGuard()){
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
                com.sk89q.worldguard.WorldGuard worldGuard = Epidemic.instance().dependencies().getWorldGuard();
                RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
                if (!worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, wgWorld)) {
                    return query.testState(wgLocation, wgPlayer, Flags.BUILD);
                }
            }
        } catch (Exception ex) {
            Error.save(
                    "003",
                    "WorldGuard",
                    "canBuild",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return true;
        }
        return true; // If we reach this point, something didn't go well with WorldGuard, so return true (can build)
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * PVP is assumed true, Mob damage is assumed true, explosion damage is assumed true, fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @return If true, player can receive damage
     */
    public static boolean canReceiveDamage(Player player, Location location) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        return canReceiveDamage(player, location, false, false, false, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * Mob damage is assumed true, explosion damage is assumed true, fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @return If true, player can receive damage
     */
    public static boolean canReceiveDamage(Player player, Location location, boolean pvp) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        return canReceiveDamage(player, location, pvp, false, false, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * Explosion damage is assumed true, fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @param mobDamage Check for mob damage
     * @return If true, player can receive damage
     */
    public static boolean canReceiveDamage(Player player, Location location, boolean pvp, boolean mobDamage) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        return canReceiveDamage(player, location, pvp, mobDamage, false, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * Fall damage is assumed true
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @param mobDamage Check for mob damage
     * @param explosionDamage Check for explosion damage
     * @return If true, player can receive damage
     */
    public static boolean canReceiveDamage(Player player, Location location, boolean pvp, boolean mobDamage, boolean explosionDamage) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        return canReceiveDamage(player, location, pvp, mobDamage, explosionDamage, false);
    }

    /**
     * Checks if WorldGuard will allow various types of damage
     * @param player Player to check
     * @param location Location to check
     * @param pvp Check for PVP
     * @param mobDamage Check for mob damage
     * @param explosionDamage Check for explosion damage
     * @param fallDamage Check for fall damage
     * @return If true, player can receive damage
     */
    public static boolean canReceiveDamage(Player player, Location location, boolean pvp, boolean mobDamage, boolean explosionDamage, boolean fallDamage) {
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        if (location == null) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Invalid parameters",
                    "Location is null",
                    Error.Severity.URGENT,
                    null
            );
            return true;
        }
        try {
            if(Epidemic.instance().dependencies().hasWorldGuard()){
                WorldGuardPlugin worldGuardPlugin = Epidemic.instance().dependencies().getWorldGuardPlugin(); // Get WorldGuard plugin
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player); // Convert player
                com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())); // Convert world
                com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location); // Convert location
                com.sk89q.worldguard.WorldGuard worldGuard = Epidemic.instance().dependencies().getWorldGuard(); // Get the WorldGuard instance
                RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery(); // Create a new region query
                ApplicableRegionSet regionSet = query.getApplicableRegions(wgLocation); // Get all the regions at the location
                // Check that the player is in at least 1 region
                if (regionSet.getRegions().size() > 0) {
                    // Check that the player is not in bypass
                    if (!worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, wgWorld)) {
                        boolean result = true;
                        // If checking for PVP and the PVP flag on the region returns false (deny) to pvp then return false (player cannot receive this damage)
                        if (pvp && !query.testState(wgLocation, wgPlayer, Flags.PVP)) {
                            return false;
                        }
                        // If checking for mob damage and the region returns false (deny) to mob, wither, or ghast fireball damage then return false (player cannot receive this damage)
                        if (
                                mobDamage && (
                                        !query.testState(wgLocation, wgPlayer, Flags.MOB_DAMAGE) ||
                                                !query.testState(wgLocation, wgPlayer, Flags.WITHER_DAMAGE) ||
                                                !query.testState(wgLocation, wgPlayer, Flags.GHAST_FIREBALL)
                                )
                        ) {
                            return false;
                        }
                        // If checking for explosion damage and the region returns false (deny) to creeper or other explosions, then return false (player cannot receive this damage)
                        if (
                                explosionDamage && (
                                        !query.testState(wgLocation, wgPlayer, Flags.CREEPER_EXPLOSION) ||
                                                !query.testState(wgLocation, wgPlayer, Flags.OTHER_EXPLOSION)
                                )
                        ) {
                            return false;
                        }
                        // If checking for fall damage and the region returns false (deny) to fall damage, then return false (player cannot receive this damage)
                        if (fallDamage && !query.testState(wgLocation, wgPlayer, Flags.FALL_DAMAGE)) {
                            return false;
                        }
                    } else {
                        Logging.debug("WorldGuard", "receiveDamage", player.getName() + " has WorldGuard bypass");
                    }
                } else {
                    // Player is not in a WorldGuard region
                    Logging.debug("WorldGuard", "receiveDamage", player.getName() + " is not in a WorldGuard region");
                }
            }
        } catch (Exception ex) {
            Error.save(
                    "003",
                    "WorldGuard",
                    "canReceiveDamage",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            // Player can receive this damage as something went wrong
            return true;
        }
        // If we reach this point, the player is either not in a region, is in bypass, or no flags are preventing the damage
        // so return true (player can receive this damage)
        Logging.debug("WorldGuard", "receiveDamage", player.getName() + " can receive damage here");
        return true;
    }

    /**
     * Checks if the player is in an excluded region
     * @param player Player to check
     * @param excludedRegions List of region names to exclude
     * @return If true, player is an excluded region
     */
    public static boolean inRegion(Player player, List<String> excludedRegions)  {
        Logging.debug("WorldGuard", "inRegion", "Checking for excluded regions");
        if (player == null) {
            Error.save(
                    "001",
                    "WorldGuard",
                    "inRegion",
                    "Invalid parameters",
                    "Player is null",
                    Error.Severity.URGENT,
                    null
            );
            return false;
        }
        if (excludedRegions == null || excludedRegions.size() == 0) {
            return false;
        }
        try {
            com.sk89q.worldguard.WorldGuard worldGuard = Epidemic.instance().dependencies().getWorldGuard();
            com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());
            RegionContainer container = worldGuard.getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet regionSet = query.getApplicableRegions(loc);
            for (ProtectedRegion region : regionSet) {
                for (String excludedRegion : excludedRegions) {
                    if (excludedRegion.equalsIgnoreCase(region.getId())) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            Error.save(
                    "002",
                    "WorldGuard",
                    "inRegion",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return false;
        }
        return false;
    }
}