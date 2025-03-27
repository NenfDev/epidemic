package com.ibexmc.epidemic.commands;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AfflictPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class InfectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
         try {
            Player senderPlayer = null;
            Player player = null;
            boolean hasPerm = false;
            if (sender instanceof Player) {
                senderPlayer = (Player) sender;
                if (Permission.hasPermission(senderPlayer, Permission.PermissionType.INFECT)) {
                    hasPerm = true;
                }
            } else {
                // console sent
                hasPerm = true;
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

            StringBuilder allArguments = new StringBuilder();
            int argumentCount = 0;
            if (args.length > 0) // Check to make sure we have some arguments
            {
                for (Object o : args) {
                    allArguments.append(o.toString());
                    allArguments.append(" ");
                    argumentCount++;
                }
            }
            if (argumentCount > 0) {
                Logging.debug(
                        "InfectCommand",
                        "onCommand",
                        "Command called with arguments: " + allArguments
                );

                if (args[0] != null) {
                    if (argumentCount > 1) {
                        if (args[1] != null) {

                            player = Bukkit.getPlayer(args[0]);
                            if (player != null) {
//                                if (senderPlayer != null) {
//                                    if (senderPlayer.getUniqueId().equals(player.getUniqueId())) {
//                                        SendMessage.Sender("infect_error_004", "&4You can't infect yourself", sender, true, null);
//                                        return true;
//                                    }
//                                }
                                Ailment ailment = Epidemic.instance().data()
                                        .getAvailableAilmentByInternalName(args[1]);
                                if (ailment != null) {
                                    if (!AfflictPlayer.canAfflict(player.getUniqueId(), ailment)) {
                                        SendMessage.Sender("command_infect_invincible", "&cERROR &r- Player is invincible", sender, true, null);
                                    }
                                    Logging.debug(
                                            "InfectCommand",
                                            "onCommand.Infect",
                                            "Infecting " + player.getDisplayName() + " with ailment: " + ailment.getDisplayName()
                                    );
                                    UUID senderUUID = null;
                                    if (senderPlayer != null) {
                                        senderUUID = senderPlayer.getUniqueId();
                                    }
                                    AfflictPlayer.afflictPlayer(player.getUniqueId(), ailment, null, senderUUID);
                                } else {
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("<%ailment%>", args[1]);
                                    SendMessage.Sender("infect_error_002", "&4Invalid ailment. Ailment entered - &b<%ailment%>", sender, true, param);
                                    return true;
                                }
                            } else {
                                HashMap<String, String> param = new HashMap<>();
                                param.put("<%player%>", args[0]);
                                SendMessage.Sender("infect_error_003", "&4Unable to infect player. Invalid player - <%player%>", sender, true, param);
                                return true;
                            }
                        } else {
                            StringBuilder allAilments = new StringBuilder();
                            for (Ailment ailments : Epidemic.instance().data().getAvailableAilments()) {
                                allAilments.append(ailments.getInternalName());
                                allAilments.append(" ");
                            }
                            allAilments.append("#");
                            HashMap<String, String> param = new HashMap<>();
                            param.put("<%ailments%>", allAilments.toString().replace(" #", ""));
                            SendMessage.Sender("infect_usage_001", "Usage /infect &b<player> <ailment>", sender, true, null);
                            SendMessage.Sender("infect_usage_002", "Valid ailments are - <%ailments%>", sender, true, param);
                            return true;
                        }
                    } else {
                        StringBuilder allAilments = new StringBuilder();
                        for (Ailment ailments : Epidemic.instance().data().getAvailableAilments()) {
                            allAilments.append(ailments.getInternalName());
                            allAilments.append(" ");
                        }
                        allAilments.append("#");
                        HashMap<String, String> param = new HashMap<>();
                        param.put("<%ailments%>", allAilments.toString().replace(" #", ""));
                        SendMessage.Sender("infect_usage_001", "Usage /infect &b<player> <ailment>", sender, true, null);
                        SendMessage.Sender("infect_usage_002", "Valid ailments are - <%ailments%>", sender, true, param);
                    }
                } else {
                    SendMessage.Sender("infect_usage_001", "Usage /infect &b<player> <ailment>", sender, true, null);
                }

            } else {
                Logging.debug(
                        "InfectCommand",
                        "onCommand",
                        "Command called without arguments"
                );
            }
        } catch (Exception ex) {
            Error.save(
                    "InfectCommand.onCommand.001",
                    "InfectCommand",
                    "onCommand()",
                    "/infect command",
                    "Unexpected error during /infect command",
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
        return true;
    }
}
