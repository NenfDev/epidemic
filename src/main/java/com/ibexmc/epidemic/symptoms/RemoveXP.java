package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.functions.XPFunctions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;

public class RemoveXP {

    /**
     * Removes XP from a player
     * @param player Player to remove XP from
     * @param amount Amount of XP to remove
     */
    public static void remove(Player player, int amount) {
        if (Bukkit.getOnlinePlayers().contains(player)) {
            if (amount >= 0) {
                XPFunctions.addXP(player, amount * -1);
            }
        }
    }

    /**
     * Applies XP removal to a player
     * @param player Player to remove XP from
     * @param afflicted Affliction causing XP loss
     */
    public static void applyRemoveXP(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player)  || player.getGameMode().equals(GameMode.CREATIVE)) {
                if (afflicted.getAilment().getRemoveXP() > 0) {
                    remove(player, afflicted.getAilment().getRemoveXP());
                }
            }
        }
    }
}
