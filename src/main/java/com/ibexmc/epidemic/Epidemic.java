package com.ibexmc.epidemic;

import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.api.EpidemicAPI;
import com.ibexmc.epidemic.commands.*;
import com.ibexmc.epidemic.data.InstanceData;
import com.ibexmc.epidemic.data.LiveData;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.dependencies.Dependencies;
import com.ibexmc.epidemic.equipment.EquipmentManager;
import com.ibexmc.epidemic.events.block.BlockBreak;
import com.ibexmc.epidemic.events.entity.ProjectileEvents;
import com.ibexmc.epidemic.events.player.*;
import com.ibexmc.epidemic.server.ServerManager;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.scheduled.*;
import com.ibexmc.epidemic.temperature.Temperature;
import com.ibexmc.epidemic.util.*;
import com.ibexmc.epidemic.util.log.Error;
import com.ibexmc.epidemic.dependencies.papi.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.events.craft.CraftItem;
import com.ibexmc.epidemic.events.entity.EntityDamage;
import com.ibexmc.epidemic.events.entity.EntityDamageByEntity;
import com.ibexmc.epidemic.events.inventory.InventoryClick;
import com.ibexmc.epidemic.events.entity.PlayerDeath;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.functions.FileFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Epidemic extends JavaPlugin {
    // Epidemic
    private static Epidemic instance;

    @Deprecated
    public static Epidemic getInstance() {
        return instance;
    }

    /**
     * Gets the current running instance
     * @return Epidemic instance
     */
    public static Epidemic instance() { return instance; }

    private EpidemicAPI api;
    @Deprecated
    public EpidemicAPI getAPI() {
        return api;
    }
    public EpidemicAPI api() { return api; }

    // Naming
    private final String displayName = "Epidemic";

    public String getDisplayName() {
        return displayName;
    }
    public String getInternalName() {
        return "epidemic";
    }

    // Instance Data
    private InstanceData instanceData;
    public InstanceData gameData() {
        return this.instanceData;
    }
    // Live Data - To be replaced by InstanceData
    // At which point, we'll rename gameData() to data()
    @Deprecated
    private LiveData liveData;
    @Deprecated
    public LiveData getLiveData() {
        return liveData;
    }
    public LiveData data() {
        return liveData;
    }

    private PersistentData persistentData;
    public PersistentData persistentData() { return persistentData; }

    // Config
    public ConfigManager config;
    public ConfigManager config() { return config; }

    private final Dependencies dependencies = new Dependencies();
    public Dependencies dependencies() { return dependencies; }

    // Event Listeners
    private void loadEventListeners() {
        try {
            Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
            Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
            Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerBedEnter(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerInteractEntity(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerItemConsume(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerTeleport(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
            Bukkit.getPluginManager().registerEvents(new CraftItem(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerChat(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
            Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
            Bukkit.getPluginManager().registerEvents(new ProjectileEvents(), this);
            Bukkit.getPluginManager().registerEvents(new com.ibexmc.epidemic.events.entity.MythicMobsEvents(), this);
            if (dependencies.hasItemsAdder()) {
                Bukkit.getPluginManager().registerEvents(new com.ibexmc.epidemic.events.ItemsAdderEvents(), this);
            }
        } catch (Exception ex) {
            Error.save(
                    "Epidemic.loadEventListeners.001",
                    "Epidemic",
                    "loadEventListeners()",
                    "Loading Event Listeners",
                    "Unexpected error loading event listeners",
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    // Commands
    private void loadCommands() {
        Logging.debug(
                "Epidemic",
                "loadCommands",
                "Loading Commands"
        );
        try {
            getCommand("epidemic").setExecutor(new EpidemicCommand());
            getCommand("cure").setExecutor(new CureCommand());
            getCommand("health").setExecutor(new HealthCommand());
            getCommand("infect").setExecutor(new InfectCommand());
            getCommand("thirst").setExecutor(new ThirstCommand());
            getCommand("temp").setExecutor(new TempCommand());
            getCommand("epigive").setExecutor(new GiveCommand());
            getCommand("epirecipes").setExecutor(new RecipeCommand());
        } catch (Exception ex) {
            Error.save(
                    "Epidemic.loadCommands.001",
                    "Epidemic",
                    "loadCommands()",
                    "Loading Commands",
                    "Unexpected error loading commands",
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    // Scheduled
    private void startAfflict() {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                Afflict.Run();
            }
        }.runTaskTimer(instance, 0, config.getAfflictedTime() * 20);
    }
    private void startSymptomCheck() {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                ApplySymptom.Run();
            }
        }.runTaskTimer(instance, 0, config.getSymptomTime() * 20);
    }
    private void startThirstCheck() {
        Logging.debug("Epidemic", "startThirstCheck", "Thirst Seconds = " + config.getThirstSeconds());
        BukkitTask taskThirst = new BukkitRunnable() {
            public void run() {
                ApplyThirst.Run();
            }
        }.runTaskTimer(instance, 0, config.getThirstSeconds() * 20);
        Logging.debug("Epidemic", "startThirstCheck", "Thirst Task ID = " + taskThirst.getTaskId());
    }
    private void startTemperatureCheck() {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                CheckTemp.Run();
            }
        }.runTaskTimer(instance, 0, config.getTempSeconds() * 20);
    }
    private void startCleanup() {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                Cleanup.Run();
            }
        }.runTaskTimer(instance, (60 * 20), (60 * 20));
    }
    private void startPAPIUpdate() {
        if (config.getPAPIUpdateTime() > 0) {
            BukkitTask task = new BukkitRunnable() {
                public void run() {
                    UpdatePAPIValues.Run();
                }
            }.runTaskTimer(instance, 0, (config.getPAPIUpdateTime() * 20));
        }
    }
    private void startDayCounter() {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                DayCounter.Run();
            }
        }.runTaskTimer(instance, 0, (100));
    }

    @Override
    public void onEnable() {

        instance = this;

        instanceData = new InstanceData(this);
        liveData = new LiveData(this);
        api = new EpidemicAPI(this);

        Logging.log(">", "&a=========================================&6[<]");
        //Logging.log(">", "&aTRIAL COPY&6[<]");
        Logging.log(
                "",
                "&f                 " + displayName
        );
        Logging.log(
                "",
                "  Bringing real survival to your server"
        );
        Logging.log(">", "&a=========================================&6[<]");

        String configPath = Epidemic.instance().getDataFolder().getAbsolutePath() + File.separator + "config.yml";
        File confFile = new File(configPath);
        if (!confFile.exists()) {
            saveDefaultConfig();
            FileFunctions.saveResourceIfNotExists("ailments/broken_leg.yml");
            FileFunctions.saveResourceIfNotExists("ailments/bubonic_plague.yml");
            FileFunctions.saveResourceIfNotExists("ailments/burn.yml");
            FileFunctions.saveResourceIfNotExists("ailments/commoncold.yml");
            FileFunctions.saveResourceIfNotExists("ailments/gangrene.yml");
            FileFunctions.saveResourceIfNotExists("ailments/rabies.yml");
            FileFunctions.saveResourceIfNotExists("ailments/radiation.yml");
            FileFunctions.saveResourceIfNotExists("ailments/sepsis.yml");
            FileFunctions.saveResourceIfNotExists("ailments/typhus.yml");
            FileFunctions.saveResourceIfNotExists("ailments/wound.yml");
            FileFunctions.saveResourceIfNotExists("remedies/antibiotics.yml");
            FileFunctions.saveResourceIfNotExists("remedies/bandage.yml");
            FileFunctions.saveResourceIfNotExists("remedies/boiled_water.yml");
            FileFunctions.saveResourceIfNotExists("remedies/bubonic_cure.yml");
            FileFunctions.saveResourceIfNotExists("remedies/cane.yml");
            FileFunctions.saveResourceIfNotExists("remedies/coldremedy.yml");
            FileFunctions.saveResourceIfNotExists("remedies/cooling_compress.yml");
            FileFunctions.saveResourceIfNotExists("remedies/healthbar.yml");
            FileFunctions.saveResourceIfNotExists("remedies/goop.yml");
            FileFunctions.saveResourceIfNotExists("remedies/rabies_cure.yml");
            FileFunctions.saveResourceIfNotExists("remedies/snowmelt.yml");
            FileFunctions.saveResourceIfNotExists("remedies/splint.yml");
            FileFunctions.saveResourceIfNotExists("remedies/typhus_cure.yml");
            FileFunctions.saveResourceIfNotExists("remedies/potassium_iodide.yml");
            FileFunctions.saveResourceIfNotExists("equipment/empty_syringe.yml");
            FileFunctions.saveResourceIfNotExists("equipment/full_syringe.yml");
            FileFunctions.saveResourceIfNotExists("equipment/gas_mask.yml");
            FileFunctions.saveResourceIfNotExists("equipment/hazmat_part1.yml");
            FileFunctions.saveResourceIfNotExists("equipment/hazmat_part2.yml");
            FileFunctions.saveResourceIfNotExists("equipment/hazmat_suit.yml");
            FileFunctions.saveResourceIfNotExists("equipment/infected_arrow.yml");
            FileFunctions.saveResourceIfNotExists("equipment/infected_sample.yml");
            FileFunctions.saveResourceIfNotExists("equipment/infected_splash_potion.yml");
            FileFunctions.saveResourceIfNotExists("config/hotspots.yml");
        } else {
            String configV = "0.0";
            ConfigParser configParser = new ConfigParser(this, confFile);
            ConfigParser.ConfigReturn configVersion = configParser.getStringValue(
                    "v",
                    "0.0",
                    false
            );
            if (configVersion.isSuccess()) {
                configV = configVersion.getString();
            }
            //Logging.log("&lConfig Version", configV);
        }
        config = new ConfigManager(this);
        config.load();

        //staticData.loadLanguage(); // Load lang.yml file
        Locale.load();

        persistentData = new PersistentData(this);

        //VersionCheck.checkVersion();

        //Logging.log("&lLoading From Config", config.getPluginVersion());


        loadAilments();

        loadRemedies();

        loadEquipment();

        loadEventListeners();
        loadCommands();


        startAfflict();
        startSymptomCheck();

        if (config.isEnableThirst()) {
            Logging.debug("Epidemic", "onEnable", "Starting Thirst Checking");
            startThirstCheck();
        }

        if (config.isEnableTemperature()) {
            Logging.debug("Epidemic", "onEnable", "Starting Temperature Checking");
            startTemperatureCheck();
        }

        startCleanup();



        //saveDefaultConfig();
        try {
            File configFile = new File(getDataFolder(), "config.yml");
            ConfigUpdater.update(Epidemic.instance(), "config.yml", configFile, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        setupPlaceholderAPI();

        startPAPIUpdate();

        if (dependencies.hookDomain()) {
            Logging.log("Epidemic-Domain", "&aDomain dependency found");
        } else {
            Logging.log("Epidemic-Domain", "&cDomain dependency not found - Domain checking for affliction/symptoms will not function");
        }

        if (dependencies.hookWorldGuard()) {
            Logging.log("Epidemic-WorldGuard", "&aWorldGuard dependency found");
        } else {
            Logging.log("Epidemic-WorldGuard", "&cWorldGuard dependency not found - WorldGuard checking for symptoms will not function");
        }

        if (dependencies.hookItemsAdder()) {
            Logging.log("Epidemic-ItemsAdder", "&aItemsAdder dependency found");
        }

        if (dependencies.hookMythicMobs()) {
            Logging.log("Epidemic-MythicMobs", "&aMythicMobs dependency found");
            com.ibexmc.epidemic.dependencies.MythicMobsHook.register(this);
        }

        ServerManager.load();
        startDayCounter();
        //startBypass();
    }
    @Override
    public void onDisable() {
        // Save any online players
        Logging.log("Disabling Plugin", "Saving any remaining online players");
        liveData.saveAllPlayers();
        ServerManager.save();
    }

    public void reload() {

        config = new ConfigManager(this);
        config.load();

        //Logging.debug("Epidemic", "reload", "Random Chance = " + config.getRandomChance());

        //staticData.loadLanguage(); // Load lang.yml file
        Locale.load();

        liveData.clearAilments();
        liveData.clearRemedies();

        loadAilments();
        loadRemedies();
        loadEquipment();


        for (Player player : Bukkit.getOnlinePlayers()) {
            liveData.clearPlayerAfflictions(player.getUniqueId());
            if (config.isAutoDiscoverRecipes()) {
                Epidemic.instance().data().discoverRecipes(player);
            }

            try {
                Epidemic.instance().data().loadPlayer(player);

                Temperature temp = new Temperature(player);
                temp.applyTemperature();

            } catch (Exception ex) {
                Error.save(
                        "PlayerJoin.onJoin.001",
                        "PlayerJoin",
                        "onJoin()",
                        "Player Join Event",
                        "Unexpected error during Player Join event",
                        Error.Severity.URGENT,
                        ex.getStackTrace()
                );
            }
        }



    }

    private void loadRemedies() {
        List<String> loadedRemedies = new ArrayList<>();
        String filepathRem = this.getDataFolder().getAbsolutePath() + File.separator + "remedies";
        File datadirRem = new File(filepathRem);
        if (datadirRem.exists()) {
            for (File dataFile : new File(filepathRem).listFiles()) {
                if (dataFile.exists()) {
                    Remedy remedy = new Remedy(dataFile);
                    loadedRemedies.add(remedy.getDisplayName());
                }
            }
            String delimitedString = String.join("&f, ", loadedRemedies);
            Logging.log(
                    "Startup",
                    "Added Remedies: " + delimitedString
            );
        }
    }
    private void loadAilments() {
        List<String> loadedAilments = new ArrayList<>();
        String filepath = Epidemic.instance().getDataFolder().getAbsolutePath() + File.separator + "ailments";
        File datadir = new File(filepath);
        if (datadir.exists()) {
            for (File dataFile : new File(filepath).listFiles()) {
                if (dataFile.exists()) {
                    Ailment ailment = new Ailment(dataFile);
                    loadedAilments.add(ailment.getDisplayName());
                    if (ailment.isValid()) {
                        Epidemic.instance().data().addAvailableAilment(ailment);
                    }

                }
            }
            String delimitedString = String.join("&f, ", loadedAilments);
            Logging.log(
                    "Startup",
                    "Added Ailments: " + delimitedString
            );
        }

        AilmentManager.loadHotspots();
    }
    private void loadEquipment() {
        EquipmentManager.load();
    }


    // PlaceholderAPI
    private void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPISupport(this).register();
        }
    }

}
