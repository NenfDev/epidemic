package com.ibexmc.epidemic.util.functions;

public class MathUtil {
    /**
     * Checks if the specified value is between min/max values
     * @param value Value to check
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return If true, value is in the range
     */
    public static boolean between(double value, double min, double max) {
        if (value >= min && value <= max) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Rounds a double value to a precision
     * @param value Value to round
     * @param precision Precision
     * @return Rounded double value
     */
    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
