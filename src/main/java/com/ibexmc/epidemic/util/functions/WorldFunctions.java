package com.ibexmc.epidemic.util.functions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class WorldFunctions {
    /**
     * Checks if a location is safe to teleport to
     * @param location Location to check
     * @return If true, location is safe
     */
    public static boolean safeTeleport(Location location) {
        final Location tpLoc = location.clone();
        final Location head = tpLoc.clone().add(0, 1, 0);
        Location below = tpLoc.clone();
        if (
                head.getBlock().getType().equals(Material.AIR) ||
                        head.getBlock().getType().equals(Material.CAVE_AIR) ||
                        head.getBlock().getType().equals(Material.WATER)
        ) {
            for (int i = 0; i < 4; i++) {
                below = below.add(0, -1, 0);
                if (below.getBlock().getType().isSolid()) {
                    // A solid block was found, return true
                    return true;
                } else {
                    if (
                            below.getBlock().getType().equals(Material.LAVA) &&
                                    !below.getBlock().getType().equals(Material.VOID_AIR)
                    ) {
                        // Either lava or void has been detected, return false
                        return false;
                    }
                }
            }
        } else {
            // Player would suffocate, return false
            return false;
        }
        // Unsafe, or we would have exited the function by now
        return false;
    }

    /**
     * Returns true if the location is inside a cuboid
     * @param location Location to check
     * @param corner1 Corner 1 of the cuboid
     * @param corner2 Corner 2 of the cuboid
     * @return If true, location is inside the cuboid
     */
    public static boolean insideCuboid(Location location, Location corner1, Location corner2) {

        if (location.getWorld().equals(corner1.getWorld()) && location.getWorld().equals(corner2.getWorld()))
        {
            int x = location.getBlockX();
            int y= location.getBlockY();
            int z = location.getBlockZ();
            int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
            int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
            int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
            int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
            int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
            int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

            return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
        } else {
            return false;
        }

    }

    /**
     * Gets a location from a string value.  Can deserialize regular or simplified location strings
     * @param locationString String to deserialize
     * @return Location (null if invalid)
     */
    public static org.bukkit.Location deserialize(String locationString) {
        if (locationString == null || locationString.trim() == "") {
            return null;
        }
        final String[] parts = locationString.split(":");
        if (parts.length >= 4) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = 0;
            float pitch = 0;
            if (parts.length == 6) {
                yaw = Float.parseFloat(parts[4]);
                pitch = Float.parseFloat(parts[5]);
            }
            return new org.bukkit.Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }

    /**
     * Serializes a Location into a simple string (no pitch/yaw)
     * @param location Location to serialize
     * @return Serialized simple location
     */
    public static String serializeSimple(Location location) {
        if (location == null) {
            return "";
        }
        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ();
    }
    /**
     * Serializes a Location into a string
     * @param location Location to serialize
     * @return Serialized location
     */
    public static String serialize(Location location) {
        if (location == null) {
            return "";
        }
        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ() + ":" +
                location.getPitch() + ":" +
                location.getYaw();
    }

    /**
     * Returns a new Location object with pitch and yaw removed
     * @param location Location to simplify
     * @return Simplified location
     */
    public static Location simplify(Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    /**
     * Returns a simplified string location for a location
     * @param location Location to simplify
     * @return Simplified location in String format
     */
    public static String simpleReadable(Location location) {
        String returnValue = "";
        if (location != null) {
            returnValue =
                    location.getWorld().getName() +
                            " X:" + location.getBlockX() +
                            " Y: " + location.getBlockY() +
                            " Z: " + location.getBlockZ();
        }
        return returnValue;
    }

    /**
     * Returns a nicely formatted Location string
     * @param location Location to format
     * @return Formatted string for the location
     */
    public static String fancyReadable(Location location) {
        if (location == null) {
            return "";
        }
        String ret = "";
        ret = "&5World: &d" +
                location.getWorld().getName() +
                " &5X:&d" +
                location.getBlockX() +
                " &5Y:&d " +
                location.getBlockY() +
                " &5Z:&d " +
                location.getBlockZ() +
                " &5Pitch: &d " +
                location.getPitch() +
                " &5Yaw: &d " +
                location.getYaw();
        return ret;
    }

    /**
     * Returns a nicely formatted simple Location string
     * @param location Location to format
     * @return Formatted string for the simple location
     */
    public static String simpleFancyReadable(Location location) {
        if (location == null) {
            return "";
        }
        String ret = "";
        ret = "&5World: &d" +
                location.getWorld().getName() +
                " &5X:&d" +
                location.getBlockX() +
                " &5Y:&d " +
                location.getBlockY() +
                " &5Z:&d " +
                location.getBlockZ();
        return ret;
    }

    /**
     * Returns true if the two locations match
     * @param location1 First Location
     * @param location2 Second Location
     * @param simple If true, pitch and yaw will not be checked
     * @return True if locations match
     */
    public static boolean match(Location location1, Location location2, boolean simple) {
        if (location1.getWorld().getUID().equals(location2.getWorld().getUID())) {
            if (location1.getX() == location2.getX()) {
                if (location1.getY() == location2.getY()) {
                    if (location1.getZ() == location2.getZ()) {
                        if (simple) {
                            return true;
                        } else {
                            if (location1.getYaw() == location2.getYaw()) {
                                if (location1.getPitch() == location2.getPitch()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the two blocks match
     * @param block1 First Block
     * @param block2 Second Block
     * @return True if locations match
     */
    public static boolean blockLocationMatch(Block block1, Block block2) {
        Location location1 = block1.getLocation().clone();
        Location location2 = block2.getLocation().clone();
        if (location1.getWorld().getUID().equals(location2.getWorld().getUID())) {
            if (location1.getBlockX() == location2.getBlockX()) {
                if (location1.getBlockY() == location2.getBlockY()) {
                    if (location1.getBlockZ() == location2.getBlockZ()) {
                        if (block1.getType() == block2.getType()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets nearby entities to a location
     * @param location Location to get nearby entities for
     * @param x X Distance
     * @param y Y Distance
     * @param z Z Distance
     * @return Set of Entities nearby to the location
     */
    public static Set<Entity> nearbyEntities(Location location, double x, double y, double z) {
        Set<Entity> nearbyEntities = new HashSet<>();
        Collection<Entity> nearby = location.getWorld().getNearbyEntities(location, x, y, z);
        if (nearby != null) {
            for (Entity entity : nearby) {
                nearbyEntities.add(entity);
            }
        }
        return nearbyEntities;
    }

    /**
     * Gets a random location in the cuboid defined by location1 and location2
     * @param location1 Corner Location 1
     * @param location2 Corner Location 2
     * @return Random Location
     */
    public static Location randomLocationInCuboid(Location location1, Location location2) {

        if (!location1.getWorld().getUID().equals(location2.getWorld().getUID())) {
            return null;
        }

        int minX = Math.min(location1.getBlockX(), location2.getBlockX()) + 1;
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX()) - 1;
        int minY = Math.min(location1.getBlockY(), location2.getBlockY()) + 1;
        int maxY = Math.max(location1.getBlockY(), location2.getBlockY()) - 1;
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ()) + 1;
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ()) - 1;

        int randomX = NumberFunctions.random(minX, maxX);
        int randomY = NumberFunctions.random(minY, maxY);
        int randomZ = NumberFunctions.random(minZ, maxZ);

        return new Location(location1.getWorld(), randomX, randomY, randomZ);

    }

    public static Biome biomeFromName(String name) {
        try {
            for (Biome biome : Biome.values()) {
                if (biome.name().equalsIgnoreCase(name)) {
                    return biome;
                }
            }
        }
        catch (Exception ex) {
            return null;
        }
        return null;
    }
}
