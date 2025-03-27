package com.ibexmc.epidemic.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface InvGUI extends InventoryHolder {
    /**
     * Used to handle inventory clicking
     * @param event InventoryClickEvent to process
     */
    void onClick(InventoryClickEvent event);
}
