package com.ibexmc.epidemic.util.functions;

import org.bukkit.potion.PotionEffectType;

public class PotionFunctions {
    /**
     * Gets a potion effect by name
     * @param name Potion effect to lookup
     * @return Potion Effect requested, LUCK if not found
     */
    public static PotionEffectType potionEffectTypeFromName(String name) {
        PotionEffectType ret = PotionEffectType.LUCK;
        PotionEffectType pet = PotionEffectType.getByName(name.toUpperCase());
        if (pet != null)
        {
            ret = pet;
        }
        return ret;
    }
}
