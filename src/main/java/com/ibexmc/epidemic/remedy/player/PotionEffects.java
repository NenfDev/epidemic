package com.ibexmc.epidemic.remedy.player;

import com.ibexmc.epidemic.helpers.PlayerPotionEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class PotionEffects {

    /**
     * Returns the current potion effect for the player if it matches the
     * PotionEffectType provided.
     * @param player Player that is being checked
     * @param potionEffectType PotionEffectType that is being checked
     * @return PotionEffect Returns the potion effect, null if does not exist
     */
    public static PotionEffect getPotionEffect(Player player, PotionEffectType potionEffectType) {
        if (player == null || potionEffectType == null) {
            return null;
        }
        PotionEffect currentEffect = null;
        if (player.getActivePotionEffects() != null) {
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                if (potionEffect.getType() == potionEffectType) {
                    currentEffect = potionEffect;
                }
            }
        }
        return currentEffect;
    }

    /**
     * Gets the current potion effect, if it matches the PotionEffecType provided,
     * sets the amplifier (if higher than current) and adds the time to the effect
     * @param player The player to set the potion effect on
     * @param effect The potion effect to add
     */
    public static void addPotionEffect(Player player, PlayerPotionEffect effect) {
        int amplifier = effect.getAmplifier();
        int time = (effect.getSeconds() * 20); // in ticks

        PotionEffect potionEffect = getPotionEffect(player, effect.getPotionEffectType());
        if (potionEffect != null) {
            amplifier = Math.max(potionEffect.getAmplifier(), effect.getAmplifier());
            if (potionEffect.getDuration() > 0) {
                time = time + potionEffect.getDuration();
            }
        }

        player.removePotionEffect(effect.getPotionEffectType());

        PotionEffect newPotionEffect = new PotionEffect(
                effect.getPotionEffectType(),
                time,
                amplifier
        );

        player.addPotionEffect(newPotionEffect);
    }

    /**
     * Gets the current potion effect, if it matches the PotionEffecType provided,
     * reduces the amplifier by the set amount, then removes the time from the effect
     * @param player The player to reduce the potion effect for
     * @param effect The potion effect to reduce
     */
    public static void reducePotionEffect(Player player, PlayerPotionEffect effect) {
        PotionEffect potionEffect = getPotionEffect(player, effect.getPotionEffectType());
        if (potionEffect != null) {
            int amplifier = potionEffect.getAmplifier() - effect.getAmplifier();
            int time = potionEffect.getDuration() - (effect.getSeconds() * 20);
            if (time < 1 || amplifier < 1) {
                player.removePotionEffect(effect.getPotionEffectType());
            } else {
                PotionEffect newPotionEffect = new PotionEffect(
                        effect.getPotionEffectType(),
                        amplifier,
                        time
                );
                player.addPotionEffect(newPotionEffect);
            }
        }
    }

    /**
     * Completely removes a potion effect type from a player
     * @param player Player to remove the effect from
     * @param potionEffectType PotionEffectType to remove
     */
    public static void removePotionEffect(Player player, PotionEffectType potionEffectType) {
        if (player.hasPotionEffect(potionEffectType)) {
            player.removePotionEffect(potionEffectType);
        }
    }

    /**
     * Returns a set of negative potion effect types
     * @return Set<PotionEffectType> Negative potion effects
     */
    public static Set<PotionEffectType> negativePotionEffectTypes() {
        Set<PotionEffectType> effects = new HashSet<>();
        effects.add(PotionEffectType.BAD_OMEN);
        effects.add(PotionEffectType.BLINDNESS);
        effects.add(PotionEffectType.CONFUSION);
        effects.add(PotionEffectType.HARM);
        effects.add(PotionEffectType.HUNGER);
        effects.add(PotionEffectType.POISON);
        effects.add(PotionEffectType.SLOW);
        effects.add(PotionEffectType.SLOW_DIGGING);
        effects.add(PotionEffectType.UNLUCK);
        effects.add(PotionEffectType.WEAKNESS);
        effects.add(PotionEffectType.WITHER);
        return effects;
    }
}
