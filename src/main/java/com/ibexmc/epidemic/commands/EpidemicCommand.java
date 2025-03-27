package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.equipment.*;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import com.ibexmc.epidemic.util.functions.PlayerFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.gui.RemedyListGUI;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EpidemicCommand implements CommandExecutor, TabCompleter {

    /**
     * onTabComplete Returns a list of tab completions for online players
     * @param sender CommandSender sending the command
     * @param cmd Command sent
     * @param label Label of command
     * @param args Arguments sent to the command
     * @return List<String> String list of avaialble tab completions
     */
    @Override
    public List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args){
        List<String> tabCompletions = new ArrayList<>();
        Map<Integer, String> arguments = StringFunctions.getArguments(args);
        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if (arguments.containsKey(0)) {
                if (Permission.isAdmin(senderPlayer)) {
                    if (!arguments.containsKey(1)) {
                        tabCompletions.add("day");
                        tabCompletions.add("debug");
                        tabCompletions.add("reload");
                        tabCompletions.add("version");
                        tabCompletions.add("invincible");
                    }
                    if (arguments.get(0).equalsIgnoreCase("invincible")) {
                        if (!arguments.containsKey(2)) {
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                tabCompletions.add(ChatColor.stripColor(onlinePlayer.getName()));
                            }
                        } else {
                            if (!arguments.containsKey(3)) {
                                tabCompletions.add("7");
                            }
                        }
                    }

                }

                if (!arguments.containsKey(1)) {
                    if (Permission.canBypass(senderPlayer)) {
                        tabCompletions.add("bypass");
                    }
                    if (Permission.hasPermission(senderPlayer, Permission.PermissionType.RECIPES_DISPLAY)) {
                        tabCompletions.add("recipes");
                    }
                }

            }
        }
        return tabCompletions;
    }

    /**
     * onCommand Called when the player submits the /epidemic or /epi command
     * @param sender CommandSender sending the command
     * @param cmd Command sent
     * @param label Label of command
     * @param args Arguments sent to the command
     * @return boolean Returns true if successful
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Map<Integer, String> arguments = StringFunctions.getArguments(args);

        boolean hasAdmin = false;
        boolean canBypass = false;
        boolean hasRecipe = false;
        Player senderPlayer = null;
        if (sender instanceof Player) {
            senderPlayer = (Player) sender;
            hasAdmin = Permission.isAdmin(senderPlayer);
            canBypass = Permission.canBypass(senderPlayer);
            hasRecipe = Permission.hasPermission(senderPlayer, Permission.PermissionType.RECIPES_DISPLAY);
        } else {
            hasAdmin = true;
        }

        if (arguments.containsKey(0)) {
            switch (arguments.get(0)) {
                /*
                case "test":
                    FullSyringe fullSyringe = (FullSyringe) Epidemic.instance().data().equipment.get("full_syringe");
                    Ailment plague = AilmentManager.getAilmentByInternalName("bubonic_plague");
                    ItemStack fullSyringeItem = fullSyringe.get().clone();
                    if (plague != null) {
                        InventoryFunctions.addItemsToPlayerInventory(senderPlayer, EquipmentManager.applyAilmentToFullSyringe(fullSyringeItem, plague));
                    }
                    break;
                 */
                case "debuginfo":
                    CommandManager.getInfo(sender, arguments);
                    break;
                case "playerinfo":
                    CommandManager.getPlayerInfo(sender, arguments);
                    break;
                case "head":
                    senderPlayer.getInventory().setItemInMainHand(PlayerFunctions.head(StringFunctions.uuidFromString("senderPlayer.getInventory()")));
                    break;
                case "day":
                    //command_day: "&rIt is day <%day%>"
                    boolean canDay = false;
                    if (sender instanceof Player) {
                        Player dayPlayer = (Player) sender;
                        if (Permission.isAdmin(dayPlayer)) {
                            canDay = true;
                        }
                    } else {
                        canDay = true;
                    }
                    if (canDay) {
                        int day = Epidemic.instance().gameData().day().get();
                        HashMap<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%day%>", "" + day);
                        SendMessage.Sender(
                                "command_day",
                                "&rIt is day <%day%>",
                                sender,
                                true,
                                placeHolder
                        );
                    } else {
                        SendMessage.Sender("command_no_perm_001", "&cYou do not have permission to use that command", sender, true, null);
                    }
                    break;
                case "item":
                    Logging.log("", InventoryFunctions.itemToStringBlob(senderPlayer.getInventory().getItemInMainHand()));
                    break;

                case "invincible":
                    // /epi invincible <uuid/player name> <number of days>
                    // Overwrites any existing entry
                    // Forces file save for player
                    CommandManager.setInvincible(sender, arguments);
                    break;


                case "apply_remedy":
                    CommandManager.applyRemedy(sender, arguments);
                    break;
                case "apply_ailment":
                    CommandManager.applyAilmentChance(sender, arguments);
                    break;

                case "debug":
                    if (hasAdmin) {
                        debug(sender);
                    }
                    else {
                        SendMessage.Sender(
                                "command_no_perm_001",
                                "&4You do not have permission to use this command",
                                sender,
                                true,
                                null
                        );
                    }
                    break;
                case "reload":
                    if (hasAdmin) {
                        reload(sender);
                    }
                    else {
                        SendMessage.Sender(
                                "command_no_perm_001",
                                "&4You do not have permission to use this command",
                                sender,
                                true,
                                null
                        );
                    }
                    break;
                case "version":
                    if (hasAdmin) {
                        version(sender);
                    }
                    else {
                        SendMessage.Sender(
                                "command_no_perm_001",
                                "&4You do not have permission to use this command",
                                sender,
                                true,
                                null
                        );
                    }
                    break;
                case "bypass":
                    if (senderPlayer != null) {
                        if (canBypass) {
                            bypass(senderPlayer);
                        }
                        else {
                            SendMessage.Sender(
                                    "command_no_perm_001",
                                    "&4You do not have permission to use this command",
                                    sender,
                                    true,
                                    null
                            );
                        }
                    }
                    else {
                        SendMessage.Sender(
                                "command_online_only_001",
                                "&cThis command can only be used by online players",
                                sender,
                                true,
                                null
                        );
                    }
                    break;
                case "recipes":
                    if (senderPlayer != null) {
                        if (hasRecipe) {
                            recipes(senderPlayer);
                        }
                        else {
                            SendMessage.Sender(
                                    "command_no_perm_001",
                                    "&4You do not have permission to use this command",
                                    sender,
                                    true,
                                    null
                            );
                        }
                    }
                    else {
                        SendMessage.Sender(
                                "command_online_only_001",
                                "&cThis command can only be used by online players",
                                sender,
                                true,
                                null
                        );
                    }
                    break;
                default:
                    usage(sender, senderPlayer);
                    break;
            }
        } else {
            usage(sender, senderPlayer);
        }

        return true;
    }

    /**
     * debug Enables and disables the debug mode.  When debug mode is active, additional logging is captured
     * and sent to the console.  If enabled, disables.  If disabled, enables.
     * @param sender CommandSender running the command
     */
    private void debug(CommandSender sender) {
        Epidemic.instance().gameData().debug().toggle();
        if (Epidemic.instance().gameData().debug().get()) {
            SendMessage.Sender(
                    "epidemic_debug_on_001",
                    "&aDebug mode enabled",
                    sender,
                    true,
                    null
            );
        } else {
            SendMessage.Sender(
                    "epidemic_debug_off_001",
                    "&cDebug mode disabled",
                    sender,
                    true,
                    null
            );
        }
    }

    /**
     * reload Performs a complete reload of configuration files.  Only accessible via console or for
     * players with the epidemic.admin permission.
     * @param sender CommandSender running the reload
     */
    private void reload(CommandSender sender) {
        SendMessage.Sender(
                "epidemic_reload_configuration_001",
                "Reloading configuration..",
                sender,
                true,
                null
        );
        Epidemic.instance().reload();
        SendMessage.Sender(
                "epidemic_reload_configuration_002",
                "&aReload complete",
                sender,
                true,
                null
        );
    }

    /**
     * version Displays the current plugin version.  Only accessible via console or for players with
     * the epidemic.debug permission
     * @param sender CommandSender to return the version to
     */
    private void version(CommandSender sender) {
        HashMap<String, String> versionParam = new HashMap<>();
        versionParam.put("<%name%>", Epidemic.instance().getDisplayName());
        versionParam.put("<%ver%>", Epidemic.instance().getDescription().getVersion());
        SendMessage.Sender(
                "epidemic_get_version_001",
                "<%name%> - Version <%ver%>",
                sender,
                true,
                versionParam
        );
    }

    /**
     * bypass Enables/disables the bypass mode for the player.  Only accessible for online players with
     * the epidemic.bypass or epidemic.admin permissions.
     * @param player Player to put into bypass mode
     */
    private void bypass(Player player) {
        Epidemic plugin = Epidemic.instance();
        if (plugin.gameData().bypass().has(player.getUniqueId())) {
            plugin.gameData().bypass().remove(player.getUniqueId());
            SendMessage.Player(
                    "epidemic_bypass_off",
                    "&cDisabled Bypass",
                    player,
                    true,
                    null
            );
        } else {
            plugin.gameData().bypass().add(player.getUniqueId());
            SendMessage.Player(
                    "epidemic_bypass_on",
                    "&aEnabling Bypass",
                    player,
                    true,
                    null
            );
        }
    }

    /**
     * recipes Displays the recipes GUI.  Accessible only for online players
     * @param player Player to open the GUI for
     */
    private void recipes(Player player) {
        if (player == null) {
            return;
        }
        RemedyListGUI gui = new RemedyListGUI(
                Locale.localeText(
                        "remedy_def_gui_header",
                        "Remedies",
                        null
                ), 9);
        player.openInventory(gui.getInventory());
        Logging.debug(
                "EpidemicCommand",
                "recipes()",
                "/epidemic recipes command run by player: " + player.getDisplayName()
        );
    }

    /**
     * usage Displays the usage for this command, uses the permissions in order to decide what to show
     * @param sender CommandSender to return the output to
     * @param senderPlayer Player that is calling the command (if not console) in order to check permissions
     */
    private void usage(CommandSender sender, Player senderPlayer) {
        boolean canUse = false;
        boolean hasAdmin = false;
        boolean canBypass = false;
        boolean hasRecipe = false;

        if (senderPlayer == null) {
            hasAdmin = true;
        } else {
            hasAdmin = Permission.isAdmin(senderPlayer);
            canBypass = Permission.canBypass(senderPlayer);
            hasRecipe = Permission.hasPermission(senderPlayer, Permission.PermissionType.RECIPES_DISPLAY);
        }

        if (hasAdmin) {
            SendMessage.Sender(
                    "epicmd_admin_usage_001",
                    "&d/epidemic &bdebug &r- Enables/disables debug mode",
                    sender,
                    true,
                    null
            );
            SendMessage.Sender(
                    "epicmd_admin_usage_002",
                    "&d/epidemic &breload &r- Reloads the configuration files",
                    sender,
                    true,
                    null
            );
            SendMessage.Sender(
                    "epicmd_admin_usage_003",
                    "&d/epidemic &bversion &r- Displays the current version",
                    sender,
                    true,
                    null
            );
            SendMessage.Sender(
                    "epicmd_admin_usage_004",
                    "&d/epigive &b<player name> <item key>",
                    sender,
                    true,
                    null
            );
            canUse = true;
        }
        if (canBypass) {
            SendMessage.Sender(
                    "epicmd_bypass_usage_001",
                    "&d/epidemic &bbypass &r- Enables/disables bypass mode",
                    sender,
                    true,
                    null
            );
            canUse = true;
        }
        if (hasRecipe) {
            SendMessage.Sender(
                    "epicmd_recipe_usage_001",
                    "&d/epidemic &brecipes &r-  Opens the Recipes GUI",
                    sender,
                    true,
                    null
            );
            canUse = true;
        }
        if (!canUse) {
            SendMessage.Sender(
                    "command_no_perm_001",
                    "&4You do not have permission to use this command",
                    sender,
                    true,
                    null
            );
        }
    }
}
