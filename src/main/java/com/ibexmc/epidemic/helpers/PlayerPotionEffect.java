package com.ibexmc.epidemic.helpers;

import org.bukkit.potion.PotionEffectType;

public class PlayerPotionEffect {

    //region Objects
    private PotionEffectType potionEffectType;
    private int amplifier;
    private int seconds;
    //endregion
    //region Constructors
    public PlayerPotionEffect() {
    }

    public PlayerPotionEffect(PotionEffectType potionEffectType, int amplifier, int seconds) {
        this.potionEffectType = potionEffectType;
        this.amplifier = amplifier;
        this.seconds = seconds;
    }
    //endregion
    //region Getters

    /**
     * Gets the potion effect type
     * @return Potion effect type
     */
    public PotionEffectType getPotionEffectType() {
        return potionEffectType;
    }

    /**
     * Gets the amplifier
     * @return Potion Effect amplifier
     */
    public int getAmplifier() {
        return amplifier;
    }

    /**
     * Gets number of seconds to apply for
     * @return Potion effect seconds
     */
    public int getSeconds() {
        return seconds;
    }
    //endregion
}
