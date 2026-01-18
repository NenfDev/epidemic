package com.ibexmc.epidemic.ailments;

import java.sql.Timestamp;
import java.util.*;
import java.io.File;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.api.events.AfflictionEvent;
import com.ibexmc.epidemic.equipment.GasMask;
import com.ibexmc.epidemic.equipment.HazmatSuit;
import com.ibexmc.epidemic.equipment.IEquipment;
import com.ibexmc.epidemic.symptoms.*;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.helpers.PlayerPotionEffect;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.Logging;

public class Ailment {

    private String internalName = "";
    private String displayName = "";
    private boolean infectious = false;
    private int infectiousChance = 0;
    private Map<String, Integer> infectiousChanceBiomeModifier = new HashMap<>();
    private int nonInfectiousChance = 0;
    private int contagiousChanceBoost = 0;
    private int deliberateTransmissionChance = 0;
    private int maxImmunity = 0;
    private int immunityModifier = 0;
    private boolean reportBeforeSymptoms = false;
    private boolean warnOnAfflicted = false;
    private String afflictedText = "";
    private String symptomText = "";
    private String infectedOtherText = "";
    private String healedText = "";
    private String naturalCureText = "";
    private int naturalCureDays = 0;
    private int timeToSymptoms = 0;
    private boolean isFatal = false;
    private double damage = 0;
    private String secondaryAilment = "";
    private int secondaryAilmentTime = 0;
    private int gracePeriod = 0;
    private int cureWithSleepChance = 0;
    private String cureWithSleepMessage = "";
    private boolean fever = false;
    private boolean chills = false;
    private int contagiousRadius = 0;
    private boolean displayVomit = false;
    private boolean displayBleeding = false;
    private boolean displayBleedingFace = false;
    private boolean displayBleedingHead = false;
    private boolean displayBleedingChest = false;
    private boolean displayBleedingBack = false;
    private boolean displayBleedingLeftArm = false;
    private boolean displayBleedingRightArm = false;
    private boolean displayBleedingLeftLeg = false;
    private boolean displayBleedingRightLeg = false;
    private boolean displayBowel = false;
    private boolean displayUrinate = false;
    private boolean displaySweat = false;
    private boolean displayInjury = false;
    private boolean displayContagious = false;
    private boolean insomnia = false;
    private boolean hallucination = false;
    private boolean gibberish = false;
    private boolean mute = false;
    private boolean foodRot = false;
    private int removeXP = 0;
    private boolean rust = false;
    private int rustAmount = 0;
    private boolean clumsy = false;
    private int suddenDeathChance = 0;
    private String suddenDeathMessage = "";
    private boolean causedByFall = false;
    private boolean causedByInjury = false;
    private boolean causedByWeaponInjury = false;
    private boolean causedByExplosion = false;
    private boolean causedByFire = false;
    private boolean causedByWater = false;
    private boolean causedByDirtyWater = false;
    private boolean causedByDehydration = false;
    private boolean causedByConsume = false;
    private boolean causedByMagic = false;
    private int damageChanceModifier = 0;
    private List<EntityType> causedByEntityType = new ArrayList<>();
    private List<PlayerPotionEffect> ailmentEffects = new ArrayList<>();
    private Particle particleOnHeal;
    private Effect soundOnHeal;
    private List<String> preventAfflictWorlds = new ArrayList<>();
    private List<String> preventSymptomWorlds = new ArrayList<>();
    private boolean isValid = false;
    private Map<Material, BlockBreak> blockBreaks = new HashMap<>();


    public Ailment() {
        this.isValid = false;
    }
    public Ailment(File file) {
        loadFromFile(file);
    }

    /**
     * Gets the internal name of the ailment
     * @return Ailment internal name
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Gets the display name of the ailment
     * @return Ailment in-game name
     */
    public String getDisplayName() {
        return StringFunctions.colorToString(displayName);
    }

    /**
     * If the ailment is infectious
     * @return If true, ailment is infectious
     */
    public boolean isInfectious() {
        return infectious;
    }

    /**
     * Gets the infectious chance of the ailment
     * @return Infectious Chance (1-10000)
     */
    public int getInfectiousChance() {
        return infectiousChance;
    }

    /**
     * Gets the Chance modifier for the biome
     * @param biome Biome
     * @return Chance modifier
     */
    public int getInfectiousChanceBiomeModifier(String biome) {
        if (infectiousChanceBiomeModifier.containsKey(biome)) {
            return infectiousChanceBiomeModifier.get(biome);
        } else {
            return 0;
        }
    }

    /**
     * Gets the non-infectious chance of the ailment
     * @return Non-Infectious Chance (1-10000)
     */
    public int getNonInfectiousChance() {
        return nonInfectiousChance;
    }

    /**
     * Gets the contagious chance boost of the ailment
     * @return Contagious chance boost (1-10000)
     */
    public int getContagiousChanceBoost() {
        return contagiousChanceBoost;
    }

    /**
     * Gets the deliberate transmission chance of the ailment
     * @return Deliberate transmission chance
     */
    public int getDeliberateTransmissionChance() { return deliberateTransmissionChance; }

    /**
     * Gets the maximum immunity for this ailment
     * @return Maximum immunity (1-10000)
     */
    public int getMaxImmunity() {
        return maxImmunity;
    }

    /**
     * Gets the immunity modifier, which is how much immunity to add each time the player is healed
     * @return Immunity Modifier (1-10000)
     */
    public int getImmunityModifier() {
        return immunityModifier;
    }

    /**
     * If the player should be warned when afflicted with an ailment
     * @return If true, warn the player
     */
    public boolean isWarnOnAfflicted() {
        return warnOnAfflicted;
    }

    /**
     * Text to provide to the player if warning them of an affliction
     * @return Affliction text
     */
    public String getAfflictedText() {
        return StringFunctions.colorToString(afflictedText);
    }

    /**
     * Text to provide to the player when symptoms begin
     * @return Symptom text
     */
    public String getSymptomText() {
        return StringFunctions.colorToString(symptomText);
    }

    /**
     * Text to provide when the player infects someone else with this ailment
     * @return Infected other player text
     */
    public String getInfectedOtherText() {
        return StringFunctions.colorToString(infectedOtherText);
    }

    /**
     * If the player should see the affliction in /health if symptoms have not yet started
     * @return If true, show in /health
     */
    public boolean isReportBeforeSymptoms() {
        return reportBeforeSymptoms;
    }

    /**
     * Text to show to the player when they are cured of this ailment
     * @return Healed text
     */
    public String getHealedText() {
        return StringFunctions.colorToString(healedText);
    }

    /**
     * Gets the text to display when the ailment naturally heals
     * @return Natural heal text
     */
    public String getNaturalCureText() {
        return naturalCureText;
    }

    /**
     * Gets the number of days until the ailment naturally heals
     * @return Natural heal days
     */
    public int getNaturalCureDays() {
        return naturalCureDays;
    }

    /**
     * Number of seconds between being afflicted with the ailment, and symptoms being applied
     * @return Number of seconds until symptoms
     */
    public int getTimeToSymptoms() {
        return timeToSymptoms;
    }

    /**
     * Gets the secondary ailment key
     * @return Secondary Ailment key
     */
    public String getSecondaryAilment() {
        return secondaryAilment;
    }

    /**
     * Gets the number of seconds until secondary ailment kicks in
     * @return Time, in seconds
     */
    public int getSecondaryAilmentTime() {
        return secondaryAilmentTime;
    }

    /**
     * Returns the grace period after an illness has been healed until
     * it can be applied again.  Does NOT persist after restart
     * @return Grace period, in seconds
     */
    public int getGracePeriod() {
        return gracePeriod;
    }

    /**
     * If the ailment should cause fatal damage
     * @return If true, ailment should be fatal
     */
    public boolean isFatal() {
        return isFatal;
    }

    /**
     * Amount of damage to apply each time symptoms are applied
     * @return Damage amount
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Returns the chance that the ailment will be cured by sleep
     * This number is out of 1000 - lower numbers indicate less chance
     * Set to 1000 for always cure with sleep, 0 to never cure with sleep
     * @return Cure with sleep chance (out of 1000)
     */
    public int cureWithSleepChance() {
        return cureWithSleepChance;
    }

    /**
     * Returns the message to send to the player if they are cured by sleeping
     * @return Cure with Sleep message
     */
    public String getCureWithSleepMessage() {
        return cureWithSleepMessage;
    }

    /**
     * If the player should get a fever, impacting their temperature
     * @return If true, player should get a fever
     */
    public boolean isFever() {
        return fever;
    }

    /**
     * If the player should get the chills, impacting their temperature
     * @return If true, player should get the chills
     */
    public boolean isChills() {
        return chills;
    }

    /**
     * Radius in which the player is contagious
     * @return Number of block contagious radius
     */
    public int getContagiousRadius() {
        return contagiousRadius;
    }

