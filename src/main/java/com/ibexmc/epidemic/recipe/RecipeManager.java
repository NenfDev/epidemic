package com.ibexmc.epidemic.recipe;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.util.*;

public class RecipeManager {

    /**
     * Gets a map of ItemStack entries that should be in each position
     * @param file File to read from
     * @return Recipe map
     */
    public static Map<RecipePosition, RecipeItem> recipe(File file) {
        Map<RecipePosition, RecipeItem> recipe = new HashMap<>();
        if (file.exists()) {
            ConfigParser recipeYML = new ConfigParser(
                    Epidemic.instance(),
                    file
            );
            recipe.put(RecipePosition.TOP_LEFT, getItem(recipeYML, RecipePosition.TOP_LEFT));
            recipe.put(RecipePosition.TOP_CENTER, getItem(recipeYML, RecipePosition.TOP_CENTER));
            recipe.put(RecipePosition.TOP_RIGHT, getItem(recipeYML, RecipePosition.TOP_RIGHT));
            recipe.put(RecipePosition.MIDDLE_LEFT, getItem(recipeYML, RecipePosition.MIDDLE_LEFT));
            recipe.put(RecipePosition.MIDDLE_CENTER, getItem(recipeYML, RecipePosition.MIDDLE_CENTER));
            recipe.put(RecipePosition.MIDDLE_RIGHT, getItem(recipeYML, RecipePosition.MIDDLE_RIGHT));
            recipe.put(RecipePosition.BOTTOM_LEFT, getItem(recipeYML, RecipePosition.BOTTOM_LEFT));
            recipe.put(RecipePosition.BOTTOM_CENTER, getItem(recipeYML, RecipePosition.BOTTOM_CENTER));
            recipe.put(RecipePosition.BOTTOM_RIGHT, getItem(recipeYML, RecipePosition.BOTTOM_RIGHT));
        }
        return recipe;
    }

    /**
     * Gets an item for the position from the ConfigParser provided
     * Gets either item, or a combination of material, name, amount, data
     * @param recipeYML ConfigParser to read
     * @param position Position to lookup
     * @return RecipeItem for the position provided
     */
    private static RecipeItem getItem(ConfigParser recipeYML, RecipePosition position) {
        String positionText;
        switch (position) {
            case TOP_LEFT:
                positionText = "top_left";
                break;
            case TOP_CENTER:
                positionText = "top_center";
                break;
            case TOP_RIGHT:
                positionText = "top_right";
                break;
            case MIDDLE_LEFT:
                positionText = "middle_left";
                break;
            case MIDDLE_CENTER:
                positionText = "middle_center";
                break;
            case MIDDLE_RIGHT:
                positionText = "middle_right";
                break;
            case BOTTOM_LEFT:
                positionText = "bottom_left";
                break;
            case BOTTOM_CENTER:
                positionText = "bottom_center";
                break;
            case BOTTOM_RIGHT:
                positionText = "bottom_right";
                break;
            default:
                return null;
        }

        Material material = Material.AIR;
        String name = "";
        List<String> lore = new ArrayList<>();
        PotionType potionType = null;
        UUID skullOwner = null;
        List<String> reqData = new ArrayList<>();
        int amount = 1;

        ConfigParser.ConfigReturn crMaterial = recipeYML.getMaterialValue(
                "recipe." + positionText + ".material",
                Material.AIR,
                false
        );
        if (crMaterial.isSuccess()) {
            material = crMaterial.getMaterial();
            Logging.debug("RecipeManager", "getItem", "Material found at " + positionText + " = " + material.name());
        }

        ConfigParser.ConfigReturn crName = recipeYML.getStringValue(
                "recipe." + positionText + ".name",
                "",
                false
        );
        if (crName.isSuccess()) {
            name = crName.getString();
        }

        ConfigParser.ConfigReturn crLore = recipeYML.getStringList(
                "recipe." + positionText + ".lore",
                false
        );
        if (crLore.isSuccess()) {
            lore = crLore.getStringList();
        }

        ConfigParser.ConfigReturn crPotionType = recipeYML.getPotionTypeValue(
                "recipe." + positionText + ".potion",
                PotionType.WATER,
                false
        );
        if (crPotionType.isSuccess()) {
            potionType = crPotionType.getPotionType();
        }

        ConfigParser.ConfigReturn crSkullOwner = recipeYML.getStringValue(
                "recipe." + positionText + ".head_owner",
                null,
                false
        );
        if (crSkullOwner.isSuccess()) {
            skullOwner = StringFunctions.uuidFromString(crSkullOwner.getString());
        }

        ConfigParser.ConfigReturn crAmount = recipeYML.getIntValue(
                "recipe." + positionText + ".amount",
                1,
                false
        );
        if (crAmount.isSuccess()) {
            amount = crAmount.getInt();
        }
        ConfigParser.ConfigReturn crData = recipeYML.getStringList(
                "recipe." + positionText + ".data",
                false
        );
        if (crData.isSuccess()) {
            reqData = crData.getStringList();
        }

        return new RecipeItem(
                position,
                material,
                name,
                lore,
                amount,
                potionType,
                skullOwner,
                reqData
        );


    }

