package com.ibexmc.epidemic.scheduled;

import com.ibexmc.epidemic.Epidemic;

public class Cleanup {
    /**
     * Called by the cleanup scheduler.
     * Cleans up old entries
     */
    public static void Run() {
        Epidemic.instance().data().clearOldSymptomRelief();
    }
}
