package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.functions.ParticleFunctions;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;

public class ParticleEffects {
    /**
     * Applies bleeding effect to a player
     * @param player Player to apply effect to
     * @param afflicted Affliction causing effect
     */
    public static void applyBleeding(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().isDisplayBleeding()) {
                    ParticleFunctions.bleed(
                            player,
                            afflicted.getAilment().isDisplayBleedingFace(),
                            afflicted.getAilment().isDisplayBleedingHead(),
                            afflicted.getAilment().isDisplayBleedingChest(),
                            afflicted.getAilment().isDisplayBleedingBack(),
                            afflicted.getAilment().isDisplayBleedingLeftArm(),
                            afflicted.getAilment().isDisplayBleedingRightArm(),
                            afflicted.getAilment().isDisplayBleedingLeftLeg(),
                            afflicted.getAilment().isDisplayBleedingRightLeg()
                    );
                }
            }
        }
    }

    /**
     * Applies vomit effect to a player
     * @param player Player to apply effect to
     * @param afflicted Affliction causing effect
     */
    public static void applyVomit(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().isDisplayVomit()) {
                    ParticleFunctions.vomit(player);
                }
            }
        }
    }

    /**
     * Applies losing control of bowels effect to a player
     * @param player Player to apply effect to
     * @param afflicted Affliction causing effect
     */
    public static void applyBowel(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().isDisplayBowel()) {
                    ParticleFunctions.bowel(player);
                }
            }
        }
    }

    /**
     * Applies urination effect to a player
     * @param player Player to apply effect to
     * @param afflicted Affliction causing effect
     */
    public static void applyUrinate(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().isDisplayUrinate()) {
                    ParticleFunctions.urinate(player);
                }
            }
        }
    }

    /**
     * Applies sweating effect to a player
     * @param player Player to apply effect to
     * @param afflicted Affliction causing effect
     */
    public static void applySweat(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().isDisplaySweat()) {
                    ParticleFunctions.sweat(player);
                }
            }
        }
    }

}
