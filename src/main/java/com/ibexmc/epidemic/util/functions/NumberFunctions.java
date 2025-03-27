package com.ibexmc.epidemic.util.functions;


import java.util.Random;

public class NumberFunctions {
    /**
     * Checks if the provided string is an int
     * @param value String value to check
     * @return If true, is an int
     */
    public static boolean isInt(String value) {
        try
        {
            Integer.parseInt(value.trim());
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided string is a double
     * @param value String value to check
     * @return If true, is a double
     */
    public static boolean isDouble(String value) {
        try
        {
            Double.parseDouble(value.trim());
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided string is a long
     * @param value String value to check
     * @return If true, is a long
     */
    public static boolean isLong(String value) {
        try
        {
            Long.parseLong(value.trim());
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Converts a string value into an int
     * @param value String value to convert
     * @return Integer value from string, 0 if unable to convert
     */
    public static int stringToInt(String value) {
        int returnVal = 0;
        try
        {
            returnVal = Integer.parseInt(value.trim());
        }
        catch(NumberFormatException e)
        {
            // Unable to parse
        }
        return returnVal;
    }
    /**
     * Converts a string value into a double
     * @param value String value to convert
     * @return Double value from string, 0 if unable to convert
     */
    public static double stringToDouble(String value) {
        double returnVal = 0d;
        try
        {
            returnVal = Double.parseDouble(value.trim());
        }
        catch(NumberFormatException e)
        {
            // Unable to parse
        }
        return returnVal;
    }

    /**
     * Converts a string value into a long
     * @param value String value to convert
     * @return long value from string, 0 if unable to convert
     */
    public static long stringToLong(String value) {
        long returnVal = 0;
        try
        {
            returnVal = Long.parseLong(value.trim());
        }
        catch(NumberFormatException e)
        {
            // Unable to parse
        }
        return returnVal;
    }

    /**
     * Returns a random number between min and max provided
     * @param min Minimum number in random number generation
     * @param max Maximum number in random number generation
     * @return Random number
     */
    public static int random(int min, int max) {
        //Logging.debug("NumberFunctions", "random", "Min = " + min + " Max = " + max);
        int lower = Math.min(min, max);
        int upper = Math.max(min, max);
        if (min == max) {
            return min;
        }
        Random random = new Random();
        return random.nextInt(upper - lower) + lower;
    }

    /**
     * Returns a random number between 1 and max provided
     * @param max Maximum number in random number generation
     * @return Random number
     */
    public static int random(int max) {
        int min = 0;
        if (min == max) {
            return min;
        }
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
