package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.HashMap;

public class HealthCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        try {
            StringBuilder allArguments = new StringBuilder();
            int argumentCount = 0;
            if (args.length > 0) {
                for (Object o : args) {
                    allArguments.append(o.toString());
                    allArguments.append(" ");
                    argumentCount++;
                }
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (argumentCount > 0) {
                    Logging.debug(
                            "HealthCommand",
                            "onCommand",
                            "Command called with arguments: " + allArguments
                    );
                    SendMessage.Sender("health_usage_001", "Usage /health", sender, true, null);
                } else {
                    if (Permission.hasPermission(player, Permission.PermissionType.HEALTH)) {
                        Logging.debug(
                                "HealthCommand",
                                "onCommand",
                                "Command called without arguments"
                        );
                        if (Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId()) != null) {
                            SendMessage.Player("na", ChatColor.YELLOW + "========================", player, false, null);
                            SendMessage.Player("health_header_001", "&6       &lEpidemic - Health", player, false, null);
                            SendMessage.Player("na", ChatColor.YELLOW + "========================", player, false, null);
                            if (Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId()).size() > 0) {
                                int i = 0;
                                for (Afflicted afflicted : Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId())) {
                                    boolean hasSymptoms = false;
                                    if (afflicted.getStartTimestamp() != null) {
                                        long afflictedSeconds = (new Timestamp(System.currentTimeMillis()).getTime() - afflicted.getStartTimestamp().getTime()) / 1000;
                                        hasSymptoms = (afflictedSeconds >= afflicted.getAilment().getTimeToSymptoms());
                                    }
                                    if (afflicted.getAilment().isReportBeforeSymptoms() || hasSymptoms) {
                                        HashMap<String, String> placeHolder = new HashMap<>();
                                        placeHolder.put("<%ailment%>", afflicted.getAilment().getDisplayName());
                                        SendMessage.Player("health_header_002", ChatColor.YELLOW + "Ailment - <%ailment%>", player, false, placeHolder);
                                        i++;
                                    }
                                }
                                if (i == 0) {
                                    SendMessage.Player("health_no_ailment_001", "&4No ailment found", player, true, null);
                                }
                            } else {
                                SendMessage.Player("health_no_ailment_001", "&4No ailment found", player, true, null);
                            }
                        } else {
                            SendMessage.Player("health_no_ailment_001", "&4No ailment found", player, true, null);
                        }
                    } else {
                        SendMessage.Player("command_no_perm_001", "&4You do not have permission to use that command", player, true, null);
                    }
                }
            } else {
                SendMessage.Sender("command_online_only_001", "&4This command can only be used by online players", sender, true, null);
            }
        } catch (Exception ex) {
            Error.save(
                    "HealthCommand.onCommand.001",
                    "HealthCommand",
                    "onCommand()",
                    "/health command",
                    "Unexpected error during /health command",
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
        return true;
    }
}
