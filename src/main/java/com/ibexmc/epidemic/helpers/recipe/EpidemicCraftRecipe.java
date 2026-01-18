package com.ibexmc.epidemic.helpers.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.util.functions.StringFunctions;

public class EpidemicCraftRecipe implements EpidemicRecipe {

    public static class Ingredient {
        private Material material;
        private String itemsAdderID;
        private int customModelData = 0;

        public Ingredient(Material material) {
            this.material = material;
            this.itemsAdderID = "";
            this.customModelData = 0;
        }

        public Ingredient(Material material, int customModelData) {
            this.material = material;
            this.itemsAdderID = "";
            this.customModelData = customModelData;
        }

        public Ingredient(String itemsAdderID) {
            this.material = Material.AIR;
            this.itemsAdderID = itemsAdderID;
            this.customModelData = 0;
        }

        public Material getMaterial() {
            return material;
        }

        public String getItemsAdderID() {
            return itemsAdderID;
        }

        public int getCustomModelData() {
            return customModelData;
        }

        public boolean hasCustomModelData() {
            return customModelData > 0;
        }

        public boolean isItemsAdder() {
            return itemsAdderID != null && !itemsAdderID.isEmpty();
        }

        public boolean isAir() {
            return !isItemsAdder() && material == Material.AIR;
        }
    }

    //region Objects
    private NamespacedKey key;
    private EpidemicCraftRecipe.Row top;
    private EpidemicCraftRecipe.Row middle;
    private EpidemicCraftRecipe.Row bottom;
    private ItemStack result;
    //endregion
    //region Constructors
    public EpidemicCraftRecipe() {
        // Blank constructor used for passing to ConfigParser
    }
    public EpidemicCraftRecipe(Row top, Row middle, Row bottom) {
        this.top = top;
        this.middle = middle;
        this.bottom = bottom;
    }
    public EpidemicCraftRecipe(NamespacedKey key, ItemStack itemStack, Row top, Row middle, Row bottom) {
        this.key = key;
        this.result = itemStack;
        this.top = top;
        this.middle = middle;
        this.bottom = bottom;
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
    //region Methods

    /**
     * Gets the top Crafting row for this recipe
     * @return Top Row entry
     */
    public EpidemicCraftRecipe.Row getTop() {
        return top;
    }

    /**
     * Gets the middle Crafting row for this recipe
     * @return Middle Row entry
     */
    public EpidemicCraftRecipe.Row getMiddle() {
        return middle;
    }

    /**
     * Gets the bottom Crafting row for this recipe
     * @return Bottom Row entry
     */
    public EpidemicCraftRecipe.Row getBottom() {
        return bottom;
    }

    /**
     * Gets the top row shape
     * @return Three character shape for top row
     */
    public String getShapeTop() {
        String shapeText = "";
        if (this.top.getLeft().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "A";
        }
        if (this.top.getCenter().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "B";
        }
        if (this.top.getRight().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "C";
        }
        return shapeText;
    }

    /**
     * Gets the middle row shape
     * @return Three character shape for middle row
     */
    public String getShapeMiddle() {
        String shapeText = "";
        if (this.middle.getLeft().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "D";
        }
        if (this.middle.getCenter().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "E";
        }
        if (this.middle.getRight().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "F";
        }
        return shapeText;
    }

    /**
     * Gets the bottom row shape
     * @return Three character shape for bottom row
     */
    public String getShapeBottom() {
        String shapeText = "";
        if (this.bottom.getLeft().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "G";
        }
        if (this.bottom.getCenter().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "H";
        }
        if (this.bottom.getRight().isAir()) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "I";
        }
        return shapeText;
    }
    //endregion
    //region Sub-Classes
    public static class Row {
        //region Objects
        private Ingredient left;
        private Ingredient center;
        private Ingredient right;
        //endregion
        //region Constructors
        public Row(Ingredient left, Ingredient center, Ingredient right) {
            this.left = left;
            this.center = center;
            this.right = right;
        }
        //endregion
        //region Getters & Setters

        /**
         * Gets the left Ingredient for this Row
         * @return Ingredient in Left position of this Row
         */
        public Ingredient getLeft() {
            return left;
        }

        /**
         * Sets the left Ingredient for this row
         * @param left Ingredient to set
         */
        public void setLeft(Ingredient left) {
            this.left = left;
        }

        /**
         * Gets the center Ingredient for this Row
         * @return Ingredient in center position of this Row
         */
        public Ingredient getCenter() {
            return center;
        }

        /**
         * Sets the center Ingredient for this row
         * @param center Ingredient to set
         */
        public void setCenter(Ingredient center) {
            this.center = center;
        }

        /**
         * Gets the right Ingredient for this Row
         * @return Ingredient in right position of this Row
         */
        public Ingredient getRight() {
            return right;
        }

        /**
         * Sets the right Ingredient for this row
         * @param right Ingredient to set
         */
        public void setRight(Ingredient right) {
            this.right = right;
        }
        //endregion
    }
    //endregion
}
