package com.ibexmc.epidemic.api.events;


import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class AfflictionEvent extends EpidemicEvent implements Cancellable {
    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Ailment ailment; // Ailment that is being applied

    /**
     * Called when a player is afflicted with an ailment
     * Cancelling the event will prevent the player being afflicted
     * @param player Player that is being afflicted
     * @param ailment Ailment that the player is receiving
     */
    public AfflictionEvent(Player player, Ailment ailment) {

        super(player);

        if (player == null) {
            this.cancelled = true;
            this.cancelReason = "Player is a required value";
            return;
        }
        if (ailment == null) {
            this.cancelled = true;
            this.cancelReason = "Ailment is a required value";
            return;
        } else {
            this.ailment = ailment;
        }

        Logging.debug(
                "AfflictionEvent",
                "Event Fired",
                "Player: " + player + " Ailment: " + ailment.getDisplayName()
        );

        this.cancelled = false;
        this.cancelReason = "";

    }

    /**
     * Gets the ailment being applied to the player
     * @return Ailment that is being applied
     */
    public Ailment getAilment() {
        return ailment;
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