    /**
     * If the player should vomit when symptoms are applied
     * @return If true, player should vomit when symptoms are applied
     */
    public boolean isDisplayVomit() {
        return displayVomit;
    }

    /**
     * If the player should bleed when symptoms are applied
     * @return If true, player should bleed when symptoms are applied
     */
    public boolean isDisplayBleeding() {
        return (
                displayBleedingFace ||
                displayBleedingChest ||
                displayBleedingBack ||
                displayBleedingLeftArm ||
                displayBleedingRightArm ||
                displayBleedingLeftLeg ||
                displayBleedingRightLeg
        );
    }

    /**
     * If the player should bleed from the face when symptoms are applied
     * @return If true, player should bleed from the face when symptoms are applied
     */
    public boolean isDisplayBleedingFace() {
        return displayBleedingFace;
    }

    /**
     * If the player should bleed from the head when symptoms are applied
     * @return If true, player should bleed from the head when symptoms are applied
     */
    public boolean isDisplayBleedingHead() {
        return displayBleedingHead;
    }

    /**
     * If the player should bleed from the chest when symptoms are applied
     * @return If true, player should bleed from the chest when symptoms are applied
     */
    public boolean isDisplayBleedingChest() {
        return displayBleedingChest;
    }

    /**
     * If the player should bleed from the back when symptoms are applied
     * @return If true, player should bleed from the back when symptoms are applied
     */
    public boolean isDisplayBleedingBack() {
        return displayBleedingBack;
    }

    /**
     * If the player should bleed from the left arm when symptoms are applied
     * @return If true, player should bleed from the left arm when symptoms are applied
     */
    public boolean isDisplayBleedingLeftArm() {
        return displayBleedingLeftArm;
    }

    /**
     * If the player should bleed from the right arm when symptoms are applied
     * @return If true, player should bleed from the right arm when symptoms are applied
     */
    public boolean isDisplayBleedingRightArm() {
        return displayBleedingRightArm;
    }

    /**
     * If the player should bleed from the left leg when symptoms are applied
     * @return If true, player should bleed from the left leg when symptoms are applied
     */
    public boolean isDisplayBleedingLeftLeg() {
        return displayBleedingLeftLeg;
    }

    /**
     * If the player should bleed from the right leg when symptoms are applied
     * @return If true, player should bleed from the right leg when symptoms are applied
     */
    public boolean isDisplayBleedingRightLeg() {
        return displayBleedingRightLeg;
    }

    /**
     * If the player should lose control of their bowels when symptoms are applied
     * @return If true, player should lose control of their bowels when symptoms are applied
     */
    public boolean isDisplayBowel() {
        return displayBowel;
    }

    /**
     * If the player should urinate when symptoms are applied
     * @return If true, player should urinate when symptoms are applied
     */
    public boolean isDisplayUrinate() {
        return displayUrinate;
    }

    /**
     * If the player should sweat when symptoms are applied
     * @return If true, player should sweat when symptoms are applied
     */
    public boolean isDisplaySweat() {
        return displaySweat;
    }

    /**
     * If the player should display a particle effect if injured when symptoms are applied
     * @return If true, player should display a particle effect if injured when symptoms are applied
     */
    public boolean isDisplayInjury() {
        return displayInjury;
    }

    /**
     * If the player should display a particle effect if contagious when symptoms are applied
     * @return If true, player should display a particle effect if contagious when symptoms are applied
     */
    public boolean isDisplayContagious() {
        return displayContagious;
    }

    /**
     * If the player has insomnia
     * @return If true, player has insomnia
     */
    public boolean isInsomnia() {
        return insomnia;
    }

    /**
     * If the player is suffering from hallucinations
     * @return If true, player has hallucinations
     */
    public boolean isHallucination() {
        return hallucination;
    }

    /**
     * If the player is speaking gibberish.  Any chat sent to game will be garbled
     * @return If true, player is speaking gibberish
     */
    public boolean isGibberish() { return gibberish; }

    /**
     * If the player is mute.  No chat will be sent to game
     * @return If true, player is mute
     */
    public boolean isMute() {
        return mute;
    }

    /**
     * If the players food should rot when symptoms are applied
     * @return If true, food should rot
     */
    public boolean isFoodRot() {
        return foodRot;
    }

    /**
     * Amount of XP to remove from the player each time symptoms are applied
     * @return Amount of XP to remove from player
     */
    public int getRemoveXP() {
        return removeXP;
    }

    /**
     * If the players iron armor, tools and weapons will rust
     * @return If true, the players iron armor, tools and weapons will rust
     */
    public boolean isRust() {
        return rust;
    }

    /**
     * Amount of damage to apply to the players iron armor, tools & weapons when symptoms are applied
     * @return Damage amount to apply to iron armor, tools & weapons
     */
    public int getRustAmount() {
        return rustAmount;
    }

    /**
     * If the player is clumsy.  If they are, there is a 1 in 5 chance each time symptoms are applied that the
     * player will drop the item in their main hand
     * @return If true, player is clumsy
     */
    public boolean isClumsy() {
        return clumsy;
    }

    /**
     * Returns the chance of sudden death.  Number between 0-1000
     * If the random number < chance, player will be killed immediately
     * @return Sudden death chance
     */
    public int getSuddenDeathChance() {
        return suddenDeathChance;
    }

    /**
     * Returns the message to send to the player when they suddenly die
     * @return Sudden death message
     */
    public String suddenDeathMessage() {
        return suddenDeathMessage;
    }

    /**
     * If the ailment can be caused by fall damage
     * @return If true, ailment is caused by fall damage
     */
    public boolean isCausedByFall() {
        return causedByFall;
    }

    /**
     * If the ailment can be caused by injury (being hit, shot with arrows etc.)
     * @return If true, ailment is caused by injury damage
     */
    public boolean isCausedByInjury() {
        return causedByInjury;
    }

    /**
     * If the ailment can be caused by injury from a weapon
     * @return If true, ailment is caused by weapon damage
     */
    public boolean isCausedByWeaponInjury() {
        return causedByWeaponInjury;
    }

    /**
     * If the ailment can be caused by explosion damage
     * @return If true, ailment is caused by explosion damage
     */
    public boolean isCausedByExplosion() {
        return causedByExplosion;
    }

    /**
     * If the ailment can be caused by fire damage
     * @return If true, ailment is caused by fire damage
     */
    public boolean isCausedByFire() {
        return causedByFire;
    }

    /**
     * If the ailment can be caused by water damage
     * @return If true, ailment is caused by water damage
     */
    public boolean isCausedByWater() {
        return causedByWater;
    }

    /**
     * If the ailment can be caused by dirty water
     * @return If true, ailment is caused by dirty water
     */
    public boolean isCausedByDirtyWater() {
        return causedByDirtyWater;
    }

    /**
     * If the ailment can be caused by dehydration
     * @return If true, ailment is caused by dehydration
     */
    public boolean isCausedByDehydration() {
        return causedByDehydration;
    }

    /**
     * If the ailment can be caused by food/drink consumption
     * @return If true, ailment is caused by food/drink consumption
     */
    public boolean isCausedByConsume() {
        return causedByConsume;
    }

    /**
     * If the ailment can be caused by magic damage
     * @return If true, ailment is caused by magic damage
     */
    public boolean isCausedByMagic() {
        return causedByMagic;
    }

    /**
     * Damage chance modifier, used as a multiplier for damage when calculating infectious chance
     * @return Amount to multiply optional damage amount by for infectious chance
     */
    public int getDamageChanceModifier() {
        return damageChanceModifier;
    }

    /**
     * List of entities that can cause an ailment if they cause any damage
     * @return List of entities that can cause the ailment
     */
    public List<EntityType> getCausedByEntityType() {
        return causedByEntityType;
    }

