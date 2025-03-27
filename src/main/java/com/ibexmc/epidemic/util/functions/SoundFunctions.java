package com.ibexmc.epidemic.util.functions;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundFunctions {
    /**
     * Plays a sound at the location
     * @param location Location to play sound at
     * @param volume Volume to play at
     * @param pitch Pitch to play at
     * @param sound Sound to play
     */
    public static void playSoundWorld(Location location, float volume, float pitch, Sound sound) {
        World world = location.getWorld();
        if (world != null) {
            world.playSound(location, sound, volume, pitch);
        }
    }

    /**
     * Plays the specified sound for the player specified
     * @param player Player to play the sound for
     * @param sound Sound to play
     * @param volume Volume to play the sound at
     * @param pitch Pitch to play the sound at
     */
    public static void playerSoundPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
