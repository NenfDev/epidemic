package com.ibexmc.epidemic.events.block;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.util.SendMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockPlace {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Epidemic plugin = Epidemic.instance();
        if (event.isCancelled()) {
            return;
        }
        if (plugin.persistentData().hasString(event.getPlayer().getInventory().getItemInMainHand(), PersistentData.Key.EQUIPMENT)) {
            SendMessage.Player("place_equipment", "&4You cannot place that", event.getPlayer(), true, null);
            event.setCancelled(true);
            return;
        }
    }
}
