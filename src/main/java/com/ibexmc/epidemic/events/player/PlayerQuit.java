package com.ibexmc.epidemic.events.player;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    /**
     * Called when a player quits the game.  Used to save the player data to the data file.
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Logging.debug(
                "PlayerQuit",
                "onQuit",
                "Event Fired"
        );
        try {
            Logging.debug(
                    "PlayerQuit",
                    "onQuit",
                    "Saving Player " + event.getPlayer().getName() +
                            " (" + event.getPlayer().getUniqueId() + ")"
            );
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
            Epidemic.instance().data().savePlayer(
                    Epidemic.instance().data().getPlayerFile(event.getPlayer().getUniqueId()),
                    offlinePlayer
            );
            //Epidemic.instance().data().clearPlayerAfflictions(event.getPlayer().getUniqueId());
        } catch (Exception ex) {
            Error.save(
                    "PlayerQuit.onQuit.001",
                    "PlayerQuit",
                    "onQuit()",
                    "Player Quit Event",
                    "Unexpected error during Player Quit event",
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }
}
