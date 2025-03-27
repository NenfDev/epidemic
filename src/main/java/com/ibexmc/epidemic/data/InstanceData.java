package com.ibexmc.epidemic.data;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.Hotspot;
import com.ibexmc.epidemic.ailments.Immunity;
import com.ibexmc.epidemic.dependencies.papi.PAPIValues;
import com.ibexmc.epidemic.equipment.IEquipment;
import com.ibexmc.epidemic.helpers.LastHealed;
import com.ibexmc.epidemic.players.EpiPlayer;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.temperature.Temperature;
import com.ibexmc.epidemic.thirst.Thirst;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Timestamp;
import java.util.*;

public class InstanceData {
    Epidemic instance;

    public InstanceData(Epidemic instance) {
        this.instance = instance;
    }

    private final Affliction affliction = new Affliction();
    private final Bypass bypass = new Bypass();
    private final Day day = new Day();
    private final Debug debug = new Debug();
    private final EpiPlayers epiPlayer = new EpiPlayers();
    private final Hotspots hotspots = new Hotspots();
    private final Locale locale = new Locale();
    private final PAPI papi = new PAPI();
    private final Recipes recipes = new Recipes();

    public Affliction affliction() { return this.affliction; }
    public Bypass bypass() { return this.bypass; }
    public Day day() { return this.day; }
    public Debug debug() { return this.debug; }
    public EpiPlayers epiPlayer() { return this.epiPlayer; }
    public Hotspots hotspots() {
        return this.hotspots;
    }
    public Locale locale() { return this.locale; }
    public PAPI papi() { return this.papi; }
    public Recipes recipes() { return this.recipes; }

