package com.ibexmc.epidemic.util;

import org.bukkit.Bukkit;

public class SpigotVersion {
    /**
     * Checks if the current Spigot version is 1.13.x
     * @return If true, it is the current version
     */
    private static boolean is113() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_13");
    }

    /**
     * Checks if the current Spigot version is 1.14.x
     * @return If true, it is the current version
     */
    private static boolean is114() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_14");
    }

    /**
     * Checks if the current Spigot version is 1.15.x
     * @return If true, it is the current version
     */
    private static boolean is115() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_15");
    }

    /**
     * Checks if the current Spigot version is 1.16.x
     * @return If true, it is the current version
     */
    private static boolean is116() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_16");
    }

    /**
     * Checks if the current Spigot version is 1.17.x
     * @return If true, it is the current version
     */
    private static boolean is117() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_17");
    }

    /**
     * Checks if the current Spigot version is 1.18.x
     * @return If true, it is the current version
     */
    private static boolean is118() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_18");
    }

    /**
     * Checks if the current Spigot version is 1.19.x
     * @return If true, it is the current version
     */
    private static boolean is119() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("v1_19");
    }


    /**
     * Checks if the current Spigot version is 1.13.x safe
     * @return If true, it is version safe
     */
    public static boolean is113Safe() {
        return is113() || is114() || is115() || is116() || is117() || is118() || is119();
    }

    /**
     * Checks if the current Spigot version is 1.14.x safe
     * @return If true, it is version safe
     */
    public static boolean is114Safe() {
        return is114() || is115() || is116() || is117() || is118() || is119();
    }

    /**
     * Checks if the current Spigot version is 1.15.x safe
     * @return If true, it is version safe
     */
    public static boolean is115Safe() {
        return is115() || is116() || is117() || is118() || is119();
    }

    /**
     * Checks if the current Spigot version is 1.16.x safe
     * @return If true, it is version safe
     */
    public static boolean is116Safe() {
        return is116() || is117() || is118() || is119();
    }

    /**
     * Checks if the current Spigot version is 1.17.x safe
     * @return If true, it is version safe
     */
    public static boolean is117Safe() {
        return is117() || is118() || is119();
    }

    /**
     * Checks if the current Spigot version is 1.18.x safe
     * @return If true, it is version safe
     */
    public static boolean is118Safe() {
        return is118() || is119();
    }

    /**
     * Checks if the current Spigot version is 1.19.x safe
     * @return If true, it is version safe
     */
    public static boolean is119Safe() {
        return is119();
    }

}
