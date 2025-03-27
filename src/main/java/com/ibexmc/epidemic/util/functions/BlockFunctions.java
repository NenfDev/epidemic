package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.util.SpigotVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockFunctions {

    /**
     * Gets all the blocks in a radius of a location
     * @param start Central starting point
     * @param radius Radius to check
     * @return List of blocks in radius
     */
    public static List<Block> getBlocks(Block start, int radius){
        List<Block> blocks = new ArrayList<>();
        for(double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++){
            for(double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++){
                for(double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++){
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }

    /**
     * Checks if a location is underneath a block or open to the sky
     * @param location Location to check
     * @return If true, location is under a block and not open to the sky
     */
    public static boolean isUnderBlock(Location location){
        int y;
        for(y = location.getBlockY(); y <= 255; y++){
            Location l = new Location (location.getWorld(),location.getBlockX(), y, location.getZ());
            if (l.getBlock().getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a block is a heat source
     * @param block Block to check
     * @return If true, block is a heat source
     */
    public static boolean isHeatSource(Block block) {
        boolean campfire = false;
        if (block.getType().equals(Material.CAMPFIRE)) {
            campfire = true;
        }
        if (SpigotVersion.is116Safe()) {
            if (block.getType().equals(Material.SOUL_CAMPFIRE)) {
                campfire = true;
            }
        }
        return campfire || block.getType().equals(Material.FIRE) || block.getType().equals(Material.LAVA);
    }
}
