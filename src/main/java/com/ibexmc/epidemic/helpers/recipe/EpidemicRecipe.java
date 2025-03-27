package com.ibexmc.epidemic.helpers.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface EpidemicRecipe {
    /**
     * Gets the key for this recipe
     * @return NamespacedKey of the recipe
     */
    NamespacedKey getKey();

    /**
     * Sets the key for this recipe
     * @param key NamespacedKey to use for the recipe
     */
    void setKey(String key);

    /**
     * Gets the ItemStack result of this recipe
     * @return ItemStack result
     */
    ItemStack getResult();

    /**
     * Sets the ItemStack result of this receipe
     * @param itemStack ItemStack to set
     */
    void setResult(ItemStack itemStack);
}
