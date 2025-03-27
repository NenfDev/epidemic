package com.ibexmc.epidemic.recipe;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.UUID;

public class RecipeItem {

    private final RecipePosition position;
    private final Material material;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final PotionType potionType;
    private final UUID skullOwner;
    private final List<String> reqData;

    public RecipeItem(RecipePosition position, Material material, String name, List<String> lore, int amount, PotionType potionType, UUID skullOwner, List<String> reqData) {
        this.position = position;
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.potionType = potionType;
        this.skullOwner = skullOwner;
        this.reqData = reqData;
    }

    /**
     * Gets the position
     * @return Position
     */
    public RecipePosition position() {
        return position;
    }

    /**
     * Gets the material
     * @return Material
     */
    public Material material() {
        return material;
    }

    /**
     * Gets the item amount
     * @return Amount
     */
    public int amount() {
        return amount;
    }

    /**
     * Gets the name
     * @return Name
     */
    public String name() {
        return name;
    }

    /**
     * Gets the lore
     * @return Lore
     */
    public List<String> lore() {
        return lore;
    }

    /**
     * Gets the potion type
     * @return Potion Type
     */
    public PotionType potionType() {
        return potionType;
    }

    /**
     * Gets the skull owner
     * @return Skull Owner
     */
    public UUID skullOwner() {
        return skullOwner;
    }

    /**
     * Gets the required data
     * @return Required Data
     */
    public List<String> reqData() {
        return reqData;
    }

}
