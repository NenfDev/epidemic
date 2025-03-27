package com.ibexmc.epidemic.events.player;

import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.symptoms.Gibberish;
import com.ibexmc.epidemic.util.SendMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    /**
     * Called any time chat is sent from a client ot the game.  Used for gibberish checks.
     * @param event AsyncPlayerChatEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Check the player for gibberish, chance is 1-100, must have ailment with gibberish
        // Mute players are piggybacked onto gibberish and return !#mute#! if muted
        // Sore throat also piggybacks on gibberish
        Gibberish gibberish = new Gibberish();
        String text = gibberish.getText(event.getPlayer(), event.getMessage(), 50, true);
        if (text.equalsIgnoreCase("!#mute#!")) {
            SendMessage.actionBar(event.getPlayer(), Locale.localeText("mute_player", "&cYou can't talk right now", null));
            event.setCancelled(true);
            return;
        }
        event.setMessage(text);
    }
}
