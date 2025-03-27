package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.Epidemic;
import org.bukkit.Server;
import org.bukkit.World;

import java.sql.Timestamp;
import java.util.Calendar;

public class TimeFunctions {

    public enum TimeOfDay {
        DAY,
        NOONISH,
        NIGHT
    }

    /**
     * Gets the current timestamp
     * @return Current timestamp
     */
    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Checks if a timestamp is in the future
     * @param timestamp Timestamp to check
     * @return If true, timestamp is in the future
     */
    public static boolean inFuture(Timestamp timestamp) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int res = timestamp.compareTo(now);
        return (res > 0);
    }

    /**
     * Adds a number of seconds to a timestamp
     * @param timestamp Timestamp to add to
     * @param seconds Number of seconds to add
     * @return Timestamp with added seconds
     */
    public static Timestamp addSeconds(Timestamp timestamp, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.SECOND, seconds);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Gets the number of seconds until now for the timestamp provided.
     * If the number returns a negative number, now is in the past.
     * @param timestamp Timestamp to check
     * @return Number of seconds until now
     */
    public static int secondsUntilNow(Timestamp timestamp) {
        // Returns number of seconds the timestamp is until now
        // If the number returns a negative number, now is
        // in the past
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long milliseconds = now.getTime() - timestamp.getTime() ;
        return (int) milliseconds / 1000;
    }

    /**
     * Gets number of seconds from now for the provided timestamp
     * @param timestamp Timestamp to check
     * @return Number of seconds from now
     */
    public static int secondsFromNow(Timestamp timestamp) {
        return (secondsUntilNow(timestamp) * -1);
    }

    /**
     * Gets the time of day in the world
     * @param world World to check
     * @return TimeOfDay entry (DAY, NOONISH, NIGHT)
     */
    public static TimeOfDay getTimeOfDay(World world) {
        Server server = Epidemic.instance().getServer();
        long time = world.getTime();
        if (time >= 5950 && time < 8000) {
            return TimeOfDay.NOONISH;
        }
        if (time <= 12300 || time >= 23850) {
            return TimeOfDay.DAY;
        } else {
            return TimeOfDay.NIGHT;
        }
    }

}
