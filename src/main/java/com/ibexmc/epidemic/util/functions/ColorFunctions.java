package com.ibexmc.epidemic.util.functions;

import org.bukkit.Color;

public class ColorFunctions {
    /**
     * Gets a color by name
     * @param name Color to lookup
     * @return Color requested, WHITE if not found
     */
    public static Color colorFromName(String name) {
        Color ret;
        switch (name)
        {
            case "AQUA" :
                ret = Color.AQUA;
                break;
            case "BLACK" :
                ret = Color.BLACK;
                break;
            case "BLUE" :
                ret = Color.BLUE;
                break;
            case "FUCHSIA" :
                ret = Color.FUCHSIA;
                break;
            case "GRAY" :
                ret = Color.GRAY;
                break;
            case "GREEN" :
                ret = Color.GREEN;
                break;
            case "LIME" :
                ret = Color.LIME;
                break;
            case "MAROON" :
                ret = Color.MAROON;
                break;
            case "NAVY" :
                ret = Color.NAVY;
                break;
            case "OLIVE" :
                ret = Color.OLIVE;
                break;
            case "ORANGE" :
                ret = Color.ORANGE;
                break;
            case "PURPLE" :
                ret = Color.PURPLE;
                break;
            case "SILVER" :
                ret = Color.SILVER;
                break;
            case "TEAL" :
                ret = Color.TEAL;
                break;
            case "YELLOW" :
                ret = Color.YELLOW;
                break;
            case "WHITE" :
            default:
                ret = Color.WHITE;
                break;
        }
        return ret;
    }
}
