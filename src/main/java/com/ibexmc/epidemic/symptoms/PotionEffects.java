package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.helpers.PlayerPotionEffect;

public class PotionEffects {

    /**
     * Applies potion effects to the player
     * @param player Player to apply effect for
     * @param afflicted Affliction causing effect
     */
    public static void applyPotionEffects(Player player, Afflicted afflicted) {
        if (player != null)
        {
            if (!Permission.inBypass(player) && !player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (afflicted.getAilment().getAilmentEffects() != null) {
                    for (PlayerPotionEffect ape : afflicted.getAilment().getAilmentEffects()) {
                        player.removePotionEffect(ape.getPotionEffectType());
                        PotionEffect potionEffect = new PotionEffect(ape.getPotionEffectType(), (ape.getSeconds() * 20), ape.getAmplifier());
                        Logging.debug(
                                "PotionEffects",
                                "applyPotionEffects(UUID, Afflicted)",
                                "Applying ailment  potion effects to " + player.getDisplayName() + " " +
                                        "Potion Effect: " + ape.getPotionEffectType().getName()
                        );
                        player.addPotionEffect(potionEffect);
                    }
                }
            }
        }
    }

    /**
     * Applies Bad Omen effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void badOmen(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.BAD_OMEN, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Blindness effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void blindness(Player player, int duration, int amplifier) {
        Logging.debug(
                "PotionEffects",
                "blindness(Player, int, int)",
                "Blindness Applied | Duration: " + duration + " ticks | Amplifier: " + amplifier
        );
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Confusion effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void confusion(Player player, int duration, int amplifier) {
        Logging.debug(
                "PotionEffects",
                "confusion(Player, int, int)",
                "Confusion Applied | Duration: " + duration + " ticks | Amplifier: " + amplifier
        );
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.NAUSEA, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Glowing effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void glowing(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.GLOWING, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Harm effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void harm(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Hunger effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void hunger(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.HUNGER, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Poison effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void poison(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Slow effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void slow(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOWNESS, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Slow Digging effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void slowDigging(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Unlucky effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void unlucky(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.UNLUCK, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Weakness effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void weakness(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.WEAKNESS, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }

    /**
     * Applies Wither effect to a player
     * @param player Player to apply effect to
     * @param duration Number of seconds to apply effect for
     * @param amplifier Effect amplifier
     */
    public static void wither(Player player, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.WITHER, duration, amplifier);
        player.addPotionEffect(potionEffect);
    }
}