    /**
     * Matches a RecipeItem against an ItemStack from the crafting table
     * @param craftItem Crafting table ItemStack
     * @param recipeItem RecipeItem
     * @return If true, recipe and crafting table match
     */
    public static boolean matchPosition(RecipePosition position, ItemStack craftItem, RecipeItem recipeItem) {

        Logging.debug("RecipeManager", "matchPosition", "Checking position: " + position.name());

        ItemMeta craftMeta = craftItem.getItemMeta();

        if (recipeItem.material() != Material.AIR) {
            if (!(craftItem.getType() == recipeItem.material())) {
                // Material does not match
                Logging.debug(
                        "RecipeManager",
                        "matchPosition",
                        "Material does not match.  Craft material = " + craftItem.getType().name() + " Recipe material = " + recipeItem.material().name()
                );
                return false;
            }
        }
        if (!StringFunctions.isNullOrEmpty(recipeItem.name())) {
            if (craftMeta != null) {
                if (!craftMeta.getDisplayName().equals(recipeItem.name())) {
                    // Display name does not match
                    Logging.debug(
                            "RecipeManager",
                            "matchPosition",
                            "Name does not match.  Craft name = " + craftMeta.getDisplayName() + " Recipe name = " + recipeItem.name()
                    );
                    return false;
                }
            }
        }
        if (recipeItem.lore().size() > 0) {
            if (craftMeta != null) {
                if (!StringFunctions.loreToString(craftMeta.getLore()).equals(StringFunctions.loreToString(recipeItem.lore()))) {
                    // Lore does not match
                    Logging.debug(
                            "RecipeManager",
                            "matchPosition",
                            "Lore does not match.  Craft lore = " + StringFunctions.loreToString(craftMeta.getLore()) +
                                    " Recipe lore = " + StringFunctions.loreToString(recipeItem.lore())
                    );
                    return false;
                }
            }
        }
        if (recipeItem.potionType() != null) {
            if (craftMeta instanceof PotionMeta) {
                if (((PotionMeta) craftMeta).getBasePotionData().getType() != recipeItem.potionType()) {
                    // Potion Type does not match
                    Logging.debug(
                            "RecipeManager",
                            "matchPosition",
                            "Potion Type does not match.  Craft potion type = " + ((PotionMeta) craftMeta).getBasePotionData().getType().name() +
                            " Recipe potion type = " + recipeItem.potionType().name()
                    );
                    return false;
                }
            }
        }

        if (recipeItem.skullOwner() != null && craftItem.getType() == Material.PLAYER_HEAD) {
            if (craftMeta instanceof SkullMeta) {
                SkullMeta skull = (SkullMeta) craftMeta;
                if (skull.getOwningPlayer() != null) {
                    if (skull.getOwningPlayer().getUniqueId() != recipeItem.skullOwner()) {
                        // Skull owner doesn't match
                        Logging.debug(
                                "RecipeManager",
                                "matchPosition",
                                "Skull owner does not match.  Craft skull owner = " + skull.getOwningPlayer().getUniqueId() +
                                        " Recipe skull owner = " + recipeItem.skullOwner()
                        );
                        return false;
                    }
                }
            }
        }


        if (recipeItem.reqData() != null) {
            if (recipeItem.reqData().size() > 0) {
                for (String reqDataEntry : recipeItem.reqData()) {
                    // If it isn't properly formatted, don't check it
                    if (reqDataEntry.contains(":")) {
                        String key = reqDataEntry.split(":")[0];
                        String value = reqDataEntry.split(":")[1];
                        if (!StringFunctions.isNull(key, "").equals("") && !StringFunctions.isNull(value, "").equals("")) {
                            if (!Epidemic.instance().persistentData().hasString(craftMeta, key)) {
                                Logging.debug(
                                        "RecipeManager",
                                        "matchPosition",
                                        "Required data missing. Key required: " + key
                                );
                                return false;
                            }
                        }
                    }
                }
            }
        }

        if (craftItem.getAmount() < recipeItem.amount()) {
            // Not enough of the item
            Logging.debug(
                    "RecipeManager",
                    "matchPosition",
                    "Minimum amount does not match.  Craft amount = " + craftItem.getAmount() +
                            " Recipe amount = " + recipeItem.amount()
            );
            return false;
        }

        // If we get this far, we've matched on whatever we need to
        //Logging.debug("RecipeManager", "matchPosition", "Item matches recipe in position " + position.name());
        return true;
    }

