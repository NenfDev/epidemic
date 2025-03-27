package com.ibexmc.epidemic.ailments;

import java.sql.Timestamp;
import java.util.UUID;

public class Afflicted {

    private UUID uuid;
    private Ailment ailment;
    private int afflictDay;
    private long afflictTime;
    private Timestamp startTimestamp;
    private boolean secondaryApplied;

    public Afflicted(UUID uuid, Ailment ailment, int day, long time, Timestamp startTimestamp) {
        this.uuid = uuid;
        this.ailment = ailment;
        this.afflictDay = day;
        this.afflictTime = time;
        this.startTimestamp = startTimestamp;
    }

    public Afflicted(UUID uuid, Ailment ailment, int day, long time, Timestamp startTimestamp, boolean hasRelief, Timestamp reliefUntil) {
        this.uuid = uuid;
        this.ailment = ailment;
        this.afflictDay = day;
        this.afflictTime = time;
        this.startTimestamp = startTimestamp;
    }

    public Afflicted(UUID uuid, Ailment ailment, int day, long time, Timestamp startTimestamp, boolean hasRelief, Timestamp reliefUntil, boolean infected, boolean wasinfected) {
        this.uuid = uuid;
        this.ailment = ailment;
        this.afflictDay = day;
        this.afflictTime = time;
        this.startTimestamp = startTimestamp;
    }

    public Afflicted(UUID uuid, Ailment ailment, int day, long time, Timestamp startTimestamp, boolean secondaryApplied) {
        this.uuid = uuid;
        this.ailment = ailment;
        this.afflictDay = day;
        this.afflictTime = time;
        this.startTimestamp = startTimestamp;
        this.secondaryApplied = secondaryApplied;
    }


    /**
     * Gets the Affliction unique identifier
     * @return Affliction Unique Identifier
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the affliction unique identifier
     * @param uuid Affliction Unique Identifier
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the ailment for the affliction
     * @return Ailment
     */
    public Ailment getAilment() {
        return ailment;
    }

    /**
     * Sets the ailment for the affliction
     * @param ailment Ailment to set
     */
    public void setAilment(Ailment ailment) {
        this.ailment = ailment;
    }

    /**
     * Gets the day the player was afflicted
     * @return Affliction Day
     */
    public int getAfflictDay() {
        return afflictDay;
    }

    /**
     * Sets the day the player was afflicted
     * @param afflictDay Affliction Day
     */
    public void setAfflictDay(int afflictDay) {
        this.afflictDay = afflictDay;
    }

    /**
     * Gets the Time the player was afflicted
     * @return Affliction Time
     */
    public long getAfflictTime() {
        return afflictTime;
    }

    /**
     * Sets the Time the player was afflicted
     * @param afflictTime Affliction Time
     */
    public void setAfflictTime(long afflictTime) {
        this.afflictTime = afflictTime;
    }

    /**
     * Gets the timestamp when the affliction began
     * @return Start Timestamp
     */
    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Checks if a secondary ailmetn is applied
     * @return
     */
    public boolean isSecondaryApplied() {
        return secondaryApplied;
    }

    /**
     * Sets the flag for if a secondary affliction is applied
     * @param secondaryApplied
     */
    public void setSecondaryApplied(boolean secondaryApplied) {
        this.secondaryApplied = secondaryApplied;
    }

}
