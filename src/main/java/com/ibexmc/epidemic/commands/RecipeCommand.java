package com.ibexmc.epidemic.commands;

import com.ibexmc.epidemic.gui.RemedyListGUI;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RecipeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        boolean hasRecipe = false;
        Player senderPlayer = null;
        if (sender instanceof Player) {
            senderPlayer = (Player) sender;
            if (Permission.hasPermission(senderPlayer, Permission.PermissionType.RECIPES_DISPLAY)) {
                hasRecipe = true;
            }
        } else {
            hasRecipe = true;
        }


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
        return true;
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
}