    /**
     * List of potion effects to apply to the player when symptoms occur
     * @return List of potion effects to apply
     */
    public List<PlayerPotionEffect> getAilmentEffects() {
        if (ailmentEffects != null) {
            return ailmentEffects;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Particle to display when the player is healed
     * @return Particle effect to show when healed
     */
    public Particle getParticleOnHeal() {
        return particleOnHeal;
    }

    /**
     * Sound effect to play when the player is healed
     * @return Sound Effect to use when player is healed
     */
    public Effect getSoundOnHeal() {
        return soundOnHeal;
    }

    /**
     * List of world names to to ignore for this ailment
     * @return List of world names to ignore
     */
    public List<String> getPreventAfflictWorlds() {
        if (Epidemic.instance().config.getPreventAfflictWorlds() != null) {
            if (Epidemic.instance().config.getPreventAfflictWorlds().size() > 0) {
                List<String> worlds = this.preventAfflictWorlds;
                for (String worldName : Epidemic.instance().config.getPreventAfflictWorlds()){
                    if (!worlds.contains(worldName))
                        worlds.add(worldName);
                }
                return worlds;
            } else {
                return preventAfflictWorlds;
            }
        } else {
            return preventAfflictWorlds;
        }
    }

    /**
     * List of world names to ignore for symptoms of this ailment
     * @return List of world names to ignore
     */
    public List<String> getPreventSymptomWorlds() {
        if (Epidemic.instance().config.getPreventSymptomWorlds() != null) {
            if (Epidemic.instance().config.getPreventSymptomWorlds().size() > 0) {
                List<String> worlds = this.preventSymptomWorlds;
                for (String worldName : Epidemic.instance().config.getPreventSymptomWorlds()){
                    if (!worlds.contains(worldName))
                        worlds.add(worldName);
                }
                return worlds;
            } else {
                return preventSymptomWorlds;
            }
        } else {
            return preventSymptomWorlds;
        }
    }

    public Map<Material, BlockBreak> getBlockBreaks() {
        if (this.blockBreaks != null) {
            return this.blockBreaks;
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Flag to indicate if the ailment is valid
     * @return If true, ailment is valid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Loads the provided file, assigns all the Ailment values and saves the ailment to
     * the ailments list
     * @param file File to load
     */
    private void loadFromFile(File file) {

        /*
            Ailment Version 1.1
        */

        Epidemic plugin = Epidemic.instance();

        if (file.exists()) {


            ConfigParser configParser = new ConfigParser(Epidemic.instance(), file);
            Logging.debug(
                    "Ailment",
                    "Constructor(String)",
                    "Loading " + file.getName()
            );

            // Internal Name
            ConfigParser.ConfigReturn crInternalName = configParser.getStringValue(
                    "internal_name",
                    "",
                    true
            );
            if (crInternalName.isSuccess()) {
                this.internalName = crInternalName.getString();
            } else {
                return;
            }

            // Ailment Version - if not 1.1, run the conversion before continuing
            ConfigParser.ConfigReturn crVersion = configParser.getDoubleValue(
                    "v",
                    0d,
                    false
            );
            if (crVersion.isSuccess()) {
                if (crVersion.getDouble() != 1.1) {
                    return;
                }
            }

            // Display Name
            ConfigParser.ConfigReturn crDisplayName = configParser.getStringValue(
                    "display_name",
                    "",
                    true
            );
            if (crDisplayName.isSuccess()) {
                this.displayName = crDisplayName.getString();
            } else {
                return;
            }

            // Infectious
            ConfigParser.ConfigReturn crInfectious = configParser.getBooleanValue(
                    "infectious",
                    true,
                    false
            );
            if (crInfectious.isSuccess()) {
                this.infectious = crInfectious.getBoolean();
            } else {
                this.infectious = true;
            }


            // Infectious Chance
            ConfigParser.ConfigReturn crInfectiousChance = configParser.getIntValue(
                    "infectious_chance",
                    0,
                    false
            );
            if (crInfectiousChance.isSuccess()) {
                this.infectiousChance = crInfectiousChance.getInt();
            } else {
                this.infectiousChance = 0;
            }

            // Infectious Chance Biome Modifier
            infectiousChanceBiomeModifier = new HashMap<>();
            ConfigParser.ConfigReturn crInfectiousBiomeMod = configParser.getConfigSection(
                    "infectious_chance_biome_modifier",
                    false
            );
            if (crInfectiousBiomeMod.isSuccess()) {
                for (String key : crInfectiousBiomeMod.getConfigSection().getKeys(false)) {
                    try {
                        Biome biome = Biome.valueOf(key);
                        ConfigParser.ConfigReturn crInfectiousBiomeModBiome = configParser.getIntValue(
                                "infectious_chance_biome_modifier." + key,
                                0,
                                false
                        );
                        if (crInfectiousBiomeModBiome.isSuccess()) {
                            infectiousChanceBiomeModifier.put(key, crInfectiousBiomeModBiome.getInt());
                        }
                    } catch (Exception ex) {
                        // error
                    }
                }
            }

            // Non-Infectious Chance
            ConfigParser.ConfigReturn crNonInfectiousChance = configParser.getIntValue(
                    "non_infectious_chance",
                    0,
                    false
            );
            if (crNonInfectiousChance.isSuccess()) {
                this.nonInfectiousChance = crNonInfectiousChance.getInt();
            } else {
                this.nonInfectiousChance = 0;
            }

            // Contagious Chance Boost
            ConfigParser.ConfigReturn crContagiousChanceBoost = configParser.getIntValue(
                    "contagious_chance_boost",
                    0,
                    false
            );
            if (crContagiousChanceBoost.isSuccess()) {
                this.contagiousChanceBoost = crContagiousChanceBoost.getInt();
            } else {
                this.contagiousChanceBoost = 0;
            }

            // Deliberate Transmission Chance
            ConfigParser.ConfigReturn crDeliberateTransmissionChance = configParser.getIntValue(
                    "deliberate_transmission_chance",
                    0,
                    false
            );
            if (crDeliberateTransmissionChance.isSuccess()) {
                this.deliberateTransmissionChance = crDeliberateTransmissionChance.getInt();
            } else {
                this.deliberateTransmissionChance = 0;
            }

            // Immunity Modifier
            ConfigParser.ConfigReturn crImmunityModifier = configParser.getIntValue(
                    "immunity_modifier",
                    0,
                    false
            );
            if (crImmunityModifier.isSuccess()) {
                this.immunityModifier = crImmunityModifier.getInt();
            } else {
                this.immunityModifier = 0;
            }


            // Max Immunity
            ConfigParser.ConfigReturn crMaxImmunity = configParser.getIntValue(
                    "max_immunity",
                    0,
                    false
            );
            if (crMaxImmunity.isSuccess()) {
                this.maxImmunity = crMaxImmunity.getInt();
            } else {
                this.maxImmunity = 0;
            }

            // Report Before Symptoms
            ConfigParser.ConfigReturn crReportBeforeSymptoms = configParser.getBooleanValue(
                    "report_before_symptoms",
                    true,
                    false
            );
            if (crReportBeforeSymptoms.isSuccess()) {
                this.reportBeforeSymptoms = crReportBeforeSymptoms.getBoolean();
            } else {
                this.reportBeforeSymptoms = true;
            }

            // Warn on Afflicted
            ConfigParser.ConfigReturn crWarnAfflicted = configParser.getBooleanValue(
                    "warn_on_afflicted",
                    true,
                    false
            );
            if (crWarnAfflicted.isSuccess()) {
                this.warnOnAfflicted = crWarnAfflicted.getBoolean();
            } else {
                this.warnOnAfflicted = true;
            }

            // Afflicted Text
            ConfigParser.ConfigReturn crAfflictedText = configParser.getStringValue(
                    "afflicted_text",
                    "",
                    false
            );
            if (crAfflictedText.isSuccess()) {
                this.afflictedText = crAfflictedText.getString();
            } else {
                this.afflictedText = "";
            }

            // Symptom Text
            ConfigParser.ConfigReturn crSymptomText = configParser.getStringValue(
                    "symptom_text",
                    "",
                    false
            );
            if (crSymptomText.isSuccess()) {
                this.symptomText = crSymptomText.getString();
            } else {
                this.symptomText = "";
            }

            // Infected Other Text
            ConfigParser.ConfigReturn crInfectedOther = configParser.getStringValue(
                    "infected_other_text",
                    "",
                    false
            );
            if (crInfectedOther.isSuccess()) {
                this.infectedOtherText = crInfectedOther.getString();
            } else {
                this.infectedOtherText = "";
            }

            // Healed Text
            ConfigParser.ConfigReturn crHealedText = configParser.getStringValue(
                    "healed_text",
                    "",
                    false
            );
            if (crHealedText.isSuccess()) {
                this.healedText = crHealedText.getString();
            } else {
                this.healedText = "";
            }

            // Natural Cure Days
            ConfigParser.ConfigReturn crNaturalCureDays = configParser.getIntValue(
                    "natural_cure_days",
                    0,
                    false
            );
            if (crNaturalCureDays.isSuccess()) {
                this.naturalCureDays = crNaturalCureDays.getInt();
            } else {
                this.naturalCureDays = 0;
            }

            // Natural Cure Text
            ConfigParser.ConfigReturn crNaturalCureText = configParser.getStringValue(
                    "natural_cure_text",
                    "",
                    false
            );
            if (crNaturalCureText.isSuccess()) {
                this.naturalCureText = crNaturalCureText.getString();
            } else {
                this.naturalCureText = "";
            }

            // Time to Symptoms
            ConfigParser.ConfigReturn crTimeToSymptoms = configParser.getIntValue(
                    "time_to_symptoms",
                    0,
                    false
            );
            if (crTimeToSymptoms.isSuccess()) {
                this.timeToSymptoms = crTimeToSymptoms.getInt();
            } else {
                this.timeToSymptoms = 0;
            }

            // Secondary Ailment
            ConfigParser.ConfigReturn crSecondaryAilment = configParser.getStringValue(
                    "secondary_ailment.ailment",
                    "",
                    false
            );
            if (crSecondaryAilment.isSuccess()) {
                this.secondaryAilment = crSecondaryAilment.getString().toLowerCase();
                // Secondary Ailment Time (only get if we have a valid secondary ailment)
                ConfigParser.ConfigReturn crTimeToSecondary = configParser.getIntValue(
                        "secondary_ailment.time",
                        0,
                        false
                );
                if (crTimeToSecondary.isSuccess()) {
                    this.secondaryAilmentTime = crTimeToSecondary.getInt();
                } else {
                    this.secondaryAilmentTime = 0;
                }
            } else {
                this.secondaryAilment = "";
                this.secondaryAilmentTime = 0;
            }

            // Is Fatal
            ConfigParser.ConfigReturn crIsFatal = configParser.getBooleanValue(
                    "is_fatal",
                    true,
                    false
            );
            if (crIsFatal.isSuccess()) {
                this.isFatal = crIsFatal.getBoolean();
            } else {
                this.isFatal = true;
            }

            // Damage
            ConfigParser.ConfigReturn crDamage = configParser.getDoubleValue(
                    "damage",
                    0,
                    false
            );
            if (crDamage.isSuccess()) {
                this.damage = crDamage.getDouble();
            } else {
                this.damage = 0;
            }

            // Cure with Sleep
            ConfigParser.ConfigReturn crCureWithSleepChance = configParser.getIntValue(
                    "cure_with_sleep_chance",
                    0,
                    false
            );
            if (crCureWithSleepChance.isSuccess()) {
                this.cureWithSleepChance = crCureWithSleepChance.getInt();
                ConfigParser.ConfigReturn crCureWithSleepMessage = configParser.getStringValue(
                        "cure_with_sleep_message",
                        "",
                        false
                );
                if (crCureWithSleepMessage.isSuccess()) {
                    this.cureWithSleepMessage = crCureWithSleepMessage.getString();
                }
            }

            // -- SYMPTOMS --

            // Fever
            ConfigParser.ConfigReturn crFever = configParser.getBooleanValue(
                    "fever",
                    false,
                    false
            );
            if (crFever.isSuccess()) {
                this.fever = crFever.getBoolean();
            } else {
                this.fever = false;
            }

            // Chills
            ConfigParser.ConfigReturn crChills = configParser.getBooleanValue(
                    "chills",
                    false,
                    false
            );
            if (crChills.isSuccess()) {
                this.chills = crChills.getBoolean();
            } else {
                this.chills = false;
            }

            // Display Vomit
            ConfigParser.ConfigReturn crVomit = configParser.getBooleanValue(
                    "display_vomit",
                    false,
                    false
            );
            if (crVomit.isSuccess()) {
                this.displayVomit = crVomit.getBoolean();
            } else {
                this.displayVomit = false;
            }

            // Display Bleeding - Face
            ConfigParser.ConfigReturn crBleedingFace = configParser.getBooleanValue(
                    "display_bleeding.face",
                    false,
                    false
            );
            if (crBleedingFace.isSuccess()) {
                this.displayBleedingFace = crBleedingFace.getBoolean();
            } else {
                this.displayBleedingFace = false;
            }

            // Display Bleeding - Head
            ConfigParser.ConfigReturn crBleedingHead = configParser.getBooleanValue(
                    "display_bleeding.head",
                    false,
                    false
            );
            if (crBleedingHead.isSuccess()) {
                this.displayBleedingHead = crBleedingHead.getBoolean();
            } else {
                this.displayBleedingHead = false;
            }

            // Display Bleeding - Chest
            ConfigParser.ConfigReturn crBleedingChest = configParser.getBooleanValue(
                    "display_bleeding.chest",
                    false,
                    false
            );
            if (crBleedingChest.isSuccess()) {
                this.displayBleedingChest = crBleedingChest.getBoolean();
            } else {
                this.displayBleedingChest = false;
            }

            // Display Bleeding - Back
            ConfigParser.ConfigReturn crBleedingBack = configParser.getBooleanValue(
                    "display_bleeding.back",
                    false,
                    false
            );
            if (crBleedingBack.isSuccess()) {
                this.displayBleedingBack = crBleedingBack.getBoolean();
            } else {
                this.displayBleedingBack = false;
            }

            // Display Bleeding - LeftArm
            ConfigParser.ConfigReturn crBleedingLeftArm = configParser.getBooleanValue(
                    "display_bleeding.left_arm",
                    false,
                    false
            );
            if (crBleedingLeftArm.isSuccess()) {
                this.displayBleedingLeftArm = crBleedingLeftArm.getBoolean();
            } else {
                this.displayBleedingLeftArm = false;
            }

            // Display Bleeding - LeftLeg
            ConfigParser.ConfigReturn crBleedingLeftLeg = configParser.getBooleanValue(
                    "display_bleeding.left_leg",
                    false,
                    false
            );
            if (crBleedingLeftLeg.isSuccess()) {
                this.displayBleedingLeftLeg = crBleedingLeftLeg.getBoolean();
            } else {
                this.displayBleedingLeftLeg = false;
            }

            // Display Bleeding - RightArm
            ConfigParser.ConfigReturn crBleedingRightArm = configParser.getBooleanValue(
                    "display_bleeding.right_arm",
                    false,
                    false
            );
            if (crBleedingRightArm.isSuccess()) {
                this.displayBleedingRightArm = crBleedingRightArm.getBoolean();
            } else {
                this.displayBleedingRightArm = false;
            }

            // Display Bleeding - RightLeg
            ConfigParser.ConfigReturn crBleedingRightLeg = configParser.getBooleanValue(
                    "display_bleeding.right_leg",
                    false,
                    false
            );
            if (crBleedingRightLeg.isSuccess()) {
                this.displayBleedingRightLeg = crBleedingRightLeg.getBoolean();
            } else {
                this.displayBleedingRightLeg = false;
            }
            
            this.displayBleeding = (
                        this.displayBleedingBack ||
                        this.displayBleedingChest ||
                        this.displayBleedingFace ||
                        this.displayBleedingHead ||
                        this.displayBleedingLeftArm ||
                        this.displayBleedingLeftLeg ||
                        this.displayBleedingRightArm ||        
                        this.displayBleedingRightLeg        
                    );

            // Display Bowel
            ConfigParser.ConfigReturn crBowel = configParser.getBooleanValue(
                    "display_bowel",
                    false,
                    false
            );
            if (crBowel.isSuccess()) {
                this.displayBowel = crBowel.getBoolean();
            } else {
                this.displayBowel = false;
            }

            // Display Urinate
            ConfigParser.ConfigReturn crUrine = configParser.getBooleanValue(
                    "display_urinate",
                    false,
                    false
            );
            if (crUrine.isSuccess()) {
                this.displayUrinate = crUrine.getBoolean();
            } else {
                this.displayUrinate = false;
            }

            // Display Sweat
            ConfigParser.ConfigReturn crSweat = configParser.getBooleanValue(
                    "display_sweat",
                    false,
                    false
            );
            if (crSweat.isSuccess()) {
                this.displaySweat = crSweat.getBoolean();
            } else {
                this.displaySweat = false;
            }

            // Display Contagious
            ConfigParser.ConfigReturn crContagious = configParser.getBooleanValue(
                    "display_contagious",
                    false,
                    false
            );
            if (crContagious.isSuccess()) {
                this.displayContagious = crContagious.getBoolean();
            } else {
                this.displayContagious = false;
            }

            // Display Injury
            ConfigParser.ConfigReturn crInjury = configParser.getBooleanValue(
                    "display_injury",
                    false,
                    false
            );
            if (crInjury.isSuccess()) {
                this.displayInjury = crInjury.getBoolean();
            } else {
                this.displayInjury = false;
            }

            // Insomnia
            ConfigParser.ConfigReturn crInsomnia = configParser.getBooleanValue(
                    "insomnia",
                    false,
                    false
            );
            if (crInsomnia.isSuccess()) {
                this.insomnia = crInsomnia.getBoolean();
            } else {
                this.insomnia = false;
            }

            // Hallucinations
            ConfigParser.ConfigReturn crHallucination = configParser.getBooleanValue(
                    "hallucination",
                    false,
                    false
            );
            if (crHallucination.isSuccess()) {
                this.hallucination = crHallucination.getBoolean();
            } else {
                this.hallucination = false;
            }

            // Gibberish
            ConfigParser.ConfigReturn crGibberish = configParser.getBooleanValue(
                    "gibberish",
                    false,
                    false
            );
            if (crGibberish.isSuccess()) {
                this.gibberish = crGibberish.getBoolean();
            } else {
                this.gibberish = false;
            }

            // Food Rot
            ConfigParser.ConfigReturn crFoodRot = configParser.getBooleanValue(
                    "food_rot",
                    false,
                    false
            );
            if (crFoodRot.isSuccess()) {
                this.foodRot = crFoodRot.getBoolean();
            } else {
                this.foodRot = false;
            }

            // Remove XP
            ConfigParser.ConfigReturn crRemoveXP = configParser.getIntValue(
                    "remove_xp",
                    0,
                    false
            );
            if (crRemoveXP.isSuccess()) {
                this.removeXP = crRemoveXP.getInt();
            } else {
                this.removeXP = 0;
            }

            // Rust
            ConfigParser.ConfigReturn crRust = configParser.getBooleanValue(
                    "food_rot",
                    false,
                    false
            );
            if (crRust.isSuccess()) {
                this.rust = crRust.getBoolean();
                if (this.rust) {
                    // Rust Amount - get only if Rust = true
                    ConfigParser.ConfigReturn crRustAmount = configParser.getIntValue(
                            "rust_amount",
                            0,
                            false
                    );
                    if (crRustAmount.isSuccess()) {
                        this.rustAmount = crRustAmount.getInt();
                    } else {
                        this.rustAmount = 0;
                    }
                } else {
                    this.rustAmount = 0;
                }
            } else {
                this.rust = false;
                this.rustAmount = 0;
            }

            // Clumsy
            ConfigParser.ConfigReturn crClumsy = configParser.getBooleanValue(
                    "clumsy",
                    false,
                    false
            );
            if (crClumsy.isSuccess()) {
                this.clumsy = crClumsy.getBoolean();
            } else {
                this.clumsy = false;
            }

            // Sudden Death
            ConfigParser.ConfigReturn crSuddenDeathChance = configParser.getIntValue(
              "sudden_death_chance",
              0,
              false
            );
            if (crSuddenDeathChance.isSuccess()) {
                this.suddenDeathChance = crSuddenDeathChance.getInt();
                ConfigParser.ConfigReturn crSuddenDeathMessage = configParser.getStringValue(
                        "sudden_death_message",
                        "",
                        false
                );
                if (crSuddenDeathMessage.isSuccess()) {
                    this.suddenDeathMessage = crSuddenDeathMessage.getString();
                }
            }

            // Potion Effects
            ConfigParser.ConfigReturn crAddEffects = configParser.getEffectList(
                    "ailment_effects",
                    false
            );
            if (crAddEffects.isSuccess()) {
                if (crAddEffects.getEffects() != null) {
                    this.ailmentEffects = crAddEffects.getEffects();
                }
            }

            // -- NON-INFECTIOUS CAUSED BY --

            // Caused By Fall
            ConfigParser.ConfigReturn crCausedByFall = configParser.getBooleanValue(
                    "caused_by_fall",
                    false,
                    false
            );
            if (crCausedByFall.isSuccess()) {
                this.causedByFall = crCausedByFall.getBoolean();
            } else {
                this.causedByFall = false;
            }

            // Caused By Injury
            ConfigParser.ConfigReturn crCausedByInjury = configParser.getBooleanValue(
                    "caused_by_injury",
                    false,
                    false
            );
            if (crCausedByInjury.isSuccess()) {
                this.causedByInjury = crCausedByInjury.getBoolean();
            } else {
                this.causedByInjury = false;
            }

            // Caused By Weapon Injury
            ConfigParser.ConfigReturn crCausedByWeaponInjury = configParser.getBooleanValue(
                    "caused_by_weapon_injury",
                    false,
                    false
            );
            if (crCausedByWeaponInjury.isSuccess()) {
                this.causedByWeaponInjury = crCausedByWeaponInjury.getBoolean();
            } else {
                this.causedByWeaponInjury = false;
            }

            // Caused By Explosion
            ConfigParser.ConfigReturn crCausedByExplosion = configParser.getBooleanValue(
                    "caused_by_explosion",
                    false,
                    false
            );
            if (crCausedByExplosion.isSuccess()) {
                this.causedByExplosion = crCausedByExplosion.getBoolean();
            } else {
                this.causedByExplosion = false;
            }

            // Caused By Fire
            ConfigParser.ConfigReturn crCausedByFire = configParser.getBooleanValue(
                    "caused_by_fire",
                    false,
                    false
            );
            if (crCausedByFire.isSuccess()) {
                this.causedByFire = crCausedByFire.getBoolean();
            } else {
                this.causedByFire = false;
            }

            // Caused By Water
            ConfigParser.ConfigReturn crCausedByWater = configParser.getBooleanValue(
                    "caused_by_water",
                    false,
                    false
            );
            if (crCausedByWater.isSuccess()) {
                this.causedByWater = crCausedByWater.getBoolean();
            } else {
                this.causedByWater = false;
            }

            // Caused By Consume
            ConfigParser.ConfigReturn crCausedByConsume = configParser.getBooleanValue(
                    "caused_by_consume",
                    false,
                    false
            );
            if (crCausedByConsume.isSuccess()) {
                this.causedByConsume = crCausedByConsume.getBoolean();
            } else {
                this.causedByConsume = false;
            }
            
            // Caused By Magic
            ConfigParser.ConfigReturn crCausedByMagic = configParser.getBooleanValue(
                    "caused_by_magic",
                    false,
                    false
            );
            if (crCausedByMagic.isSuccess()) {
                this.causedByMagic = crCausedByMagic.getBoolean();
            } else {
                this.causedByMagic = false;
            }

            // Caused By Entity
            ConfigParser.ConfigReturn crCausedByEntity = configParser.getStringList(
                    "caused_by_entity",
                    false
            );
            if (crCausedByEntity.isSuccess()) {
                List<String> entityTypeList = crCausedByEntity.getStringList();
                this.causedByEntityType = new ArrayList<>();
                if (entityTypeList != null) {
                    for (String entityTypeName : entityTypeList) {
                        EntityType entityType = EntityType.valueOf(entityTypeName);
                        if (entityType != null) {
                            this.causedByEntityType.add(entityType);
                        }
                    }
                }
            } else {
                this.causedByEntityType = new ArrayList<>();
            }

            // Damage Chance Modifier
            ConfigParser.ConfigReturn crDamageChanceModifier = configParser.getIntValue(
                    "damage_chance_modifier",
                    0,
                    false
            );
            if (crDamageChanceModifier.isSuccess()) {
                this.damageChanceModifier = crDamageChanceModifier.getInt();
            } else {
                this.damageChanceModifier = 0;
            }

            // --

            // Particle On Heal
            ConfigParser.ConfigReturn crParticleOnHeal = configParser.getStringValue(
                    "particle_on_heal",
                    "",
                    false
            );
            if (crParticleOnHeal.isSuccess()) {
                Particle particile = Particle.valueOf(crParticleOnHeal.getString());
                if (particile != null) {
                    this.particleOnHeal = particile;
                } else {
                    this.particleOnHeal = Particle.SPIT;
                }
            } else {
                this.particleOnHeal = Particle.SPIT;
            }

            // Sound On Heal
            ConfigParser.ConfigReturn crSoundOnHeal = configParser.getStringValue(
                    "sound_on_heal",
                    "",
                    false
            );
            if (crSoundOnHeal.isSuccess()) {
                Effect effect = Effect.valueOf(crSoundOnHeal.getString());
                if (effect != null) {
                    this.soundOnHeal = effect;
                } else {
                    this.soundOnHeal = Effect.EXTINGUISH;
                }
            } else {
                this.soundOnHeal = Effect.EXTINGUISH;
            }

            // Prevent Afflict Worlds
            ConfigParser.ConfigReturn crPreventAfflictWorlds = configParser.getStringList(
                    "prevent_afflict_worlds",
                    false
            );
            if (crPreventAfflictWorlds.isSuccess()) {
                this.preventAfflictWorlds = new ArrayList<>();
                if (crPreventAfflictWorlds.getStringList() != null) {
                    for (String preventAfflictWorldName : crPreventAfflictWorlds.getStringList()) {
                        this.preventAfflictWorlds.add(preventAfflictWorldName);
                    }
                }
            }

            // Prevent Symptom Worlds
            ConfigParser.ConfigReturn crPreventSymptomWorlds = configParser.getStringList(
                    "prevent_symptom_worlds",
                    false
            );
            if (crPreventSymptomWorlds.isSuccess()) {
                this.preventSymptomWorlds = new ArrayList<>();
                if (crPreventSymptomWorlds.getStringList() != null) {
                    for (String preventSymptomWorldName : crPreventSymptomWorlds.getStringList()) {
                        this.preventSymptomWorlds.add(preventSymptomWorldName);
                    }
                }
            }

            // Active
            boolean active = true;
            ConfigParser.ConfigReturn crActive = configParser.getBooleanValue(
                    "is_active",
                    true,
                    false
            );
            if (crActive.isSuccess()) {
                active = crActive.getBoolean();
            }

            if (active) {
                Epidemic.instance().data().addAvailableAilment(this);
            }

            // Block Breaks
            ConfigParser.ConfigReturn crBlocks = configParser.getConfigSection(
                    "blocks",
                    false
            );
            if (crBlocks.isSuccess()) {
                for (Object key : crBlocks.getConfigSection().getKeys(false).toArray()) {

                    Material material = ItemFunctions.materialFromName(key.toString());
                    if (material != null) {
                        if (material.isBlock()) {
                            BlockBreak blockBreak = new BlockBreak(material);
                            ConfigParser.ConfigReturn crBlockShowCloud = configParser.getBooleanValue(
                                    "blocks." + key.toString() + ".show_cloud",
                                    true,
                                    false
                            );
                            if (crBlockShowCloud.isSuccess()) {
                                blockBreak.showCloud(crBlockShowCloud.getBoolean());
                            }
                            if (blockBreak.showCloud()) {
                                ConfigParser.ConfigReturn crBlockCloudRed = configParser.getIntValue(
                                        "blocks." + key.toString() + ".cloud_red",
                                        0,
                                        false
                                );
                                ConfigParser.ConfigReturn crBlockCloudGreen = configParser.getIntValue(
                                        "blocks." + key.toString() + ".cloud_green",
                                        0,
                                        false
                                );
                                ConfigParser.ConfigReturn crBlockCloudBlue = configParser.getIntValue(
                                        "blocks." + key.toString() + ".cloud_blue",
                                        0,
                                        false
                                );
                                if (crBlockCloudRed.isSuccess() && crBlockCloudGreen.isSuccess() && crBlockCloudBlue.isSuccess()) {
                                    blockBreak.cloudRed(crBlockCloudRed.getInt());
                                    blockBreak.cloudGreen(crBlockCloudGreen.getInt());
                                    blockBreak.cloudBlue(crBlockCloudBlue.getInt());
                                }
                                ConfigParser.ConfigReturn crBlockCloudSize = configParser.getIntValue(
                                        "blocks." + key.toString() + ".cloud_size",
                                        3,
                                        false
                                );
                                if (crBlockCloudSize.isSuccess()) {
                                    blockBreak.cloudSize(crBlockCloudSize.getInt());
                                }
                                ConfigParser.ConfigReturn crBlockCloudCount = configParser.getIntValue(
                                        "blocks." + key.toString() + ".cloud_count",
                                        5,
                                        false
                                );
                                if (crBlockCloudCount.isSuccess()) {
                                    blockBreak.cloudSize(crBlockCloudCount.getInt());
                                }
                            }
                            ConfigParser.ConfigReturn crBlockChance = configParser.getIntValue(
                                    "blocks." + key.toString() + ".chance",
                                    0,
                                    false
                            );
                            if (crBlockChance.isSuccess()) {
                                blockBreak.chance(crBlockChance.getInt());
                            }
                            ConfigParser.ConfigReturn crBlocksBiome = configParser.getConfigSection(
                                    "blocks." + key.toString() + ".chance_biome_modifier",
                                    false
                            );
                            if (crBlocks.isSuccess()) {
                                for (Object biomeKey : crBlocks.getConfigSection().getKeys(false).toArray()) {
                                    Biome biome = WorldFunctions.biomeFromName(biomeKey.toString());
                                    if (biome != null) {
                                        ConfigParser.ConfigReturn crBlockBiomeChance = configParser.getIntValue(
                                                "blocks." + key.toString() + ".chance_biome_modifier." + biomeKey.toString() + ".chance",
                                                0,
                                                false
                                        );
                                        if (crBlockBiomeChance.isSuccess()) {
                                            blockBreak.addBiomeChanceModifier(biome.name(), crBlockBiomeChance.getInt());
                                        }
                                    }
                                }
                            }
                            this.blockBreaks.put(blockBreak.material(), blockBreak);

                            if (Epidemic.instance().data().blockAilments == null) {
                                Epidemic.instance().data().blockAilments = new HashMap<>();
                            }
                            if (Epidemic.instance().data().blockAilments.containsKey(blockBreak.material())) {
                                Logging.debug(
                                        "Ailment",
                                        "loadFromFile",
                                        "Adding ailment to existing block break for " + blockBreak.material().name()
                                );
                                Set<Ailment> ailments = Epidemic.instance().data().blockAilments.get(blockBreak.material());
                                ailments.add(this);
                                Epidemic.instance().data().blockAilments.put(blockBreak.material(), ailments);
                            } else {
                                Logging.debug(
                                        "Ailment",
                                        "loadFromFile",
                                        "Adding ailment to new block break for " + blockBreak.material().name()
                                );
                                Set<Ailment> ailments = new HashSet<>();
                                ailments.add(this);
                                Epidemic.instance().data().blockAilments.put(blockBreak.material(), ailments);
                            }
                        }
                    }



                }
            }




        } else {
            // error - file does not exist
        }
    }

    /**
     * Gets the infectiousChance for the ailment, adds in a contagiousChanceBoost
     * also checks the player location, gets the biome, and if there is a modifier, applies it
     * if byPlayer is true, decrements by players current immunity to this ailment, then generates
     * a number between 1-100000, if that number is less than the calculated number, returns true
     * which would indicate that the player should be afflicted.  The 100000 number can overridden
     * in the config with random_chance
     * @param player Player to check
     * @param byPlayer If a player who had the ailment was near the player
     * @return True if the player should be afflicted
     */
    public boolean checkInfectious(Player player, boolean byPlayer) {

        int chance = this.infectiousChance;
        if (byPlayer && this.infectious) {
            chance = chance + this.contagiousChanceBoost;
        }
        if (this.infectiousChanceBiomeModifier.containsKey(player.getLocation().getBlock().getBiome().name())) {
            chance = chance + this.infectiousChanceBiomeModifier.get(player.getLocation().getBlock().getBiome().name());
        }
        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance - immunity.getAmount();
        }
        IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
        HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
        IEquipment eGasMask = Epidemic.instance().data().equipment.get("gas_mask");
        GasMask gasMask = (GasMask) eGasMask;
        if (hazmatSuit != null) {
            if (hazmatSuit.equipped(player)) {
                chance = chance - hazmatSuit.protection();
            }
        }
        if (gasMask != null) {
            if (gasMask.equipped(player)) {
                chance = chance - gasMask.protection();
            }
        }
        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkInfectious()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkInfectious()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the player should be afflicted from an infected projectile
     * Chance of getting ailment should be max possible chance from config (100,000)
     * minus player immunity amount.  Player should be almost certain to get the ailment
     * if hit by an infected projectile.
     * Hazmat suits/gas masks do not come into play here as a projectile would pierce them
     * @param player Player to check
     * @return If true, player should be afflicted
     */
    public boolean checkProjectile(Player player) {

        int chance = Epidemic.instance().config.getRandomChance();

        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance - immunity.getAmount();
        }

        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkProjectile()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkProjectile()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the player should be afflicted from being splashed by an infected splash potion
     * Chance of getting ailment should be max possible chance from config (100,000)
     * minus player immunity amount, minus if the player has a Hazmat Suit on.  Unlike a projectile, if
     * the player has a Hazmat suit, a splash potion won't impact them.  A gas mask should should also
     * provide additional protection
     * @param player Player to check
     * @return If true, player should be afflicted
     */
    public boolean checkSplash(Player player) {
        int chance = Epidemic.instance().config.getRandomChance();

        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance - immunity.getAmount();
        }
        IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
        if (eHazmatSuit != null) {
            if (eHazmatSuit instanceof HazmatSuit) {
                HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
                if (hazmatSuit.equipped(player)) {
                    chance = chance - hazmatSuit.protection();
                }
            }
        }
        IEquipment eGasMask = Epidemic.instance().data().equipment.get("gas_mask");
        if (eGasMask != null) {
            if (eGasMask instanceof GasMask) {
                GasMask gasMask = (GasMask) eGasMask;
                if (gasMask.equipped(player)) {
                    chance = chance - gasMask.protection();
                }
            }
        }

        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkSplash()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkSplash()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    public boolean checkCommand(Player player, int chance) {

        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance - immunity.getAmount();
        }
        IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
        if (eHazmatSuit != null) {
            if (eHazmatSuit instanceof HazmatSuit) {
                HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
                if (hazmatSuit.equipped(player)) {
                    chance = chance - hazmatSuit.protection();
                }
            }
        }
        IEquipment eGasMask = Epidemic.instance().data().equipment.get("gas_mask");
        if (eGasMask != null) {
            if (eGasMask instanceof GasMask) {
                GasMask gasMask = (GasMask) eGasMask;
                if (gasMask.equipped(player)) {
                    chance = chance - gasMask.protection();
                }
            }
        }

        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkCommand()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkCommand()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    /**
     * Gets the nonInfectiousChance for the ailment, gets in the optional damage amount, multiplies that
     * by the damageChanceModifier and uses that adds that calculated number to the nonInfectiousChance.  It
     * then generates a number between 1-100000, if that number is less than the calculated number, returns
     * true which would indicate that the player should be afflicted. The 100000 number can overridden
     *      * in the config with random_chance
     * @param damage Optional damage amount to add
     * @return True if the player should be afflicted
     */
    public boolean checkNonInfectious(double damage) {
        int chance = this.nonInfectiousChance;
        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        if (this.damageChanceModifier > 0) {
            double dcmChance = chance + (damage * damageChanceModifier);
            chance = (int) dcmChance;
        }

        Logging.debug(
                "Ailment",
                "checkNonInfectious()",
                "Checking for " + this.displayName + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        return random < chance;
    }

    /**
     * Gets the deliberateTransmissionChance for the ailment, decrements by players current immunity to
     * this ailment, then generates a number between 1-<random chance>, if that number is less than the
     * calculated number, returns true which would indicate that the player should be afflicted.
     * @param player Player to check
     * @return True if the player should be afflicted
     */
    public boolean checkDeliberateTransmission(Player player) {
        int chance = this.deliberateTransmissionChance;
        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance - immunity.getAmount();
        }

        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkDeliberateTransmission()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkDeliberateTransmission()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    /**
     * Gets the infectiousChance for the ailment, adds in a contagiousChanceBoost
     * if byPlayer is true, decrements by players current immunity to this ailment, then generates
     * a number between 1-100000, if that number is less than the calculated number, returns true
     * which would indicate that the player should be afflicted.  The 100000 number can overridden
     * in the config with random_chance
     * @param player Player to check
     * @param block The block causing the check
     * @param blockBreak The ailment Block Break (chance, cloud color etc.)
     * @return True if the player should be afflicted
     */
    public boolean checkBlockInfection(Player player, Block block, BlockBreak blockBreak) {
        if (block == null) {
            return false;
        }
        if (blockBreak == null) {
            return false;
        }
        int chance = blockBreak.chance();
        if (blockBreak.getBiomeChanceModifier().containsKey(block.getBiome().name())) {
            chance = chance + blockBreak.getBiomeChanceModifier().get(block.getBiome().name());
        }
        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance + getInfectiousChanceBiomeModifier(player.getLocation().getBlock().getBiome().name());
            chance = chance - immunity.getAmount();
        }
        IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
        HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
        IEquipment eGasMask = Epidemic.instance().data().equipment.get("gas_mask");
        GasMask gasMask = (GasMask) eGasMask;
        if (hazmatSuit != null) {
            if (hazmatSuit.equipped(player)) {
                chance = chance - hazmatSuit.protection();
            }
        }
        if (gasMask != null) {
            if (gasMask.equipped(player)) {
                chance = chance - gasMask.protection();
            }
        }
        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkBlockInfection()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Block: " + block.getType().name() + "\n" +
                        "Block Location: " + WorldFunctions.simpleReadable(block.getLocation()) + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkBlockInfection()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    /**
     * Checks if the player should bypass symptoms or affliction based on game mode or permissions.
     * @param player Player to check
     * @return True if the player should bypass
     */
    public boolean shouldBypass(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return true;
        }
        if (Permission.inBypass(player)) {
            return true;
        }
        return false;
    }

    /**
     * Uses various checks to check if the player should be considered invincible for this ailment at this
     * time.  Checks include: Gamemode, bypass, permission, world, ailment, and grace period
     * @param player Player to check
     * @return True if the player is invincible
     */
    public boolean invincible(Player player) {
        return invincible(player, true);
    }

    /**
     * Uses various checks to check if the player should be considered invincible for this ailment at this
     * time.  Checks include: Gamemode, bypass, permission, world, ailment, and grace period
     * @param player Player to check
     * @param checkExisting If true, checks if the player already has the ailment and if they are in the grace period
     * @return True if the player is invincible
     */
    public boolean invincible(Player player, boolean checkExisting) {

        Epidemic plugin = Epidemic.instance();

        // Bypass check (Game mode + Bypass permission)
        if (shouldBypass(player)) {
            Logging.debug(
                    "Ailment",
                    "invincible()",
                    "Player in bypass mode or non-survival/adventure game mode"
            );
            return true;
        }

        // World check
        if (this.preventAfflictWorlds != null) {
            for (String prevent : this.preventAfflictWorlds) {
                if (prevent.equalsIgnoreCase(player.getLocation().getWorld().getName())) {
                    Logging.debug(
                            "Ailment",
                            "invincible()",
                            "Ailment is not available in the world the player is in"
                    );
                    return true;
                }
            }
        }

        if (checkExisting) {
            // Check if player already has ailment
            Set<Afflicted> afflictions = plugin.data().getPlayerAfflictionsByUUID(player.getUniqueId());
            if (afflictions != null) {
                for (Afflicted afflicted : afflictions) {
                    if (afflicted.getAilment().getInternalName().equalsIgnoreCase(this.internalName)) {
                        Logging.debug(
                                "Ailment",
                                "invincible()",
                                "Player already has this ailment"
                        );
                        return true;
                    }
                }
            }
            // Grace period check
            if (this.gracePeriod > 0) {
                Timestamp lastHealedTimestamp = plugin.data().getLastHealed(
                        player.getUniqueId(),
                        this.internalName
                );
                if (lastHealedTimestamp != null) {
                    if (TimeFunctions.addSeconds(
                            lastHealedTimestamp,
                            this.gracePeriod
                    ).after(TimeFunctions.now())) {
                        Logging.debug(
                                "Ailment",
                                "invincible()",
                                "Player is in the grace period for this ailment"
                        );
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Afflict the player with this ailment
     * @param player Player to afflict
     * @param sourcePlayer Optional player that caused the affliction
     */
    public void afflict(Player player, Player sourcePlayer) {
        AfflictionEvent afflictionEvent = new AfflictionEvent(
                player, this
        );
        Bukkit.getServer().getPluginManager().callEvent(afflictionEvent);
        if (!afflictionEvent.isCancelled()) {
            Afflicted affliction = new Afflicted(
                    player.getUniqueId(),
                    this,
                    Epidemic.instance().gameData().day().get(),
                    Epidemic.instance().gameData().day().getWorld().getTime(),
                    new Timestamp(System.currentTimeMillis())
            );
            Epidemic.instance().data().addPlayerAffliction(
                    player.getUniqueId(),
                    affliction
            );
            if (sourcePlayer != null) {
                HashMap<String, String> placeHolder = new HashMap<>();
                placeHolder.put("<%player%>", player.getDisplayName());
                SendMessage.Player(
                        "afflictplayer_infect_success",
                        "&2<%player%> has been afflicted",
                        sourcePlayer,
                        true,
                        placeHolder
                );
            }
            if (this.warnOnAfflicted) {
                SendMessage.Player("na", this.afflictedText, player, true, null);
            }
            if (this.timeToSymptoms < 1) {
                applySymptoms(player, affliction);
            }
        }
    }

    /**
     * Applies the symptoms for this ailment to the player, also triggers the secondary
     * ailment check.
     * @param player Player to apply symptoms to
     * @param affliction The affliction applied to the player
     */
    public void applySymptoms(Player player, Afflicted affliction) {

        if (shouldBypass(player)) {
            return;
        }

        Logging.debug(
                "Ailment",
                "applySymptoms",
                "Trying to apply symptoms now"
        );

        long afflictedSeconds = (System.currentTimeMillis() - affliction.getStartTimestamp().getTime()) / 1000;
        if (afflictedSeconds < this.timeToSymptoms) {
            Logging.debug(
                    "Ailment",
                    "applySymptoms",
                    "Not applying symptoms yet, time to symptoms: " + this.timeToSymptoms + " seconds. " +
                            "Player has been afflicted for " + afflictedSeconds + " seconds."
            );
            return;
        }

        if (!affliction.isSymptomsStarted()) {
            if (this.symptomText != null && !this.symptomText.isEmpty()) {
                SendMessage.Player("na", this.symptomText, player, true, null);
            }
            affliction.setSymptomsStarted(true);
            Epidemic.instance().data().savePlayer(player);
        }

        String playerWorldName = player.getLocation().getWorld().getName();

        Logging.debug(
                "Ailment",
                "applySymptoms",
                "Player world name: " + playerWorldName
        );

        if (affliction.getAilment().getPreventSymptomWorlds() != null) {
            for (String preventWorldName : affliction.getAilment().getPreventSymptomWorlds()) {
                if (preventWorldName.equalsIgnoreCase(playerWorldName)) {
                    Logging.debug(
                            "Ailment",
                            "applySymptoms",
                            "Not applying symptom for " + this.displayName +
                                    " symptoms are not active in " + playerWorldName
                    );
                    return;
                }
            }
        }

        Logging.debug(
                "Ailment",
                "applySymptoms",
                "Checking if player has symptom relief"
        );

        if (Epidemic.instance().data().getCurrentSymptomRelief(player.getUniqueId())) {
            Logging.debug(
                    "Ailment",
                    "applySymptoms",
                    "Not applying symptom for " + this.displayName +
                            " as the player has symptom relief"
            );
            return;
        }

        Logging.debug(
                "Ailment",
                "applySymptoms",
                "Trying to apply secondary ailment"
        );

        applySecondaryAilment(player, affliction);

        if (this.infectious) {
            if (this.displayContagious) {
                ParticleFunctions.contagious(player);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 1f);
            }
        } else {
            if (this.displayInjury) {
                ParticleFunctions.injury(player);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 1f);
            }
        }
        PotionEffects.applyPotionEffects(player, affliction);
        Hurt.damagePlayer(player, affliction);
        ParticleEffects.applyVomit(player, affliction);
        ParticleEffects.applyBleeding(player, affliction);
        ParticleEffects.applyBowel(player, affliction);
        ParticleEffects.applyUrinate(player, affliction);
        ParticleEffects.applySweat(player, affliction);
        Insomnia.applyInsomnia(player, affliction);
        Hallucination.applyHallucination(player, affliction);
        RemoveXP.applyRemoveXP(player, affliction);
        Food.applyRotFood(player, affliction);
        Rust.applyRust(player, affliction);
        SuddenDeath.applySuddenDeath(player, affliction);
    }

    /**
     * Applies the secondary ailment to the player, if enough time has passed
     * @param player Player to apply the secondary ailment to
     * @param affliction The affliction applied to the player
     */
    public void applySecondaryAilment(Player player, Afflicted affliction) {

        Logging.debug(
                "Ailment",
                "applySecondaryAilment",
                "Checking if the affliction time + secondary ailment time is in the future"
        );
        if (
            !TimeFunctions.inFuture(
                    TimeFunctions.addSeconds(
                        affliction.getStartTimestamp(), this.secondaryAilmentTime
                    )
                )
        ) {

            Logging.debug(
                    "Ailment",
                    "applySecondaryAilment",
                    "Time is not in the future, try and apply"
            );

            Logging.debug(
                    "Ailment",
                    "applySecondaryAilment",
                    "Secondary ailment is applied?: " + affliction.isSecondaryApplied()
            );

            Logging.debug(
                    "Ailment",
                    "applySecondaryAilment",
                    "Secondary ailment: " + this.secondaryAilment
            );

            if (!affliction.isSecondaryApplied()) {
                if (this.secondaryAilment != "") {
                    Ailment secondary = Epidemic.instance().data().getAvailableAilmentByInternalName(
                            this.secondaryAilment
                    );
                    if (secondary != null) {
                        Logging.debug(
                                "Ailment",
                                "applySecondaryAilment",
                                "Attempting to apply secondary ailment now"
                        );


                        secondary.afflict(player, null);
                        affliction.setSecondaryApplied(true);
                        Epidemic.instance().data().addPlayerAffliction(
                                player.getUniqueId(),
                                affliction
                        );
                    }
                }
            } else {
                Logging.debug(
                        "Ailment",
                        "applySecondaryAilment",
                        "Not applying secondary ailment as it was applied previously"
                );
            }
        } else {
            Logging.debug(
                    "Ailment",
                    "applySecondaryAilment",
                    "Time is in the future, don't apply yet"
            );
        }

    }

    /**
     * Cures the player of this ailment and removes the negative potion effects caused by it if required
     * @param player Player to cure
     * @param removeEffects If true, removes the potion effects
     */
    public void cure(Player player, boolean removeEffects, boolean showMessage) {
        Epidemic plugin = Epidemic.instance();

        if (plugin.data().getPlayerAfflictionsByUUID(player.getUniqueId()) != null) {
            for (Afflicted afflicted : plugin.data().getPlayerAfflictionsByUUID(player.getUniqueId())) {
                if (afflicted.getAilment().getInternalName().equalsIgnoreCase(this.internalName)) {
                    Logging.debug(
                            "Ailment",
                            "cure()",
                            "Removing ailment " +
                                    player.getDisplayName() + " - " +
                                    afflicted.getAilment().getDisplayName()
                    );
                    plugin.data().removePlayerAffliction(player.getUniqueId(), afflicted);
                    if (afflicted.getAilment().isInsomnia()) {
                        plugin.data().removeInsomniaPlayers(player.getUniqueId());
                    }
                    if (showMessage && afflicted.getAilment().getHealedText() != null) {
                        if (afflicted.getAilment().getHealedText().length() > 0) {
                            SendMessage.Player(
                                    "na",
                                    afflicted.getAilment().getHealedText(),
                                    player,
                                    true,
                                    null
                            );
                        }
                    }
                    if (removeEffects) {
                        if (this.ailmentEffects != null) {
                            for (PlayerPotionEffect effect : this.ailmentEffects) {
                                com.ibexmc.epidemic.remedy.player.PotionEffects.reducePotionEffect(
                                        player,
                                        effect
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the player in the hotspot should be afflicted
     * @param player Player
     * @param chance Hotspot chance
     * @return If true, afflict the player
     */
    public boolean checkHotspotAfflict(Player player, int chance) {

        Immunity immunity = Epidemic.instance().data().getPlayerImmunityByUUIDAndAilment(
                player.getUniqueId(),
                this.internalName
        );
        if (immunity != null) {
            chance = chance - immunity.getAmount();
        }

        int random = NumberFunctions.random(Epidemic.instance().config.getRandomChance());

        Logging.debug(
                "Ailment",
                "checkHotspotAfflict()",
                "Checking for " + this.displayName + "\n" +
                        "Player: " + player.getDisplayName() + "\n" +
                        "Random Max: " + Epidemic.instance().config.getRandomChance() + "\n" +
                        "Chance: " + chance + "\n" +
                        "Random Number: " + random
        );

        IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
        HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
        if (hazmatSuit != null) {
            if (hazmatSuit.equipped(player)) {
                Logging.debug(
                        "Ailment",
                        "checkHotspotAfflict()",
                        "Player is wearing a Hazmat suit - reducing chance by " + hazmatSuit.protection()
                );
                chance = chance - hazmatSuit.protection();
            }
        }
        IEquipment eGasMask = Epidemic.instance().data().equipment.get("gas_mask");
        GasMask gasMask = (GasMask) eGasMask;
        if (gasMask != null) {
            if (gasMask.equipped(player)) {
                Logging.debug(
                        "Ailment",
                        "checkHotspotAfflict()",
                        "Player is wearing a Gas mask - reducing chance by " + gasMask.protection()
                );
                chance = chance - gasMask.protection();
            }
        }

        if (random <= chance) {
            Logging.debug(
                    "Ailment",
                    "checkHotspotAfflict()",
                    "Random number is <= chance, apply affliction"
            );
            return true;
        }
        return false;
    }

    public class BlockBreak {
        private Material material = Material.AIR;
        private boolean showCloud = false;
        private int cloudRed = 0;
        private int cloudGreen = 0;
        private int cloudBlue = 0;
        private int cloudCount = 10;
        private int cloudSize = 1;
        private int chance = 0;
        private Map<String, Integer> biomeChanceModifier = new HashMap<>();

        public BlockBreak(Material material) {
            this.material = material;
        }
        public Material material() { return this.material; }
        public boolean showCloud() {
            return this.showCloud;
        }
        public int cloudRed() {
            return this.cloudRed;
        }
        public int cloudGreen() {
            return this.cloudGreen;
        }
        public int cloudBlue() {
            return this.cloudBlue;
        }
        public int cloudCount() {
            return this.cloudCount;
        }
        public int cloudSize() {
            return this.cloudSize;
        }
        public int chance() {
            return this.chance;
        }
        public Map<String, Integer> getBiomeChanceModifier() {
            return this.biomeChanceModifier;
        }
        public void showCloud(boolean showCloud) {
            this.showCloud = showCloud;
        }
        public void cloudRed(int cloudRed) {
            this.cloudRed = cloudRed;
        }
        public void cloudGreen(int cloudGreen) {
            this.cloudGreen = cloudGreen;
        }
        public void cloudBlue(int cloudBlue) {
            this.cloudBlue = cloudBlue;
        }
        public void cloudCount(int cloudCount) {
            this.cloudCount = cloudCount;
        }
        public void cloudSize(int cloudSize) {
            this.cloudSize = cloudSize;
        }
        public void chance(int chance) {
            this.chance = chance;
        }
        public void addBiomeChanceModifier(String biome, int chanceModifier) {
            this.biomeChanceModifier.put(biome, chanceModifier);
        }
    }
}
