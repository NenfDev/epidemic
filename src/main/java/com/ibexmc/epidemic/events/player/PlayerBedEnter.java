package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.NumberFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Set;

public class PlayerBedEnter implements Listener {

    /**
     * Called any time a player tries to enter a bed.  Used for insomnia checks.
     * @param event PlayerBedEnterEvent
     */
    @EventHandler
    public void onEnter(PlayerBedEnterEvent event) {
        Logging.debug(
                "PlayerBedEnter",
                "onEnter",
                "Event Fired"
        );
        try {
            Set<Afflicted> afflictedSet = Epidemic.instance().data().getPlayerAfflictionsByUUID(event.getPlayer().getUniqueId());
            if (afflictedSet != null) {
                for (Afflicted afflicted : afflictedSet) {
                    if (afflicted.getAilment() != null) {
                        int cureChance = afflicted.getAilment().cureWithSleepChance();
                        int randomChance = NumberFunctions.random(1000);
                        Logging.debug(
                                "PlayerBedEnter",
                                "onEnter",
                                "Cure With Sleep Check : " +
                                        " Ailment - " + afflicted.getAilment().getDisplayName() +
                                        " Player UUID = " + event.getPlayer().getUniqueId() +
                                        " Cure from sleep chance = " + afflicted.getAilment().cureWithSleepChance() +
                                        " Random Chance = " + randomChance
                        );
                        if (randomChance < cureChance) {
                            afflicted.getAilment().cure(event.getPlayer(), true, true);
                        }
                    }
                }
            }
            if (Epidemic.instance().data().containsInsomniaPlayers(event.getPlayer().getUniqueId())) {
                Logging.debug(
                        "PlayerBedEnter",
                        "onEnter",
                        "Player UUID: " + event.getPlayer().getUniqueId() + " does has insomnia"
                );
                event.setCancelled(true);
                SendMessage.Player("has_insomnia", "You cannot sleep - the insomnia is wearing away at you", event.getPlayer(), true, null);
            } else {
                Logging.debug(
                        "PlayerBedEnter",
                        "onEnter",
                        "Player UUID: " + event.getPlayer().getUniqueId() + " does not have insomnia"
                );
            }
        } catch (Exception ex) {
            Error.save(
                    "PlayerBedEnter.onEnter.001",
                    "PlayerBedEnter",
                    "onEnter()",
                    "Player Bed Enter Event",
                    "Unexpected error during Player Bed Enter event",
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }
}
