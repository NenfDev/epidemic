package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.temperature.Temperature;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.log.Error;
import com.ibexmc.epidemic.dependencies.papi.PAPIValues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    /**
     * Called any time a player joins the game.  Used to populate the players information
     * from their data file and to auto-discover recipes.
     * @param event PlayerJoinEvent
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Logging.debug(
                "PlayerJoin",
                "onJoin",
                "Event Fired"
        );

        if (Epidemic.instance().config.isAutoDiscoverRecipes()) {
            Epidemic.instance().data().discoverRecipes(event.getPlayer());
        }

        try {
            Epidemic.instance().data().loadPlayer(event.getPlayer());

            Temperature temp = new Temperature(event.getPlayer());
            temp.applyTemperature();

            PAPIValues.update(event.getPlayer());

        } catch (Exception ex) {
            Error.save(
                    "PlayerJoin.onJoin.001",
                    "PlayerJoin",
                    "onJoin()",
                    "Player Join Event",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }
}
