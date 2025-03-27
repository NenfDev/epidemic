package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.dependencies.WorldGuard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.domain.Domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ApplySymptom {
    /**
     * Called by the symptom scheduler.  Applies symptoms to players.
     */
    public static void Run() {

        Epidemic plugin = Epidemic.instance();

        if (plugin.data().getPlayerAfflictions() != null) {
            for (Map.Entry<UUID, Set<Afflicted>> afflictions : plugin.data().getPlayerAfflictions().entrySet()) {
                Player player = Bukkit.getPlayer(afflictions.getKey());
                if (player != null) {

                    if (plugin.dependencies().hasWorldGuard()) {
                        if (plugin.config.isPreventAilmentWorldGuardPVPDeny()) {
                            if (!WorldGuard.canReceiveDamage(player, player.getLocation(), true)) {
                                Logging.debug(
                                        "ApplySymptom",
                                        "Run",
                                        "Player is in a WorldGuard protected area"
                                );
                                break;
                            }
                        }
                        List<String> excludedRegions = plugin.config.getPreventSymptomWorldGuardRegions();
                        if (excludedRegions != null && excludedRegions.size() > 0) {
                            if (WorldGuard.inRegion(player, excludedRegions)) {
                                Logging.debug(
                                        "ApplySymptom",
                                        "Run",
                                        "Player is in a WorldGuard region where symptoms cannot be applied"
                                );
                                break;
                            }
                        }
                    }

                    if (plugin.dependencies().hasDomain()) {
                        try {
                            com.ibexmc.domain.api.API domain = plugin.dependencies().getDomain();
                            com.ibexmc.domain.flag.Flag.Type flag = domain.flagFromName("EPIDEMIC_PREVENT_SYMPTOMS");
                            if (domain.flagAtLocation(
                                    flag,
                                    player.getLocation()
                            )
                            ) {
                                Logging.debug(
                                        "ApplySymptom",
                                        "Run",
                                        "Player is in a Domain field that prevents symptoms"
                                );
                                break;
                            }
                        } catch (Exception ex) {
                            Logging.debug(
                            "ApplySymptom",
                                    "Run",
                                    "Unexpected error applying symptoms - " + ex.getMessage()
                                    );
                        }
                    }

                    if (afflictions.getValue() != null) {
                        for (Afflicted affliction : afflictions.getValue()) {
                            affliction.getAilment().applySymptoms(player, affliction);
                        }
                    }
                }
            }
        }
    }
}
