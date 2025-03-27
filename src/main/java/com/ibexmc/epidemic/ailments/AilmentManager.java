package com.ibexmc.epidemic.ailments;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.dependencies.WorldGuard;
import com.ibexmc.epidemic.equipment.EquipmentManager;
import com.ibexmc.epidemic.equipment.HazmatSuit;
import com.ibexmc.epidemic.equipment.IEquipment;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.MaterialGroups;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.ParticleFunctions;
import com.ibexmc.epidemic.util.functions.WorldFunctions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import com.ibexmc.epidemic.util.functions.PlayerFunctions;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.domain.Domain;


import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AilmentManager {

    /**
     * getInfectiousAilments loops through all available ailments and returns
     * a hashset of those that have an infectious chance > 0
     * @return Set<Ailment> Infectious ailments
     */
    private static Set<Ailment> getInfectiousAilments() {
        Set<Ailment> ailments = new HashSet<>();
        Set<Ailment> available = Epidemic.instance().data().getAvailableAilments();
        if (available != null) {
            for (Ailment ailment : available) {
                if (ailment.getInfectiousChance() > 0) {
                    ailments.add(ailment);
                }
            }
        }
        return ailments;
    }

    /**
     * getAilmentsByCause passes the hashset of AilmentCause entries to getAilmentsByCause
     * and sets the damager entity to null. Used for method overloading.
     * @param causes Set<AilmentCause> Ailment causes
     * @return Set<Ailment> Ailments that match the ailment causes
     */
    private static Set<Ailment> getAilmentsByCause(Set<AilmentCause> causes) {
        return getAilmentsByCause(causes, null);
    }

    /**
     * getAilmentsByCause takes a hashset of AilmentCause entries along with a
     * nullable damager entity and uses it to populate a list of ailments which
     * are caused by one of the AilmentCauses, and when the AilmentCause = ENTITY,
     * checks to make sure the entity type matches what the ailment allows.
     * @param causes Set<AilmentCause> Ailment causes
     * @param damager Entity that caused the player damage
     * @return Set<Ailment> Ailments that match the ailment causes
     */
    private static Set<Ailment> getAilmentsByCause(Set<AilmentCause> causes, Entity damager) {

        Logging.debug(
                "AilmentManager",
                "getAilmentsByCause",
                "Started with " + causes.size() + " causes"
        );
        for (AilmentCause ac : causes) {
            Logging.debug(
                    "AilmentManager",
                    "getAilmentsByCause",
                    "Cause: " + ac.name()
            );
        }

        Set<Ailment> ailments = new HashSet<>();
        Set<Ailment> available = Epidemic.instance().data().getAvailableAilments();

        Logging.debug(
                "AilmentManager",
                "getAilmentsByCause",
                "Available ailments: " + available.size()
        );

        if (available != null) {
            if (causes != null) {
                for (AilmentCause cause : causes) {

                    Logging.debug(
                            "AilmentManager",
                            "getAilmentsByCause",
                            "with cause: " + cause.name()
                    );

                    for (Ailment ailment : available) {

                        Logging.debug(
                                "AilmentManager",
                                "getAilmentsByCause",
                                "with available ailment: " + ailment.getInternalName()
                        );

                        // Consume
                        if (ailment.isCausedByConsume() && cause.equals(AilmentCause.CONSUME)) {
                            ailments.add(ailment);
                        }
                        // Dehydration
                        if (ailment.isCausedByDehydration() && cause.equals(AilmentCause.DEHYDRATION)) {
                            ailments.add(ailment);
                        }
                        // Dirty Water
                        if (ailment.isCausedByDirtyWater() && cause.equals(AilmentCause.DIRTY_WATER)) {
                            ailments.add(ailment);
                        }
                        // Entity
                        Logging.debug(
                                "AilmentManager",
                                "getAilmentsByCause",
                                "Entity Types causing this ailment: " + ailment.getCausedByEntityType().size()
                        );
                        if (ailment.getCausedByEntityType() != null) {
                            if (ailment.getCausedByEntityType().size() > 0) {
                                if (cause.equals(AilmentCause.ENTITY)) {
                                    for (EntityType entityType : ailment.getCausedByEntityType()) {
                                        Logging.debug(
                                                "AilmentManager",
                                                "getAilmentsByCause",
                                                "with entity type: " + entityType + " and damager type: " + damager.getType().name()
                                        );
                                        if (damager.getType().equals(entityType)) {
                                            Logging.debug(
                                                    "AilmentManager",
                                                    "getAilmentsByCause",
                                                    "Adding ailment"
                                            );
                                            ailments.add(ailment);
                                        }
                                    }
                                }
                            }
                        }
                        // Explosion
                        if (ailment.isCausedByExplosion() && cause.equals(AilmentCause.EXPLOSION)) {
                            ailments.add(ailment);
                        }
                        // Fall
                        if (ailment.isCausedByFall() && cause.equals(AilmentCause.FALL)) {
                            ailments.add(ailment);
                        }
                        // Fire
                        if (ailment.isCausedByFire() && cause.equals(AilmentCause.FIRE)) {
                            ailments.add(ailment);
                        }
                        // Injury
                        if (ailment.isCausedByInjury() && cause.equals(AilmentCause.INJURY)) {
                            ailments.add(ailment);
                        }
                        // Weapon Injury
                        if (ailment.isCausedByWeaponInjury() && cause.equals(AilmentCause.WEAPON)) {
                            ailments.add(ailment);
                        }
                        // Magic
                        if (ailment.isCausedByMagic() && cause.equals(AilmentCause.MAGIC)) {
                            ailments.add(ailment);
                        }
                        // Water
                        if (ailment.isCausedByWater() && cause.equals(AilmentCause.WATER)) {
                            ailments.add(ailment);
                        }
                    }
                }
            }
        }
        return ailments;
    }

    /**
     * Checks if the player can be afflicted at their current location
     * @param player Player to check
     * @return If true, player can be afflicted at their location
     */
    public static boolean afflictAtLocation(Player player) {

        if (Epidemic.instance().dependencies().hasWorldGuard()) {
            List<String> excludedRegions = Epidemic.instance().config.getPreventAfflictWorldGuardRegions();
            if (excludedRegions != null && excludedRegions.size() > 0) {
                if (WorldGuard.inRegion(player, excludedRegions)) {
                    Logging.debug(
                            "AilmentManager",
                            "afflictAtLocation",
                            "Player is in a WorldGuard region that does not allow new ailments"
                    );
                    return false;
                }
            }
            if (Epidemic.instance().config.isPreventAilmentWorldGuardPVPDeny()) {
                if (!WorldGuard.canReceiveDamage(player, player.getLocation(), true)) {
                    Logging.debug(
                            "AilmentManager",
                            "afflictAtLocation",
                            "Player is in a WorldGuard PVP protected area that prevents affliction"
                    );
                    return false;
                }
            }
        }

        if (Epidemic.instance().dependencies().hasDomain()) {
            try {
                com.ibexmc.domain.api.API domain = Epidemic.instance().dependencies().getDomain();
                com.ibexmc.domain.flag.Flag.Type flag = domain.flagFromName("EPIDEMIC_PREVENT_AFFLICT");
                if (domain.flagAtLocation(
                        flag,
                        player.getLocation()
                )
                ) {
                    Logging.debug(
                            "AilmentManager",
                            "afflictAtLocation",
                            "Player is in a Domain field that prevents affliction"
                    );
                    return false;
                }
            }
            catch (Exception ex)
            {
                Logging.debug(
                        "AilmentManager",
                        "afflictAtLocation",
                        "Unexpected error checking Domain field - " + ex.getMessage()
                );
                return false;
            }
        }

        return true;
    }

    /**
     * afflicCheck loops through a list of ailments provided to it, checks to see if
     * ailment infecitous/non-infectious checks state that the player should be be
     * afflicted with that ailment, checks to see if the player is currently invincible
     * for that ailment type (due to gamemode, world, permissions, grace period  etc.)
     * and if not, then calls the ailment.afflict() code
     * @param player Player to check
     * @param ailmentCauseType AilmentCauseType used to decide if infectious/non-infectious check should take place
     */
    public static void afflictCheck(Player player, AilmentCauseType ailmentCauseType, Ailment ailment) {
        Set<Ailment> ailments = new HashSet<>();
        ailments.add(ailment);
        afflictCheck(player, ailmentCauseType, ailments, null, 0);
    }

    /**
     * afflicCheck loops through a list of ailments provided to it, checks to see if
     * ailment infecitous/non-infectious checks state that the player should be be
     * afflicted with that ailment, checks to see if the player is currently invincible
     * for that ailment type (due to gamemode, world, permissions, grace period  etc.)
     * and if not, then calls the ailment.afflict() code
     * @param player Player to check
     * @param ailmentCauseType AilmentCauseType used to decide if infectious/non-infectious check should take place
     * @param ailments Set<Ailment> Ailments that the player could get
     * @param sourcePlayer Player that caused the infection (nullable)
     */
    public static void afflictCheck(Player player, AilmentCauseType ailmentCauseType, Set<Ailment> ailments, Player sourcePlayer, double damage) {
        afflictCheck(player, ailmentCauseType, ailments, sourcePlayer, damage, null, -1);
    }

    /**
     * afflicCheck loops through a list of ailments provided to it, checks to see if
     * ailment infecitous/non-infectious checks state that the player should be be
     * afflicted with that ailment, checks to see if the player is currently invincible
     * for that ailment type (due to gamemode, world, permissions, grace period  etc.)
     * and if not, then calls the ailment.afflict() code
     * @param player Player to check
     * @param ailmentCauseType AilmentCauseType used to decide if infectious/non-infectious check should take place
     * @param ailments Set<Ailment> Ailments that the player could get
     * @param sourcePlayer Player that caused the infection (nullable)
     * @param block Block causing the affliction (nullable)
     */
    public static void afflictCheck(Player player, AilmentCauseType ailmentCauseType, Set<Ailment> ailments, Player sourcePlayer, double damage, Block block, int chance) {
        if (player == null) {
            Logging.debug(
                    "AilmentManager",
                    "afflictCheck",
                    "Player is null"
            );
            return;
        }
        Logging.debug(
                "AilmentManager",
                "afflictCheck",
                "Player: " + player.getDisplayName()
        );

        if (!afflictAtLocation(player)) {
            return;
        }

        boolean byPlayer = false;
        if (sourcePlayer != null) {
            byPlayer = true;
        }

        if (ailments != null) {
            for (Ailment ailment : ailments) {
                if (!ailment.invincible(player)) {
                    Logging.debug(
                            "AilmentManager",
                            "afflictCheck",
                            "Ailment: " + ailment.getDisplayName()
                    );
                    switch (ailmentCauseType) {
                        case INFECTIOUS:
                            if (ailment.checkInfectious(player, byPlayer)) {
                                ailment.afflict(player, sourcePlayer);
                            }
                            break;
                        case NONINFECTIOUS:
                            if (ailment.checkNonInfectious(damage)) {
                                ailment.afflict(player, null);
                            }
                            break;
                        case BLOCK:
                            if (block != null) {
                                if (ailment.getBlockBreaks().containsKey(block.getType())) {
                                    Ailment.BlockBreak blockBreak = ailment.getBlockBreaks().get(block.getType());
                                    if (blockBreak != null) {
                                        if (ailment.checkBlockInfection(player, block, blockBreak)) {
                                            ailment.afflict(player, null);
                                            if (blockBreak.showCloud()) {
                                                Logging.debug("AilmentManager", "afflictCheck", "Showing Block Particles");
                                                ParticleFunctions.displayParticleDust(
                                                        block.getLocation().clone().add(0.5,0.5,0.5),
                                                        blockBreak.cloudCount(),
                                                        blockBreak.cloudRed(),
                                                        blockBreak.cloudGreen(),
                                                        blockBreak.cloudBlue(),
                                                        blockBreak.cloudSize()
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case PROJECTILE:
                            if (ailment.checkProjectile(player)) {
                                ailment.afflict(player, sourcePlayer);
                            }
                            break;
                        case SPLASH_POTION:
                            if (ailment.checkSplash(player)) {
                                ailment.afflict(player, sourcePlayer);
                            }
                            break;
                        case COMMAND:
                            if (ailment.checkCommand(player, chance)) {
                                ailment.afflict(player, null);
                            }
                            break;
                        default:
                            Logging.debug(
                                    "AilmentManager",
                                    "afflictCheck",
                                    "Not applying ailments, player is invincible to this ailment (" +
                                            ailment.getDisplayName() + ")"
                            );
                            break;
                    }
                } else {
                    Logging.debug(
                            "AilmentManager",
                            "afflictCheck",
                            "Not applying ailments, player is invincible to this ailment (" +
                                    ailment.getDisplayName() + ")"
                    );
                }
            }
        } else {
            Logging.debug(
                    "AilmentManager",
                    "afflictCheck",
                    "Ailments was null"
            );
        }
    }

    /**
     * onEntityDamage is run when the EntityDamageEvent event is called.  It
     * checks if the entity being damaged is a player, if so, gets the cause
     * of the damage, maps it to an AilmentCause and uses that to get a list
     * of all the ailments caused by that AilmentCause.  It then passes the
     * player and ailments to afflictCheck()
     * @param event EntityDamageEvent event to get event details from
     */
    public static void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // If the player is wearing a hazmat suit, check if we should destroy it
            HazmatSuit hazmatSuit = (HazmatSuit) Epidemic.instance().data().equipment.get("hazmat_suit");
            if (hazmatSuit != null) {
                if (hazmatSuit.equipped(player)) {
                    if (EquipmentManager.checkDestroyHazmatSuit()) {
                        SendMessage.Player(
                                "hazmat_destroyed",
                                "&4Your Hazmat suit is damaged beyond repair",
                                player,
                                true,
                                null
                        );
                        hazmatSuit.unapply(player, false);
                    }
                }
            }

            Set<AilmentCause> causes = new HashSet<>();

            switch (event.getCause()) {
                case ENTITY_EXPLOSION:
                case BLOCK_EXPLOSION:
                    causes.add(AilmentCause.EXPLOSION);
                    causes.add(AilmentCause.INJURY);
                    break;
                case FALL:
                    causes.add(AilmentCause.FALL);
                    break;
                case FIRE:
                case FIRE_TICK:
                case HOT_FLOOR:
                case LAVA:
                case LIGHTNING:
                    causes.add(AilmentCause.FIRE);
                    break;
                case CONTACT:
                case ENTITY_ATTACK:
                case ENTITY_SWEEP_ATTACK:
                case FALLING_BLOCK:
                case THORNS:
                    causes.add(AilmentCause.INJURY);
                    break;
                case PROJECTILE:
                    causes.add(AilmentCause.INJURY);
                    causes.add(AilmentCause.WEAPON);
                    break;
                case MAGIC:
                    causes.add(AilmentCause.MAGIC);
                    break;
                case DROWNING:
                    causes.add(AilmentCause.WATER);
                    break;
                default:
                    break;
            }

            Set<Ailment> ailments = getAilmentsByCause(causes);
            if (ailments != null) {
                afflictCheck(
                        player,
                        AilmentCauseType.NONINFECTIOUS,
                        ailments,
                        null,
                        event.getDamage()
                );
            }
        }
    }

    /**
     * onEntityDamageByEntity is run when the EntityDamageByEntityEvent event is
     * called.  The event is passed to this function which gets a list of all
     * ailments caused by an entity damaging the player, if the entity damaging
     * matches an entity listed by the ailment.  It then passes the player and entity
     * to afflictCheck()
     * @param event EntityDamageByEntityEvent to get the event details from
     */
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {

            Logging.debug(
                    "AilmentManager",
                    "onEntityDamageByEntity",
                    "Damage cause: " + event.getCause().name()
            );

            Player victim = (Player) event.getEntity();
            Set<AilmentCause> causes = new HashSet<>();
            if (event.getDamager() instanceof Player) {
                Player damageDealer = (Player) event.getDamager();
                ItemStack damageItem = damageDealer.getInventory().getItemInMainHand();
                switch (event.getCause()) {
                    case ENTITY_ATTACK:
                    case ENTITY_SWEEP_ATTACK:
                    case PROJECTILE:
                        if (MaterialGroups.isWeapon(damageItem.getType().name())) {
                            causes.add(AilmentCause.WEAPON);
                        }
                        break;
                    default:
                        break;
                }
            }

            causes.add(AilmentCause.ENTITY);

            Logging.debug(
                    "AilmentManager",
                    "onEntityDamageByEntity",
                    "Getting ailments by cause"
            );
            Set<Ailment> ailments = getAilmentsByCause(causes, event.getDamager());
            for (Ailment a : ailments) {
                Logging.debug(
                        "AilmentManager",
                        "onEntityDamageByEntity",
                        "Ailments to check: " + a.getInternalName()
                );
            }
            if (ailments != null) {
                Logging.debug(
                        "AilmentManager",
                        "onEntityDamageByEntity",
                        "Calling afflict check"
                );
                afflictCheck(
                        victim,
                        AilmentCauseType.NONINFECTIOUS,
                        ailments,
                        null,
                        event.getDamage()
                );
            } else {
                Logging.debug(
                        "AilmentManager",
                        "onEntityDamageByEntity",
                        "Ailments list is null"
                );
            }
        }
    }

    /**
     * onItemConsume is run when the PlayerItemConsume event is called, the event
     * is passed to this function, gets a list of all ailments caused by consuming
     * and if the consumed item is not a potion, then sends the player and ailments
     * to afflictCheck()
     * @param event PlayerItemConsumeEvent to get the event details from
     */
    public static void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            Set<AilmentCause> causes = new HashSet<>();
            causes.add(AilmentCause.CONSUME);
            Set<Ailment> ailments = getAilmentsByCause(causes);
            if (ailments != null) {
                afflictCheck(
                        event.getPlayer(),
                        AilmentCauseType.NONINFECTIOUS,
                        ailments,
                        null,
                        0
                );
            }
        }
    }

    /**
     * Takes in a player, then gets all the available infectious ailments and a list of nearby players
     * based on the players location.  It then loops through all the ailments, it then checks each nearby
     * player, if that player has the same ailment that we're checking, it adds the ailment to a new hashset
     * of ailments and adds the ailment to it, then sends that single ailment in the set to afflictPlayer()
     * If none of the nearby players have the ailment, or if there are no nearby players, then the ailment
     * is added a hashset of ailments which is sent to afflictPlayer() after the ailment loop has been completed.
     * So we should send ailments individually if there is a nearby player, but in a set if not.
     * @param player The player to check
     */
    public static void timed(Player player) {
        Logging.debug(
                "AilmentManager",
                "timed()",
                "Started for " + player.getDisplayName()
        );

        Set<Afflicted> playerAfflictions = Epidemic.instance().data().getPlayerAfflictionsByUUID(
                player.getUniqueId()
        );
        if (playerAfflictions != null) {
            for (Afflicted afflicted : playerAfflictions) {
                if (afflicted.getAilment().getNaturalCureDays() > 0) {
                    if (Epidemic.instance().gameData().day().get() - afflicted.getAfflictDay() >= afflicted.getAilment().getNaturalCureDays()) {
                        if (Epidemic.instance().gameData().day().getWorld().getTime() > afflicted.getAfflictTime()) {
                            // Player has been afflicted long enough
                            if (afflicted.getAilment().getNaturalCureText() != "") {
                                SendMessage.Player(
                                        "na",
                                        afflicted.getAilment().getNaturalCureText(),
                                        player,
                                        true,
                                        null
                                );
                            }
                            afflicted.getAilment().cure(player, true, false);
                        }
                    }
                }
            }
        }


        Set<Ailment> infectiousAilments = getInfectiousAilments();
        Set<Ailment> nonNearbyAilments = new HashSet<>();
        Player sourcePlayer = null;
        IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
        HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
        if (infectiousAilments != null) {
            for (Ailment ailment : infectiousAilments) {
                Set<Player> nearbyPlayers = PlayerFunctions.nearbyPlayers(player.getLocation(), 20, 10, 20);
                if (nearbyPlayers != null) {
                    if (nearbyPlayers.size() > 0) {
                        for (Player nearbyPlayer : nearbyPlayers) {
                            if (hazmatSuit != null) {
                                if (hazmatSuit.equipped(nearbyPlayer)) {
                                    // If the nearby player is in a Hazmat suit, they can't pass anything on
                                    break;
                                }
                            }
                            Set<Afflicted> afflictions = Epidemic.instance().data().getPlayerAfflictionsByUUID(
                                    nearbyPlayer.getUniqueId()
                            );
                            if (afflictions != null) {
                                for (Afflicted affliction : afflictions) {
                                    if (ailment.getInternalName().equalsIgnoreCase(affliction.getAilment().getInternalName())) {
                                        sourcePlayer = nearbyPlayer;
                                        break;
                                    }
                                }
                            }
                        }
                        if (sourcePlayer != null) {
                            Set<Ailment> nearbyAilment = new HashSet<>();
                            nearbyAilment.add(ailment);
                            afflictCheck(
                                    player,
                                    AilmentCauseType.INFECTIOUS,
                                    nearbyAilment,
                                    sourcePlayer,
                                    0
                            );
                        } else {
                            nonNearbyAilments.add(ailment);
                        }
                        sourcePlayer = null;
                    } else {
                        nonNearbyAilments.add(ailment);
                    }
                } else {
                    nonNearbyAilments.add(ailment);
                }
            }
            afflictCheck(
                    player,
                    AilmentCauseType.INFECTIOUS,
                    nonNearbyAilments,
                    null,
                    0
            );
        }

    }

    /**
     * Attempts to afflict a player with an ailment via touch
     * @param player Player being touched
     * @param source Player touching
     */
    public static void touch(Player player, Player source) {
        Logging.debug(
                "AilmentManager",
                "touch()",
                "Started for " + player.getDisplayName() + " touched by " + source.getDisplayName()
        );
        Set<Ailment> sourceAilments = new HashSet<>();
        for (Ailment ailment : getInfectiousAilments()) {
            if (hasAilment(source, ailment)) {
                sourceAilments.add(ailment);
            }
        }
        if (sourceAilments.size() > 0) {
            afflictCheck(
                    player,
                    AilmentCauseType.INFECTIOUS,
                    sourceAilments,
                    source,
                    0
            );
        }
    }

    /**
     * Checks if the player has the ailment specified
     * @param player Player to check
     * @param ailment Ailment
     * @return If true, player has the ailment
     */
    public static boolean hasAilment(Player player, Ailment ailment) {
        for (Afflicted afflicted : Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId())) {
            if (afflicted.getAilment().getInternalName().equalsIgnoreCase(ailment.getInternalName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets an ailment by the internal name
     * @param internalName Internal Name
     * @return Ailment, if not found, returns null
     */
    public static Ailment getAilmentByInternalName(String internalName) {
        for (Ailment ailment : Epidemic.instance().data().getAvailableAilments()) {
            if (ailment.getInternalName().equalsIgnoreCase(internalName)) {
                return ailment;
            }
        }
        return null;
    }

    public static void loadHotspots() {
        File hotspotFile = new File(
                Epidemic.instance().getDataFolder() +
                        File.separator +
                        "config" + File.separator +
                        "hotspots.yml"
        );
        if (hotspotFile.exists()) {
            ConfigParser configParser = new ConfigParser(Epidemic.instance(), hotspotFile);

            ConfigParser.ConfigReturn crHotspots = configParser.getConfigSection(
                    "hotspots",
                    false
            );
            if (crHotspots.isSuccess()) {
                for (Object hotspotLocation : crHotspots.getConfigSection().getKeys(false).toArray()) {
                    Location location = WorldFunctions.deserialize((String) hotspotLocation);
                    if (location != null) {
                        Epidemic.instance().gameData().hotspots().put(location, new Hotspot(Epidemic.instance(), hotspotFile, location, (String) hotspotLocation));
                    }
                }
            }
        }
    }

    public static void hotspotAfflictCheck(Player player, Ailment ailment, Hotspot hotspot) {
        if (!afflictAtLocation(player)) {
            return;
        }
        if (!ailment.invincible(player)) {
            if (ailment.checkHotspotAfflict(player, hotspot.getChance())) {
                ailment.afflict(player, null);
            }
        }
    }
}
