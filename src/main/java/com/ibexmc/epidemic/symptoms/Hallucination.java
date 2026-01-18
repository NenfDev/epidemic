package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Permission;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.util.SpigotVersion;

import java.util.Random;

public class Hallucination {
    public enum Type {
        NONE,
        ARROW,
        CREEPER,
        DOORBREAK,
        ENDERMAN,
        FOOTSTEPS,
        GHAST,
        LAVA,
        PHANTOM,
        PILLAGER,
        SLIME,
        SPIDER,
        TRIDENT,
        WITCH,
        WOLF,
        ZOMBIE
    }

    /**
     * Gets the sound by paranoia type
     * @param hallucinationType Paranoia type to lookup
     * @return Sound for this paranoia type
     */
    public static Sound getSound(Type hallucinationType) {
        Sound returnSound = Sound.ENTITY_PLAYER_BREATH;
        switch (hallucinationType) {
            case ARROW:
                returnSound = Sound.ENTITY_ARROW_HIT;
                break;
            case CREEPER:
                returnSound = Sound.ENTITY_CREEPER_PRIMED;
                break;
            case DOORBREAK:
                returnSound = Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR;
                break;
            case ENDERMAN:
                returnSound = Sound.ENTITY_ENDERMAN_SCREAM;
                break;
            case FOOTSTEPS:
                returnSound = Sound.BLOCK_GRAVEL_STEP;
                break;
            case GHAST:
                returnSound = Sound.ENTITY_GHAST_SCREAM;
                break;
            case LAVA:
                returnSound = Sound.BLOCK_LAVA_AMBIENT;
                break;
            case PHANTOM:
                returnSound = Sound.ENTITY_PHANTOM_SWOOP;
                break;
            case SLIME:
                returnSound = Sound.ENTITY_SLIME_SQUISH;
                break;
            case SPIDER:
                returnSound = Sound.ENTITY_SPIDER_AMBIENT;
                break;
            case TRIDENT:
                returnSound = Sound.ITEM_TRIDENT_HIT_GROUND;
                break;
            case WITCH:
                if (SpigotVersion.is114Safe()) {
                    returnSound = Sound.ENTITY_WITCH_CELEBRATE;
                } else {
                    returnSound = Sound.ENTITY_WITCH_AMBIENT;
                }
                break;
            case WOLF:
                returnSound = Sound.ENTITY_WOLF_GROWL;
                break;
            case ZOMBIE:
                returnSound = Sound.ENTITY_ZOMBIE_AMBIENT;
            default:
                break;
        }
        if (SpigotVersion.is114Safe()) {
            if (hallucinationType == Type.PILLAGER) {
                returnSound = Sound.ENTITY_PILLAGER_CELEBRATE;
            }
        }
        return returnSound;
    }

    /**
     * Plays sound at the player for a specific hallucination type
     * @param player Player to play sound for
     * @param hallucinationType Hallucination type
     * @param volume Volume to play the sound at
     * @param pitch Pitch to play the sound at
     */
    public static void playSoundAtPlayer(Player player, Type hallucinationType, float volume, float pitch) {
        if (hallucinationType == Type.NONE) {
            return;
        }
        Location location = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(3));
        player.playSound(location, Hallucination.getSound(hallucinationType), volume, pitch);
    }

    /**
     * Plays a sound at the player with a random hallucination type
     * @param player Player to play sound for
     * @param volume Volume to play sound at
     * @param pitch Pitch to play the sound at
     */
    public static void playSoundAtPlayer(Player player, float volume,float pitch) {
        // Gets a random hallucination
        Random rand = new Random();
        int chance = rand.nextInt(14);

        Type hallucinationType = Type.NONE;
        switch (chance) {
            case 0:
                hallucinationType = Type.ARROW;
                break;
            case 1:
                hallucinationType = Type.CREEPER;
                break;
            case 2:
                hallucinationType = Type.ENDERMAN;
                break;
            case 3:
                hallucinationType = Type.GHAST;
                break;
            case 4:
                hallucinationType = Type.LAVA;
                break;
            case 5:
                hallucinationType = Type.PHANTOM;
                break;
            case 6:
                hallucinationType = Type.SLIME;
                break;
            case 7:
                hallucinationType = Type.SPIDER;
                break;
            case 8:
                hallucinationType = Type.PILLAGER;
                break;
            case 9:
                hallucinationType = Type.WITCH;
                break;
            case 10:
                hallucinationType = Type.WOLF;
                break;
            case 11:
                hallucinationType = Type.ZOMBIE;
                break;
            case 12:
                hallucinationType = Type.FOOTSTEPS;
                break;
            case 13:
                hallucinationType = Type.DOORBREAK;
                break;
            default:
                break;
        }
        Random r = new Random();
        int randomInt = r.nextInt(100) + 1;
        if (randomInt <= 50) {
            if (player.isInsideVehicle()) {
                if(player.getVehicle() instanceof Boat) {
                    hallucinationType = Type.TRIDENT;
                }
            }
            if (player.isSwimming()) {
                hallucinationType = Type.TRIDENT;
            }
        } else {
            hallucinationType = Type.NONE;
        }

        playSoundAtPlayer(player, hallucinationType, volume, pitch);

    }

    /**
     * Applies hallucination to the player
     * @param player Player to apply hallucination for
     * @param afflicted Affliction causing hallucination
     */
    public static void applyHallucination(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().isHallucination()) {
                    Random r = new Random();
                    int randomInt = r.nextInt(100) + 1;
                    if (randomInt <= 20) {
                        PotionEffects.confusion(player, 40, 3); // Apply 1 second of confusion
                    }
                    playSoundAtPlayer(player, 1.0f, 1.0f);
                }
            }
        }
    }
}
