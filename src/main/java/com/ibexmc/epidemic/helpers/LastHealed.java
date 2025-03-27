package com.ibexmc.epidemic.helpers;

import java.sql.Timestamp;

public class LastHealed {

    //region Objects
    private String ailmentKey = "";
    private Timestamp timestamp = null;
    //endregion
    //Constructors
    public LastHealed(String ailmentKey, Timestamp timestamp) {
        this.ailmentKey = ailmentKey;
        this.timestamp = timestamp;
    }
    //endregion
    //region Getters

    /**
     * Gets the Ailment internal name for this LastHealed entry
     * @return Ailment internal name
     */
    public String getAilmentKey() {
        return ailmentKey;
    }

    /**
     * Gets the timestamp the player was last healed of this ailment
     * @return Last healed timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }
    //endregion
}
