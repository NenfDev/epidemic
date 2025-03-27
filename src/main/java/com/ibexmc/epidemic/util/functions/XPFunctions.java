package com.ibexmc.epidemic.util.functions;

import org.bukkit.entity.Player;

public class XPFunctions {

    /**
     * Adds to the players XP
     * @param player Player
     * @param exp Experience points to add
     * @return New xp level
     */
    public static int addXP(Player player, int exp){
        int currentExp = getCurrentXP(player);
        player.setExp(0); // Reset current xp to 0
        player.setLevel(0); // Reset current level to 0
        // Give XP back + difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);
        return newExp;
    }

    /**
     * Sets a players XP
     * @param player Player
     * @param exp Experience points to set to
     */
    public static void setXP(Player player, int exp){
        player.setExp(0); // Reset current xp to 0
        player.setLevel(0); // Reset current level to 0
        // Give XP back + difference
        player.giveExp(exp);
    }

    /**
     * Gets a players current XP
     * @param player Player to lookup
     * @return XP the player has
     */
    public static int getCurrentXP(Player player){
        int currentXP = 0;
        int currentLevel = player.getLevel();
        currentXP += getXPAtLevel(currentLevel);
        currentXP += Math.round(getXPNeededToLevelUp(currentLevel) * player.getExp());
        return currentXP;
    }

    /**
     * Gets the number of xp required to level up to the provided level
     * @param level Level to check
     * @return Number of xp required
     */
    public static int getXPNeededToLevelUp(int level){
        if (level <= 15) {
            return 2*level+7;
        } else if (level <= 30) {
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    /**
     * Gets the XP at the provided level
     * @param level Level to lookup
     * @return XP at level
     */
    public static int getXPAtLevel(int level){
        if (level <= 16) {
            return (int) (Math.pow(level,2) + 6*level);
        } else if (level <= 31) {
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }


}
