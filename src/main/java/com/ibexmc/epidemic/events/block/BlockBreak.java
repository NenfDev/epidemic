package com.ibexmc.epidemic.events.block;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentCauseType;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Logging.debug(
                "BlockBreak",
                "onBlockBreak",
                "Block broken" + "\n" +
                        "Block Type: " + event.getBlock().getType().name() + "\n" +
                        "Broken by: " + event.getPlayer().getName()
        );
        if (Epidemic.instance().data().blockAilments != null) {
            if (Epidemic.instance().data().blockAilments.containsKey(event.getBlock().getType())) {
                Logging.debug("BlockBreak", "onBlockBreak", "Block Break Ailment exists for this material");
                if (Epidemic.instance().data().blockAilments.get(event.getBlock().getType()) != null) {
                    if (Epidemic.instance().data().blockAilments.get(event.getBlock().getType()).size() > 0) {
                        for (Ailment ailment : Epidemic.instance().data().blockAilments.get(event.getBlock().getType())) {
                            // Random check for this ailment
                            // true:
                            // > Particles
                            // > Loop through each nearby player, afflict if possible
                        }

                        AilmentManager.afflictCheck( // TODO: remove this
                                event.getPlayer(),
                                AilmentCauseType.BLOCK,
                                Epidemic.instance().data().blockAilments.get(event.getBlock().getType()),
                                event.getPlayer(),
                                0,
                                event.getBlock(),
                                -1
                        );
                    }
                }
            }
        }
    }

}
