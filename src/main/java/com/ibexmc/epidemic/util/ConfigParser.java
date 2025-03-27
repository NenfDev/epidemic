package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.helpers.PlayerPotionEffect;
import com.ibexmc.epidemic.helpers.recipe.EpidemicCraftRecipe;
import com.ibexmc.epidemic.helpers.recipe.EpidemicFurnaceRecipe;
import com.ibexmc.epidemic.helpers.recipe.EpidemicRecipe;
import com.ibexmc.epidemic.util.functions.ColorFunctions;
import com.ibexmc.epidemic.util.functions.PotionFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {

    //region Objects
    Epidemic plugin;
    FileConfiguration config;
    //endregion
    //region Constructors
    public ConfigParser(Epidemic plugin, File configFile) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }
    //endregion
    //region Methods

    /**
     * Gets a list of keys for this configItem
     * For a list of all root level keys, use ""
     * @param configItem Config Item
     * @return List of keys
     */
    public List<String> getKeys(String configItem) {
        List<String> keys = new ArrayList<>();
        for (String key : this.config.getConfigurationSection(configItem).getKeys(false)) {
            keys.add(key);
        }
        return keys;
    }

    /**
     * Gets an Integer config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getIntValue(String configItem, Integer defaultValue, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isInt(configItem)) {
                    return new ConfigReturn(true, this.config.getInt(configItem));
                } else {
                    Error.save(
                            "ConfigParser.getValue.001",
                            "ConfigParser",
                            "getValue(String configItem, Integer defaultValue, boolean required)",
                            "Config item is not int",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, defaultValue);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, Integer defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, Integer defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a double config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getDoubleValue(String configItem, double defaultValue, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isDouble(configItem) || this.config.isInt(configItem)) {
                    return new ConfigReturn(true, this.config.getDouble(configItem));
                } else {
                    Error.save(
                            "ConfigParser.getDoubleValue.001",
                            "ConfigParser",
                            "getDoubleValue(String configItem, double defaultValue, boolean required)",
                            "Config item is not an int or double",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, defaultValue);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getDoubleValue.002",
                            "ConfigParser",
                            "getDoubleValue(String configItem, double defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, double defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a String config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getStringValue(String configItem, String defaultValue, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isString(configItem)) {
                    return new ConfigReturn(true, this.config.getString(configItem));
                } else {
                    Error.save(
                            "ConfigParser.getValue.001",
                            "ConfigParser",
                            "getValue(String configItem, String defaultValue, boolean required)",
                            "Config item is not string",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, defaultValue);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, String defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, String defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a boolean config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getBooleanValue(String configItem, boolean defaultValue, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isBoolean(configItem)) {
                    return new ConfigReturn(true, this.config.getString(configItem));
                } else {
                    Error.save(
                            "ConfigParser.getValue.001",
                            "ConfigParser",
                            "getValue(String configItem, boolean defaultValue, boolean required)",
                            "Config item is not boolean",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, defaultValue);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, boolean defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, boolean defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a string list config item
     * @param configItem Configuration item to check
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getStringList(String configItem, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isList(configItem)) {
                    return new ConfigReturn(true, this.config.getStringList(configItem));
                } else {
                    Error.save(
                            "ConfigParser.getValue.001",
                            "ConfigParser",
                            "getValue(String configItem, List<String> defaultValue, boolean required)",
                            "Config item is not a list",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, new ArrayList<String>());
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, boolean defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, new ArrayList<String>());
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, boolean defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, new ArrayList<String>());
        }
    }

    /**
     * Gets a Material config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getMaterialValue(String configItem, Material defaultValue, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isString(configItem)) {
                    Material material = StringFunctions.stringToMaterial(this.config.getString(configItem));
                    return new ConfigReturn(true, material);
                } else {
                    Error.save(
                            "ConfigParser.getValue.001",
                            "ConfigParser",
                            "getValue(String configItem, Material defaultValue, boolean required)",
                            "Config item is not string",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, defaultValue);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, Material defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, Material defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a Material config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getPotionTypeValue(String configItem, PotionType defaultValue, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isString(configItem)) {
                    PotionType potionType = PotionType.valueOf(this.config.getString(configItem));
                    return new ConfigReturn(true, potionType);
                } else {
                    Error.save(
                            "ConfigParser.getValue.001",
                            "ConfigParser",
                            "getValue(String configItem, PotionType defaultValue, boolean required)",
                            "Config item is not string",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, defaultValue);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, PotionType defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, Material defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a material from a config item
     * @param configItem Configuration item to check
     * @param row Row to get
     * @param column Column to get
     * @return Material for that row/column.  Returns Material.AIR if not found
     */
    public Material getMaterialForCraftRecipe(String configItem, String row, String column) {
        if (this.config.contains(configItem + ".craft." + row + "." + column)) {
            if (this.config.isString(configItem + ".craft." + row + "." + column)) {
                String materialName = config.getString(
                        configItem + ".craft." + row + "." + column
                );
                Material material = StringFunctions.stringToMaterial(materialName);
                if (material == null) {
                    material = Material.BARRIER;
                }
                return material;
            } else {
                return Material.AIR;
            }
        } else {
            return Material.AIR;
        }
    }

    /**
     * Gets a Recipe from a config item.  Will lookup either craft or furnace recipes
     * @param configItem Configuration item to check
     * @return ConfigReturn object
     */
    public ConfigReturn getRecipeValue(String configItem) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.contains(configItem + ".craft")) {
                    EpidemicCraftRecipe.Row top = new EpidemicCraftRecipe.Row(
                            this.getMaterialForCraftRecipe(configItem, "top", "left"),
                            this.getMaterialForCraftRecipe(configItem, "top", "center"),
                            this.getMaterialForCraftRecipe(configItem, "top", "right")
                    );
                    EpidemicCraftRecipe.Row middle = new EpidemicCraftRecipe.Row(
                            this.getMaterialForCraftRecipe(configItem, "middle", "left"),
                            this.getMaterialForCraftRecipe(configItem, "middle", "center"),
                            this.getMaterialForCraftRecipe(configItem, "middle", "right")
                    );
                    EpidemicCraftRecipe.Row bottom = new EpidemicCraftRecipe.Row(
                            this.getMaterialForCraftRecipe(configItem, "bottom", "left"),
                            this.getMaterialForCraftRecipe(configItem, "bottom", "center"),
                            this.getMaterialForCraftRecipe(configItem, "bottom", "right")
                    );
                    EpidemicRecipe recipe = new EpidemicCraftRecipe(top, middle, bottom);
                    return new ConfigReturn(true, recipe);
                } else if (this.config.contains(configItem + ".furnace")) {
                    if (this.config.contains(configItem + ".furnace.item")) {
                        Material item = Material.AIR;
                        int experience = 0;
                        int time = 3;
                        if (this.config.isString(configItem + ".furnace.item")) {
                            item = StringFunctions.stringToMaterial(
                                    this.config.getString(configItem + ".furnace.item")
                            );
                            if (item != null) {
                                if (item == Material.AIR) {
                                    // error - item is air
                                    return new ConfigReturn(false, null);
                                }
                            } else {
                                // error - item is null
                                return new ConfigReturn(false, null);
                            }
                        } else {
                            // error - furnace item is missing
                            return new ConfigReturn(false, null);
                        }
                        if (this.config.contains(configItem + ".furnace.experience")) {
                            if (this.config.isInt(configItem + ".furnace.experience")) {
                                experience = this.config.getInt(configItem + ".furnace.experience");
                            }
                        }
                        if (this.config.contains(configItem + ".furnace.time")) {
                            if (this.config.isInt(configItem + ".furnace.time")) {
                                time = this.config.getInt(configItem + ".furnace.time");
                            }
                        }
                        EpidemicFurnaceRecipe recipe = new EpidemicFurnaceRecipe(
                                item,
                                experience,
                                time
                        );
                        return new ConfigReturn(true, recipe);
                    } else {
                        return new ConfigReturn(false, null);
                    }
                } else {
                    return new ConfigReturn(false, null);
                }
            } else {
                return new ConfigReturn(false, null);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, Recipe defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, null);
        }
    }

    /**
     * Gets a potion effect list from a config item
     * @param configItem Configuration item to check
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getEffectList(String configItem, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isConfigurationSection(configItem)) {

                    List<PlayerPotionEffect> effects = new ArrayList<>();


                    Object[] fields = this.config.getConfigurationSection(configItem).getKeys(false).toArray();
                    for (Object key : fields) {

                        String potionEffectName = key.toString();
                        PotionEffectType potionEffect = PotionFunctions.potionEffectTypeFromName(
                                potionEffectName
                        );
                        if (potionEffect != null) {
                            int amplifier = 1;
                            int seconds = 0;

                            if (this.config.contains(configItem + "." + key.toString() + ".amplifier")) {
                                if (this.config.isInt(configItem + "." + key.toString() + ".amplifier")) {
                                    amplifier = config.getInt(configItem + "." + key.toString() + ".amplifier");
                                }
                            }
                            if (this.config.contains(configItem + "." + key.toString() + ".time")) {
                                if (this.config.isInt(configItem + "." + key.toString() + ".time")) {
                                    seconds = config.getInt(configItem + "." + key.toString() + ".time");
                                }
                            }

                            PlayerPotionEffect effect = new PlayerPotionEffect(potionEffect, amplifier, seconds);
                            effects.add(effect);
                        }
                    }

                    return new ConfigReturn(true, effects);
                } else {
                    return new ConfigReturn(false, new ArrayList<PlayerPotionEffect>());
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getValue.002",
                            "ConfigParser",
                            "getValue(String configItem, List<Effect> defaultValue, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, new ArrayList<PlayerPotionEffect>());
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getValue.003",
                    "ConfigParser",
                    "getValue(String configItem, List<Effect> defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, new ArrayList<PlayerPotionEffect>());
        }
    }

    /**
     * Gets a color from a config item
     * @param configItem Configuration item to check
     * @param defaultValue Default value
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getColor(String configItem, Color defaultValue, boolean required) {
        try {
            Color returnValue = defaultValue;
            if (this.config.contains(configItem)) {
                if (config.contains(configItem + ".name")) {
                    returnValue = ColorFunctions.colorFromName(config.getString(configItem + ".name"));
                } else if (
                        config.contains(configItem + ".red") &&
                                config.contains(configItem + ".green") &&
                                config.contains(configItem + ".blue")
                ) {
                    if (
                            config.isInt(configItem + ".red") &&
                                    config.isInt(configItem + ".green") &&
                                    config.isInt(configItem + ".blue")
                    ) {
                        returnValue = Color.fromRGB(
                                config.getInt(configItem + ".red"),
                                config.getInt(configItem + ".green"),
                                config.getInt(configItem + ".blue")
                        );
                    }

                }

                return new ConfigReturn(true, returnValue);

            } else {
                return new ConfigReturn(false, defaultValue);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getColor.002",
                    "ConfigParser",
                    "getValue(String configItem, Color defaultValue, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, defaultValue);
        }
    }

    /**
     * Gets a Configuration Section from a config item
     * @param configItem Configuration item to check
     * @param required If true, is required
     * @return ConfigReturn object
     */
    public ConfigReturn getConfigSection(String configItem, boolean required) {
        try {
            if (this.config.contains(configItem)) {
                if (this.config.isConfigurationSection(configItem)) {

                    ConfigurationSection configSection = this.config.getConfigurationSection(configItem);

                    return new ConfigReturn(true, configSection);
                } else {
                    Error.save(
                            "ConfigParser.getConfigSection.001",
                            "ConfigParser",
                            "getConfigSection(String configItem, boolean required)",
                            "Config item is not config section",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                    return new ConfigReturn(false, null);
                }
            } else {
                if (required) {
                    Error.save(
                            "ConfigParser.getConfigSection.002",
                            "ConfigParser",
                            "getConfigSection(String configItem, boolean required)",
                            "Config item is missing and required",
                            "Config Item: " + configItem,
                            Error.Severity.WARN,
                            null
                    );
                }
                return new ConfigReturn(false, null);
            }
        } catch (Exception ex) {
            Error.save(
                    "ConfigParser.getConfigSection.003",
                    "ConfigParser",
                    "getConfigSection(String configItem, boolean required)",
                    "Unexpected Error getting String",
                    "Config Item: " + configItem + "\nError: " + ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
            return new ConfigReturn(false, null);
        }
    }
    //endregion
    //region Sub-Classes
    public static class ConfigReturn {
        //region Objects
        private boolean success;
        private Object returnObject;
        //endregion
        //region Constructors
        public ConfigReturn(boolean success, Object returnObject) {
            this.success = success;
            this.returnObject = returnObject;
        }
        //endregion
        //region Getters

        /**
         * Checks if the config lookup was successful
         * @return If true, was successful
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Gets the return object
         * @return Object being returned
         */
        public Object getReturnObject() {
            return returnObject;
        }

        /**
         * Gets the return object as a String
         * @return String of the returned object
         */
        public String getString() {
            if (returnObject instanceof String) {
                return (String) returnObject;
            } else {
                return null;
            }
        }

        /**
         * Gets the return object as a boolean
         * @return boolean of the returned object
         */
        public Boolean getBoolean() {
            if (returnObject instanceof String) {
                String returnString = (String) returnObject;
                if ("true".equalsIgnoreCase(returnString)) {
                    return true;
                }
                if ("false".equalsIgnoreCase(returnString)) {
                    return false;
                }

            }
            return false;
        }

        /**
         * Gets the return object as an Integer
         * @return Integer of the returned object
         */
        public Integer getInt() {
            if (returnObject instanceof Integer) {
                return (Integer) returnObject;
            } else {
                return -1;
            }
        }

        /**
         * Gets the return object as a double
         * @return Double of the returned object
         */
        public Double getDouble() {
            if (returnObject instanceof Double) {
                return (Double) returnObject;
            } else {
                return (double) -1;
            }
        }

        /**
         * Gets the return object as a String List
         * @return String List of the returned object
         */
        public List<String> getStringList() {
            if (returnObject instanceof List) {
                return (List<String>) returnObject;
            } else {
                return new ArrayList<>();
            }
        }

        /**
         * Gets the return object as a Material
         * @return Material of the returned object
         */
        public Material getMaterial() {
            if (returnObject instanceof Material) {
                return (Material) returnObject;
            } else {
                return Material.AIR;
            }
        }

        /**
         * Gets the return object as a PotionType
         * @return PotionType of the returned object
         */
        public PotionType getPotionType() {
            if (returnObject instanceof PotionType) {
                return (PotionType) returnObject;
            } else {
                return PotionType.WATER;
            }
        }

        /**
         * Gets the return object either an EpidemicCraftRecipe or EpidemicFurnaceRecipe
         * @return EpidemicCraftRecipe or EpidemicFurnaceRecipe of the returned object
         */
        public EpidemicRecipe getRecipe() {
            if (returnObject instanceof EpidemicRecipe) {
                if (returnObject instanceof EpidemicCraftRecipe) {
                    return (EpidemicCraftRecipe) returnObject;
                }
                if (returnObject instanceof EpidemicFurnaceRecipe) {
                    return (EpidemicFurnaceRecipe) returnObject;
                }
                return (EpidemicRecipe) returnObject;
            } else {
                return null;
            }
        }

        /**
         * Gets the return object as a Color
         * @return Color of the returned object
         */
        public Color getColor() {
            if (returnObject instanceof Color) {
                return (Color) returnObject;
            } else {
                return Color.WHITE;
            }
        }

        /**
         * Gets the return object as a List of PlayerPotionEffect
         * @return List of PlayerPotionEffects of the returned object
         */
        public List<PlayerPotionEffect> getEffects() {
            if (returnObject instanceof List) {
                return (List<PlayerPotionEffect>) returnObject;
            } else {
                return new ArrayList<>();
            }
        }

        /**
         * Gets the return object as a Configuration Section
         * @return Configuration Section of the returned object
         */
        public ConfigurationSection getConfigSection() {
            if (returnObject instanceof ConfigurationSection) {
                return (ConfigurationSection) returnObject;
            } else {
                return null;
            }
        }
        //endregion
    }
    //endregion
}
