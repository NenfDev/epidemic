package com.ibexmc.epidemic.ailments;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.symptoms.*;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;

import com.ibexmc.epidemic.util.functions.ParticleFunctions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.util.functions.TimeFunctions;

import java.sql.Timestamp;
import java.util.HashMap;

import java.util.Set;
import java.util.UUID;

public class AfflictPlayer {
    /**
     * Checks if the player can be afflicted with the ailment
     * @param uuid Player unique identifier
     * @param ailment Ailment
     * @return If true, player can be afflicted
     */
    public static boolean canAfflict(UUID uuid, Ailment ailment) {
        Epidemic plugin = Epidemic.instance();

        if (Epidemic.instance().data().playerIsInvincible(uuid)) {
            Logging.debug(
                    "AfflictPlayer",
                    "canAfflict",
                    "Player is marked as invincible"
            );
            return false;
        }

        // Null UUID check
        if (uuid == null) {
            Logging.debug(
                    "AfflictPlayer",
                    "canAfflict",
                    "Invalid UUID"
            );
            return false;
        }
        // Invalid player check
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            Logging.debug(
                    "AfflictPlayer",
                    "canAfflict",
                    "Invalid player"
            );
            return false;
        }
        // Null ailment check
        if (ailment == null) {
            Logging.debug(
                    "AfflictPlayer",
                    "canAfflict",
                    "Invalid ailment"
            );
            return false;
        }

        if (Epidemic.instance().dependencies().hasDomain()) {
            Object domainFlag = Epidemic.instance().dependencies().flagFromName("EPIDEMIC_PREVENT_AFFLICT");
            if (Epidemic.instance().dependencies().flagAtLocation(
                    domainFlag,
                    player.getLocation()
            )
            ) {
                Logging.debug(
                        "AfflictPlayer",
                        "canAfflict",
                        "Player is in a Domain field that prevents affliction"
                );
                return false;
            }
        }




        // Gamemode check
        if (player.getGameMode() != GameMode.SURVIVAL) {
            Logging.debug(
                    "AfflictPlayer",
                    "canAfflict",
                    "Player is not in survival mode"
            );
            return false;
        }
        // Permission check
        if (Permission.inBypass(player)) {
            Logging.debug(
                    "AfflictPlayer",
                    "canAfflict",
                    "Player is in bypass mode"
            );
            return false;
        }
        // World check
        if (ailment.getPreventAfflictWorlds() != null) {
            for (String prevent : ailment.getPreventAfflictWorlds()) {
                if (prevent.equalsIgnoreCase(player.getLocation().getWorld().getName())) {
                    Logging.debug(
                            "AfflictPlayer",
                            "canAfflict",
                            "Ailment is not available in the world the player is in"
                    );
                    return false;
                }
            }
        }
        // Check if player already has ailment
        Set<Afflicted> afflictions = plugin.data().getPlayerAfflictionsByUUID(uuid);
        if (afflictions != null) {
            for (Afflicted afflicted : afflictions) {
                if (afflicted.getAilment().getInternalName().equalsIgnoreCase(ailment.getInternalName())) {
                    Logging.debug(
                            "AfflictPlayer",
                            "canAfflict",
                            "Player already has this ailment"
                    );
                    return false;
                }
            }
        }
        // Grace period check
        if (ailment.getGracePeriod() > 0) {
            Timestamp lastHealedTimestamp = plugin.data().getLastHealed(uuid, ailment.getInternalName());
            if (lastHealedTimestamp != null) {
                if (TimeFunctions.addSeconds(
                        lastHealedTimestamp,
                        ailment.getGracePeriod()
                ).after(TimeFunctions.now())) {
                    Logging.debug(
                            "AfflictPlayer",
                            "canAfflict",
                            "Player is in the grace period for this ailment"
                    );
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Afflicts the player with the ailment
     * @param uuid Player unique identifier
     * @param ailment Ailment
     * @param causeUUID Player unique identifier for who caused the ailment
     * @param infectUUID Player unique identifier for who is being infected
     */
    public static void afflictPlayer(UUID uuid, Ailment ailment, UUID causeUUID, UUID infectUUID) {

        if (!canAfflict(uuid, ailment)) {
            Logging.debug(
                    "AfflictPlayer",
                    "afflictPlayer(UUID, Ailment)",
                    "Not applying ailment - player can not be afflicted"
            );
            return;
        }

        Player player = Bukkit.getPlayer(uuid);

        Player infectPlayer = null;
        if (infectUUID != null) {
            infectPlayer = Bukkit.getPlayer(infectUUID);
        }

        Logging.debug("AfflictPlayer", "afflictPlayer(UUID, Ailment)", "Applying Ailment: " + ailment.getDisplayName() + " to " + player.getDisplayName());
        if (player != null) {

                if (causeUUID != null) {
                    Player causePlayer = Bukkit.getPlayer(causeUUID);
                    if (causePlayer != null)  {
                        Logging.debug(
                                "AfflictPlayer",
                                "afflictPlayer(UUID, Ailment)",
                                "Applying affliction for " + ailment.getDisplayName() + " to " + player.getDisplayName() + " caused by " + causePlayer.getDisplayName()
                        );
                    } else {
                        Logging.debug(
                                "AfflictPlayer",
                                "afflictPlayer(UUID, Ailment)",
                                "Applying affliction for " + ailment.getDisplayName() + " to " + player.getDisplayName()
                        );
                    }
                } else {
                    Logging.debug(
                            "AfflictPlayer",
                            "afflictPlayer(UUID, Ailment)",
                            "Applying affliction for " + ailment.getDisplayName() + " to " + player.getDisplayName()
                    );
                }
                Afflicted newAfflicted = new Afflicted(uuid, ailment, Epidemic.instance().gameData().day().get(), Epidemic.instance().gameData().day().getWorld().getTime(), new Timestamp(System.currentTimeMillis()));
                Epidemic.instance().data().addPlayerAffliction(uuid, newAfflicted);
                if (infectPlayer != null) {
                    HashMap<String, String> pholder = new HashMap<>();
                    pholder.put("<%player%>", player.getDisplayName());
                    SendMessage.Player("afflictplayer_infect_success", "&2<%player%> has been afflicted", infectPlayer, true, pholder);
                }
                Logging.debug(
                        "AfflictPlayer",
                        "afflictPlayer(UUID, Ailment)",
                        "Applying ailment. Details: " +
                                "Valid: " + ailment.isValid() + " " +
                                "Warn On Afflicted: " + ailment.isWarnOnAfflicted() + " " +
                                "Afflicted Text: " + ailment.getAfflictedText() + " " +
                                "Time to symptoms: " + ailment.getTimeToSymptoms() + " " +
                                "AilmentEffects Count: " + ailment.getAilmentEffects().size()
                );

                if (ailment.isWarnOnAfflicted()) {
                    SendMessage.Player("na", ailment.getAfflictedText(), player, true, null);
                }
                if (ailment.getTimeToSymptoms() < 1) {
                    ailment.applySymptoms(player, newAfflicted);
                }
            } else {
        }
    }
}
