package com.ibexmc.epidemic.helpers.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.util.functions.StringFunctions;

public class EpidemicCraftRecipe implements EpidemicRecipe {

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
        if (this.top.getLeft() == Material.AIR) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "A";
        }
        if (this.top.getCenter() == Material.AIR) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "B";
        }
        if (this.top.getRight() == Material.AIR) {
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
        if (this.middle.getLeft() == Material.AIR) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "D";
        }
        if (this.middle.getCenter() == Material.AIR) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "E";
        }
        if (this.middle.getRight() == Material.AIR) {
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
        if (this.bottom.getLeft() == Material.AIR) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "G";
        }
        if (this.bottom.getCenter() == Material.AIR) {
            shapeText = shapeText + " ";
        } else {
            shapeText = shapeText + "H";
        }
        if (this.bottom.getRight() == Material.AIR) {
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
        private Material left;
        private Material center;
        private Material right;
        //endregion
        //region Constructors
        public Row(Material left, Material center, Material right) {
            this.left = left;
            this.center = center;
            this.right = right;
        }
        //endregion
        //region Getters & Setters

        /**
         * Gets the left Material for this Row
         * @return Material in Left position of this Row
         */
        public Material getLeft() {
            return left;
        }

        /**
         * Sets the left Material for this row
         * @param left Material to set
         */
        public void setLeft(Material left) {
            this.left = left;
        }

        /**
         * Gets the center Material for this Row
         * @return Material in center position of this Row
         */
        public Material getCenter() {
            return center;
        }

        /**
         * Sets the center Material for this row
         * @param center Material to set
         */
        public void setCenter(Material center) {
            this.center = center;
        }

        /**
         * Gets the right Material for this Row
         * @return Material in right position of this Row
         */
        public Material getRight() {
            return right;
        }

        /**
         * Sets the right Material for this row
         * @param right Material to set
         */
        public void setRight(Material right) {
            this.right = right;
        }
        //endregion
    }
    //endregion
}
