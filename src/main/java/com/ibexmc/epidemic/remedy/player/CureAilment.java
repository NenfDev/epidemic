package com.ibexmc.epidemic.remedy.player;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.Immunity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CureAilment {

    /**
     * Applies immunity for the player and ailment specified
     * @param player Player to apply immunity to
     * @param ailment Ailment to apply immunity for
     * @param immunityModifier Amount of immunity to apply
     */
    public static void applyImmunity(Player player, Ailment ailment, int immunityModifier) {
        Epidemic plugin = Epidemic.instance();
        if (ailment.getMaxImmunity() > 0 && immunityModifier > 0) {
            if (plugin.data().getPlayerImmunitiesByUUID(player.getUniqueId()).size() > 0) {

                boolean immunityFound = false;
                Set<Immunity> immunities = plugin.data().getPlayerImmunitiesByUUID(player.getUniqueId());
                Set<Immunity> newImmunities = new HashSet<>();
                for (Immunity i : immunities) {
                    if (i.getAilment().getInternalName().equals(ailment.getInternalName())) {
                        immunityFound = true;
                        i.setAmount(Math.min(i.getAmount() + immunityModifier, ailment.getMaxImmunity()));
                    }
                    newImmunities.add(i);
                }
                if (immunityFound) {
                    plugin.data().putPlayerImmunitySet(player.getUniqueId(), newImmunities);
                } else {
                    Immunity immunity = new Immunity(
                            ailment,
                            0
                    );
                    if (immunity.getAmount() + ailment.getImmunityModifier() < ailment.getMaxImmunity()) {
                        immunity.setAmount(immunity.getAmount() + immunityModifier);
                    } else {
                        immunity.setAmount(ailment.getMaxImmunity());
                    }
                    plugin.data().addPlayerImmunity(player.getUniqueId(), immunity);
                }
            } else {
                Immunity immunity = new Immunity(
                        ailment,
                        immunityModifier
                );
                plugin.data().addPlayerImmunity(player.getUniqueId(), immunity);
            }
        }
    }
}