    /**
     * Checks if the crafting table recipe matches the recipe
     * @param craftingTable Crafting table recipe
     * @param recipe Recipe
     * @return If true, recipes match
     */
    public static boolean match(Map<RecipePosition, ItemStack> craftingTable, Map<RecipePosition, RecipeItem> recipe, String recipeKey) {
        if (recipe == null) {
            return false;
        }
        Logging.debug("RecipeManager", "match", "Checking against " + recipeKey);
        // Loop through all the positions and check the items
        for (RecipePosition position : RecipePosition.values()) {
            if (
                    !matchPosition(
                            position,
                            craftingTable.get(position),
                            recipe.get(position)
                    )
            ) {
                //Logging.debug("RecipeManager", "match", position.name() + " does not match");
                return false;
            }
        }

        // If we get this far, it is a match
        Logging.debug("RecipeManager", "match", "match found");
        return true;

    }

    /**
     * Populates the Craft Item Map
     * @param items Array of ItemStacks
     * @return Craft item map
     */
    public static Map<RecipePosition, ItemStack> populateCraftItemMap(ItemStack[] items) {
        Map<RecipePosition, ItemStack> craftRecipe = new HashMap<>();
        RecipePosition position = null;
        for (int i = 0; i < items.length; i++) {
            switch (i) {
                case 0:
                    position = RecipePosition.TOP_LEFT;
                    break;
                case 1:
                    position = RecipePosition.TOP_CENTER;
                    break;
                case 2:
                    position = RecipePosition.TOP_RIGHT;
                    break;
                case 3:
                    position = RecipePosition.MIDDLE_LEFT;
                    break;
                case 4:
                    position = RecipePosition.MIDDLE_CENTER;
                    break;
                case 5:
                    position = RecipePosition.MIDDLE_RIGHT;
                    break;
                case 6:
                    position = RecipePosition.BOTTOM_LEFT;
                    break;
                case 7:
                    position = RecipePosition.BOTTOM_CENTER;
                    break;
                case 8:
                    position = RecipePosition.BOTTOM_RIGHT;
                    break;
            }
            ItemStack item = new ItemStack(Material.AIR, 1);
            if (items[i] != null) {
                item = items[i];
            }
            craftRecipe.put(position, item);
        }
        return craftRecipe;
    }

}
