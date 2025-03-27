package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CureCommand implements CommandExecutor, TabCompleter {
    @Override
    public List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args){
        List<String> tabCompletions = new ArrayList<>();
        Map<Integer, String> arguments = StringFunctions.getArguments(args);
        if (sender instanceof Player) {
            if (arguments.containsKey(0) && !arguments.containsKey(1)) {
                // player list
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tabCompletions.add(player.getName());
                }
            }
            if (arguments.containsKey(1) && !arguments.containsKey(2)) {
                Set<Ailment> ailments = Epidemic.instance().data().getAvailableAilments();
                if (ailments != null) {
                    for (Ailment ailment : ailments) {
                        tabCompletions.add(ailment.getInternalName().toLowerCase());
                    }
                }
            }
        }
        return tabCompletions;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Epidemic plugin = Epidemic.instance();

        Map<Integer, String> arguments = StringFunctions.getArguments(args);

        Player senderPlayer = null;
        boolean hasPerm = false;
        if (sender instanceof Player) {
            senderPlayer = (Player) sender;
            if (Permission.hasPermission(senderPlayer, Permission.PermissionType.CURE)) {
                hasPerm = true;
            }
        } else {
            hasPerm = true; // Console
        }

        if (!hasPerm) {
            SendMessage.Sender(
                    "command_no_perm_001",
                    "&4You do not have permission to use this command",
                    sender,
                    true,
                    null
            );
            return true;
        }

        if (arguments.containsKey(0)) {
            Player player = Bukkit.getPlayer(arguments.get(0));
            HashMap<String, String> param = new HashMap<>();
            param.put("<%player%>", arguments.get(0));
            int curedCount = 0;
            if (player != null) {
                Set<Afflicted> afflictions = plugin.data().getPlayerAfflictionsByUUID(player.getUniqueId());
                if (afflictions != null) {
                    if (afflictions.size() > 0) {
                        for (Afflicted affliction : afflictions) {
                            if (arguments.containsKey(1)) {
                                if (affliction.getAilment().getInternalName().equalsIgnoreCase(arguments.get(1))) {
                                    affliction.getAilment().cure(player, true, true);
                                    SendMessage.Sender(
                                            "cure_player_001",
                                            "<%player%> has been cured",
                                            sender,
                                            true,
                                            param
                                    );
                                    curedCount++;
                                }
                            } else {
                                affliction.getAilment().cure(player, true, true);
                                curedCount++;
                            }
                        }
                        if (curedCount > 0) {
                            SendMessage.Sender(
                                    "cure_player_001",
                                    "<%player%> has been cured",
                                    sender,
                                    true,
                                    param
                            );
                            return true;
                        } else {
                            SendMessage.Sender(
                                    "cure_player_002",
                                    "<%player%> does not need to be cured",
                                    sender,
                                    true,
                                    param
                            );
                            return true;
                        }
                    } else {
                        // player not sick
                        SendMessage.Sender(
                                "cure_player_002",
                                "<%player%> does not need to be cured",
                                sender,
                                true,
                                param
                        );
                        return true;
                    }
                } else {
                    // player not sick
                    SendMessage.Sender(
                            "cure_player_002",
                            "<%player%> does not need to be cured",
                            sender,
                            true,
                            param
                    );
                    return true;
                }
            } else {
                // invalid player
                SendMessage.Sender(
                        "cure_invalid_player_001",
                        "Unable to cure player. Invalid player - <%player%>",
                        sender,
                        true,
                        param
                );
                return true;
            }
        } else {
            // usage
            SendMessage.Sender(
                    "cure_usage_001",
                    "Usage &1/cure &b<player> <ailment>",
                    sender,
                    true,
                    null
            );
        }
        return true;
    }

}
