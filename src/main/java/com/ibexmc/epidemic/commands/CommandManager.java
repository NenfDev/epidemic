package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentCauseType;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.equipment.IEquipment;
import com.ibexmc.epidemic.players.InvinciblePlayers;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.remedy.RemedyManager;
import com.ibexmc.epidemic.temperature.Temperature;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.*;

public class CommandManager {

    /**
     * Attempts to give a player an item
     * @param sender Command Sender
     * @param arguments Arguments
     * @param label Label
     */
    public static void give(CommandSender sender, Map<Integer, String> arguments, String label) {
        Epidemic plugin = Epidemic.instance();
        Player player;
        boolean giveItem = false;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (Permission.hasPermission(player, Permission.PermissionType.GIVE)) {
                giveItem = true;
            }
        } else {
            giveItem = true;
        }
        if (giveItem) {
            if (arguments.containsKey(0) && arguments.containsKey(1)) {
                String playerName = arguments.get(0);
                String itemName = arguments.get(1);
                Logging.debug(
                        "EpidemicCommand",
                        "onCommand.cure",
                        "Player Name: " + playerName + " Item Name: " + itemName
                );

                Player recipient = null;
                for (Player recipePlayer : Bukkit.getOnlinePlayers()) {
                    if (recipePlayer.getName().equalsIgnoreCase(ChatColor.stripColor(playerName))) {
                        recipient = recipePlayer;
                    }
                }
                if (recipient != null) {

                    ItemStack itemStack = new ItemStack(Material.AIR, 1);
                    Remedy remedy = plugin.data().getRemedy(itemName);
                    IEquipment equipment = plugin.data().equipment.get(itemName);
                    if (remedy != null) {
                        itemStack = remedy.getItemStack(1);
                    } else if (equipment != null) {
                        itemStack = equipment.get();
                    }
                    if (itemStack != null) {
                        if (itemStack.getType() != Material.AIR) {
                            InventoryFunctions.addItemsToPlayerInventory(recipient, itemStack, true);
                            SendMessage.Sender(
                                    "command_epigive_success",
                                    "&2Item has been given to the player",
                                    sender,
                                    true,
                                    null
                            );
                        } else {
                            SendMessage.Sender(
                                    "command_epigive_invaliditem",
                                    "&cERROR &r- Invalid Item",
                                    sender,
                                    true,
                                    null
                            );
                        }
                    } else {
                        SendMessage.Sender(
                                "command_epigive_invaliditem",
                                "&cERROR &r- Invalid Item",
                                sender,
                                true,
                                null
                        );
                    }

                } else {
                    // invalid player
                    SendMessage.Sender(
                            "command_epigive_invalidplayer",
                            "&cERROR &r- Invalid Player or player is offline",
                            sender,
                            true,
                            null
                    );

                }

            } else {
                SendMessage.Sender(
                        "command_epigive_usage",
                        "&lUsage: &r/epigive &b<player name> <item key>",
                        sender,
                        true,
                        null
                );
            }
        } else {
            SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
        }
    }

    public static void getInfo(CommandSender sender, Map<Integer, String> arguments) {
        Epidemic plugin = Epidemic.instance();
        Player player;
        boolean canGet = false;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (Permission.hasPermission(player, Permission.PermissionType.ADMIN)) {
                canGet = true;
            }
        } else {
            canGet = true;
        }
        if (canGet) {
            String configs = "Config\n";
            String worlds = "";
            for (World world : Bukkit.getWorlds()) {
                if (world != null) {
                    worlds = worlds + world.getName() + ",";
                }
            }
            if (!worlds.isEmpty()) {
                worlds = worlds.substring(0, worlds.length() - 1);
            }
            configs = configs + " - available_worlds: " + "[" + worlds + "]" + "\n";
            configs = configs + " - afflicted_time: " + plugin.config.getAfflictedTime() + "\n";
            configs = configs + " - symptom_time: " + plugin.config.getSymptomTime() + "\n";
            configs = configs + " - papi_update_time: " + plugin.config.getPAPIUpdateTime() + "\n";
            configs = configs + " - new_player_invincible_days: " + plugin.config.getNewPlayerInvincibleDays() + "\n";
            configs = configs + " - clear_ailment_on_death: " + plugin.config.isClearAilmentOnDeath() + "\n";
            configs = configs + " - contagious_radius: " + plugin.config.getContagiousRadius() + "\n";

            String preventAilmentWorlds = "";
            for (String ailmentWorld : plugin.config.getPreventAfflictWorlds()) {
                preventAilmentWorlds = preventAilmentWorlds + ailmentWorld + ",";
            }
            if (!preventAilmentWorlds.isEmpty()) {
                preventAilmentWorlds = preventAilmentWorlds.substring(0, preventAilmentWorlds.length() - 1);
            }
            configs = configs + " - prevent_afflict_worlds: " + "[" + preventAilmentWorlds + "]" + "\n";

            String preventSymptomWorlds = "";
            for (String symptomWorld : plugin.config.getPreventSymptomWorlds()) {
                preventSymptomWorlds = preventSymptomWorlds + symptomWorld + ",";
            }
            if (!preventSymptomWorlds.isEmpty()) {
                preventSymptomWorlds = preventSymptomWorlds.substring(0, preventSymptomWorlds.length() - 1);
            }
            configs = configs + " - prevent_symptom_worlds: " + "[" + preventSymptomWorlds + "]" + "\n";

            String preventAilmentWorldguard = "";
            for (String wgAilmentWorld : plugin.config.getPreventAfflictWorldGuardRegions()) {
                preventAilmentWorldguard = preventAilmentWorldguard + wgAilmentWorld + ",";
            }
            if (!preventAilmentWorldguard.isEmpty()) {
                preventAilmentWorldguard = preventAilmentWorldguard.substring(0, preventAilmentWorldguard.length() - 1);
            }
            configs = configs + " - prevent_ailment_worldguard_regions: " + "[" + preventAilmentWorldguard + "]" + "\n";

            String preventSymptomWorldguard = "";
            for (String wgSymptomWorld : plugin.config.getPreventSymptomWorlds()) {
                preventSymptomWorldguard = preventSymptomWorldguard + wgSymptomWorld + ",";
            }
            if (!preventSymptomWorldguard.isEmpty()) {
                preventSymptomWorldguard = preventSymptomWorldguard.substring(0, preventSymptomWorldguard.length() - 1);
            }
            configs = configs + " - prevent_symptom_worldguard_regions: " + "[" + preventSymptomWorldguard + "]" + "\n";


            configs = configs + " - random_chance: " + plugin.config.getRandomChance() + "\n";
            configs = configs + " - prevent_ailment_worldguard_pvp_deny: " + plugin.config.isPreventAilmentWorldGuardPVPDeny() + "\n";
            configs = configs + " - disable_epidemic_equipment: " + plugin.config.isDisableEpidemicEquipment() + "\n";
            configs = configs + " - auto_discover_recipes: " + plugin.config.isAutoDiscoverRecipes() + "\n";
            configs = configs + " - enable_thirst: " + plugin.config.isEnableThirst() + "\n";

            String preventThirstWorld = "";
            for (String thirstWorld : plugin.config.getPreventThirstWorlds()) {
                preventThirstWorld = preventThirstWorld + thirstWorld + ",";
            }
            if (!preventThirstWorld.isEmpty()) {
                preventThirstWorld = preventThirstWorld.substring(0, preventThirstWorld.length() - 1);
            }
            configs = configs + " - prevent_thirst_worlds: " + "[" + preventThirstWorld + "]" + "\n";


            configs = configs + " - dirty_water_chance: " + plugin.config.getDirtyWaterChance() + "\n";
            configs = configs + " - respawn_thirst_level: " + plugin.config.getRespawnThirstLevel() + "\n";
            configs = configs + " - disable_cauldron: " + plugin.config.isCauldronDisabled() + "\n";
            configs = configs + " - disable_water_bowl: " + plugin.config.isWaterBowlDisabled() + "\n";
            configs = configs + " - disable_water_hands: " + plugin.config.isWaterHandsDisabled() + "\n";
            configs = configs + " - use_thirst_text: " + plugin.config.useThirstText() + "\n";
            configs = configs + " - display_thirst_warning: " + plugin.config.displayThirstWarning() + "\n";
            configs = configs + " - display_drink_notification: " + plugin.config.displayDrinkNotification() + "\n";
            configs = configs + " - thirst_damage: " + plugin.config.thirstDamage() + "\n";
            configs = configs + " - enable_temp: " + plugin.config.isEnableTemperature() + "\n";
            configs = configs + " - temp_seconds: " + plugin.config.getTempSeconds() + "\n";

            String preventTempWorld = "";
            for (String tempWorld : plugin.config.getPreventTempWorlds()) {
                preventTempWorld = preventTempWorld + tempWorld + ",";
            }
            if (!preventTempWorld.isEmpty()) {
                preventTempWorld = preventTempWorld.substring(0, preventTempWorld.length() - 1);
            }
            configs = configs + " - prevent_temp_worlds: " + "[" + preventTempWorld + "]" + "\n";


            Logging.log("Config info", configs);
        } else {
            SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
        }
    }

    public static void getPlayerInfo(CommandSender sender, Map<Integer, String> arguments) {
        Epidemic plugin = Epidemic.instance();
        Player player;
        Player infoPlayer = null;
        boolean canGet = false;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (Permission.hasPermission(player, Permission.PermissionType.ADMIN)) {
                canGet = true;
            }
        } else {
            canGet = true;
        }
        if (canGet) {
            UUID uuid = null;
            if (arguments.containsKey(0) && arguments.containsKey(1)) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.getName().equalsIgnoreCase(arguments.get(1))) {
                        infoPlayer = onlinePlayer;
                    }
                }
            } else {
                // error - usage
                SendMessage.Sender("command_playerinfo_usage", "&lUsage: &r/epidemic &bplayerinfo <player name>", sender, true, null);
            }
            String returnString = "";
            String playerName = "";
            if (infoPlayer != null) {
                playerName = infoPlayer.getDisplayName();
                returnString = "PLAYER INFO\n===================\n";
                returnString = returnString + "Location: " + WorldFunctions.fancyReadable(infoPlayer.getLocation()) + "\n&r";
                if (!Epidemic.instance().data().getPlayerAfflictionsByUUID(infoPlayer.getUniqueId()).isEmpty()) {
                    int i = 0;
                    for (Afflicted afflicted : Epidemic.instance().data().getPlayerAfflictionsByUUID(infoPlayer.getUniqueId())) {
                        returnString = returnString + "- Has Ailment: " + afflicted.getAilment().getDisplayName() + "\n";
                    }
                }
                if (!AilmentManager.afflictAtLocation(infoPlayer)) {
                    returnString = returnString + "- Player cannot be afflicted at this location due to WorldGuard or Domain\n";
                }
                Temperature temperature = new Temperature(infoPlayer);
                returnString = returnString + "Temp: " + temperature.getTemperature() + "\n";
                Set<Ailment> available = Epidemic.instance().data().getAvailableAilments();
                if (available != null) {
                    for (Ailment ailment : available) {
                        returnString = returnString + "Checking Ailment - " + ailment.getDisplayName() + "\n";
                        // Gamemode check
                        if (infoPlayer.getGameMode() != GameMode.SURVIVAL) {
                            returnString = returnString + " - Player cannot be afflicted with " + ailment.getDisplayName() + " due to not being in SURVIVAL mode\n";
                        } else {
                            returnString = returnString + " - Player is in SURVIVAL mode\n";
                        }
                        // Permission check
                        if (Permission.inBypass(infoPlayer)) {
                            returnString = returnString + " - Player cannot be afflicted with " + ailment.getDisplayName() + " due to being in bypass mode\n";
                        } else {
                            returnString = returnString + " - Player is in not in bypass mode\n";
                        }
                        // World check
                        if (ailment.getPreventAfflictWorlds() != null) {
                            String preventAfflictWorlds = "";
                            for (String noAilmentWorld : ailment.getPreventAfflictWorlds()) {
                                preventAfflictWorlds = preventAfflictWorlds + noAilmentWorld + ",";
                            }
                            if (!preventAfflictWorlds.isEmpty()) {
                                preventAfflictWorlds = preventAfflictWorlds.substring(0, preventAfflictWorlds.length() - 1);
                                returnString = returnString + " - PreventAfflictWorlds: [" + preventAfflictWorlds + "]\n";
                                if (ailment.getPreventAfflictWorlds().size() > 0) {
                                    for (String prevent : ailment.getPreventAfflictWorlds()) {
                                        if (prevent.equalsIgnoreCase(infoPlayer.getLocation().getWorld().getName())) {
                                            returnString = returnString + " - Player cannot be afflicted with " + ailment.getDisplayName() + " due to being in a prevent afflict world\n";
                                        }
                                    }
                                } else {
                                    returnString = returnString + " - No PreventAfflictWorlds found (empty)\n";
                                }
                            } else {
                                returnString = returnString + " - No PreventAfflictWorlds found (blank)\n";
                            }
                        } else {
                            returnString = returnString + " - No PreventAfflictWorlds found (null)\n";
                        }
                        // Check if player already has ailment
                        Set<Afflicted> afflictions = plugin.data().getPlayerAfflictionsByUUID(uuid);
                        if (afflictions != null) {
                            for (Afflicted afflicted : afflictions) {
                                if (afflicted.getAilment().getInternalName().equalsIgnoreCase(ailment.getInternalName())) {
                                    returnString = returnString + "Player cannot be afflicted with " + ailment.getDisplayName() + " due to already having the ailment\n";
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
                                    returnString = returnString + "Player cannot be afflicted with " + ailment.getDisplayName() + " due to being in a grace period for the ailment\n";
                                }
                            }
                        }
                    }
                }
            } else {
                // error
                returnString = "Invalid player";
                playerName = "Invalid Player";
            }
            Logging.log("Player Info Check - " + playerName, returnString);
        } else {
            SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
        }
    }

    public static void setInvincible(CommandSender sender, Map<Integer, String> arguments) {
        Epidemic plugin = Epidemic.instance();
        Player player;
        boolean canSet = false;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (Permission.hasPermission(player, Permission.PermissionType.ADMIN)) {
                canSet = true;
            }
        } else {
            canSet = true;
        }
        if (canSet) {
            UUID uuid = null;
            if (arguments.containsKey(0) && arguments.containsKey(1) && arguments.containsKey(2)) {
                if (NumberFunctions.isInt(arguments.get(2))) {
                    int today = Epidemic.instance().gameData().day().get();
                    int days = NumberFunctions.stringToInt(arguments.get(2));
                    if (days < 1) {
                        // error - must be at least 1 day
                        SendMessage.Sender("command_epiinvicible_invaliddays", "&cERROR &r- You must enter a valid number of days (at least 1)", sender, true, null);
                    }
                    int end = today + days;
                    if (StringFunctions.isUuid(arguments.get(1))) {
                        uuid = StringFunctions.uuidFromString(arguments.get(1));
                    } else {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getName().equalsIgnoreCase(arguments.get(1))) {
                                uuid = onlinePlayer.getUniqueId();
                            }
                        }
                    }
                    if (uuid != null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        if (offlinePlayer != null) {
                            InvinciblePlayers invinciblePlayer = new InvinciblePlayers(uuid, today, end);
                            plugin.data().putInvinciblePlayer(uuid, invinciblePlayer);
                            plugin.data().savePlayer(offlinePlayer);
                            HashMap<String, String> placeHolder = new HashMap<>();
                            placeHolder.put("<%days%>", arguments.get(2));
                            SendMessage.Sender("command_epiinvicible_success", "&aPlayer has been set as invincible for <%days%> game days", sender, true, placeHolder);
                        }
                    } else {
                        // error - unknown player
                        SendMessage.Sender("command_invalid_player", "&cERROR &r- Invalid player", sender, true, null);
                    }
                }
            } else {
                // error - usage
                SendMessage.Sender("command_epiinvicible_usage", "&lUsage: &r/epidemic &binvincible <player name or uuid> <number of days>", sender, true, null);
            }
        } else {
            SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
        }
    }

    public static void applyRemedy(CommandSender sender, Map<Integer, String> arguments) {

        // epi apply_remedy {player} {hand = MAIN|OFFHAND}
        Epidemic plugin = Epidemic.instance();
        Player player = null;
        if (sender instanceof Player) {
            SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
            return;
        }

        if (!arguments.containsKey(2)) {
            SendMessage.Sender("command_apply_remedy_usage", "&lUsage: &r/epidemic &bapply_remedy <player name or uuid> <hand (can be MAIN or OFFHAND)>", sender, true, null);
            return;
        }
        if (StringFunctions.isUuid(arguments.get(1))) {
            UUID uuid = StringFunctions.uuidFromString(arguments.get(1));
            player = Bukkit.getPlayer(uuid);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().equalsIgnoreCase(arguments.get(1))) {
                    player = onlinePlayer;
                }
            }
        }
        EquipmentSlot equipmentSlot = null;
        ItemStack itemStack;
        if (player != null) {
            String hand = arguments.get(2);
            if (hand.equalsIgnoreCase("main")) {
                equipmentSlot = EquipmentSlot.HAND;
                itemStack = player.getInventory().getItemInMainHand();
            } else if (hand.equalsIgnoreCase("offhand")) {
                equipmentSlot = EquipmentSlot.OFF_HAND;
                itemStack = player.getInventory().getItemInOffHand();
            } else {
                SendMessage.Sender("command_apply_remedy_usage", "&lUsage: &r/epidemic &bapply_remedy <player name or uuid> <hand (can be MAIN or OFFHAND)>", sender, true, null);
                return;
            }
        } else {
            SendMessage.Sender("command_invalid_player", "&cERROR &r- Invalid player", sender, true, null);
            return;
        }

        Remedy remedy = RemedyManager.getRemedy(player, equipmentSlot, itemStack);
        if (remedy != null) {
            remedy.use(player, player, equipmentSlot, new ItemStack(remedy.getReturnItem(), 1));
        } else {
            SendMessage.Sender("command_apply_remedy_na", "&cERROR &r- Invalid Item in hand", sender, true, null);
        }

    }

    public static void applyAilmentChance(CommandSender sender, Map<Integer, String> arguments) {

        // epi apply_ailment {player} {ailment} {chance}

        Player player = null;
        if (sender instanceof Player) {
            SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
            return;
        }

        if (!arguments.containsKey(3)) {
            SendMessage.Sender("command_apply_ailment_usage", "&lUsage: &r/epidemic &bapply_ailment <player name or uuid> <ailment> <chance>", sender, true, null);
            return;
        }
        if (StringFunctions.isUuid(arguments.get(1))) {
            UUID uuid = StringFunctions.uuidFromString(arguments.get(1));
            player = Bukkit.getPlayer(uuid);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().equalsIgnoreCase(arguments.get(1))) {
                    player = onlinePlayer;
                }
            }
        }
        Set<Ailment> ailmentSet = new HashSet<>();
        Ailment ailment = AilmentManager.getAilmentByInternalName(arguments.get(2));
        if (ailment == null) {
            SendMessage.Sender("command_apply_ailment_invalid_ailment", "&cERROR - &fInvalid ailment", sender, true, null);
            return;
        } else {
            ailmentSet.add(ailment);
        }
        int chance = 0;
        if (NumberFunctions.isInt(arguments.get(3))) {
            chance = NumberFunctions.stringToInt(arguments.get(3));
        } else {
            SendMessage.Sender("command_apply_ailment_usage", "&lUsage: &r/epidemic &bapply_ailment <player name or uuid> <ailment> <chance>", sender, true, null);
            return;
        }
        if (chance > 0) {
            AilmentManager.afflictCheck(player, AilmentCauseType.COMMAND, ailmentSet, null, 0, null, chance);
        } else {
            SendMessage.Sender("command_apply_ailment_invalid_chance", "&cERROR - &fInvalid chance", sender, true, null);
            // invalid chance
        }
    }
}
