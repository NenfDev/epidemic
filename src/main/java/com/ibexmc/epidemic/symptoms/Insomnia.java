package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Permission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;

public class Insomnia {
    /**
     * Applies insomnia to a player
     * @param player Player to apply insomnia for
     * @param afflicted Affliction causing insomnia
     */
    public static void applyInsomnia(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player)  || player.getGameMode().equals(GameMode.CREATIVE)) {
                if (afflicted.getAilment().isInsomnia()) {
                    Epidemic.instance().data().putInsomniaPlayers(player.getUniqueId(), true);
                }
            }
        }
    }
}
