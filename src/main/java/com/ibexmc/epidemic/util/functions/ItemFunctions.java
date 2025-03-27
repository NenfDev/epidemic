package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.SpigotVersion;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemFunctions {

    /**
     * Gets the number of uses of a Remedy from the PersistentDataContainer in the ItemMeta
     * @param itemMeta ItemMeta to check
     * @return Number of uses
     */
    public static int getUses(ItemMeta itemMeta) {
        int uses = 0;
        NamespacedKey useKey = new NamespacedKey(Epidemic.instance(), "epi_current_uses");
        if (itemMeta != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(useKey, PersistentDataType.INTEGER)) {
                uses = container.get(useKey, PersistentDataType.INTEGER);
            }
        }
        return uses;
    }

    /**
     * Gets the maximum number of uses of a Remedy from the PersistentDataContainer in the ItemMeta
     * @param itemMeta ItemMeta to check
     * @return Maximum number of uses
     */
    public static int getMaxUses(ItemMeta itemMeta) {
        int maxUses = 0;
        NamespacedKey useKey = new NamespacedKey(Epidemic.instance(), "epi_max_uses");
        if (itemMeta != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(useKey, PersistentDataType.INTEGER)) {
                maxUses = container.get(useKey, PersistentDataType.INTEGER);
            }
        }
        return maxUses;
    }

    /**
     * Sets the number of uses for a Remedy, modifies the PersistentDataContainer value in the ItemMeta
     * @param itemMeta ItemMeta to update
     * @param amount Amount of uses
     */
    public static void setUses(ItemMeta itemMeta, int amount) {
        int uses = 0;
        NamespacedKey useKey = new NamespacedKey(Epidemic.instance(), "epi_current_uses");
        if (itemMeta != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(useKey, PersistentDataType.INTEGER, amount);
        }
    }

    /**
     * Gets the Material from its name.  Returns AIR if not valid.
     * @param materialName Material name to lookup
     * @return Material from name, Material.AIR if not valid.
     */
    public static Material materialFromName(String materialName) {
        Material mat;
        if (materialName != null) {
            try {
                mat = Material.getMaterial(materialName);
            } catch (Exception ex) {
                Error.save(
                        "Provide.Materials.materialFromName.001",
                        "Provide.Materials",
                        "materialFromName(String)",
                        "Name does not match a material",
                        "Name: " + materialName,
                        Error.Severity.WARN,
                        ex.getStackTrace()
                );
                mat = Material.AIR;
            }
        } else{
            mat = Material.AIR;
        }
        return mat;
    }

    /**
     * Gets the empty food container from a food material
     * @param food Food material to lookup
     * @return Empty food container material
     */
    public static Material emptyFoodContainer(Material food) {
        if (food == null) {
            return Material.AIR;
        }
        Material returnMaterial = Material.AIR;
        if (SpigotVersion.is113Safe()) {
            switch (food) {
                case MUSHROOM_STEW:
                case RABBIT_STEW:
                case BEETROOT_SOUP:
                    returnMaterial = Material.BOWL;
                    break;
                default:
                    // Do nothing, we are already set to air
                    break;
            }
        }
        if (SpigotVersion.is114Safe()) {
            if (food.equals(Material.SUSPICIOUS_STEW)) {
                returnMaterial = Material.BOWL;
            }
        }
        if (SpigotVersion.is115Safe()) {
            if (food.equals(Material.HONEY_BOTTLE)) {
                returnMaterial = Material.GLASS_BOTTLE;
            }
        }
        return returnMaterial;
    }

    /**
     * Converts an ItemStack into a String blob field, which maintains all the settings for that
     * item completely.
     * @param itemStack ItemStack to convert
     * @return String Blob of the ItemStack
     */
    public static String itemToStringBlob(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    /**
     * Converts a String blob field into an ItemStack
     * @param stringBlob
     * @return
     */
    public static ItemStack stringBlobToItem(String stringBlob) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringBlob);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }

    /**
     * Tries to get the ItemFlag from the name provided
     * If not found, exception is thrown, and we return null
     * @param name Name of ItemFlag
     * @return ItemFlag, if not found, returns null
     */
    public static ItemFlag itemFlagFromName(String name) {
        try {
            return ItemFlag.valueOf(name);
        } catch (Exception ex) {
            return null;
        }
    }
}
