package com.ibexmc.epidemic.api.events;

import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class RemedyEvent extends EpidemicEvent implements Cancellable {
    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Remedy remedy; // Remedy being applied

    /**
     * Called when a player uses a Remedy.  Cancelling the event will
     * stop the remedy being used
     * @param player Player that is using the Remedy
     * @param remedy Remedy being applied
     */
    public RemedyEvent(Player player, Remedy remedy) {

        super(player);

        if (player == null) {
            this.cancelled = true;
            this.cancelReason = "Player is a required value";
            return;
        }
        if (remedy == null) {
            this.cancelled = true;
            this.cancelReason = "Remedy is a required value";
            return;
        } else {
            this.remedy = remedy;
        }

        Logging.debug(
                "RemedyEvent",
                "Event Fired",
                "Player: " + player + " Remedy: " + remedy.getDisplayName()
        );

        this.cancelled = false;
        this.cancelReason = "";

    }

    /**
     * Gets the remedy currently being applied
     * @return Remedy being applied
     */
    public Remedy getRemedy() {
        return remedy;
    }

    /**
     * Gets the cancellation reason
     * @return Free-text cancellation reason
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * Sets the cancellation reason
     * @param cancelReason Reason to cancel the event
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    /**
     * Gets the cancellation flag for the event
     * @return If true, prevents the remedy being applied
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation flag for the event
     * @param cancel If true, cancel the event
     */
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

}
