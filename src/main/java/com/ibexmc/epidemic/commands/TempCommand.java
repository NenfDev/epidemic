package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.temperature.Temperature;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempCommand implements CommandExecutor, TabCompleter {
    private Map<Integer, String> getArguments(String[] args) {
        Map<Integer, String> arguments = new HashMap<>();
        int argumentCount = 0;
        if (args.length > 0) // Check to make sure we have some arguments
        {
            for (Object o : args) {
                arguments.put(argumentCount, o.toString());
                argumentCount++;
            }
        }
        return arguments;
    }
    @Override
    public List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args){
        List<String> tabCompletions = new ArrayList<>();
        Map<Integer, String> arguments = getArguments(args);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, Permission.PermissionType.TEMP_INFO)) {
                tabCompletions.add("info");
            }
        }
        return tabCompletions;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!Epidemic.instance().config.isEnableTemperature()) {
            SendMessage.Sender(
                    "temp_disabled",
                    "&cTemperature is disabled, unable to use command",
                    sender,
                    true,
                    null
            );
            return true;
        }

        Map<Integer, String> arguments = getArguments(args);
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!Permission.hasPermission(player, Permission.PermissionType.TEMP)) {
                SendMessage.Sender(
                        "command_no_perm_001",
                        "&4You do not have permission to use this command",
                        sender,
                        true,
                        null);
                return true;
            }
            Temperature temperature = new Temperature(player);

            if (arguments.containsKey(0)) {
                if ("info".equals(arguments.get(0))) {
                    if (Permission.hasPermission(player, Permission.PermissionType.TEMP_INFO)) {
                        SendMessage.Player("na", temperature.getThermometerText(),
                                player, true, null);
                        SendMessage.Player("na", temperature.getTemperatureInfo(),
                                player, true, null);
                    } else {
                        SendMessage.Sender(
                                "temp_no_perm",
                                "&cSorry, you do not have permission to do use that command",
                                sender,
                                true,
                                null);
                        return true;
                    }
                } else {
                    SendMessage.Sender(
                            "temp_usage",
                            "&cSorry, that usage is not supported.  Use /temp or /temp info",
                            sender,
                            true,
                            null);
                }
            } else {
                SendMessage.Player("na", temperature.getThermometerText(),
                        player, true, null);
            }
        } else {
            // must be a player
            SendMessage.Sender(
                    "command_online_only_001",
                    "&cThis command can only be used by online players",
                    sender,
                    true,
                    null);
        }
        return true;
    }
}
