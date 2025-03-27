package com.ibexmc.epidemic.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class EpidemicEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;

    /**
     * Base event
     * @param player Player the event is being applied for
     */
    public EpidemicEvent(Player player) {
        this.player = player;
    }

    /**
     * The player the event is being applied to
     * @return Online player
     */
    public Player getPlayer() {
        return player;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

