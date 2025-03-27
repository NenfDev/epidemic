package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import com.ibexmc.epidemic.util.Logging;

import java.util.*;

public class StringFunctions {
    /**
     * stringToLore(String)
     * Converts a string to a list which can be used for Lore - New lines are indicated with a | character
     * @param loreString String to convert
     * @return List which can be used for a lore
     */
    public static List<String> stringToLore(String loreString) {
        String[] new_lore = loreString.split("\\|");
        List<String> newLore = new ArrayList<>(Arrays.asList(new_lore));
        return newLore;
    }

    /**
     * Converts a string list Lore to a | delimited string
     * @param lore Lore to convert
     * @return Lore string (| delimited)
     */
    public static String loreToString(List<String> lore) {
        StringBuilder returnValue = new StringBuilder();
        if (lore != null) {
            if (lore.size() > 0) {
                for (String loreLine : lore) {
                    returnValue.append(loreLine).append("|");
                }
                returnValue = new StringBuilder((returnValue + "!").replace("|!", ""));
            }
        }
        return returnValue.toString();
    }

    /**
     * stringToMaterial(String)
     * Converts a string value to the appropriate Minecraft Material.  If invalid, returns AIR
     * @param materialName String value of the material.  Must match Material exactly
     * @return Material based on name provided, returns AIR if invalid
     */
    public static Material stringToMaterial(String materialName)
    {
        Material mat;

        if (materialName == null)
        {
            materialName = "<null>";
        }
        try
        {
            mat = Material.getMaterial(materialName.toUpperCase());
        }
        catch (Exception ex)
        {
            Logging.log("StringFunctions.stringToMaterial", ChatColor.RED + "Unable to get Material from Name: " + materialName + " - " + ex.getMessage());
            mat = Material.AIR;
        }
        return mat;
    }

    /**
     * uuidFromString(String)
     * Converts a string value to a UUID.  Returns null if invalid
     * @param uuid String value of the UUID
     * @return UUID
     */
    public static UUID uuidFromString(String uuid) {
        return UUID.fromString(uuid);
    }

    /**
     * Colorizes a string
     * @param input String to colorize
     * @return Colorized string
     */
    public static String colorToString(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * Loops through all lines in the provided string list and applies color to them.
     * @param input String list to color
     * @return Colored string list
     */
    public static List<String> colorStringList(List<String> input) {
        if (input == null) {
            return null;
        }
        List<String> returnList = new ArrayList<>();
        for (String line : input) {
            returnList.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return returnList;
    }

    /**
     * Adds a line to a string list
     * @param list List to add to
     * @param input Line to add
     * @return String list
     */
    public static List<String> addLineToStringList(List<String> list, String input) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(ChatColor.translateAlternateColorCodes('&', input));
        return list;
    }

    /**
     * Converts a string into a NamespacedKey
     * @param input String to convert
     * @return NamespacedKey with original input as its key
     */
    public static NamespacedKey stringToKey(String input) {
        return new NamespacedKey(Epidemic.instance(), input); // Create a new named key for the item
    }

    /**
     * Gets the item uses line for reusable remedies
     * @param uses Number of uses
     * @param usesMax Maximum number of uses
     * @return Item Uses line for reusable remedies
     */
    public static String getItemUsesLine(int uses, int usesMax) {
        return "■ &f" + uses + "/" + usesMax + Locale.localeText(
                "uses_remaining",
                " remaining",
                null
        );
    }

    /**
     * Gets the arguments into a Map from a string array
     * @param args String array to convert
     * @return Map of Integer and String.  Integer is position, String is argument
     */
    public static Map<Integer, String> getArguments(String[] args) {
        Map<Integer, String> arguments = new HashMap<>();
        int argumentCount = 0;
        if (args.length > 0) // Check to make sure we have some arguments
        {
            for (Object o : args) {
                arguments.put(argumentCount, o.toString());
                argumentCount++;
            }
        }
        return arguments;
    }

    /**
     * Performs a null check and replaces with the default
     * @param text Text to check for null
     * @param defaultText Default to return if text is null.  If null, sets to blank string
     * @return Non-null string
     */
    public static String isNull(String text, String defaultText) {
        if (defaultText == null) {
            defaultText = "";
        }
        if (text != null) {
            return text;
        } else {
            return defaultText;
        }
    }

    /**
     * Checks if a string is null or empty
     * @param text String to check
     * @return If true, string is null or empty
     */
    public static boolean isNullOrEmpty(String text) {
        if (text != null) {
            if (text.length() > 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * Converts & color codes to color
     * @param input String to colorize
     * @return Colorized string
     */
    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

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

    public static boolean isUuid(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
