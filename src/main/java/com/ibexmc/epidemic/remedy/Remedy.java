package com.ibexmc.epidemic.remedy;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.helpers.PlayerPotionEffect;
import com.ibexmc.epidemic.helpers.recipe.EpidemicCraftRecipe;
import com.ibexmc.epidemic.helpers.recipe.EpidemicFurnaceRecipe;
import com.ibexmc.epidemic.helpers.recipe.EpidemicRecipe;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.SpigotVersion;
import com.ibexmc.epidemic.remedy.player.*;
import com.ibexmc.epidemic.util.functions.*;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import com.ibexmc.epidemic.util.ConfigParser;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class Remedy {

    /*
        Remedy is used for curing ailments, providing custom drinks, relief cures etc.
    */

    //region Objects
    private Epidemic plugin;                                                // An instance of main class
    private File remedyFile;                                                // File to save/load from
    private NamespacedKey namespacedKey;                                    // Remedy key
    private String displayName = "";                                        // Display name
    private List<String> lore = new ArrayList<>();                          // Item lore
    private String usedText = "";                                           // Text to display when used
    private Material baseItem;                                              // Base item to use for the remedy
    private Material returnItem = Material.AIR;                             // Item returned after use (AIR if none)
    private Color potionColor;                                              // Potion color IF base item is a potion
    private boolean enchantedGlow;                                          // Should an enchantment glow be on the item
    private EpidemicRecipe recipe;                                          // Recipe for the remedy, can be craft or furnace
    private boolean requireCraftPerm;                                       // Should permission be needed to craft
    private int amount;                                                     // Number of items generated
    private boolean food = false;                                           // Flag to indicate if food
    private boolean drink = false;                                          // Flag to indicate if drink
    private boolean itemConsume = false;                                    // Flag to indicate if the item is consumed on use
    private int itemUses = 0;                                               // How many uses a non-consumable item has (0 = infinite)
    private List<Ailment> cures = new ArrayList<>();                        // List of ailments the remedy cures
    private List<PlayerPotionEffect> addEffects = new ArrayList<>();        // List of potion effects to add
    private List<PlayerPotionEffect> removeEffects = new ArrayList<>();     // List of potion effects to remove
    private Map<String, Integer> immunity = new HashMap<>();                // Immunity amounts to apply
    private int health = 0;                                                 // Health to restore (negative removes)
    private int hunger = 0;                                                 // Hunger points to restore (negative removes)
    private int thirst = 0;                                                 // Thirst points to restore (negative removes)
    private int symptomReliefSeconds = 0;                                   // Number of seconds to apply symptom relief (0 = none)
    private int coldReliefSeconds = 0;                                      // Number of seconds to apply cold relief (0 = none)
    private int heatReliefSeconds = 0;                                      // Number of seconds to apply heat relief (0 = none)
    private boolean applyOthers = true;                                     // Flag to indicate if you can apply the remedy to others
    private String itemsAdderID = "";                                       // ItemsAdder namespaced ID
    private int customModelData = 0;                                        // Custom Model Data for the item
    //endregion
    //region Constructors
    /**
     * The blank constructor is used for importing existing items into a Remedy
     * and should not be used beyond that use case
     */
    public Remedy() {
        // Used only for importing data
        this.plugin = Epidemic.instance();
    }

    /**
     * This constructor takes in a File object and loads the Remedy data from it
     * @param file The file to load in
     */
    public Remedy(File file) {

        this.plugin = Epidemic.instance();

        boolean activeFile = true;

        this.remedyFile = file;

        ConfigParser configParser = new ConfigParser(Epidemic.instance(), file);

        // Key
        ConfigParser.ConfigReturn crKey = configParser.getStringValue(
                "key",
                "",
                true
        );
        if (crKey.isSuccess()) {
            namespacedKey = StringFunctions.stringToKey(crKey.getString());
        } else {
            // Invalid key - hard stop
            Logging.log("Remedy", "Invalid key");
            activeFile = false;
        }

        // Display Name
        ConfigParser.ConfigReturn crDisplayName = configParser.getStringValue(
                "display_name",
                "",
                true
        );
        if (crDisplayName.isSuccess()) {
            displayName = crDisplayName.getString();
        } else {
            Logging.log("Remedy", "Invalid display name");
            activeFile = false;
        }

        // Lore
        ConfigParser.ConfigReturn crLore = configParser.getStringList(
                "lore",
                false
        );
        if (crLore.isSuccess()) {
            lore = crLore.getStringList();
        } else {
            lore = new ArrayList<>();
        }

        // Used text
        ConfigParser.ConfigReturn crUsedText = configParser.getStringValue(
                "used_text",
                "",
                false
        );
        if (crUsedText.isSuccess()) {
            usedText = crUsedText.getString().trim();
        } else {
            Logging.log("Remedy", "Invalid used text");
            activeFile = false;
        }

        // Base Item
        ConfigParser.ConfigReturn crBaseItem = configParser.getMaterialValue(
                "base_item",
                Material.AIR,
                true
        );
        if (crBaseItem.isSuccess()) {
            baseItem = crBaseItem.getMaterial();
        } else {
            Logging.log("Remedy", "Invalid base item");
            activeFile = false;
        }

        // Return Item
        ConfigParser.ConfigReturn crReturnItem = configParser.getMaterialValue(
                "return_item",
                Material.AIR,
                false
        );
        if (crReturnItem.isSuccess()) {
            returnItem = crReturnItem.getMaterial();
        } else {
            returnItem = Material.AIR;
        }

        // Potion Color
        ConfigParser.ConfigReturn crPotionColor = configParser.getColor(
                "potion_color",
                Color.WHITE,
                false
        );
        if (crPotionColor.isSuccess()) {
            potionColor = crPotionColor.getColor();
        }

        // Enchanted Glow
        ConfigParser.ConfigReturn crEnchantedGlow = configParser.getBooleanValue(
                "enchanted_glow",
                false,
                false
        );
        if (crEnchantedGlow.isSuccess()) {
            enchantedGlow = crEnchantedGlow.getBoolean();
        } else {
            enchantedGlow = false;
        }

        // Custom Model Data
        ConfigParser.ConfigReturn crCustomModelData = configParser.getIntValue(
                "custom_model_data",
                0,
                false
        );
        if (crCustomModelData.isSuccess()) {
            customModelData = crCustomModelData.getInt();
        } else {
            customModelData = 0;
        }

        // Recipe
        ConfigParser.ConfigReturn crRecipe = configParser.getRecipeValue(
                "recipe"
        );
        if (crRecipe.isSuccess()) {
            if (crRecipe.getRecipe() != null) {
                recipe = crRecipe.getRecipe();
            }
        }

        // Require Craft Permission
        ConfigParser.ConfigReturn crRequireCraftPerm = configParser.getBooleanValue(
                "recipe.require_craft_perm",
                false,
                false
        );
        if (crRequireCraftPerm.isSuccess()) {
            requireCraftPerm = crRequireCraftPerm.getBoolean();
        } else {
            requireCraftPerm = false;
        }

        // Recipe Amount
        ConfigParser.ConfigReturn crRecipeAmount = configParser.getIntValue(
                "recipe.amount",
                1,
                false
        );
        if (crRecipeAmount.isSuccess()) {
            amount = crRecipeAmount.getInt();
        } else {
            amount = 1;
        }

        // Food
        ConfigParser.ConfigReturn crFood = configParser.getBooleanValue(
                "food",
                false,
                false
        );
        if (crFood.isSuccess()) {
            food = crFood.getBoolean();
        } else {
            food = false;
        }

        // Drink
        ConfigParser.ConfigReturn crDrink = configParser.getBooleanValue(
                "drink",
                false,
                false
        );
        if (crDrink.isSuccess()) {
            drink = crDrink.getBoolean();
        } else {
            drink = false;
        }


        // Item Consumed
        if (this.food || this.drink) {
            this.itemConsume = true;
        } else {
            ConfigParser.ConfigReturn crItemConsumed = configParser.getBooleanValue(
                    "item_consumed",
                    false,
                    false
            );
            if (crItemConsumed.isSuccess()) {
                itemConsume = crItemConsumed.getBoolean();
            } else {
                itemConsume = false;
            }
        }

        // Item Uses
        if (this.itemConsume) {
            this.itemUses = 0;
        } else {
            ConfigParser.ConfigReturn crItemUses = configParser.getIntValue(
                    "item_uses",
                    0,
                    false
            );
            if (crItemUses.isSuccess()) {
                itemUses = crItemUses.getInt();
            } else {
                itemUses = 0;
            }
        }

        // Cures
        ConfigParser.ConfigReturn crCures = configParser.getStringList(
                "cures",
                false
        );
        if (crCures.isSuccess()) {
            if (crCures.getStringList() != null) {
                for (String ailment : crCures.getStringList()) {
                    cures.add(Epidemic.instance().data().getAvailableAilmentByInternalName(ailment));
                }
            }
        } else {
            cures = new ArrayList<>();
        }

        // Add Effects
        ConfigParser.ConfigReturn crAddEffects = configParser.getEffectList(
                "add_effects",
                false
        );
        if (crAddEffects.isSuccess()) {
            if (crAddEffects.getEffects() != null) {
                addEffects = crAddEffects.getEffects();
            }
        }

        // Remove Effects
        ConfigParser.ConfigReturn crRemoveEffects = configParser.getEffectList(
                "remove_effects",
                false
        );
        if (crRemoveEffects.isSuccess()) {
            if (crRemoveEffects.getEffects() != null) {
                removeEffects = crRemoveEffects.getEffects();
            }
        }

        // Immunity
        ConfigParser.ConfigReturn crImmunities = configParser.getConfigSection(
                "immunity",
                false
        );
        if (crImmunities.isSuccess()) {
            if (crImmunities.getConfigSection() != null) {
                for (Object key : crImmunities.getConfigSection().getKeys(false).toArray()) {
                    Ailment immunityAilment = Epidemic.instance().data().getAvailableAilmentByInternalName(
                            key.toString()
                    );
                    if (immunityAilment != null) {
                        ConfigParser.ConfigReturn crImmunityInner = configParser.getIntValue(
                                "immunity." + key.toString(),
                                0,
                                false
                        );
                        if (crImmunityInner.isSuccess()) {
                            //TODO: Put or add?
                            immunity.put(immunityAilment.getInternalName(), crImmunityInner.getInt());
                        }
                    }
                }
            }
        }

        // Health
        ConfigParser.ConfigReturn crHealth = configParser.getIntValue(
                "health",
                0,
                false
        );
        if (crHealth.isSuccess()) {
            health = crHealth.getInt();
        }

        // Hunger
        ConfigParser.ConfigReturn crHunger = configParser.getIntValue(
                "hunger",
                0,
                false
        );
        if (crHunger.isSuccess()) {
            hunger = crHunger.getInt();
        }

        // Thirst
        ConfigParser.ConfigReturn crThirst = configParser.getIntValue(
                "thirst",
                0,
                false
        );
        if (crThirst.isSuccess()) {
            thirst = crThirst.getInt();
        }

        // Symptom Relief Seconds
        ConfigParser.ConfigReturn crSymptomReliefSeconds = configParser.getIntValue(
                "symptom_relief_seconds",
                0,
                false
        );
        if (crSymptomReliefSeconds.isSuccess()) {
            symptomReliefSeconds = crSymptomReliefSeconds.getInt();
        }

        // Cold Relief Seconds
        ConfigParser.ConfigReturn crColdReliefSeconds = configParser.getIntValue(
                "cold_relief_seconds",
                0,
                false
        );
        if (crColdReliefSeconds.isSuccess()) {
            coldReliefSeconds = crColdReliefSeconds.getInt();
        }

        // Heat Relief Seconds
        ConfigParser.ConfigReturn crHeatReliefSeconds = configParser.getIntValue(
                "heat_relief_seconds",
                0,
                false
        );
        if (crHeatReliefSeconds.isSuccess()) {
            heatReliefSeconds = crHeatReliefSeconds.getInt();
        }

        // Apply Self
        ConfigParser.ConfigReturn crApplyOthers = configParser.getBooleanValue(
                "apply_others",
                true,
                false
        );
        if (crApplyOthers.isSuccess()) {
            applyOthers = crApplyOthers.getBoolean();
        } else {
            applyOthers = true;
        }

        // ItemsAdder ID
        ConfigParser.ConfigReturn crItemsAdderID = configParser.getStringValue(
                "items_adder_id",
                "",
                false
        );
        if (crItemsAdderID.isSuccess()) {
            itemsAdderID = crItemsAdderID.getString();
        }

        if (activeFile) {
            if (recipe != null) {
                this.recipe.setKey(this.namespacedKey.getKey());
                this.recipe.setResult(this.getItemStack(this.amount));
            }
            addToGame();
        } else {
            Logging.log("Remedy", "Non-active File");
        }

    }
    
    //endregion
    //region Methods

    /**
     * Creates and returns a new File object based on the key provided
     * @param key The key will become the filename (+.yml)
     * @return File The new file to be generated
     */
    private File newFile(String key) {
        return new File(
                Epidemic.instance().getDataFolder() +
                        File.separator +
                        "remedies" +
                        File.separator +
                        key +
                        ".yml"
        );
    }

    /**
     * Saves the current Remedy to a file.  It has no use case
     * except during intial conversion, however, for future use, it will not be
     * deprecated
     */
    public void save() {
        if (namespacedKey == null) {
            return;
        }
        if (remedyFile == null) {
            remedyFile = newFile(namespacedKey.getKey());
        }
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(remedyFile);

        fileConfig.set("key", this.namespacedKey.getKey());
        fileConfig.set("display_name", this.displayName.replace("§", "&"));
        if (this.lore != null) {
            if (this.lore.size() > 0) {
                fileConfig.set("lore", StringFunctions.colorStringList(this.lore));
            } else {
                fileConfig.set("lore", null);
            }
        } else {
            fileConfig.set("lore", null);
        }

        fileConfig.set("used_text", this.usedText.replace("§", "&"));
        fileConfig.set("base_item", this.baseItem.name());
        if (returnItem != null) {
            if (returnItem != Material.AIR) {
                fileConfig.set("return_item", this.returnItem.name());
            } else {
                fileConfig.set("return_item", null);
            }
        }

        if (this.baseItem == Material.POTION) {
            if (this.potionColor != null) {
                fileConfig.set("potion_color.red", potionColor.getRed());
                fileConfig.set("potion_color.green", potionColor.getGreen());
                fileConfig.set("potion_color.blue", potionColor.getBlue());
            }
        }
        fileConfig.set("enchanted_glow", this.enchantedGlow);
        if (recipe != null) {
            if (recipe instanceof EpidemicCraftRecipe) {
                EpidemicCraftRecipe epidemicCraftRecipe = (EpidemicCraftRecipe) recipe;
                EpidemicCraftRecipe.Row top = epidemicCraftRecipe.getTop();
                EpidemicCraftRecipe.Row middle = epidemicCraftRecipe.getMiddle();
                EpidemicCraftRecipe.Row bottom = epidemicCraftRecipe.getBottom();

                saveIngredient(fileConfig, "recipe.craft.top.left", top.getLeft());
                saveIngredient(fileConfig, "recipe.craft.top.center", top.getCenter());
                saveIngredient(fileConfig, "recipe.craft.top.right", top.getRight());
                saveIngredient(fileConfig, "recipe.craft.middle.left", middle.getLeft());
                saveIngredient(fileConfig, "recipe.craft.middle.center", middle.getCenter());
                saveIngredient(fileConfig, "recipe.craft.middle.right", middle.getRight());
                saveIngredient(fileConfig, "recipe.craft.bottom.left", bottom.getLeft());
                saveIngredient(fileConfig, "recipe.craft.bottom.center", bottom.getCenter());
                saveIngredient(fileConfig, "recipe.craft.bottom.right", bottom.getRight());
            } else if (recipe instanceof EpidemicFurnaceRecipe) {
                EpidemicFurnaceRecipe epidemicFurnaceRecipe = (EpidemicFurnaceRecipe) recipe;
                fileConfig.set("recipe.furnace.item", epidemicFurnaceRecipe.getSource().name());
                fileConfig.set("recipe.furnace.experience", epidemicFurnaceRecipe.getExperience());
                fileConfig.set("recipe.furnace.time", epidemicFurnaceRecipe.getTime());
                //FurnaceRecipe boiledWater = new FurnaceRecipe(key, itemStack, Material.POTION, 0, 60);
            }

            recipe.setKey(this.namespacedKey.getKey());
            recipe.setResult(this.getItemStack(amount));
            fileConfig.set("recipe.amount", this.amount);
            fileConfig.set("recipe.require_craft_perm", this.requireCraftPerm);
        }
        fileConfig.set("food", this.food);
        fileConfig.set("drink", this.drink);
        if (this.food || this.drink) {
            fileConfig.set("item_consumed", null);
        } else {
            fileConfig.set("item_consumed", this.itemConsume);
        }

        if (this.cures != null) {
            if (this.cures.size() > 0) {
                List<String> ailmentsToCure = new ArrayList<>();
                for (Ailment cureAilment : cures) {
                    ailmentsToCure.add(cureAilment.getInternalName());
                }
                fileConfig.set("cures", ailmentsToCure);
            } else {
                fileConfig.set("cures", null);
            }
        } else {
            fileConfig.set("cures", null);
        }
        if (addEffects != null) {
            for (PlayerPotionEffect addEffect : addEffects) {
                fileConfig.set(
                        "add_effects." + addEffect.getPotionEffectType().getName() + ".amplifier",
                        addEffect.getAmplifier()
                );
                fileConfig.set(
                        "add_effects." + addEffect.getPotionEffectType().getName() + ".time",
                        addEffect.getSeconds()
                );
            }
        }
        if (removeEffects != null) {
            for (PlayerPotionEffect removeEffect : removeEffects) {
                fileConfig.set(
                        "remove_effects." + removeEffect.getPotionEffectType().getName() + ".amplifier",
                        removeEffect.getAmplifier()
                );
                fileConfig.set(
                        "remove_effects." + removeEffect.getPotionEffectType().getName() + ".time",
                        removeEffect.getSeconds()
                );
            }
        }
        fileConfig.set("health", this.health);
        fileConfig.set("hunger", this.hunger);
        fileConfig.set("thirst", this.thirst);
        fileConfig.set("symptom_relief_seconds", this.symptomReliefSeconds);
        fileConfig.set("cold_relief_seconds", this.coldReliefSeconds);
        fileConfig.set("heat_relief_seconds", this.heatReliefSeconds);
        fileConfig.set("items_adder_id", this.itemsAdderID);
        if (this.customModelData > 0) {
            fileConfig.set("custom_model_data", this.customModelData);
        } else {
            fileConfig.set("custom_model_data", null);
        }

        try {
            fileConfig.save(remedyFile);
            if (remedyFile.length() == 0) {
                //Logging.log("Blank file found", customYml.getName());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveIngredient(FileConfiguration config, String path, EpidemicCraftRecipe.Ingredient ingredient) {
        if (ingredient == null || ingredient.isAir()) {
            config.set(path, null);
        } else if (ingredient.isItemsAdder()) {
            config.set(path, "ITEMSADDER:" + ingredient.getItemsAdderID());
        } else if (ingredient.hasCustomModelData()) {
            config.set(path, "CUSTOMMODELDATA:" + ingredient.getMaterial().name() + ":" + ingredient.getCustomModelData());
        } else {
            config.set(path, ingredient.getMaterial().name());
        }
    }

    /**
     * Adds the Recipe to the game (if not null) and then
     * adds the item to the map in LiveData
     */
    private void addToGame() {

        if (recipe != null) {


            ItemStack item = new ItemStack(baseItem, 1); // Base item we will end with
            ItemMeta meta = item.getItemMeta(); // Get the item meta data
            meta.setDisplayName(displayName); // Set the name
            meta.setLore(lore); // Set the List containing our Lore to the ItemMeta object

            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta; // Cast the meta data to PotionMeta
                potionMeta.setColor(potionColor); // Set the color via the meta data

                item.setItemMeta(potionMeta); // Set the meta data to the item
            } else {
                item.setItemMeta(meta); // Set the meta data to the item
            }

            Recipe newRecipe = null;
            
            if (recipe instanceof EpidemicCraftRecipe) {
                EpidemicCraftRecipe epidemicCraftRecipe = (EpidemicCraftRecipe) recipe;

                newRecipe = new ShapedRecipe(recipe.getKey(), recipe.getResult()); // Create a new recipe for our named key and item

                ((ShapedRecipe) newRecipe).shape(
                        epidemicCraftRecipe.getShapeTop(),
                        epidemicCraftRecipe.getShapeMiddle(),
                        epidemicCraftRecipe.getShapeBottom()
                );

                char[] keys = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
                EpidemicCraftRecipe.Ingredient[] ingredients = {
                        epidemicCraftRecipe.getTop().getLeft(),
                        epidemicCraftRecipe.getTop().getCenter(),
                        epidemicCraftRecipe.getTop().getRight(),
                        epidemicCraftRecipe.getMiddle().getLeft(),
                        epidemicCraftRecipe.getMiddle().getCenter(),
                        epidemicCraftRecipe.getMiddle().getRight(),
                        epidemicCraftRecipe.getBottom().getLeft(),
                        epidemicCraftRecipe.getBottom().getCenter(),
                        epidemicCraftRecipe.getBottom().getRight()
                };

                for (int i = 0; i < keys.length; i++) {
                    EpidemicCraftRecipe.Ingredient ingredient = ingredients[i];
                    if (!ingredient.isAir()) {
                        if (ingredient.isItemsAdder()) {
                            ItemStack iaItem = ItemFunctions.getItemsAdderItem(ingredient.getItemsAdderID());
                            if (iaItem != null) {
                                ((ShapedRecipe) newRecipe).setIngredient(keys[i], new RecipeChoice.ExactChoice(iaItem));
                            } else {
                                Logging.log("Remedy", "ItemsAdder item not found for recipe: " + ingredient.getItemsAdderID());
                                // Fallback to AIR or something? If IA item missing, recipe might be broken.
                            }
                        } else if (ingredient.hasCustomModelData()) {
                            ItemStack cmdItem = new ItemStack(ingredient.getMaterial());
                            ItemMeta cmdMeta = cmdItem.getItemMeta();
                            if (cmdMeta != null) {
                                cmdMeta.setCustomModelData(ingredient.getCustomModelData());
                                cmdItem.setItemMeta(cmdMeta);
                            }
                            ((ShapedRecipe) newRecipe).setIngredient(keys[i], new RecipeChoice.ExactChoice(cmdItem));
                        } else {
                            ((ShapedRecipe) newRecipe).setIngredient(keys[i], ingredient.getMaterial());
                        }
                    }
                }
            } else if (recipe instanceof EpidemicFurnaceRecipe) {
                EpidemicFurnaceRecipe epidemicFurnaceRecipe = (EpidemicFurnaceRecipe) recipe;
                newRecipe = new FurnaceRecipe(
                        recipe.getKey(),
                        recipe.getResult(),
                        new RecipeChoice.MaterialChoice(epidemicFurnaceRecipe.getSource()),
                        epidemicFurnaceRecipe.getExperience(),
                        epidemicFurnaceRecipe.getTime() * 20
                );
            }




            try {
                boolean recipeExists = false;
                if (SpigotVersion.is115Safe()) {
                    Bukkit.removeRecipe(namespacedKey);
                } else {
                    //Logging.log( "addRecipe()", "Not 1.15 safe");
                    for(Iterator<org.bukkit.inventory.Recipe> iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
                        org.bukkit.inventory.Recipe type = iterator.next();
                        if (type != null) {
                            if (type.getResult() != null) {
                                ItemMeta recipeMeta = type.getResult().getItemMeta();
                                if (recipeMeta != null) {
                                    if (recipeMeta.getDisplayName().equals(displayName)) {
                                        Logging.debug("Ailment", "addRecipe()", "Recipe Exists: " + recipeMeta.getDisplayName());
                                        recipeExists = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!recipeExists) {
                    Bukkit.addRecipe(newRecipe); // Add the recipe to the server
                    Logging.debug("CustomItem", "addToGame()", "Added recipe for: " + displayName);
                    Epidemic.instance().data().addCustomRecipe(namespacedKey, item);
                }
            } catch (Exception ex) {
                Logging.debug(
                        "Ailment",
                        "addRecipe()",
                        "Unexpected error adding recipe for " + displayName
                );
            }


        }
        Epidemic.instance().data().putRemedy(this);
    }

    /**
     * Gets an ItemStack for the Remedy
     * @param amount The number of items to return
     * @return ItemStack This returns a pre-built ItemStack for the Remedy
     */
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack;
        boolean isItemsAdder = false;
        if (Epidemic.instance().dependencies().hasItemsAdder() && !this.itemsAdderID.isEmpty()) {
            itemStack = ItemFunctions.getItemsAdderItem(this.itemsAdderID);
            if (itemStack != null) {
                itemStack.setAmount(amount);
                isItemsAdder = true;
            } else {
                itemStack = new ItemStack(this.baseItem, amount);
            }
        } else {
            itemStack = new ItemStack(this.baseItem, amount);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;

        // Only override name and lore if it's NOT an ItemsAdder item, 
        // OR if you specifically want Epidemic to override IA settings.
        // Usually, if they use ItemsAdder, they want IA to handle look and feel.
        // But for uses, we MUST add our PDC keys.

        if (!isItemsAdder) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.displayName));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
        }

        // Setting uses - always apply for Epidemic tracking
        if (SpigotVersion.is114Safe()) {
            if (this.itemUses > 0) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                NamespacedKey useKey = new NamespacedKey(plugin, "epi_current_uses");
                container.set(useKey, PersistentDataType.INTEGER, itemUses);
                NamespacedKey maxKey = new NamespacedKey(plugin, "epi_max_uses");
                container.set(maxKey, PersistentDataType.INTEGER, itemUses);
                
                // If it's an IA item, we might want to APPEND the uses lore if it's not already there.
                // For now, let's keep it consistent with vanilla remedies.
                List<String> currentLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                currentLore.add(StringFunctions.getItemUsesLine(this.itemUses, this.itemUses));
                itemMeta.setLore(currentLore);
            }
        }

        if (itemMeta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            if (this.potionColor != null) {
                potionMeta.setColor(this.potionColor);
            }
            potionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        } else {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        if (this.customModelData > 0) {
            itemMeta.setCustomModelData(this.customModelData);
        }
        
        itemStack.setItemMeta(itemMeta);

        if (this.enchantedGlow && !isItemsAdder) { // Usually IA items have their own glow settings
            if (baseItem == Material.FISHING_ROD) {
                itemStack.addUnsafeEnchantment(Enchantment.INFINITY, 1);
            } else {
                itemStack.addUnsafeEnchantment(Enchantment.LURE, 1);
            }
            ItemMeta glowMeta = itemStack.getItemMeta();
            glowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(glowMeta);
        }
        return itemStack;
    }

    /**
     * Returns a boolean flag for if the ItemStack provided matches a Remedy
     * @param itemStack
     * @return Boolean True/False depending on if the item is a Remedy item
     */
    public boolean isMatch(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (Epidemic.instance().dependencies().hasItemsAdder() && !this.itemsAdderID.isEmpty()) {
            return ItemFunctions.isItemsAdderItem(itemStack, this.itemsAdderID);
        }

        Material material = itemStack.getType();
        List<String> lore = new ArrayList<>();
        String name = "";
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta.getDisplayName() != null) {
                name = itemMeta.getDisplayName();
            }
            if (itemMeta.getLore() != null) {
                lore = itemMeta.getLore();
            }
        }

        if (baseItem != material) {
            // Fails match because material does not match
            return false;
        }

        if (this.customModelData > 0) {
            if (itemMeta == null || !itemMeta.hasCustomModelData() || itemMeta.getCustomModelData() != this.customModelData) {
                return false;
            }
        }

        if (
                !ChatColor.stripColor(
                    StringFunctions.colorToString(displayName)
                ).equalsIgnoreCase(
                        ChatColor.stripColor(
                            StringFunctions.colorToString(name)
                        )
                )
        ) {
            // Fails match because name does not match (color removed)
           return false;
        }
        String remedyLore = StringFunctions.loreToString(this.lore);

        List<String> matchLoreList = new ArrayList<>();
        if (lore != null) {
            for (String loreLine : lore) {
                if (!loreLine.startsWith("■")) {
                    matchLoreList.add(loreLine);
                }
            }
        }
        String matchLore = StringFunctions.loreToString(matchLoreList);

        // Fails match because lore does not match (color/listing removed)
        return ChatColor.stripColor(
                StringFunctions.colorToString(remedyLore)
        ).equalsIgnoreCase(
                ChatColor.stripColor(
                        StringFunctions.colorToString(matchLore)
                )
        );

        // If we reach this point, remedy matches item
    }

    /**
     * Applies the remedy to the player, including applying
     * health/hunger/thirst benefits, cures the ailment if needed, removes
     * potion effects etc.
     * @param player The player to apply the remedy to
     */
    public void use(Player player, Player appliedBy, EquipmentSlot equipmentSlot, ItemStack consumedItem) {

        if (player == null) {
            Logging.debug(
                    "Remedy",
                    "use",
                    "Unable to use remedy, Player is null"
            );
            return;
        }
        if (appliedBy == null) {
            Logging.debug(
                    "Remedy",
                    "use",
                    "Unable to use remedy, Applied By is null"
            );
            return;
        }

        // Message player
        if (!usedText.equals("")) {
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Messaging " +
                            player.getDisplayName() +
                            " Message: " + usedText
            );
            SendMessage.Player(
                    "na",
                    usedText,
                    player,
                    true,
                    null
            );
        }

        // Apply immunities
        if (immunity != null) {
            if (immunity.containsKey(player.getUniqueId())) {
                for (Map.Entry<String, Integer> immunityEntry : immunity.entrySet()) {
                    Ailment immunityAilment = plugin.data().getAvailableAilmentByInternalName(
                            immunityEntry.getKey()
                    );
                    if (immunityAilment != null) {
                        Logging.debug(
                                "Remedy",
                                "use()",
                                "Adding Immunity for " +
                                        player.getDisplayName() + " - " +
                                        "Ailment: " + immunityAilment.getDisplayName() + " = " +
                                        immunityEntry.getValue()
                        );
                        CureAilment.applyImmunity(player, immunityAilment, immunityEntry.getValue());
                    }
                }
            } else {
                for (Ailment cureAilment : cures) {
                    if (cureAilment != null) {
                        if (plugin.data().getPlayerAfflictions() != null) {
                            for (Map.Entry<UUID, Set<Afflicted>> afflictions : plugin.data().getPlayerAfflictions().entrySet()) {
                                if (afflictions.getValue() != null) {
                                    for (Afflicted affliction : afflictions.getValue()) {
                                        if (affliction.getAilment().getInternalName().equals(cureAilment.getInternalName())) {
                                            CureAilment.applyImmunity(player, cureAilment, cureAilment.getImmunityModifier());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        // Cure ailments
        if (cures != null) {
            for (Ailment cureAilment : cures) {
                if (cureAilment != null) {
                    cureAilment.cure(player, true, true);
                }
            }
        }
        // Apply new effects to add
        if (addEffects != null) {
            for (PlayerPotionEffect addEffect : addEffects) {
                Logging.debug(
                        "Remedy",
                        "use()",
                        "Adding potion effect for " +
                                    player.getDisplayName() + " - " +
                                    addEffect.getPotionEffectType().getName() +
                                    " (Amplifier: " + addEffect.getAmplifier() +
                                    " Time: " + addEffect.getSeconds() + ")"
                );
                PotionEffects.addPotionEffect(player, addEffect);
            }
        }
        // Remove effects if the player has them
        if (removeEffects != null) {
            for (PlayerPotionEffect removeEffect : removeEffects) {
                Logging.debug(
                        "Remedy",
                        "use()",
                        "Adding potion effect for " +
                                player.getDisplayName() + " - " +
                                removeEffect.getPotionEffectType().getName() +
                                " (Amplifier: " + removeEffect.getAmplifier() +
                                " Time: " + removeEffect.getSeconds() + ")"
                );
                PotionEffects.reducePotionEffect(player, removeEffect);
            }
        }
        // Apply health
        if (health != 0) {
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Adjusting Health for " +
                            player.getDisplayName() + " - " +
                            "Amount = " + health
            );
            Health.adjust(player, health, true);
        }
        // Apply hunger
        if (hunger != 0) {
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Adjusting Hunger for " +
                            player.getDisplayName() + " - " +
                            "Amount = " + hunger
            );
            Hunger.adjust(player, hunger);
        }
        // Apply thirst
        if (thirst != 0) {
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Adjusting Hydration for " +
                            player.getDisplayName() + " - " +
                            "Amount = " + thirst
            );
            Hydration.adjust(player, thirst);
        }
        // Apply symptom relief
        if (symptomReliefSeconds > 0) {
            for (PotionEffectType negativeEffect : PotionEffects.negativePotionEffectTypes()) {
                PotionEffects.removePotionEffect(player, negativeEffect);
            }
            Timestamp symptomReliefUntil = TimeFunctions.addSeconds(
                    TimeFunctions.now(),
                    symptomReliefSeconds
            );
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Adjusting Symptom Relief for " +
                            player.getDisplayName() + " - " +
                            "Seconds = " + symptomReliefSeconds
            );
            plugin.data().addSymptomRelief(player.getUniqueId(), symptomReliefUntil);
        }
        // Apply cold relief
        if (coldReliefSeconds > 0) {
            Timestamp coldReliefUntil = TimeFunctions.addSeconds(
                    TimeFunctions.now(),
                    coldReliefSeconds
            );
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Adjusting Cold Relief for " +
                            player.getDisplayName() + " - " +
                            "Seconds = " + coldReliefSeconds
            );
            plugin.data().addColdRelief(player.getUniqueId(), coldReliefUntil);
        }
        // Apply heat relief
        if (heatReliefSeconds > 0) {
            Timestamp heatReliefUntil = TimeFunctions.addSeconds(
                    TimeFunctions.now(),
                    heatReliefSeconds
            );
            Logging.debug(
                    "Remedy",
                    "use()",
                    "Adjusting Heat Relief for " +
                            player.getDisplayName() + " - " +
                            "Seconds = " + heatReliefSeconds
            );
            plugin.data().addColdRelief(player.getUniqueId(), heatReliefUntil);
        }
        // Sounds
        if (food && !drink) {
            SoundFunctions.playSoundWorld(player.getLocation(), 1.0f, 1.0f, Sound.ENTITY_GENERIC_EAT);
        }
        else if (drink && !food) {
            SoundFunctions.playSoundWorld(player.getLocation(), 1.0f, 1.0f, Sound.ENTITY_GENERIC_DRINK);
        }
        else if (drink && food) {
            SoundFunctions.playSoundWorld(player.getLocation(), 1.0f, 1.0f, Sound.ENTITY_PLAYER_BURP);
        }
        else {
            // do nothing
        }

        // Uses
        if (!itemConsume) {
            // Update uses if 1.14+
            if (SpigotVersion.is114Safe()) {
                ItemStack itemStack = new ItemStack(Material.AIR, 1);
                if (equipmentSlot != null) {
                    if (equipmentSlot.equals(EquipmentSlot.HAND)) {
                        itemStack = player.getInventory().getItemInMainHand();
                    }
                    if (equipmentSlot.equals(EquipmentSlot.OFF_HAND)) {
                        itemStack = player.getInventory().getItemInOffHand();
                    }
                } else {
                    itemStack = consumedItem;
                }
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (itemMeta != null) {
                    int uses = ItemFunctions.getUses(itemMeta);
                    if (uses - 1 < 1) {
                        // break
                        ItemStack returnItemStack = new ItemStack(Material.AIR, 1);
                        if (returnItem != null) {
                            returnItemStack = new ItemStack(returnItem, 1);
                        }
                        boolean slotEmpty = false;
                        if (equipmentSlot.equals(EquipmentSlot.HAND)) {
                            appliedBy.getInventory().setItemInMainHand(
                                    InventoryFunctions.addItemStackAmount(
                                            appliedBy.getInventory().getItemInMainHand(),
                                            -1
                                    )
                            );
                            if (appliedBy.getInventory().getItemInMainHand() != null) {
                                if (appliedBy.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                    slotEmpty = true;
                                }
                            } else {
                                slotEmpty = true;
                            }
                        }
                        else if (equipmentSlot.equals(EquipmentSlot.OFF_HAND)) {
                            appliedBy.getInventory().setItemInOffHand(
                                    InventoryFunctions.addItemStackAmount(
                                            player.getInventory().getItemInOffHand(),
                                            -1
                                    )
                            );
                            if (appliedBy.getInventory().getItemInOffHand() != null) {
                                if (player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
                                    slotEmpty = true;
                                }
                            } else {
                                slotEmpty = true;
                            }
                        }
                        SoundFunctions.playerSoundPlayer(player, Sound.ITEM_SHIELD_BREAK, 1f, 1f);
                        if (!returnItemStack.getType().equals(Material.AIR)) {
                            if (slotEmpty) {
                                if (equipmentSlot.equals(EquipmentSlot.HAND)) {
                                    appliedBy.getInventory().setItemInMainHand(returnItemStack);
                                }
                                else if (equipmentSlot.equals(EquipmentSlot.OFF_HAND)) {
                                    appliedBy.getInventory().setItemInOffHand(returnItemStack);
                                }
                            } else {
                                InventoryFunctions.addItemsToPlayerInventory(player, returnItemStack, true);
                            }
                        }
                    } else {
                        ItemFunctions.setUses(itemMeta, uses - 1);
                        List<String> newLore = new ArrayList<>();
                        if (this.lore != null) {
                            for (String loreLine : this.lore) {
                                if (!loreLine.startsWith("■")) {
                                    newLore.add(loreLine);
                                }
                            }
                        }
                        newLore.add(StringFunctions.getItemUsesLine(uses - 1, this.itemUses));
                        itemMeta.setLore(StringFunctions.colorStringList(newLore));

                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
        }

        // Item Consumption
        if (itemConsume && equipmentSlot != null) {
            if (equipmentSlot == EquipmentSlot.HAND) {
                Logging.debug(
                        "Remedy",
                        "use()",
                        "Removing main hand item from " +
                                appliedBy.getDisplayName()
                );
                appliedBy.getInventory().setItemInMainHand(
                        InventoryFunctions.addItemStackAmount(
                                appliedBy.getInventory().getItemInMainHand(),
                                -1
                        )
                );
            }
            else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                Logging.debug(
                        "Remedy",
                        "use()",
                        "Removing off hand item from " +
                                appliedBy.getDisplayName()
                );
                appliedBy.getInventory().setItemInOffHand(
                        InventoryFunctions.addItemStackAmount(
                                appliedBy.getInventory().getItemInOffHand(),
                                -1
                        )
                );
            }
            if (returnItem != null) {
                if (returnItem != Material.AIR) {
                    Logging.debug(
                            "Remedy",
                            "use()",
                            "Adding return item for " +
                                    appliedBy.getDisplayName() + " - " +
                                    "Item: " + returnItem.name()
                    );
                    ItemStack returnItemStack = new ItemStack(returnItem, 1);
                    InventoryFunctions.addItemsToPlayerInventory(appliedBy, returnItemStack, true);
                }
            }
        }

    }
    //endregion
    //region Getters & Setters
    /**
     * Gets the NamespacedKey for the Remedy
     * @return NamespacedKey The key for this Remedy
     */
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    /**
     * Gets the display name for the Remedy
     * @return String The display neme for this Remedy
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the lore for the Remedy
     * @return List<String> Lore for the ItemStack
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * Gets the used text
     * @return String The text to send to the player when Remedy is used
     */
    public String getUsedText() {
        return usedText;
    }

    /**
     * Gets the base item the ItemStack should be created from
     * @return Material The material the ItemStack is based on
     */
    public Material getBaseItem() {
        return baseItem;
    }

    /**
     * Gets the return item the ItemStack should be created from
     * @return Material The material the ItemStack is based on that is returned after Remedy use
     */
    public Material getReturnItem() {
        return returnItem;
    }

    /**
     * Gets the potion color
     * @return Color The potion color to apply for the POTION ItemStack
     */
    public Color getPotionColor() {
        return potionColor;
    }

    /**
     * Gets if the ItemStack should glow
     * @return boolean True/False for if the ItemStack should glow
     */
    public boolean isEnchantedGlow() {
        return enchantedGlow;
    }

    /**
     * Gets the Recipe for the Remedy
     * @return Recipe The recipe of the Remedy.  Null if not craftable
     */
    public EpidemicRecipe getRecipe() {
        return recipe;
    }

    /**
     * Should the item require permission to be crafted
     * @return boolean True/False for if the epidemic.craft.<key> is required to craft
     */
    public boolean isRequireCraftPerm() {
        return requireCraftPerm;
    }

    /**
     * Gets the amount of items that should be created when crafting
     * @return int The number of items to create when crafting
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Is the item food?
     * @return booelan True/False if the remedy is food
     */
    public boolean isFood() {
        return food;
    }

    /**
     * Is the item food?
     * @return booelan True/False if the remedy is a drink
     */
    public boolean isDrink() {
        return drink;
    }

    /**
     * Is the item consumed on use?
     * @return booelan True/False if the item should be consumed/removed/replaced after use
     */
    public boolean isItemConsume() {
        if (this.food || this.drink) {
            return false;
        } else {
            return itemConsume;
        }
    }

    /**
     * Is the item uses available for non-consumed non-edible items?
     * @return int Number of uses available (0 = infinite)
     */
    public int getItemUses() {
        if (this.food || this.drink || this.itemConsume) {
            return 0;
        } else {
            return itemUses;
        }
    }

    /**
     * Gets a list of ailments that will be cured by this Remedy
     * @return List<Ailment> A list of ailments that can be cured
     */
    public List<Ailment> getCures() {
        return cures;
    }

    /**
     * A list of Effects to add to the player when using the Remedy
     * @return List<Effect> A list of potion effects (with amplifier and time) to add to player when using the remedy
     */
    public List<PlayerPotionEffect> getAddEffects() {
        return addEffects;
    }

    /**
     * A list of Effects to remove from the player when using the Remedy
     * @return List<Effect> A list of potion effects (with amplifier and time) to remove from player when using the remedy
     */
    public List<PlayerPotionEffect> getRemoveEffects() {
        return removeEffects;
    }

    /**
     * A map of immunity entries to modify when using the remedy
     * @return Map<String, Integer> String is the ailment key, integer is the amount to modify the immunity for
     */
    public Map<String, Integer> getImmunity() {
        return immunity;
    }

    /**
     * Gets the number of health points to restore (or remove if negative) for the player when using the remedy
     * @return int Number of health points to restore/remove for the player
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets the number of hunger points to restore (or remove if negative) for the player when using the remedy
     * @return int Number of hunger points to restore/remove for the player
     */
    public int getHunger() {
        return hunger;
    }

    /**
     * Gets the number of thirst points to restore (or remove if negative) for the player when using the remedy
     * @return int Number of thirst points to restore/remove for the player
     */
    public int getThirst() {
        return thirst;
    }

    /**
     * Gets number of seconds to apply symptom relief for.  If < 1, do not apply
     * @return int Number of seconds of symptom relief to apply
     */
    public int getSymptomReliefSeconds() {
        return symptomReliefSeconds;
    }

    /**
     * Gets number of seconds to apply cold relief for.  If < 1, do not apply
     * @return int Number of seconds of cold relief to apply
     */
    public int getColdReliefSeconds() {
        return coldReliefSeconds;
    }

    /**
     * Gets number of seconds to apply heat relief for.  If < 1, do not apply
     * @return int Number of seconds of heat relief to apply
     */
    public int getHeatReliefSeconds() {
        return heatReliefSeconds;
    }

    /**
     * Gets if the remedy can be applied to other players
     * @return If true, remedy can be applied to others
     */
    public boolean getApplyOthers() {
        return this.applyOthers;
    }

    //endregion
}
