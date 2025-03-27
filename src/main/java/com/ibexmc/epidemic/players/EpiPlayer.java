package com.ibexmc.epidemic.players;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpiPlayer {

    public EpiPlayer(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    private UUID uuid;
    private PlayerHydration hydration = new PlayerHydration(uuid, 100);
    private List<Affliction> afflictions = new ArrayList<>();
    private List<AppliedVaccine> appliedVaccines = new ArrayList<>();
    private List<AppliedImmunity> appliedImmunities = new ArrayList<>();
    private int doctorXP = 0;
    private List<String> learnedAilments = new ArrayList<>();
    private int invincibleStartDay = 0;
    private int invincibleEndDay = 0;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerHydration hydration() {
        return hydration;
    }

    public void hydration(double hydration) {
        this.hydration.setHydration(hydration);
    }

    public List<Affliction> afflictions() {
        return afflictions;
    }

    public void afflictions(List<Affliction> afflictions) {
        this.afflictions = afflictions;
    }

    public List<AppliedVaccine> appliedVaccines() {
        return appliedVaccines;
    }

    public void appliedVaccines(List<AppliedVaccine> appliedVaccines) {
        this.appliedVaccines = appliedVaccines;
    }

    public List<AppliedImmunity> appliedImmunities() {
        return appliedImmunities;
    }

    public void appliedImmunities(List<AppliedImmunity> appliedImmunities) {
        this.appliedImmunities = appliedImmunities;
    }

    public int doctorXP() {
        return doctorXP;
    }

    public void doctorXP(int doctorXP) {
        this.doctorXP = doctorXP;
    }

    public List<String> learnedAilments() {
        return learnedAilments;
    }

    public void learnedAilments(List<String> learnedAilments) {
        this.learnedAilments = learnedAilments;
    }

    public int getInvincibleStartDay() {
        return invincibleStartDay;
    }

    public void setInvincibleStartDay(int invincibleStartDay) {
        this.invincibleStartDay = invincibleStartDay;
    }

    public int getInvincibleEndDay() {
        return invincibleEndDay;
    }

    public void setInvincibleEndDay(int invincibleEndDay) {
        this.invincibleEndDay = invincibleEndDay;
    }

    private File getFile() {
        return new File(
                Epidemic.instance().getDataFolder() +
                        File.separator +
                        "Data" + File.separator +
                        this.uuid.toString() +
                        ".yml"
        );
    }

    public void load() {
        File file = getFile();
        if (file.exists()) {
            ConfigParser configParser = new ConfigParser(Epidemic.instance(), file);
            ConfigParser.ConfigReturn crThirst = configParser.getIntValue("thirst",100,false);
            if (crThirst.isSuccess()) {
                this.hydration.setHydration(crThirst.getDouble());
            } else {
                this.hydration.setHydration(100);
            }
            ConfigParser.ConfigReturn crAilments = configParser.getConfigSection("ailments",false);
            if (crAilments.isSuccess()) {
                for (Object ailmentKey : crAilments.getConfigSection().getKeys(false).toArray()) {
                    Ailment ailment = AilmentManager.getAilmentByInternalName(ailmentKey.toString());
                    if (ailment != null) {
                        int afflictedDay = 0;
                        int afflictedTime = 0;
                        boolean secondaryApplied = false;
                        ConfigParser.ConfigReturn crAfflictedDay = configParser.getIntValue("ailments." + ailmentKey + ".afflicted_day",-1,false);
                        if (crAfflictedDay.isSuccess()) {
                            afflictedDay = crAfflictedDay.getInt();
                        } else {
                            afflictedDay = -1;
                        }
                        ConfigParser.ConfigReturn crAfflictedTime = configParser.getIntValue("ailments." + ailmentKey + ".afflicted_time",-1,false);
                        if (crAfflictedTime.isSuccess()) {
                            afflictedTime = crAfflictedTime.getInt();
                        } else {
                            afflictedTime = -1;
                        }
                        ConfigParser.ConfigReturn crSecondaryApplied = configParser.getIntValue("ailments." + ailmentKey + ".secondary_applied",-1,false);
                        if (crSecondaryApplied.isSuccess()) {
                            secondaryApplied = crSecondaryApplied.getBoolean();
                        } else {
                            secondaryApplied = false;
                        }
                        if (afflictedDay >= 0 && afflictedTime >= 0) {
                            Affliction affliction = new Affliction(ailment, afflictedDay, afflictedTime, secondaryApplied);
                            this.afflictions.add(affliction);
                        }
                    }
                }
            }
            ConfigParser.ConfigReturn crVaccines = configParser.getConfigSection("vaccines",false);
            if (crVaccines.isSuccess()) {
                for (Object ailmentKey : crVaccines.getConfigSection().getKeys(false).toArray()) {
                    Ailment ailment = AilmentManager.getAilmentByInternalName(ailmentKey.toString());
                    if (ailment != null) {
                        int lastDoseDay = 0;
                        ConfigParser.ConfigReturn crLastDoseDay = configParser.getIntValue("vaccines." + ailmentKey + ".last_dose_day",-1,false);
                        if (crLastDoseDay.isSuccess()) {
                            lastDoseDay = crLastDoseDay.getInt();
                        } else {
                            lastDoseDay = -1;
                        }
                        if (lastDoseDay >= 0) {
                            AppliedVaccine appliedVaccine = new AppliedVaccine(ailmentKey.toString(), lastDoseDay);
                            this.appliedVaccines.add(appliedVaccine);
                        }
                    }
                }
            }
            ConfigParser.ConfigReturn crImmunity = configParser.getConfigSection("immunity",false);
            if (crImmunity.isSuccess()) {
                for (Object ailmentKey : crImmunity.getConfigSection().getKeys(false).toArray()) {
                    Ailment ailment = AilmentManager.getAilmentByInternalName(ailmentKey.toString());
                    if (ailment != null) {
                        ConfigParser.ConfigReturn crImmunityAmount = configParser.getIntValue("immunity." + ailmentKey + ".amount",-1,false);
                        if (crImmunityAmount.isSuccess()) {
                            if (crImmunityAmount.getInt() > 0) {
                                AppliedImmunity appliedImmunity = new AppliedImmunity(ailmentKey.toString(), crImmunityAmount.getInt());
                                this.appliedImmunities.add(appliedImmunity);
                            }
                        }
                    }
                }
            }
            ConfigParser.ConfigReturn crInvincibleStart = configParser.getIntValue("invincible.start", 0, false);
            ConfigParser.ConfigReturn crInvincibleEnd = configParser.getIntValue("invincible.end", 0, false);
            if (crInvincibleStart.isSuccess() && crInvincibleEnd.isSuccess()) {
                if (crInvincibleStart.getInt() > 0 && crInvincibleEnd.getInt() > 0) {
                    this.invincibleStartDay = crInvincibleStart.getInt();
                    this.invincibleEndDay = crInvincibleEnd.getInt();
                }
            }
        } else {
            // new player - create file and default values
            save();
        }
    }

    public void save() {
        // Save file
    }

    public class Affliction {

        public Affliction(Ailment ailment, int afflictDay, long afflictTime, boolean secondaryApplied) {
            this.ailment = ailment;
            this.afflictDay = afflictDay;
            this.afflictTime = afflictTime;
            this.secondaryApplied = secondaryApplied;
        }

        private final Ailment ailment;
        private final int afflictDay;
        private final long afflictTime;
        private boolean secondaryApplied;

        public Ailment getAilment() {
            return ailment;
        }

        public int getAfflictDay() {
            return afflictDay;
        }

        public long getAfflictTime() {
            return afflictTime;
        }

        public boolean isSecondaryApplied() {
            return secondaryApplied;
        }

        public void setSecondaryApplied(boolean secondaryApplied) {
            this.secondaryApplied = secondaryApplied;
        }
    }
    public class AppliedVaccine {

        public AppliedVaccine(String ailmentKey, int appliedDay) {
            this.ailmentKey = ailmentKey;
            this.appliedDay = appliedDay;
        }

        private String ailmentKey;
        private int appliedDay;

        public String getAilmentKey() {
            return ailmentKey;
        }

        public int getAppliedDay() {
            return appliedDay;
        }

        public void setAppliedDay(int appliedDay) { this.appliedDay = appliedDay; }
    }
    public class AppliedImmunity {

        public AppliedImmunity(String ailmentKey, int immunityAmount) {
            this.ailmentKey = ailmentKey;
            this.immunityAmount = immunityAmount;
        }

        private String ailmentKey;
        private int immunityAmount;

        public String getAilmentKey() {
            return ailmentKey;
        }

        public int getImmunityAmount() {
            return immunityAmount;
        }

        public void setImmunityAmount(int immunityAmount) {
            this.immunityAmount = immunityAmount;
        }
    }
    public class PlayerHydration {

        public PlayerHydration() {

        }
        public PlayerHydration(UUID uuid, double hydration) {
            this.uuid = uuid;
            this.hydration = hydration;

            if (Epidemic.instance().config.useThirstBar()) {
                this.bar = Bukkit.createBossBar(Locale.localeText("thirst_bar_title", "Hydration", null), BarColor.BLUE, BarStyle.SEGMENTED_20);
                Player player = Bukkit.getPlayer(uuid);
                List<Player> lstThirstBar = bar.getPlayers();
                if (player != null) {
                    for (Player p : lstThirstBar) {
                        if (p.getUniqueId().equals(player.getUniqueId())) {
                            bar.removePlayer(player);
                        }
                    }
                    bar.addPlayer(player);
                }
            }
        }

        private UUID uuid;
        private BossBar bar;
        private double hydration = 0;

        public UUID getUuid() {
            return uuid;
        }

        public BossBar getBar() {
            if (Epidemic.instance().config.useThirstBar()) {
                return bar;
            } else {
                return null;
            }
        }

        public void setBar(BossBar bar) {
            if (Epidemic.instance().config.useThirstBar()) {
                this.bar = bar;
            }
        }

        public double getHydration() {
            return hydration;
        }

        public void setHydration(double hydration) {
            this.hydration = hydration;
        }

    }
}
