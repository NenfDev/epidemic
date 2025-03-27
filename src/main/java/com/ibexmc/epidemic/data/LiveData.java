package com.ibexmc.epidemic.data;

import com.ibexmc.epidemic.equipment.IEquipment;

import com.ibexmc.epidemic.helpers.LastHealed;
import com.ibexmc.epidemic.players.InvinciblePlayers;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.stats.Stats;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.dependencies.papi.PAPIValues;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.Immunity;
import com.ibexmc.epidemic.temperature.Temperature;

import com.ibexmc.epidemic.thirst.Thirst;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.FileUtils;
import com.ibexmc.epidemic.util.functions.TimeFunctions;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class LiveData {

    Epidemic plugin;
    public LiveData(Epidemic plugin) {
        this.plugin = plugin;
    }

    public Map<Location, Boolean> blockParticles = new HashMap<>();

    // Set of ailments that have block break
    public Map<Material, Set<Ailment>> blockAilments = new HashMap<>();

    //region Ailments
    private Set<Ailment> availableAilments = new HashSet<>();

    /**
     * Gets a set of currently available ailments
     * @return Set of currently available ailments
     */
    public Set<Ailment> getAvailableAilments() {
        if (availableAilments == null) {
            availableAilments = new HashSet<>();
        }
        return this.availableAilments;
    }

    /**
     * Gets an ailment by its internal name
     * @param internalName Internal name to lookup
     * @return Ailment that matches, null if no match
     */
    public Ailment getAvailableAilmentByInternalName(String internalName) {
        Ailment returnValue = null;
        for (Ailment ailment : this.availableAilments) {
            if (ailment.getInternalName().equals(internalName)) {
                returnValue = ailment;
            }
        }
        return returnValue;
    }

    /**
     * Adds an ailment to the avaialable ailments set
     * @param ailment Ailment to add
     */
    public void addAvailableAilment(Ailment ailment) {
        if (availableAilments == null) {
            availableAilments = new HashSet<>();
        }
        boolean add = true;
        for (Ailment a : availableAilments) {
            if (a.getInternalName().equalsIgnoreCase(ailment.getInternalName())) {
                add = false;
                break;
            }
        }
        if (add) {
            availableAilments.add(ailment);
        }
    }

    /**
     * Clear the available ailments list completely
     */
    public void clearAilments() {
        this.availableAilments = new HashSet<>();
    }
    //endregion

    //region Player Afflictions
    private Map<UUID, Set<Afflicted>> playerAfflictions = new HashMap<>();

    /**
     * Gets a map of player afflictions.  Returns a map with player unique identifier
     * and a Set of Afflicted which can be iterated through for each ailment the player
     * is afflicted with
     * @return Map of Player unique identifier and Set of Afflicted entries
     */
    public Map<UUID, Set<Afflicted>> getPlayerAfflictions() {
        return playerAfflictions;
    }

    /**
     * Gets All the player afflictions for just a particular player
     * @param uuid Unique identifier of the player to check
     * @return Set of Afflicted entries
     */
    public Set<Afflicted> getPlayerAfflictionsByUUID(UUID uuid) {
        Set<Afflicted> returnValue = new HashSet<>();

        if (this.playerAfflictions.containsKey(uuid)) {
            returnValue = this.playerAfflictions.get(uuid);
        }

        return returnValue;
    }

    /**
     * Gets the Afflicted entry for a particular player by Ailment internal name
     * @param uuid Unique identifier of the player to check
     * @param internalName Internal name of the ailment to check
     * @return Afflicted entry
     */
    public Afflicted getPlayerAfflictionByInternalName(UUID uuid, String internalName) {
        Afflicted returnValue = null;

        if (this.playerAfflictions.containsKey(uuid)) {
            Set<Afflicted> afflictedSet = this.playerAfflictions.get(uuid);
            for (Afflicted playerAffliction : afflictedSet) {
                if (internalName.equals(playerAffliction.getAilment().getInternalName())) {
                    returnValue = playerAffliction;
                }
            }
        }
        return returnValue;
    }

    /**
     * Adds a player affliction
     * @param uuid Unique identifier of the player to add the affliction for
     * @param afflicted The affliction to add to the player
     */
    public void addPlayerAffliction(UUID uuid, Afflicted afflicted) {
        Logging.debug("LiveData", "addPlayerAffliction(UUID, Afflicted)", "Saving affliction: " + afflicted.getAilment().getInternalName());
        boolean addAffliction = true;
        Set<Afflicted> afflictedSet = this.playerAfflictions.get(uuid);
        if (afflictedSet == null) {
            afflictedSet = new HashSet<>();
        }
        for (Afflicted playerAffliction : afflictedSet) {
            if (afflicted.getAilment().getInternalName().equals(playerAffliction.getAilment().getInternalName())) {
                addAffliction = false;
                break;
            }
        }
        if(addAffliction) {
            // This ailment is not already applied
            afflictedSet.add(afflicted);
            this.playerAfflictions.put(uuid, afflictedSet);
            File dataFile = plugin.data().getPlayerFile(uuid);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            plugin.data().savePlayer(dataFile, offlinePlayer);
        }
    }

    /**
     * Removes a player affliction
     * @param uuid Unique identifier of the player to remove the affliction for
     * @param afflicted The affliction to remove from the player
     */
    public void removePlayerAffliction(UUID uuid, Afflicted afflicted) {
        Set<Afflicted> newAfflictedSet = new HashSet<>();
        Set<Afflicted> afflictedSet = this.playerAfflictions.get(uuid);
        for (Afflicted playerAffliction : afflictedSet) {
            if (!afflicted.getAilment().getInternalName().equals(playerAffliction.getAilment().getInternalName())) {
                newAfflictedSet.add(playerAffliction);
            }
        }
        this.playerAfflictions.put(uuid, newAfflictedSet);
        File dataFile = plugin.data().getPlayerFile(uuid);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        plugin.data().savePlayer(dataFile, offlinePlayer);
    }

    /**
     * Clears the afflictions for a particular player
     * @param uuid Unique identifier of the player to clear the afflictions for
     */
    public void clearPlayerAfflictions(UUID uuid) {
        Set<Afflicted> newAfflictedSet = new HashSet<>();
        this.playerAfflictions.put(uuid, newAfflictedSet);
    }
    //endregion

    //region Players

    private Map<UUID, InvinciblePlayers> invinciblePlayers = new HashMap<>();
    public boolean playerIsInvincible(UUID uuid) {
        if (invinciblePlayers.containsKey(uuid)) {
            return invinciblePlayers.get(uuid).isInvincible();
        }
        return false;
    }
    public InvinciblePlayers getInvinciblePlayer(UUID uuid) {
        if (invinciblePlayers.containsKey(uuid)) {
            return invinciblePlayers.get(uuid);
        }
        return null;
    }
    public void putInvinciblePlayer(UUID uuid, InvinciblePlayers invinciblePlayers) {
        this.invinciblePlayers.put(uuid, invinciblePlayers);
    }


    /**
     * Loads the data file for the player
     * @param player Player to load
     */
    public void loadPlayer(Player player) {
        try {
            // Load the player file
            File playerFile = FileUtils.getFileByUUID(player.getUniqueId());
            if (playerFile.exists()) {
                if (playerFile.length() > 0) {
                    FileConfiguration playerFileConfig = YamlConfiguration.loadConfiguration(playerFile);

                    // Check for ailments
                    if (playerFileConfig.contains("ailment")) {
                        if (playerFileConfig.isConfigurationSection("ailment")) {
                            int afflictedCount = 0;
                            for(String ailmentInternalName : playerFileConfig.getConfigurationSection("ailment").getKeys(false)){
                                Logging.debug("LiveData", "loadPlayerFromFile()", "Loading Ailment: " + ailmentInternalName);
                                Ailment ailment = this.getAvailableAilmentByInternalName(ailmentInternalName);
                                if (ailment != null) {
                                    Timestamp created = null; // Used to store when the player got the ailment

                                    boolean hasRelief = false; // Flag to indicate if they have relief
                                    Timestamp reliefUntil = null; // Used to store when relief lasts until
                                    if (playerFileConfig.contains("ailment." + ailmentInternalName + ".created")) {
                                        if (playerFileConfig.isLong("ailment." + ailmentInternalName + ".created")) {
                                            // created is a required field
                                            created = new Timestamp(playerFileConfig.getLong("ailment." + ailmentInternalName + ".created"));

                                            int afflictDay = Epidemic.instance().gameData().day().get();
                                            long afflictTime = Epidemic.instance().gameData().day().getWorld().getTime();

                                            if (playerFileConfig.contains("ailment." + ailmentInternalName + ".afflict_day")) {
                                                afflictDay = playerFileConfig.getInt("ailment." + ailmentInternalName + ".afflict_day");
                                            }
                                            if (playerFileConfig.contains("ailment." + ailmentInternalName + ".afflict_time")) {
                                                afflictTime = playerFileConfig.getLong("ailment." + ailmentInternalName + ".afflict_time");
                                            }

                                            created = new Timestamp(playerFileConfig.getLong("ailment." + ailmentInternalName + ".created"));


                                            boolean secondaryApplied = false;

                                            if (playerFileConfig.contains("ailment." + ailmentInternalName + ".secondary_applied")) {
                                                if (playerFileConfig.isBoolean("ailment." + ailmentInternalName + ".secondary_applied")) {
                                                    secondaryApplied = playerFileConfig.getBoolean("ailment." + ailmentInternalName + ".secondary_applied");
                                                }
                                            } else {
                                                // TODO: Remove this backwards compatibility
                                                if (playerFileConfig.contains("ailment." + ailmentInternalName + ".was_infected")) {
                                                    if (playerFileConfig.isBoolean("ailment." + ailmentInternalName + ".was_infected")) {
                                                        secondaryApplied = playerFileConfig.getBoolean("ailment." + ailmentInternalName + ".was_infected");
                                                    }
                                                }
                                            }

                                            // Add the affliction to the map
                                            Afflicted afflicted = new Afflicted(player.getUniqueId(), ailment, Epidemic.instance().gameData().day().get(), Epidemic.instance().gameData().day().getWorld().getTime(), created, secondaryApplied);
                                            this.addPlayerAffliction(player.getUniqueId(), afflicted);

                                            afflictedCount++; // Increment counter of successful afflictions

                                        } else {
                                            // created is not a long
                                        }
                                    } else {
                                        // created does not exist - required field
                                        Logging.debug("LiveData", "loadPlayerFromFile()", "Missing created date");
                                    }
                                } else {
                                    // Ailment not found in available list
                                    Logging.debug("LiveData", "loadPlayerFromFile()", "Ailment does not exist");
                                }
                            }
                        } else {
                            // Not a configuration section
                            Logging.debug("LiveData", "loadPlayerFromFile()", "Not a config section");
                        }
                    } else {
                        // No ailment section
                        Logging.debug("LiveData", "loadPlayerFromFile()", "No ailment section");
                    }


                    // Check for immunities
                    if (playerFileConfig.contains("immunity")) {
                        if (playerFileConfig.isConfigurationSection("immunity")) {
                            for (String ailmentInternalName : playerFileConfig.getConfigurationSection("immunity").getKeys(false)) {
                                //if (this.availableAilments.contains(ailmentInternalName)) {
                                    Ailment ailment = this.getAvailableAilmentByInternalName(ailmentInternalName);
                                    if (ailment != null) {
                                        if (playerFileConfig.contains("immunity." + ailmentInternalName + ".amount")) {
                                            if (playerFileConfig.isInt("immunity." + ailmentInternalName + ".amount")) {
                                                int amount = playerFileConfig.getInt("immunity." + ailmentInternalName + ".amount");

                                                // Add the immunity
                                                Immunity immunity = new Immunity(ailment, amount);
                                                this.addPlayerImmunity(player.getUniqueId(), immunity);

                                            } else {
                                                // amount is not an integer
                                            }
                                        } else {
                                            // amount is missing - required
                                        }
                                    } else {
                                        // invalid ailment - null
                                    }
                                //} else {
                                //    // invalid ailment - not found
                                //}
                            }
                        } else {
                            // immunity is not a config section
                        }
                    } else {
                        // No immunity section
                    }

                    // Get player thirst if enabled
                    if (plugin.config.isEnableThirst()) {
                        int thirstAmt = 100; // Default to full
                        if (playerFileConfig.contains("thirst")) {
                            if (playerFileConfig.isDouble("thirst") || playerFileConfig.isInt("thirst")) {
                                thirstAmt = (int) playerFileConfig.getDouble("thirst");
                            } else {
                                // thirst is not an integer - no error
                                thirstAmt = 100;
                            }
                        } else {
                            // No thirst - no error
                            thirstAmt = 100;
                        }


                        if (this.thirstBar.containsKey(player.getUniqueId())) {
                            Thirst oldThirst = this.thirstBar.get(player.getUniqueId());
                            if (oldThirst != null) {
                                Logging.debug("LiveData", "loadPlayer(Player)", "Removing thirst bar from thirst");
                                BossBar oldThirstBar = oldThirst.getThirstBar();
                                if (oldThirstBar != null) {
                                    oldThirstBar.removePlayer(player);
                                    oldThirst.setThirstBar(oldThirstBar);
                                    this.thirstBar.put(player.getUniqueId(), oldThirst);
                                }
                            }
                        }
                        Thirst thirst = new Thirst(player.getUniqueId(), thirstAmt);

                        BossBar thirstBar = thirst.getThirstBar();
                        if (thirstBar != null) {
                            thirstBar.setProgress(thirst.getThirstAmount() / 100);
                            thirst.setThirstAmount(thirst.getThirstAmount());
                            thirst.setThirstBar(thirstBar);
                        } else {
                            thirst.setThirstAmount(thirst.getThirstAmount());
                        }

                        this.thirstBar.put(player.getUniqueId(), thirst);

                    }

                    if (playerFileConfig.contains("invincible.start") && playerFileConfig.contains("invincible.start")) {
                        if (playerFileConfig.isInt("invincible.start") && playerFileConfig.isInt("invincible.end")) {
                            InvinciblePlayers invinciblePlayer = new InvinciblePlayers(player.getUniqueId(), playerFileConfig.getInt("invincible.start"), playerFileConfig.getInt("invincible.start"));
                            if (invinciblePlayer.getStart() >= 0 && invinciblePlayer.getEnd() > 0) {
                                invinciblePlayers.put(player.getUniqueId(), invinciblePlayer);
                            }
                        }
                    }

                    // Get player stats
                    int othersInfected = 0;
                    int infectedByOthers = 0;
                    int afflicted = 0;
                    int cured = 0;
                    int infection = 0;
                    int bloodDrawn = 0;

                    if (playerFileConfig.contains("stats.others_infected")) {
                        if (playerFileConfig.isInt("stats.others_infected")) {
                            othersInfected = playerFileConfig.getInt("others_infected");
                        } else {
                            // others_infected is not an integer - no error
                        }
                    } else {
                        // others_infected is missing - no error
                    }

                    if (playerFileConfig.contains("stats.infected_by_others")) {
                        if (playerFileConfig.isInt("stats.infected_by_others")) {
                            infectedByOthers = playerFileConfig.getInt("infected_by_others");
                        } else {
                            // infected_by_others is not an integer - no error
                        }
                    } else {
                        // infected_by_others is missing - no error
                    }

                    if (playerFileConfig.contains("stats.afflicted")) {
                        if (playerFileConfig.isInt("stats.afflicted")) {
                            afflicted = playerFileConfig.getInt("afflicted");
                        } else {
                            // afflicted is not an integer - no error
                        }
                    } else {
                        // afflicted is missing - no error
                    }

                    if (playerFileConfig.contains("stats.cured")) {
                        if (playerFileConfig.isInt("stats.cured")) {
                            cured = playerFileConfig.getInt("cured");
                        } else {
                            // cured is not an integer - no error
                        }
                    } else {
                        // cured is missing - no error
                    }

                    if (playerFileConfig.contains("stats.blood_drawn")) {
                        if (playerFileConfig.isInt("stats.blood_drawn")) {
                            bloodDrawn = playerFileConfig.getInt("blood_drawn");
                        } else {
                            // blood_drawn is not an integer - no error
                        }
                    } else {
                        // blood_drawn is missing - no error
                    }

                    Stats stats = new Stats(othersInfected, infectedByOthers, afflicted, cured, bloodDrawn);
                    this.playerStats.put(player.getUniqueId(), stats);

                    return;
                } else {
                    // File is blank
                    if (plugin.config.isEnableThirst()) {
                        if (this.thirstBar.containsKey(player.getUniqueId())) {
                            Thirst oldThirst = this.thirstBar.get(player.getUniqueId());
                            if (oldThirst != null) {
                                Logging.debug("LiveData", "loadPlayer(Player)", "Removing thirst bar from thirst");
                                BossBar oldThirstBar = oldThirst.getThirstBar();
                                if (oldThirstBar != null) {
                                    oldThirstBar.removePlayer(player);
                                    oldThirst.setThirstBar(oldThirstBar);
                                    this.thirstBar.put(player.getUniqueId(), oldThirst);
                                }
                            }
                        }
                        Thirst thirst = new Thirst(player.getUniqueId(), 100);
                        BossBar thirstBar = thirst.getThirstBar();
                        thirstBar.setProgress(thirst.getThirstAmount() / 100);
                        thirst.setThirstAmount(thirst.getThirstAmount());
                        thirst.setThirstBar(thirstBar);
                        this.thirstBar.put(player.getUniqueId(), thirst);


                    }
                }
            } else {
                // File does not exist
                if (plugin.config.isEnableThirst()) {
                    if (this.thirstBar.containsKey(player.getUniqueId())) {
                        Thirst oldThirst = this.thirstBar.get(player.getUniqueId());
                        if (oldThirst != null) {
                            Logging.debug("LiveData", "loadPlayer(Player)", "Removing thirst bar from thirst");
                            BossBar oldThirstBar = oldThirst.getThirstBar();
                            if (oldThirstBar != null) {
                                oldThirstBar.removePlayer(player);
                                oldThirst.setThirstBar(oldThirstBar);
                                this.thirstBar.put(player.getUniqueId(), oldThirst);
                            }
                        }
                    }
                    Thirst thirst = new Thirst(player.getUniqueId(), 100);
                    BossBar thirstBar = thirst.getThirstBar();
                    thirstBar.setProgress(thirst.getThirstAmount() / 100);
                    thirst.setThirstAmount(thirst.getThirstAmount());
                    thirst.setThirstBar(thirstBar);
                    this.thirstBar.put(player.getUniqueId(), thirst);
                }

                if (plugin.config.getNewPlayerInvincibleDays() > 0) {
                    int today = plugin.gameData().day().get();
                    int end = today + plugin.config.getNewPlayerInvincibleDays();
                    InvinciblePlayers invinciblePlayer = new InvinciblePlayers(player.getUniqueId(), today, end);
                    if (invinciblePlayer.getStart() >= 0 && invinciblePlayer.getEnd() > 0) {
                        invinciblePlayers.put(player.getUniqueId(), invinciblePlayer);
                        savePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                    }
                }
            }
        } catch (Exception ex) {
            // error
        }
    }

    /**
     * Gets the data file for the player specified
     * @param uuid Unique identifier of the player to get the file for
     * @return File object for the players data file
     */
    public File getPlayerFile(UUID uuid) {
        // Create file
        File dir = new File(plugin.getDataFolder() + File.separator + "Data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File dataFile = FileUtils.getFileByUUID(uuid);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                // Error
            }
        }
        return dataFile;
    }

    /**
     * Loops through all players currently online and runs savePlayer for
     * each of them.
     */
    public void saveAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            File dataFile = getPlayerFile(player.getUniqueId());
            savePlayer(dataFile, player);
        }
    }

    public void savePlayer(OfflinePlayer offlinePlayer) {
        File dataFile = getPlayerFile(offlinePlayer.getUniqueId());
        savePlayer(dataFile, offlinePlayer);
    }

    /**
     * Saves the current player information to the data file assigned to htem
     * @param dataFile File to save to
     * @param player Player to save the information for
     */
    public void savePlayer(File dataFile, OfflinePlayer player) {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (this.playerAfflictions.containsKey(player.getUniqueId())) {
            // save affliction
            fileConfig.set("ailment", null);
            for (Afflicted afflicted : this.getPlayerAfflictionsByUUID(player.getUniqueId())) {
                fileConfig.set("ailment." + afflicted.getAilment().getInternalName() + ".created", afflicted.getStartTimestamp().getTime());
                fileConfig.set("ailment." + afflicted.getAilment().getInternalName() + ".afflict_day", afflicted.getAfflictDay());
                fileConfig.set("ailment." + afflicted.getAilment().getInternalName() + ".afflict_time", afflicted.getAfflictTime());
                fileConfig.set("ailment." + afflicted.getAilment().getInternalName() + ".secondary_applied", afflicted.isSecondaryApplied());
            }
        } else {
            fileConfig.set("ailment", null); // Clear ailments
        }
        for (Immunity immunity : this.getPlayerImmunitiesByUUID(player.getUniqueId())) {
            fileConfig.set("immunity." + immunity.getAilment().getInternalName() + ".amount", immunity.getAmount());
        }

        if (plugin.config.isEnableThirst()) {
            if (this.thirstBar.containsKey(player.getUniqueId())) {
                fileConfig.set("thirst", this.thirstBar.get(player.getUniqueId()).getThirstAmount());
            } else {
                fileConfig.set("thirst", 100);
            }
        }

        InvinciblePlayers invinciblePlayer = plugin.data().getInvinciblePlayer(player.getUniqueId());
        if (invinciblePlayer != null) {
            fileConfig.set("invincible.start", invinciblePlayer.getStart());
            fileConfig.set("invincible.end", invinciblePlayer.getEnd());
        }

        try {
            fileConfig.save(dataFile);
            if (dataFile.length() == 0) {
                //Logging.log("Blank file found", customYml.getName());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //endregion

    //region Player Immunities
    private Map<UUID, Set<Immunity>> playerImmunities = new HashMap<>();

    /**
     * Puts a Set of Immunity entries for a particular player into the playerImmunities map
     * @param uuid Unique identifier for the player
     * @param immunities Set of Immunity entries to put into the map
     */
    public void putPlayerImmunitySet(UUID uuid, Set<Immunity> immunities) {
        if (playerImmunities == null) {
            playerImmunities = new HashMap<>();
        }
        playerImmunities.put(uuid, immunities);
    }

    /**
     * Adds a single Immunity entry for a particular player into the playerImmunities map
     * @param uuid Unique identifier for the player
     * @param immunity Immunity entry to add to the map
     */
    public void addPlayerImmunity(UUID uuid, Immunity immunity) {
        if (playerImmunities == null) {
            playerImmunities = new HashMap<>();
        }
        boolean addImmunity = true;
        Set<Immunity> immunities = this.playerImmunities.get(uuid);
        if (immunities == null) {
            immunities = new HashSet<>();
        }
        for (Immunity playerImmunity : immunities) {
            if (playerImmunity.getAilment().getInternalName().equals(immunity.getAilment().getInternalName())) {
                addImmunity = false;
                break;
            }
        }
        if(addImmunity) {
            // This immunity is not already applied
            immunities.add(immunity);
            this.playerImmunities.put(uuid, immunities);
        }
        File dataFile = plugin.data().getPlayerFile(uuid);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        plugin.data().savePlayer(dataFile, offlinePlayer);
    }

    /**
     * Gets a Map of Player unique identifiers and Sets of immunities from the playerImmunities map
     * @return Map of Player Unique Identifier and Sets of Immunity entries
     */
    public Map<UUID, Set<Immunity>> getPlayerImmunities() {
        if (playerImmunities == null) {
            playerImmunities = new HashMap<>();
        }
        return this.playerImmunities;
    }

    /**
     * Gets a Set of Immnuity entries for a particular player
     * @param uuid Unique identifier for the player to lookup
     * @return Set of Immunity entries
     */
    public Set<Immunity> getPlayerImmunitiesByUUID(UUID uuid) {
        if (playerImmunities == null) {
            playerImmunities = new HashMap<>();
            return new HashSet<>();
        }
        if (playerImmunities.containsKey(uuid)) {
            return playerImmunities.get(uuid);
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Gets the Immunity entry for a particular player and Ailment by internal name
     * @param uuid Unique identifier for the player to lookup
     * @param internalName Internal name of the Ailment to look up
     * @return Immunity entry for the player, null if not found
     */
    public Immunity getPlayerImmunityByUUIDAndAilment(UUID uuid, String internalName) {
        if (playerImmunities == null) {
            playerImmunities = new HashMap<>();
        }
        if (playerImmunities.containsKey(uuid)) {
            for (Immunity immunity : playerImmunities.get(uuid)) {
                if (immunity.getAilment().getInternalName().equalsIgnoreCase(internalName)) {
                    return immunity;
                }
            }
        }
        return null;
    }

    /**
     * Reduces the Immunity amount for all immunities for a particular player.
     * Amount to lower is based on the ImmunityModifier in the Ailment.  This is
     * intended for use when a player dies
     * @param uuid Unique identifier for the player
     */
    public void lowerImmunities(UUID uuid) {
        if (playerImmunities == null) {
            playerImmunities = new HashMap<>();
            return;
        }
        Set<Immunity> newImmunitySet = new HashSet<>();
        if (playerImmunities.containsKey(uuid)) {
            Set<Immunity> immunitySet = playerImmunities.get(uuid);
            if (immunitySet != null) {
                for (Immunity immunity : immunitySet) {
                    if (immunity != null) {
                        int immunityAmt = immunity.getAmount();
                        int immunityModifier = immunity.getAilment().getImmunityModifier();
                        if (immunityModifier > 0) {
                            if (immunityAmt > immunityModifier) {
                                immunityAmt = immunityAmt - immunityModifier;
                            } else {
                                immunityAmt = 0;
                            }
                        }
                        Immunity newImmunity = new Immunity(immunity.getAilment(), immunityAmt);
                        newImmunitySet.add(newImmunity);
                    }
                }
            }
            playerImmunities.put(uuid, newImmunitySet);
        }
    }
    //endregion

    //region Thirst
    private BossBar bar = Bukkit.createBossBar("Thirst", BarColor.GREEN, BarStyle.SOLID);
    private HashMap<UUID, Thirst> thirstBar = new HashMap<>();

    /**
     * Gets the BossBar used for the Player Thirst Bar
     * @return BossBar to be used
     */
    public BossBar getBar() {
        return bar;
    }

    /**
     * Sets the Thirst Bar amount
     * @param uuid Unique identifier for the player to set the Thirst Bar for
     * @param thirst Thirst entry to use
     */
    public void setThirstBar(UUID uuid, Thirst thirst) {
        if (uuid == null || thirst == null) {
            Logging.debug("LiveData", "setThirstBar(UUID, Thirst)", "UUID or Thirst is null");
            return;
        }
        if (thirstBar == null) {
            Logging.debug("LiveData", "setThirstBar(UUID, Thirst)", "thirstBar is null, creating new entry");
            thirstBar = new HashMap<>();
        }
        this.thirstBar.put(uuid, thirst);
    }

    /**
     * Gets a Map of Player unique identifiers and Thirst entries
     * @return Map of Player unique identifiers and Thirst entries
     */
    public HashMap<UUID, Thirst> getThirstBar() {
        return thirstBar;
    }
    //endregion

    //region Insomnia Players
    private Map<UUID, Boolean> insomniaPlayers = new HashMap<>();

    /**
     * Puts a player into the insomniaPlayers map
     * @param uuid Unique identifier for the player
     * @param active If true, player has insomnia
     */
    public void putInsomniaPlayers(UUID uuid, Boolean active) {
        this.insomniaPlayers.put(uuid, active);
    }

    /**
     * Removes a player from the insomniaPlayers amp
     * @param uuid Unique identifier for the player to remove
     */
    public void removeInsomniaPlayers(UUID uuid) {
        this.insomniaPlayers.remove(uuid);
    }

    /**
     * Checks if a player is currently in the insomniaPlayers map to determine if the player
     * has insomnia
     * @param uuid Unique identifier for the player to check
     * @return If true, player is in the insomniaPlayers map
     */
    public boolean containsInsomniaPlayers(UUID uuid) {
        return this.insomniaPlayers.containsKey(uuid);
    }
    //endregion

    //region Player Stats
    private Map<UUID, Stats> playerStats = new HashMap<>();
    //endregion

    //region Player Temperature
    private Map<UUID, Temperature.PlayerTemperature> playerTemps = new HashMap<>();
    private Map<UUID, Timestamp> playerHeatRelief = new HashMap<>();
    private Map<UUID, Timestamp> playerColdRelief = new HashMap<>();

    /**
     * Sets the Players temperature level and modifier
     * @param uuid Unique identifier for the player to set
     * @param level Temperature.Level to set
     * @param modifier Modifier to set
     */
    public void setPlayerTemp(UUID uuid, Temperature.Level level, double modifier) {
        if (playerTemps == null) {
            playerTemps = new HashMap<>();
        }
        Temperature.PlayerTemperature playerTemperature = new Temperature.PlayerTemperature(level, modifier);
        playerTemps.put(uuid, playerTemperature);
    }

    /**
     * Puts a PlayerTemperature entry into the playerTemps map for a particular player
     * @param uuid Unique identifier for the player
     * @param playerTemperature PlayerTemperature entry to put in the map
     */
    public void putPlayerTemp(UUID uuid, Temperature.PlayerTemperature playerTemperature) {
        if (playerTemps == null) {
            playerTemps = new HashMap<>();
        }
        playerTemps.put(uuid, playerTemperature);
    }

    /**
     * Gets the PlayerTemperature entry for the requested player
     * @param uuid Unique identifier for the player to lookup
     * @return PlayerTemperature entry is returned, if none was found, a new one is created and returned
     */
    public Temperature.PlayerTemperature getPlayerTemp(UUID uuid) {
        if (playerTemps == null) {
            playerTemps = new HashMap<>();
        }
        if (playerTemps.containsKey(uuid)) {
            return playerTemps.get(uuid);
        } else {
            // If for some reason this player doesn't have a temperature at all, add one and return that
            Temperature.PlayerTemperature playerTemperature = new Temperature.PlayerTemperature(Temperature.Level.NORMAL, 0);
            playerTemps.put(uuid, playerTemperature);
            return playerTemperature;
        }
    }

    //endregion

    //region Custom Recipes
    private Map<NamespacedKey, ItemStack> customRecipes = new HashMap<>();

    /**
     * Adds a an ItemStack stack and the associated NamespacedKey to the customRecipes map
     * @param key NamespacedKey for the recipe
     * @param itemStack ItemStack that is generated by the recipe
     */
    public void addCustomRecipe(NamespacedKey key, ItemStack itemStack) {
        if (customRecipes == null) {
            customRecipes = new HashMap<>();
        }
        customRecipes.put(key, itemStack);
    }

    /**
     * Auto-discovers custom recipes for the player if specified in the config
     * @param player Player to discover the recipes for
     */
    public void discoverRecipes(Player player) {
        Logging.debug(
                "Livedata",
                "discoverRecipes",
                "Event Fired"
        );

        if (player == null) {
            return;
        }

        Logging.debug(
                "StaticData",
                "discoverRecipes",
                "Starting for " + player.getDisplayName()
        );

        if (customRecipes == null) {
            customRecipes = new HashMap<>();
        }
        boolean newlyEntered = false;
        for (Map.Entry<NamespacedKey, ItemStack> entry : customRecipes.entrySet()) {

            boolean addToGame = true;

            for (NamespacedKey key : player.getDiscoveredRecipes()) {

                String discNamespace = key.getNamespace();
                String discKey = key.getKey();
                String thisNamespace = entry.getKey().getNamespace();
                String thisKey = key.getKey();

                if (discNamespace.equalsIgnoreCase("epidemic")) {
                    if (discNamespace.equalsIgnoreCase(thisNamespace)) {
                        if (discKey.equalsIgnoreCase(thisKey)) {
                            addToGame = false;
                            break;
                        }
                    }
                }
            }

            if (plugin.config.isAutoDiscoverRecipes()) {
                addToGame = true;
            }
            if (addToGame) {
                Logging.debug(
                        "StaticData",
                        "discoverRecipes",
                        "Adding recipe " + entry.getKey().getKey() + " to recipe book for " + player.getDisplayName()
                );
                newlyEntered = player.discoverRecipe(entry.getKey());
                if (newlyEntered) {
                    Logging.debug(
                            "StaticData",
                            "discoverRecipes",
                            "Recipe is newly entered"
                    );
                } else {
                    Logging.debug(
                            "StaticData",
                            "discoverRecipes",
                            "Recipe is NOT newly entered"
                    );
                }
            }
        }
    }

    /**
     * Gets a Map of NamespacedKey and ItemStacks assigned as custom recipes
     * @return Map of NamespacedKey and ItemStack
     */
    public Map<NamespacedKey, ItemStack> getRecipes() {
        return customRecipes;
    }

    /**
     * Gets the ItemStack for a custom recipe by the NamespacedKey
     * @param key NamespacedKey to lookup
     * @return ItemStack for the recipe, returns an AIR itemstack if not found
     */
    public ItemStack getItemStackFromCustomRecipe(String key) {
        if (customRecipes == null) {
            customRecipes = new HashMap<>();
        }
        String alternateKey = "epidemic_" + key;
        ItemStack returnItem = new ItemStack(Material.AIR, 1);
        for (Map.Entry<NamespacedKey, ItemStack> entry : customRecipes.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey().getKey()) || alternateKey.equalsIgnoreCase(entry.getKey().getKey())) {
                returnItem = entry.getValue();
                returnItem.setAmount(1);
            }
        }
        return returnItem;
    }
    //endregion

    //region Symptom Relief
    private Map<UUID, Timestamp> symptomRelief = new HashMap<>();

    /**
     * Checks if a particular player currently has symptom relief
     * @param uuid Unique identifier of the player to lookup
     * @return If true, player currently has symptom relief
     */
    public boolean hasSymptomRelief(UUID uuid) {
        if (symptomRelief == null) {
            symptomRelief = new HashMap<>();
        }
        return symptomRelief.containsKey(uuid);
    }

    /**
     * Gets the timestamp associated with the end of the players symptom relief
     * @param uuid Unique identifier of the player to lookup
     * @return Timestamp of end of symptom relief.  Null if not found
     */
    public Timestamp getSymptomReliefUntil(UUID uuid) {
        if (symptomRelief == null) {
            symptomRelief = new HashMap<>();
        }
        return symptomRelief.get(uuid);
    }

    /**
     * Adds symptom relief for the player until the specified time
     * @param uuid Unique identifier of the player to set
     * @param until Timestamp for the end of symptom relief
     */
    public void addSymptomRelief(UUID uuid, Timestamp until) {
        if (symptomRelief == null) {
            symptomRelief = new HashMap<>();
        }
        this.symptomRelief.put(uuid, until);
    }

    /**
     * Clears any symptom relief entries that are in the past
     */
    public void clearOldSymptomRelief() {
        if (symptomRelief == null) {
            symptomRelief = new HashMap<>();
        }
        List<UUID> uuidToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Timestamp> entry : symptomRelief.entrySet()) {
            if (!TimeFunctions.inFuture(entry.getValue())) {
                uuidToRemove.add(entry.getKey());
            }
        }
        if (uuidToRemove.size() > 0) {
            for (UUID uuid : uuidToRemove) {
                symptomRelief.remove(uuid);
            }
        }
    }

    /**
     * Checks if the player currently has symptom relief.
     * Possibly duplicate of hasSymptomRelief
     * @param uuid Unique identifier of the player ot lookup
     * @return If true, player has symptom relief
     */
    public boolean getCurrentSymptomRelief(UUID uuid) {
        //TODO: Check if this can be removed and replaced with hasSymptomRelief
        if (hasSymptomRelief(uuid)) {
            Timestamp until = getSymptomReliefUntil(uuid);
            if (until != null) {
                // Player still has symptom relief
                return TimeFunctions.inFuture(until);
            }
        }
        return false;
    }
    //endregion

    //region Cold Relief
    private Map<UUID, Timestamp> coldRelief = new HashMap<>();

    /**
     * Checks if a particular player currently has cold relief
     * @param uuid Unique identifier of the player to lookup
     * @return If true, player currently has cold relief
     */
    public boolean hasColdRelief(UUID uuid) {
        if (coldRelief == null) {
            coldRelief = new HashMap<>();
        }
        return coldRelief.containsKey(uuid);
    }

    /**
     * Gets the timestamp associated with the end of the players cold relief
     * @param uuid Unique identifier of the player to lookup
     * @return Timestamp of end of cold relief.  Null if not found
     */
    public Timestamp getColdReliefUntil(UUID uuid) {
        if (coldRelief == null) {
            coldRelief = new HashMap<>();
        }
        return coldRelief.get(uuid);
    }

    /**
     * Adds cold relief for the player until the specified time
     * @param uuid Unique identifier of the player to set
     * @param until Timestamp for the end of cold relief
     */
    public void addColdRelief(UUID uuid, Timestamp until) {
        if (coldRelief == null) {
            coldRelief = new HashMap<>();
        }
        this.coldRelief.put(uuid, until);
    }

    /**
     * Clears any cold relief entries that are in the past
     */
    public void clearOldColdRelief() {
        if (coldRelief == null) {
            coldRelief = new HashMap<>();
        }
        List<UUID> uuidToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Timestamp> entry : coldRelief.entrySet()) {
            if (!TimeFunctions.inFuture(entry.getValue())) {
                uuidToRemove.add(entry.getKey());
            }
        }
        if (uuidToRemove.size() > 0) {
            for (UUID uuid : uuidToRemove) {
                coldRelief.remove(uuid);
            }
        }
    }

    /**
     * Checks if the player currently has cold relief.
     * Possibly duplicate of hasColdRelief
     * @param uuid Unique identifier of the player ot lookup
     * @return If true, player has cold relief
     */
    public boolean getCurrentColdRelief(UUID uuid) {
        //TODO: See if this can be replaced with hasColdRelief
        if (hasColdRelief(uuid)) {
            Timestamp until = getColdReliefUntil(uuid);
            if (until != null) {
                // Player still has symptom relief
                return TimeFunctions.inFuture(until);
            }
        }
        return false;
    }
    //endregion

    //region Heat Relief
    private Map<UUID, Timestamp> heatRelief = new HashMap<>();

    /**
     * Checks if a particular player currently has symptom relief
     * @param uuid Unique identifier of the player to lookup
     * @return If true, player currently has symptom relief
     */
    public boolean hasHeatRelief(UUID uuid) {
        if (heatRelief == null) {
            heatRelief = new HashMap<>();
        }
        return heatRelief.containsKey(uuid);
    }

    /**
     * Gets the timestamp associated with the end of the players symptom relief
     * @param uuid Unique identifier of the player to lookup
     * @return Timestamp of end of symptom relief.  Null if not found
     */
    public Timestamp getHeatReliefUntil(UUID uuid) {
        if (heatRelief == null) {
            heatRelief = new HashMap<>();
        }
        return heatRelief.get(uuid);
    }

    /**
     * Adds heat relief for the player until the specified time
     * @param uuid Unique identifier of the player to set
     * @param until Timestamp for the end of heat relief
     */
    public void addHeatRelief(UUID uuid, Timestamp until) {
        if (heatRelief == null) {
            heatRelief = new HashMap<>();
        }
        this.heatRelief.put(uuid, until);
    }

    /**
     * Clears any heat relief entries that are in the past
     */
    public void clearOldHeatRelief() {
        if (heatRelief == null) {
            heatRelief = new HashMap<>();
        }
        List<UUID> uuidToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Timestamp> entry : heatRelief.entrySet()) {
            if (!TimeFunctions.inFuture(entry.getValue())) {
                uuidToRemove.add(entry.getKey());
            }
        }
        if (uuidToRemove.size() > 0) {
            for (UUID uuid : uuidToRemove) {
                heatRelief.remove(uuid);
            }
        }
    }

    /**
     * Checks if the player currently has heat relief.
     * Possibly duplicate of hasHeatRelief
     * @param uuid Unique identifier of the player ot lookup
     * @return If true, player has heat relief
     */
    public boolean getCurrentHeatRelief(UUID uuid) {
        //TODO: See if this can be replaced with hasHeatRelief
        if (hasHeatRelief(uuid)) {
            Timestamp until = getHeatReliefUntil(uuid);
            if (until != null) {
                return TimeFunctions.inFuture(until);
            }
        }
        return false;
    }
    //endregion

    //region Remedies
    private Map<String, Remedy> remedies = new HashMap<>();

    /**
     * Checks if the remedies map is null, if so, sets to a blank HashMap
     */
    private void nullCheckRemedies() {
        if (remedies == null) {
           remedies = new HashMap<>();
        }
    }

    /**
     * Puts a Remedy into the remedies Map
     * @param remedy Remedy to put into the map
     */
    public void putRemedy(Remedy remedy) {
        nullCheckRemedies();
        if (remedy != null) {
            remedies.put(remedy.getNamespacedKey().getKey(), remedy);
        }
    }

    /**
     * Gets a Remedy by the key
     * @param key Key to lookup
     * @return Remedy for the specified key, null if not found
     */
    public Remedy getRemedy(String key) {
        nullCheckRemedies();
        return remedies.getOrDefault(key, null);
    }

    /**
     * Gets a Map of String version of the key, and the Remedy from the remedies Map
     * @return Map with tring version of the key, and the Remedy
     */
    public Map<String, Remedy> getRemedies() {
        nullCheckRemedies();
        return remedies;
    }

    /**
     * Clears all remedies from the remedies map
     */
    public void clearRemedies() {
        remedies = new HashMap<>();
    }
    //endregion

    //region Last Healed
    private Map<UUID, List<LastHealed>> lastHealedMap = new HashMap<>();

    /**
     * Checks if the lastHealedMap Map is null, if so, sets it to a new HashMap
     */
    public void nullCheckLastHealedMap() {
        if (lastHealedMap == null) {
            lastHealedMap = new HashMap<>();
        }
    }

    /**
     * Puts an Ailment internal name into LastHealed for a particular player
     * @param uuid Unique identifier for the player to add the LastHealed entry for
     * @param ailmentKey Ailment internal name to add to LastHealed
     */
    public void putLastHealed(UUID uuid, String ailmentKey) {
        nullCheckLastHealedMap();
        LastHealed lastHealed = new LastHealed(ailmentKey, TimeFunctions.now());
        ArrayList newList = new ArrayList();
        newList.add(lastHealed);
        List<LastHealed> lastHealedList = lastHealedMap.get(uuid);
        if (lastHealedList != null) {
            for (LastHealed lh : lastHealedList) {
                if (!lh.getAilmentKey().equalsIgnoreCase(ailmentKey)) {
                    newList.add(lh);
                }
            }
        }
        lastHealedMap.put(uuid, newList);
    }

    /**
     * Gets the timestmap of the last time the player was healed of the specified ailment
     * @param uuid Unique identifier for the player to lookup
     * @param ailmentKey The Ailment internal name to lookup
     * @return Timestamp the player was last healed of this ailment, null if not found
     */
    public Timestamp getLastHealed(UUID uuid, String ailmentKey) {
        nullCheckLastHealedMap();
        List<LastHealed> lastHealedList = lastHealedMap.get(uuid);
        if (lastHealedList != null) {
            for (LastHealed lh : lastHealedList) {
                if (lh.getAilmentKey().equalsIgnoreCase(ailmentKey)) {
                    return lh.getTimestamp();
                }
            }
        }
        return null;
    }
    //endregion

    //region Equipment
    public Map<String, IEquipment> equipment = new HashMap<>();
    //endregion

}
