package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerFunctions {

    /**
     * Returns the player head for an offline player
     * @param uuid Player Unique Id
     * @return Player head
     */
    public static ItemStack head(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta headMeta = head.getItemMeta();
        if (headMeta instanceof SkullMeta) {
            SkullMeta headSkull = (SkullMeta) headMeta;
            headSkull.setDisplayName(offlinePlayer.getName());
            headSkull.setOwningPlayer(offlinePlayer);
            head.setItemMeta(headSkull);
        }
        return head;
    }

    /**
     * Gets nearby players to a location
     * @param location Location to get nearby players for
     * @param x X Distance
     * @param y Y Distance
     * @param z Z Distance
     * @return Set of Players nearby to the location
     */
    public static Set<Player> nearbyPlayers(Location location, double x, double y, double z) {
        Set<Player> nearbyPlayers = new HashSet<>();
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, x, y, z);
        if (nearbyEntities != null) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Player) {
                    nearbyPlayers.add((Player) entity);
                }
            }
        }
        return nearbyPlayers;
    }

    /**
     * Gets an OfflinePlayer from the player unique identifier
     * @param uuid Unique identifier for the player to lookup
     * @return OfflinePlayer for the unique identifier provided
     */
    public static OfflinePlayer offlinePlayerFromUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = null;
        offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer == null)
        {
            Error.save(
                    "Provide.Entities.offlinePlayerFromUUID.001",
                    "Provide.Entities",
                    "offlinePlayerFromUUID(UUID)",
                    "Getting Offline Player from UUID",
                    "UUID does not match an OfflinePlayer",
                    Error.Severity.WARN,
                    null
            );
        }
        return offlinePlayer;

    }

}
