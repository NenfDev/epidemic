package com.ibexmc.epidemic.api;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.temperature.Temperature;
import com.ibexmc.epidemic.remedy.Remedy;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EpidemicAPI {
    Epidemic plugin;

    public EpidemicAPI(Epidemic plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets a set of all ailments available
     * @return Set of ailments
     */
    public Set<Ailment> getAilments() {
        Set<Ailment> ailments = plugin.data().getAvailableAilments();
        if (ailments == null) {
            return new HashSet<>();
        }
        return ailments;
    }

    /**
     * Gets a set of all afflictions the player has
     * @param player Player to check
     * @return Set of afflictions
     */
    public Set<Afflicted> getAfflictions(Player player) {
        Set<Afflicted> afflictions = plugin.data().getPlayerAfflictionsByUUID(player.getUniqueId());
        if (afflictions == null) {
            return new HashSet<>();
        }
        return afflictions;
    }

    /**
     * Gets a set of all remedies available in-game
     * @return Set of remedies
     */
    public Set<Remedy> getRemedies() {
        Set<Remedy> remedies = new HashSet<>();
        Map<String, Remedy> remedyMap = plugin.data().getRemedies();
        if (remedyMap != null) {
            for (Map.Entry<String, Remedy> remedyEntry : remedyMap.entrySet()) {
                remedies.add(remedyEntry.getValue());
            }
        }
        return remedies;
    }

    /**
     * Gets the players current temperature
     * @param player The player to check
     * @return PlayerTemperature, which includes Level, temp and cycles
     */
    public Temperature.PlayerTemperature getTemperature(Player player) {
        Temperature.PlayerTemperature playerTemperature = plugin.data().getPlayerTemp(player.getUniqueId());
        if (playerTemperature != null) {
            return playerTemperature;
        } else {
            return new Temperature.PlayerTemperature(Temperature.Level.NORMAL, 0);
        }
    }

    /**
     * Cures the player of a particular ailment.
     * If the ailment is null, all current ailments will be cured
     * @param player Player to cure
     * @param ailment Ailment to cure (if null, cures all ailments)
     * @return True if cured, false if not (player did not have ailment etc.)
     */
    public boolean cure(Player player, Ailment ailment) {
        Set<Afflicted> afflictions = plugin.data().getPlayerAfflictionsByUUID(player.getUniqueId());
        if (afflictions != null) {
            if (afflictions.size() > 0) {
                for (Afflicted affliction : afflictions) {
                    if (ailment != null) {
                        if (affliction.getAilment().getInternalName().equalsIgnoreCase(
                                ailment.getInternalName())
                        ) {
                            affliction.getAilment().cure(player, true, true);
                            return true;
                        }
                    } else {
                        affliction.getAilment().cure(player, true, true);
                    }
                }
                return ailment == null;
            }
        }
        return false;
    }
}
