package com.ibexmc.epidemic.events.player;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.equipment.EquipmentManager;
import com.ibexmc.epidemic.remedy.RemedyManager;
import com.ibexmc.epidemic.thirst.ThirstManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {

    /**
     * Used any time there is player interaction (left/right clicking etc.).  Used for remedy usage,
     * and water source usage.
     * @param event PlayerInteractEvent
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getPlayer() == null) {
            Logging.debug(
                    "PlayerInteract",
                    "onInteract",
                    "Player is null"
            );
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.useInteractedBlock().equals(Event.Result.DENY) || event.useItemInHand().equals(Event.Result.DENY)) {
                return;
            }
        }

        Logging.debug(
                "PlayerInteract",
                "onInteract",
                "Running Remedy code"
        );

        // Remedy
        PlayerInteractResult remedyResult = RemedyManager.onPlayerInteract(event);
        event.setCancelled(remedyResult.isCancelled());
        event.setUseInteractedBlock(remedyResult.useInteractedBlock());
        event.setUseItemInHand(remedyResult.useItemInHand());
        if (remedyResult.isCancelled()) {
            Logging.debug(
                    "PlayerInteract",
                    "onInteract",
                    "Event cancelled because of remedy"
            );
            return;
        }

        // Equipment
        if (!Epidemic.instance().config.isDisableEpidemicEquipment()) {
            PlayerInteractResult equipmentResult = EquipmentManager.onPlayerInteract(event);
            event.setCancelled(equipmentResult.isCancelled());
            event.setUseInteractedBlock(equipmentResult.useInteractedBlock());
            event.setUseItemInHand(equipmentResult.useItemInHand());
            if (equipmentResult.isCancelled()) {
                Logging.debug(
                        "PlayerInteract",
                        "onInteract",
                        "Event cancelled because of equipment"
                );
                return;
            }
        }

        Logging.debug(
                "PlayerInteract",
                "onInteract",
                "Running Water source code"
        );

        // Water Source
        PlayerInteractResult thirstResult = ThirstManager.onPlayerInteract(event);
        event.setCancelled(thirstResult.isCancelled());
        event.setUseInteractedBlock(thirstResult.useInteractedBlock());
        event.setUseItemInHand(thirstResult.useItemInHand());
        if (thirstResult.isCancelled()) {
            Logging.debug(
                    "PlayerInteract",
                    "onInteract",
                    "Event cancelled because of thirst"
            );
            return;
        }


        Logging.debug(
                "PlayerInteract",
                "onInteract",
                "End of PlayerInteract found - " +
                        "Use item in hand = " + event.useItemInHand() + " - " +
                        "Use interacted block = " + event.useInteractedBlock()
        );


    }
    public static class PlayerInteractResult {
        private boolean cancelled = false;
        private Event.Result useInteractedBlock = Event.Result.ALLOW;
        private Event.Result useItemInHand = Event.Result.ALLOW;

        public PlayerInteractResult(boolean cancelled, Event.Result useInteractedBlock, Event.Result useItemInHand) {
            this.cancelled = cancelled;
            this.useInteractedBlock = useInteractedBlock;
            this.useItemInHand = useItemInHand;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public Event.Result useInteractedBlock() {
            return useInteractedBlock;
        }

        public Event.Result useItemInHand() {
            return useItemInHand;
        }
    }
}
