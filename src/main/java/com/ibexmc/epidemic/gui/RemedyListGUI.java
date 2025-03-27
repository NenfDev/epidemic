package com.ibexmc.epidemic.gui;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import com.ibexmc.epidemic.remedy.Remedy;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RemedyListGUI implements InvGUI {

    //region Objects
    private String title;
    private int slotCount;
    private int remedyCount = 0;
    private Map<Integer, Remedy> usedSlots = new HashMap<>();
    //endregion
    //region Constructors
    public RemedyListGUI(String title, int slotCount) {
        Epidemic plugin = Epidemic.instance();
        this.title = title;
        this.slotCount = slotCount;
        Map<String, Remedy> remedies = plugin.data().getRemedies();
        this.remedyCount = remedies.size();
    }
    //endregion
    //region Events
    /**
     * Called from InventoryClick for any clicks in an inventory window of this type
     * @param event InventoryClickEvent to process
     */
    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.LEFT) {
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                if (event.getRawSlot() >= 0 && event.getRawSlot() <= this.slotCount) {
                    Remedy clickedRemedy = usedSlots.get(event.getRawSlot());
                    if (clickedRemedy != null) {
                        InvGUI remedyDefinitionGUI = new RemedyDefinitionGUI(clickedRemedy);
                        player.openInventory(remedyDefinitionGUI.getInventory());
                    }
                }
            }
        }
    }
    //endregion
    //region Methods
    /**
     * Gets the inventory being used in this GUI
     */
    @Override
    public @NotNull Inventory getInventory() {

        this.slotCount = 9;
        if (remedyCount > 9 && remedyCount <= 18 ) { this.slotCount = 18; }
        if (remedyCount > 18 && remedyCount <= 27 ) { this.slotCount = 27; }
        if (remedyCount > 27 && remedyCount <= 36 ) { this.slotCount = 36; }
        if (remedyCount > 36 && remedyCount <= 45 ) { this.slotCount = 45; }
        if (remedyCount > 45 && remedyCount <= 54 ) { this.slotCount = 54; }

        Inventory inventory = Bukkit.createInventory(
                this,
                this.slotCount,
                Locale.localeText(
                        "remedy_def_gui_header",
                        "Remedies",
                        null
                )
        );

        int inventoryPosition = 0;
        for (Map.Entry<String, Remedy> remedyEntry : Epidemic.instance().data().getRemedies().entrySet()) {
            // TODO: Allow for pagination
            if (inventoryPosition < 54) {
                inventory.setItem(inventoryPosition, remedyEntry.getValue().getItemStack(1));
                usedSlots.put(inventoryPosition, remedyEntry.getValue());
                inventoryPosition++;
            }
        }

        return inventory;
    }
    //endregion

}
