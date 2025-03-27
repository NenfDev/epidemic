package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.equipment.IEquipment;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GiveCommand implements CommandExecutor, TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> tabCompletions = new ArrayList<>();
        Map<Integer, String> arguments = StringFunctions.getArguments(args);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (arguments.size() < 2) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    tabCompletions.add(onlinePlayer.getName());
                }
            }
            if (arguments.size() == 2) {
                for (Map.Entry<String, Remedy> entry : Epidemic.instance().data().getRemedies().entrySet()) {
                    tabCompletions.add(entry.getKey().replace("epidemic_", ""));
                }
                for (Map.Entry<String, IEquipment> equipmentEntry : Epidemic.instance().data().equipment.entrySet()) {
                    tabCompletions.add(equipmentEntry.getKey());
                }
            }
        }
        return tabCompletions;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Map<Integer, String> arguments = StringFunctions.getArguments(args);
        CommandManager.give(sender, arguments, label);
        return true;
    }
}