    public class Affliction {
        private Map<UUID, Set<Afflicted>> playerAfflictions = new HashMap<>();
        public Map<UUID, Set<Afflicted>> get() { return this.playerAfflictions; }
        public Set<Afflicted> get(UUID uuid) { return this.playerAfflictions.get(uuid); }
        public boolean has(UUID uuid) { return this.playerAfflictions.containsKey(uuid); }
        public void set(Map<UUID, Set<Afflicted>> playerAfflictions) { this.playerAfflictions = playerAfflictions; }
        public void put(UUID uuid, Set<Afflicted> afflictions) { this.playerAfflictions.put(uuid, afflictions); }
        public void remove(UUID uuid) { this.playerAfflictions.remove(uuid); }
    }
    public class Bypass {
        // Bypass players
        private Set<UUID> bypassPlayers = new HashSet<>();
        public Set<UUID> get() { return this.bypassPlayers; }
        public boolean has(UUID uuid) { return bypassPlayers.contains(uuid); }
        public void set(Set<UUID> bypassPlayers) { this.bypassPlayers = bypassPlayers; }
        public void add(UUID uuid) { this.bypassPlayers.add(uuid); }
        public void remove(UUID uuid) {
            if (this.bypassPlayers.contains(uuid)) {
                this.bypassPlayers.remove(uuid);
            }
        }
    }
    public class Day {
        private long lastLogged = 0;
        private int day = 0;
        private int lastSavedDay = 0;
        private String worldName = "world";
        public boolean isNewDay(long currentTime) {
            boolean isNew = false;
            if (currentTime < lastLogged) {
                day++;
                isNew = true;
            }
            lastLogged = currentTime;
            return isNew;
        }
        public int get() {
            return this.day;
        }
        public int getLastSavedDay() {
            return this.lastSavedDay;
        }
        public void set(long currentTime, int day) {
            this.lastLogged = currentTime;
            this.day = day;
            this.lastSavedDay = day;
        }
        public void setWorldName(String worldName) { this.worldName = worldName; }
        public World getWorld() {
            World world = Bukkit.getWorld(this.worldName);
            if (world != null) {
                return world;
            } else {
                List<World> worlds = Bukkit.getWorlds();
                for (World defaultWorld : worlds) {
                    return defaultWorld;
                }
            }
            return null;
        }
    }
    public class Debug {
        private boolean debug = false;
        public boolean get() { return this.debug; }
        public void set(boolean debug) { this.debug = debug; }
        public void toggle() { this.debug = !this.debug; }
    }
    public class EpiPlayers {
        private Map<UUID, EpiPlayer> epiPlayer = new HashMap<>();
        public Map<UUID, EpiPlayer> get() { return this.epiPlayer; }
        public EpiPlayer get(UUID uuid) { return this.epiPlayer.get(uuid); }
        public void set(Map<UUID, EpiPlayer> epiPlayer) { this.epiPlayer = epiPlayer; }
        public void put(UUID uuid, EpiPlayer epiPlayer) { this.epiPlayer.put(uuid, epiPlayer); }
        public void clear() { this.epiPlayer.clear(); }
    }
    public class Hotspots {
        private Map<Location, Hotspot> hotspots = new HashMap<>();
        public Map<Location, Hotspot> get() {
            return this.hotspots;
        }
        public boolean has (Location location) {
            return this.hotspots.containsKey(location);
        }
        public void set(Map<Location, Hotspot> hotspots) {
            if (hotspots != null) {
                this.hotspots = hotspots;
            } else {
                this.hotspots = new HashMap<>();
            }
        }
        public void put(Location location, Hotspot hotspot) { this.hotspots.put(location, hotspot); }
        public void remove(Location location) { this.hotspots.remove(location); }
    }
    public class Locale {
        private Map<String, String> locale = new HashMap<>();
        public Map<String, String> get() { return this.locale; }
        public String get(String key) { return this.locale.get(key); }
        public boolean has(String key) { return this.locale.containsKey(key); }
        public void set(Map<String, String> locale) {
            if (locale != null) {
                this.locale = locale;
            } else {
                this.locale = new HashMap<>();
            }
        }
    }
    public class PAPI {
        private Map<UUID, PAPIValues> papiValues = new HashMap<>();
        public Map<UUID, PAPIValues> get() { return this.papiValues; }
        public PAPIValues get(UUID uuid) { return this.papiValues.get(uuid); }
        public boolean has(UUID uuid) { return this.papiValues.containsKey(uuid); }
        public void set(Map<UUID, PAPIValues> papiValues) { this.papiValues = papiValues; }
        public void put(UUID uuid, PAPIValues papiValues) { this.papiValues.put(uuid, papiValues); }
        public void remove(UUID uuid) { this.papiValues.remove(uuid); }
    }
    public class Recipes {
        // Epidemic Recipes
        private Map<String, Map<RecipePosition, RecipeItem>> recipes = new HashMap<>();
        public Map<String, Map<RecipePosition, RecipeItem>> get() { return recipes; }
        public Map<RecipePosition, RecipeItem> get(String key) { return recipes.get(key); }
        public boolean has(String key) { return recipes.containsKey(key);}
        public void set(Map<String, Map<RecipePosition, RecipeItem>> recipes) { this.recipes = recipes; }
        public void put(String key, Map<RecipePosition, RecipeItem> recipe) { this.recipes.put(key, recipe); }
        public void remove(String key) { recipes.remove(key); }
    }
    public class Player {

        // Player Immunities
        public Map<UUID, Set<Immunity>> playerImmunities = new HashMap<>();
        // Players with Insomnia
        public Map<UUID, Boolean> insomniaPlayers = new HashMap<>();
        // Last time a player was healed for ailments
        public Map<UUID, Set<LastHealed>> lastHealedMap = new HashMap<>();
        // Last time a player applied symptom relief
        public Map<UUID, Timestamp> symptomRelief = new HashMap<>();
        // Last time a player applied cold relief
        public Map<UUID, Timestamp> coldRelief = new HashMap<>();
        // Last time a player applied heat relief
        public Map<UUID, Timestamp> heatRelief = new HashMap<>();
        // Player temperatures
        public Map<UUID, Temperature.PlayerTemperature> playerTemps = new HashMap<>();
        // Player hydration
        public Map<UUID, Thirst> hydration = new HashMap<>();
    }
    public class Equipment {
        // Epidemic Equipment
        public Map<String, IEquipment> equipment = new HashMap<>();
    }

}
