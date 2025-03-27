package com.ibexmc.epidemic.helpers.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.util.functions.StringFunctions;

public class EpidemicFurnaceRecipe implements EpidemicRecipe {

    //region Objects
    private NamespacedKey key;
    private Material source;
    private int experience;
    private int time;
    private ItemStack result;
    //endregion
    //region Constructors
    public EpidemicFurnaceRecipe(Material source, int experience, int time) {
        this.source = source;
        this.experience = experience;
        this.time = time;
    }
    public EpidemicFurnaceRecipe(NamespacedKey key, ItemStack result, Material source, int experience, int time) {
        this.key = key;
        this.source = source;
        this.experience = experience;
        this.time = time;
        this.result = result;
    }
    //endregion
    //region Getters & Setters

    /**
     * Gets the NamespacedKey for this Craft Recipe
     * @return NamespacedKey for the recipe
     */
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Gets the ItemStack resule for this Craft Recipe
     * @return ItemStack result
     */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Gets the source material for the furnace
     * @return Source material
     */
    public Material getSource() {
        return source;
    }

    /**
     * Gets the amount of experience points to give the player
     * @return Number of xp
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Gets the amount of time in seconds for the furnace to complete the recipe
     * @return Number of seconds
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets the NamespacedKey for this recipe
     * @param key NamespacedKey to use for the recipe
     */
    public void setKey(String key) {
        this.key = StringFunctions.stringToKey(key);
    }

    /**
     * Sets the ItemStack resule for this recipe
     * @param itemStack ItemStack to set
     */
    public void setResult(ItemStack itemStack) {
        this.result = itemStack;
    }
    //endregion
}
