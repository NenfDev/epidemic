package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.functions.FileFunctions;

import org.bukkit.Material;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    //region Objects
    Epidemic plugin;
    ConfigParser ymlParser;
    // Misc
    private String pluginVersion = "";
    private boolean debugLog = false;
    private boolean debugLogToFile = false;
    private boolean clearAilmentOnDeath = true;
    private int contagiousRadius = 5;
    private int afflictedTime = 60;
    private int symptomTime = 20;
    private int papiTime = 20;
    private List<String> preventAfflictWorlds = new ArrayList<>();
    private List<String> preventSymptomWorlds = new ArrayList<>();
    private List<String> preventAfflictWorldGuardRegions = new ArrayList<>();
    private List<String> preventSymptomWorldGuardRegions = new ArrayList<>();
    private int randomChance = 10000;
    private boolean preventAilmentWorldGuardPVPDeny = false;
    private int newPlayerInvincibleDays = 0;
    // Thirst
    private boolean enableThirst = true;
    private int thirstSeconds = 60;
    private List<String> preventThirstWorlds;
    private int dirtyWaterChance = 0;
    private int respawnThirstLevel = 100;
    private boolean respawnResetThirst = false;
    private boolean disableCauldron = false;
    private boolean disableWaterBowl = false;
    private boolean disableWaterHands = false;
    private boolean useThirstBar = true;
    private boolean useThirstText = false;
    private boolean displayThirstWarning = false;
    private boolean displayDrinkNotification = false;
    private boolean disableDirtyCheckPotion = false;
    private int thirstDamage = 1;
    // Temperature
    private boolean enableTemperature = true;
    private int tempSeconds = 60;
    private List<String> preventTempWorlds = new ArrayList<>();
    private boolean tempWarnPlayers = false;
    private boolean autoDiscoverRecipes = false;
    // Equipment
    private boolean disableEpidemicEquipment = false;

    // GUI
    private Material guiRemedyRecipeBorderItem = Material.BLACK_STAINED_GLASS_PANE;
    private int guiRemedyRecipeBorderCMD = 0;
    private Material guiRemedyRecipeBackItem = Material.RED_STAINED_GLASS_PANE;
    private int guiRemedyRecipeBackCMD = 0;

    // MythicMobs
    private java.util.Map<String, com.ibexmc.epidemic.dependencies.MythicMobDisease> mythicMobDiseases = new java.util.HashMap<>();


    //endregion
    //region Constructor
    public ConfigManager(Epidemic plugin) {
        this.plugin = plugin;
        File configFile = FileFunctions.getConfigYml();
        if (configFile != null) {
            ymlParser = new ConfigParser(plugin, configFile);
        }
    }
    //endregion
    //region Methods

    public void load() {

        //region Debug Logging
        ConfigParser.ConfigReturn crDebugFlag = ymlParser.getBooleanValue(
                "debug_logging",
                false,
                false
        );
        if (crDebugFlag.isSuccess()) {
            this.debugLog = crDebugFlag.getBoolean();
        } else {
            this.debugLog = false;
        }
        //endregion
        //region Debug Logging to File
        ConfigParser.ConfigReturn crDebugFileFlag = ymlParser.getBooleanValue(
                "debug_to_file",
                false,
                false
        );
        if (crDebugFileFlag.isSuccess()) {
            this.debugLogToFile = crDebugFileFlag.getBoolean();
        } else {
            this.debugLogToFile = false;
        }
        //endregion
        //region Clear Ailment on Death
        ConfigParser.ConfigReturn crClearAilmentDeath = ymlParser.getBooleanValue(
                "clear_ailment_on_death",
                false,
                false
        );
        if (crClearAilmentDeath.isSuccess()) {
            this.clearAilmentOnDeath = crClearAilmentDeath.getBoolean();
        } else {
            this.clearAilmentOnDeath = false;
        }
        //endregion
        //region Contagious Radius
        ConfigParser.ConfigReturn crContagiousRadius = ymlParser.getIntValue(
                "contagious_radius",
                5,
                false
        );
        if (crContagiousRadius.isSuccess()) {
            this.contagiousRadius = crClearAilmentDeath.getInt();
        } else {
            this.contagiousRadius = 5;
        }
        //endregion
        //region Enable Temperature
        ConfigParser.ConfigReturn crEnableTemp = ymlParser.getBooleanValue(
                "enable_temp",
                true,
                false
        );
        if (crEnableTemp.isSuccess()) {
            this.enableTemperature = crEnableTemp.getBoolean();
        } else {
            this.enableTemperature = false;
        }
        //endregion
        //region Afflicted Time
        ConfigParser.ConfigReturn crAfflictedTime = ymlParser.getIntValue(
                "afflicted_time",
                60,
                false
        );
        if (crAfflictedTime.isSuccess()) {
            this.afflictedTime = crAfflictedTime.getInt();
        } else {
            this.afflictedTime = 60;
        }
        //endregion
        //region Symptom Time
        ConfigParser.ConfigReturn crSymptomTime = ymlParser.getIntValue(
                "symptom_time",
                20,
                false
        );
        if (crSymptomTime.isSuccess()) {
            this.symptomTime = crSymptomTime.getInt();
        } else {
            this.symptomTime = 20;
        }
        //endregion
        //region PAPI Update Time
        ConfigParser.ConfigReturn crPapiTime = ymlParser.getIntValue(
                "papi_update_time",
                10,
                false
        );
        if (crPapiTime.isSuccess()) {
            this.papiTime = crPapiTime.getInt();
        } else {
            this.papiTime = 10;
        }
        //endregion
        //region New Player Invincible Days
        ConfigParser.ConfigReturn crInvincibleDays = ymlParser.getIntValue(
                "new_player_invincible_days",
                0,
                false
        );
        if (crInvincibleDays.isSuccess()) {
            this.newPlayerInvincibleDays = crInvincibleDays.getInt();
        } else {
            this.newPlayerInvincibleDays = 10;
        }
        //endregion
        //region Prevent Afflict Worlds
        ConfigParser.ConfigReturn crPreventAfflictWorlds = ymlParser.getStringList(
                "prevent_afflict_worlds",
                false
        );
        if (crPreventAfflictWorlds.isSuccess()) {
            this.preventAfflictWorlds = crPreventAfflictWorlds.getStringList();
        } else {
            this.preventAfflictWorlds = new ArrayList<>();
        }
        //endregion
        //region Prevent Symptom Worlds
        ConfigParser.ConfigReturn crPreventSymptomWorlds = ymlParser.getStringList(
                "prevent_symptom_worlds",
                false
        );
        if (crPreventSymptomWorlds.isSuccess()) {
            this.preventSymptomWorlds = crPreventSymptomWorlds.getStringList();
        } else {
            this.preventSymptomWorlds = new ArrayList<>();
        }
        //endregion
        //region Prevent Afflict WorldGuard Regions
        ConfigParser.ConfigReturn crPreventAfflictWGRegions = ymlParser.getStringList(
                "prevent_ailment_worldguard_regions",
                false
        );
        if (crPreventAfflictWGRegions.isSuccess()) {
            this.preventAfflictWorldGuardRegions = crPreventAfflictWGRegions.getStringList();
        } else {
            this.preventAfflictWorldGuardRegions = new ArrayList<>();
        }
        //endregion
        //region Prevent Symptom WorldGuard Regions
        ConfigParser.ConfigReturn crPreventSymptomWGRegions = ymlParser.getStringList(
                "prevent_symptom_worldguard_regions",
                false
        );
        if (crPreventSymptomWorlds.isSuccess()) {
            this.preventSymptomWorldGuardRegions = crPreventSymptomWGRegions.getStringList();
        } else {
            this.preventSymptomWorldGuardRegions = new ArrayList<>();
        }
        //endregion
        //region Random Chance
        ConfigParser.ConfigReturn crRandomChance = ymlParser.getIntValue(
                "random_chance",
                10000,
                false
        );
        if (crRandomChance.isSuccess()) {
            this.randomChance = crRandomChance.getInt();
        } else {
            this.randomChance = 10000;
        }
        //endregion
        //region Prevent Ailment PVP Deny areas
        ConfigParser.ConfigReturn crWorldGuardPVP = ymlParser.getBooleanValue(
                "prevent_ailment_worldguard_pvp_deny",
                true,
                false
        );
        if (crWorldGuardPVP.isSuccess()) {
            this.preventAilmentWorldGuardPVPDeny = crWorldGuardPVP.getBoolean();
        } else {
            this.preventAilmentWorldGuardPVPDeny = false;
        }
        //endregion
        //region Temp Seconds
        ConfigParser.ConfigReturn crTempTime = ymlParser.getIntValue(
                "temp_seconds",
                30,
                false
        );
        if (crTempTime.isSuccess()) {
            this.tempSeconds = crTempTime.getInt();
        } else {
            this.tempSeconds = 30;
        }
        //endregion
        //region Prevent Temp Worlds
        ConfigParser.ConfigReturn crPreventTempWorlds = ymlParser.getStringList(
                "prevent_temp_worlds",
                false
        );
        if (crPreventTempWorlds.isSuccess()) {
            this.preventTempWorlds = crPreventTempWorlds.getStringList();
        } else {
            this.preventTempWorlds = new ArrayList<>();
        }
        //endregion
        //region Temp Warn Players
        ConfigParser.ConfigReturn crTempWarnPlayers = ymlParser.getBooleanValue(
                "temp_warn_players",
                true,
                false
        );
        if (crTempWarnPlayers.isSuccess()) {
            this.tempWarnPlayers = crTempWarnPlayers.getBoolean();
        } else {
            this.tempWarnPlayers = false;
        }
        //endregion
        //region Enable Thirst
        ConfigParser.ConfigReturn crEnableThirst = ymlParser.getBooleanValue(
                "enable_thirst",
                true,
                false
        );
        if (crEnableThirst.isSuccess()) {
            this.enableThirst = crEnableThirst.getBoolean();
        } else {
            this.enableThirst = false;
        }
        //endregion
        //region Thirst Seconds
        ConfigParser.ConfigReturn crThirstTime = ymlParser.getIntValue(
                "thirst_seconds",
                30,
                false
        );
        if (crThirstTime.isSuccess()) {
            this.thirstSeconds = crThirstTime.getInt();
        } else {
            this.thirstSeconds = 30;
        }
        //endregion
        //region Prevent Thirst Worlds
        ConfigParser.ConfigReturn crPreventThirstWorlds = ymlParser.getStringList(
                "prevent_thirst_worlds",
                false
        );
        if (crPreventThirstWorlds.isSuccess()) {
            this.preventThirstWorlds = crPreventThirstWorlds.getStringList();
        } else {
            this.preventThirstWorlds = new ArrayList<>();
        }
        //endregion
        //region Respawn Thirst Level
        ConfigParser.ConfigReturn crRespawnThirstLevel = ymlParser.getIntValue(
                "respawn_thirst_level",
                100,
                false
        );
        if (crRespawnThirstLevel.isSuccess()) {
            this.respawnThirstLevel = crRespawnThirstLevel.getInt();
        } else {
            this.respawnThirstLevel = 100;
        }
        //endregion
        //region Respawn Reset Thirst
        ConfigParser.ConfigReturn crResetThirst = ymlParser.getBooleanValue(
                "respawn_reset_thirst",
                false,
                false
        );
        if (crResetThirst.isSuccess()) {
            this.respawnResetThirst = crResetThirst.getBoolean();
        } else {
            this.respawnResetThirst = false;
        }
        //endregion
        //region Dirty Water Chance
        ConfigParser.ConfigReturn crDirtyWaterChance = ymlParser.getIntValue(
                "dirty_water_chance",
                50,
                false
        );
        if (crDirtyWaterChance.isSuccess()) {
            this.dirtyWaterChance = crDirtyWaterChance.getInt();
        } else {
            this.dirtyWaterChance = 50;
        }
        //endregion
        //region Use Thirst Bar
        ConfigParser.ConfigReturn crUseThirstBar = ymlParser.getBooleanValue(
                "use_thirst_bar",
                true,
                false
        );
        if (crUseThirstBar.isSuccess()) {
            this.useThirstBar = crUseThirstBar.getBoolean();
        } else {
            this.useThirstBar = false;
        }
        //endregion
        //region Use Thirst Text
        ConfigParser.ConfigReturn crUseThirstText = ymlParser.getBooleanValue(
                "use_thirst_text",
                false,
                false
        );
        if (crUseThirstText.isSuccess()) {
            this.useThirstText = crUseThirstText.getBoolean();
        } else {
            this.useThirstText = false;
        }
        //endregion
        //region Display Thirst Warning
        ConfigParser.ConfigReturn crDisplayThirstWarning = ymlParser.getBooleanValue(
                "display_thirst_warning",
                true,
                false
        );
        if (crDisplayThirstWarning.isSuccess()) {
            this.displayThirstWarning = crDisplayThirstWarning.getBoolean();
        } else {
            this.displayThirstWarning = false;
        }
        //endregion
        //region Display Drink Notification
        ConfigParser.ConfigReturn crDisplayDrinkNotification = ymlParser.getBooleanValue(
                "display_drink_notification",
                true,
                false
        );
        if (crDisplayDrinkNotification.isSuccess()) {
            this.displayDrinkNotification = crDisplayDrinkNotification.getBoolean();
        } else {
            this.displayDrinkNotification = false;
        }
        //endregion
        //region Disable Cauldron
        ConfigParser.ConfigReturn crDisableCauldron = ymlParser.getBooleanValue(
                "disable_cauldron",
                false,
                false
        );
        if (crDisableCauldron.isSuccess()) {
            this.disableCauldron = crDisableCauldron.getBoolean();
        } else {
            this.disableCauldron = false;
        }
        //endregion
        //region Disable Water Bowl
        ConfigParser.ConfigReturn crDisableWaterBowl = ymlParser.getBooleanValue(
                "disable_water_bowl",
                false,
                false
        );
        if (crDisableWaterBowl.isSuccess()) {
            this.disableWaterBowl = crDisableWaterBowl.getBoolean();
        } else {
            this.disableWaterBowl = false;
        }
        //endregion
        //region Disable Water Bowl
        ConfigParser.ConfigReturn crDisableWaterHands = ymlParser.getBooleanValue(
                "disable_water_hands",
                false,
                false
        );
        if (crDisableWaterHands.isSuccess()) {
            this.disableWaterHands = crDisableWaterHands.getBoolean();
        } else {
            this.disableWaterHands = false;
        }
        //endregion
        //region Disable Dirty Check for Potions
        ConfigParser.ConfigReturn crDisableDirtyPotion = ymlParser.getBooleanValue(
                "disable_dirty_check_from_bottle",
                false,
                false
        );
        if (crDisableDirtyPotion.isSuccess()) {
            this.disableDirtyCheckPotion = crDisableDirtyPotion.getBoolean();
        } else {
            this.disableDirtyCheckPotion = false;
        }
        //endregion
        //region Thirst Damage
        ConfigParser.ConfigReturn crThirstDamage = ymlParser.getIntValue(
                "thirst_damage",
                1,
                false
        );
        if (crThirstDamage.isSuccess()) {
            this.thirstDamage = crThirstDamage.getInt();
            if (this.thirstDamage > 20) {
                this.thirstDamage = 20;
            }
        } else {
            this.thirstDamage = 1;
        }
        //endregion
        //region Auto-Discover Recipes
        ConfigParser.ConfigReturn crAutoDiscoverRecipes = ymlParser.getBooleanValue(
                "auto_discover_recipes",
                false,
                false
        );
        if (crAutoDiscoverRecipes.isSuccess()) {
            this.autoDiscoverRecipes = crAutoDiscoverRecipes.getBoolean();
        } else {
            this.autoDiscoverRecipes = false;
        }
        //endregion
        ConfigParser.ConfigReturn crDisableEpiEquipment = ymlParser.getBooleanValue(
                "disable_epidemic_equipment",
                false,
                false
        );
        if (crDisableEpiEquipment.isSuccess()) {
            this.disableEpidemicEquipment = crDisableEpiEquipment.getBoolean();
        } else {
            this.disableEpidemicEquipment = false;
        }

        //region GUI
        ConfigParser.ConfigReturn crGuiBorderItem = ymlParser.getMaterialValue(
                "gui.remedy_recipe.border_item",
                Material.BLACK_STAINED_GLASS_PANE,
                false
        );
        if (crGuiBorderItem.isSuccess()) {
            this.guiRemedyRecipeBorderItem = crGuiBorderItem.getMaterial();
        }

        ConfigParser.ConfigReturn crGuiBorderCMD = ymlParser.getIntValue(
                "gui.remedy_recipe.border_cmd",
                0,
                false
        );
        if (crGuiBorderCMD.isSuccess()) {
            this.guiRemedyRecipeBorderCMD = crGuiBorderCMD.getInt();
        }

        ConfigParser.ConfigReturn crGuiBackItem = ymlParser.getMaterialValue(
                "gui.remedy_recipe.back_item",
                Material.RED_STAINED_GLASS_PANE,
                false
        );
        if (crGuiBackItem.isSuccess()) {
            this.guiRemedyRecipeBackItem = crGuiBackItem.getMaterial();
        }

        ConfigParser.ConfigReturn crGuiBackCMD = ymlParser.getIntValue(
                "gui.remedy_recipe.back_cmd",
                0,
                false
        );
        if (crGuiBackCMD.isSuccess()) {
            this.guiRemedyRecipeBackCMD = crGuiBackCMD.getInt();
        }
        //endregion

        // MythicMobs loading
        mythicMobDiseases.clear();
        ConfigParser.ConfigReturn crMythicMobs = ymlParser.getConfigSection("mythicmobs", false);
        if (crMythicMobs.isSuccess()) {
            org.bukkit.configuration.ConfigurationSection mmSection = crMythicMobs.getConfigSection();
            if (mmSection != null) {
                for (String mobType : mmSection.getKeys(false)) {
                    String ailment = mmSection.getString(mobType + ".ailment");
                    int chance = mmSection.getInt(mobType + ".chance", 100);
                    double severity = mmSection.getDouble(mobType + ".severity", 1.0);
                    int cooldown = mmSection.getInt(mobType + ".cooldown", 0);
                    boolean onHit = mmSection.getBoolean(mobType + ".on_hit", true);
                    boolean onProximity = mmSection.getBoolean(mobType + ".on_proximity", false);
                    int radius = mmSection.getInt(mobType + ".proximity_radius", 5);

                    if (ailment != null) {
                        mythicMobDiseases.put(mobType, new com.ibexmc.epidemic.dependencies.MythicMobDisease(
                                ailment, chance, severity, cooldown, onHit, onProximity, radius
                        ));
                    }
                }
            }
        }
    }

    /**
     * Gets the plugin version
     * @return Plugin version
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * Checks if the plugin should start in debug mode
     * @return If true, start in debugt mode
     */
    public boolean isDebugLog() {
        return debugLog;
    }

    /**
     * Checks if the plugin should record debug messages to file
     * @return If true, record to file
     */
    public boolean isDebugLogToFile() {
        return debugLogToFile;
    }

    /**
     * Checks if ailments should be cleared on death
     * @return If true, clear ailments on death
     */
    public boolean isClearAilmentOnDeath() {
        return clearAilmentOnDeath;
    }

    /**
     * Gets the contagious radius
     * @return Contagious radius
     */
    public int getContagiousRadius() {
        return contagiousRadius;
    }

    /**
     * Gets the number of seconds the Affliction scheduler should run at
     * @return Number of seconds
     */
    public int getAfflictedTime() {
        return afflictedTime;
    }

    /**
     * Gets the number of seconds the Symptom scheduler should run at
     * @return Number of seconds
     */
    public int getSymptomTime() {
        return symptomTime;
    }

    /**
     * Gets the number of seconds the PAPI update scheduler should run at
     * @return Number of seconds
     */
    public int getPAPIUpdateTime() {
        return papiTime;
    }

    /**
     * Gets the master list of worlds where players cannot gain an ailment
     * @return List of world names
     */
    public List<String> getPreventAfflictWorlds() {
        return preventAfflictWorlds;
    }

    /**
     * Gets the master list of worlds where players will not experience symptoms
     * @return List of world names
     */
    public List<String> getPreventSymptomWorlds() {
        return preventSymptomWorlds;
    }

    /**
     * Gets the master list of WorldGuard regions where players cannot gain an ailment
     * @return List of WorldGuard Region names
     */
    public List<String> getPreventAfflictWorldGuardRegions() {
        return preventAfflictWorldGuardRegions;
    }

    /**
     * Gets the master list of WorldGuard regions where players will not experience symptoms
     * @return List of WorldGuard Region names
     */
    public List<String> getPreventSymptomWorldGuardRegions() {
        return preventSymptomWorldGuardRegions;
    }

    public int getNewPlayerInvincibleDays() {
        return newPlayerInvincibleDays;
    }

    /**
     * Gets the random chance number to use in affliction checks
     * @return Random chance
     */
    public int getRandomChance() { return randomChance; }

    /**
     * Checks if Remedies should auto-discover
     * @return If true, auto-discover recipes
     */
    public boolean isAutoDiscoverRecipes() {
        return autoDiscoverRecipes;
    }

    /**
     * Checks if Prevent Ailment in WorldGuard PVP Deny areas is enabled
     * @return If true, prevent ailment in worldguard PVP deny areas is enabled
     */
    public boolean isPreventAilmentWorldGuardPVPDeny() {
        return preventAilmentWorldGuardPVPDeny;
    }
    // Thirst

    /**
     * Checks if thirst is enabled
     * @return If true, thirst is enabled
     */
    public boolean isEnableThirst() {
        return enableThirst;
    }

    /**
     * Gets the number of seconds the Thirst scheduler should run at
     * @return Nubmer of seconds
     */
    public int getThirstSeconds() {
        return thirstSeconds;
    }

    /**
     * Gets a String List of worlds that Thirst should NOT run in
     * @return String List of world names
     */
    public List<String> getPreventThirstWorlds() {
        return preventThirstWorlds;
    }

    /**
     * Gets the chance of dirty water
     * @return Dirty water chance
     */
    public int getDirtyWaterChance() {
        return dirtyWaterChance;
    }

    /**
     * Returns the Thirst amount players should respawn with IF they
     * died with thirst at 0
     * @return
     */
    public int getRespawnThirstLevel() {
        return respawnThirstLevel;
    }

    /**
     * Checks if players should  have their thirst reset after death
     * @return If true, thirst is reset to respawn thirst level
     */
    public boolean getRespawnResetThirst() {
        return respawnResetThirst;
    }


    /**
     * If true, the Thirst bossbar should be used
     * @return If true, use Thirst bossbar
     */
    public boolean useThirstBar() {
        return useThirstBar;
    }

    /**
     * If true,  /thirst will return text for thirst amount
     * @return If true, /thirst will report the thirst amount, if false, it will return usage
     */
    public boolean useThirstText() {
        return useThirstText;
    }

    /**
     * If true, warnings will be provided to the player in the actionbar when they get thirsty
     * @return If true, warnings are on
     */
    public boolean displayThirstWarning() {
        return displayThirstWarning;
    }

    /**
     * If true, a notification will be displayed to the user when they drink
     * @return If true, notification is shown
     */
    public boolean displayDrinkNotification() {
        return displayDrinkNotification;
    }

    /**
     * Checks if cauldrons should be disabled as a water source
     * @return If true, disable cauldrons as a water source
     */
    public boolean isCauldronDisabled() { return disableCauldron; }

    /**
     * Checks if bowls should be disabled as a water source
     * @return If true, disable bowls as a water source
     */
    public boolean isWaterBowlDisabled() {
        return disableWaterBowl;
    }

    /**
     * Checks if cupped hands should be disabled as a water source
     * @return If true, disable drinking using cupped hands
     */
    public boolean isWaterHandsDisabled() {
        return disableWaterHands;
    }

    /**
     * If true, dirty checks for potions will not be performed
     * @return If true, dirty check will not occur
     */
    public boolean dirtyCheckPotionDisabled() {
        return disableDirtyCheckPotion;
    }

    /**
     * Returns the amount of damage to apply when the player takes thirst damage
     * @return Thirst damage
     */
    public int thirstDamage() { return thirstDamage; }

    // Temperature

    /**
     * Checks if temperature should be enabled
     * @return If true, temeperature is enabled
     */
    public boolean isEnableTemperature() {
        return enableTemperature;
    }

    /**
     * Gets the number of seconds the Temperature scheduler should run at
     * @return Number of seconds
     */
    public int getTempSeconds() {
        return tempSeconds;
    }

    /**
     * Gets a String List of worlds that Temperature should NOT run in
     * @return String List of world names
     */
    public List<String> getPreventTempWorlds() {
        return preventTempWorlds;
    }

    /**
     * Checks if players should be warned about temperature
     * @return If true, warn players
     */
    public boolean isTempWarnPlayers() {
        return tempWarnPlayers;
    }

    /**
     * Checks if the Epidemic equipment should be disabled
     * @return If true, disable equipment
     */
    public boolean isDisableEpidemicEquipment() { return disableEpidemicEquipment; }

    public Material getGuiRemedyRecipeBorderItem() {
        return guiRemedyRecipeBorderItem;
    }

    public int getGuiRemedyRecipeBorderCMD() {
        return guiRemedyRecipeBorderCMD;
    }

    public Material getGuiRemedyRecipeBackItem() {
        return guiRemedyRecipeBackItem;
    }

    public int getGuiRemedyRecipeBackCMD() {
        return guiRemedyRecipeBackCMD;
    }

    public java.util.Map<String, com.ibexmc.epidemic.dependencies.MythicMobDisease> getMythicMobDiseases() {
        return mythicMobDiseases;
    }
    //endregion
}
