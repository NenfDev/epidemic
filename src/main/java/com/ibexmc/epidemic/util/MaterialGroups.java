package com.ibexmc.epidemic.util;

public class MaterialGroups {
    /**
     * Checks if the material name being passed is water cauldron
     * @param materialName Material Name
     * @return If true, material name is water cauldron
     */
    public static boolean isWaterCauldron(String materialName) {
        switch (materialName.toUpperCase()) {
            case "CAULDRON":
            case "WATER_CAULDRON":
            case "POWDER_SNOW_CAULDRON":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the material name being passed is a weapon
     * @param materialName Material Name
     * @return If true, material name is a weapon
     */
    public static boolean isWeapon(String materialName) {
        switch (materialName) {
            case "WOODEN_SWORD":
            case "STONE_SWORD":
            case "GOLDEN_SWORD":
            case "IRON_SWORD":
            case "DIAMOND_SWORD":
            case "NETHERITE_SWORD":
            case "WOODEN_AXE":
            case "STONE_AXE":
            case "GOLDEN_AXE":
            case "IRON_AXE":
            case "DIAMOND_AXE":
            case "NETHERITE_AXE":
            case "ARROW":
            case "SPECTRAL_ARROW":
            case "TIPPED_ARROW":
            case "TRIDENT":
            case "STICK":
                return true;
            default:
                return false;
        }
    }
}