package com.ibexmc.epidemic.commands;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.thirst.Thirst;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.NumberFunctions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.thirst.SetThirst;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ThirstCommand implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args){
        StringBuilder fullargs = new StringBuilder();
        if (args.length > 0) {
            for (Object o : args)
            {
                fullargs.append(o.toString());
                fullargs.append(" ");
            }
        }
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if (Permission.hasPermission(senderPlayer, Permission.PermissionType.THIRST)) {
                if (cmd.getName().equalsIgnoreCase("thirst"))
                {
                    if (args.length <= 1) {
                        completions.add("add");
                        completions.add("remove");
                        completions.add("set");
                    }
                }

                if (args.length <= 2) {
                    if (args[0] != null) {
                        switch (args[0]) {
                            case "add":
                            case "remove":
                            case "set":
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    completions.add(player.getName());
                                }
                                break;
                            default:
                                break;
                        }

                    }
                }

            } else {
                // no perms
            }
        }
        return completions;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Epidemic plugin = Epidemic.instance();

        if (!plugin.config.isEnableThirst()) {
            SendMessage.Sender(
                    "thirst_disabled",
                    "&cThirst is disabled, unable to use command",
                    sender,
                    true,
                    null
            );
        }

        boolean playerSent = false;
        Player player = null;
        Player targetPlayer = null;
        if (sender instanceof Player) {
            playerSent = true;
            player = (Player) sender;
        }
        Map<Integer, String> arguments = new HashMap<>();

        boolean invalidCommand = false;
        StringBuilder allArguments = new StringBuilder();
        int argumentCount = 0;

        if (args.length > 0) // Check to make sure we have some arguments
        {
            for (Object o : args) {

                arguments.put(argumentCount, o.toString());


                allArguments.append(o.toString());
                allArguments.append(" ");
                argumentCount++;
            }
        }
        boolean canAccess = false;
        if (playerSent) {
            if (Permission.hasPermission(player, Permission.PermissionType.THIRST)) {
                canAccess = true;
            }
        } else {
            canAccess = true;
        }
        if (!canAccess) {
            SendMessage.Sender(
                    "command_no_perm_001",
                    "&4You do not have permission to use this command",
                    sender,
                    true,
                    null);
            return true;
        }

        for (Map.Entry<Integer, String> entry : arguments.entrySet()) {
            Logging.debug("ThirstCommand", "thirst", "Argument " + entry.getKey() + " = " + entry.getValue());
        }

        if (arguments.containsKey(0)) {
            switch (arguments.get(0)) {
                case "add":
                    if (arguments.containsKey(1) && arguments.containsKey(2)) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().equalsIgnoreCase(arguments.get(1))) {
                                targetPlayer = p;
                            }
                        }
                        if (targetPlayer != null) {
                            int amount = NumberFunctions.stringToInt(arguments.get(2));
                            if (amount >= 0 && amount <= 100) {
                                SetThirst.add(targetPlayer, amount);
                                SendMessage.Sender(
                                        "thirst_update_success",
                                        "&aThirst updated for player",
                                        sender,
                                        true,
                                        null);
                            } else {
                                // error, invalid number
                                SendMessage.Sender(
                                        "thirst_invalid_number",
                                        "&cAmount must be a whole number between 0 and 100 (example: 32)",
                                        sender,
                                        true,
                                        null);
                                break;
                            }
                        } else {
                            // invalid player
                            SendMessage.Sender(
                                    "thirst_invalid_player",
                                    "&cInvalid player.  Player must be online",
                                    sender,
                                    true,
                                    null);
                            break;
                        }
                    } else {
                        // add usage
                        SendMessage.Sender(
                                "thirst_add_usage",
                                "&lUsage: &r/thirst &badd &6<player> <amount 0-100>",
                                sender,
                                true,
                                null);
                    }
                    break;
                case "remove":
                    if (arguments.containsKey(1) && arguments.containsKey(2)) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().equalsIgnoreCase(arguments.get(1))) {
                                targetPlayer = p;
                            }
                        }
                        if (targetPlayer != null) {
                            int amount = NumberFunctions.stringToInt(arguments.get(2));
                            if (amount >= 0 && amount <= 100) {
                                // remove amount
                                SetThirst.add(targetPlayer, amount * -1);
                                SendMessage.Sender(
                                        "thirst_update_success",
                                        "&aThirst updated for player",
                                        sender,
                                        true,
                                        null);
                            } else {
                                // error, invalid number
                                SendMessage.Sender(
                                        "thirst_invalid_number",
                                        "&cAmount must be a whole number between 0 and 100 (example: 32)",
                                        sender,
                                        true,
                                        null);
                                break;
                            }
                        } else {
                            // invalid player
                            SendMessage.Sender(
                                    "thirst_invalid_player",
                                    "&cInvalid player.  Player must be online",
                                    sender,
                                    true,
                                    null);
                            break;
                        }
                    } else {
                        // remove usage
                        SendMessage.Sender(
                                "thirst_remove_usage",
                                "&lUsage: &r/thirst &bremove &6<player> <amount 0-100>",
                                sender,
                                true,
                                null);
                    }
                    break;
                case "set":
                    if (arguments.containsKey(1) && arguments.containsKey(2)) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p != null) {
                                Logging.debug(
                                        "ThirstCommand",
                                        "Set",
                                        "Looping through players - " + p.getName()
                                );
                                if (
                                        p.getName().equalsIgnoreCase(arguments.get(1)) ||
                                                p.getDisplayName().equalsIgnoreCase(arguments.get(1))
                                ) {
                                    targetPlayer = p;
                                }
                            }
                        }
                        if (targetPlayer != null) {
                            int amount = NumberFunctions.stringToInt(arguments.get(2));
                            if (amount >= 0 && amount <= 100) {
                                // set amount
                                SetThirst.set(targetPlayer, amount);
                                SendMessage.Sender(
                                        "thirst_update_success",
                                        "&aThirst updated for player",
                                        sender,
                                        true,
                                        null);
                            } else {
                                // error, invalid number
                                SendMessage.Sender(
                                        "thirst_invalid_number",
                                        "&cAmount must be a whole number between 0 and 100 (example: 32)",
                                        sender,
                                        true,
                                        null);
                                break;
                            }
                        } else {
                            // invalid player
                            SendMessage.Sender(
                                    "thirst_invalid_player",
                                    "&cInvalid player.  Player must be online",
                                    sender,
                                    true,
                                    null);
                        }
                    } else {
                        // set usage
                        SendMessage.Sender(
                                "thirst_set_usage",
                                "&lUsage: &r/thirst &bset &6<player> <amount 0-100>",
                                sender,
                                true,
                                null);
                        break;
                    }
                    break;
                default:
                    // usage
                    SendMessage.Sender(
                            "thirst_usage",
                            "&lUsage: &r/thirst &badd&r|&bremove&r|&bset &6<player> <amount 0-100>",
                            sender,
                            true,
                            null);
                    break;
            }
        } else {

            if (plugin.config.useThirstText()) {
                if (playerSent) {
                    Thirst thirst = plugin.data().getThirstBar().get(player.getUniqueId());
                    if (thirst != null) {
                        boolean disabled = false;
                        SendMessage.Sender("na", thirst.getThirstText(), sender, true, null);
                    }
                }
            } else {

                // usage
                SendMessage.Sender(
                        "thirst_usage",
                        "&lUsage: &r/thirst &badd&r|&bremove&r|&bset &6<player> <amount 0-100>",
                        sender,
                        true,
                        null);
            }
        }

        return true;
    }
}
