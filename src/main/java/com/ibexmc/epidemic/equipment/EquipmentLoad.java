package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.ConfigParser;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EquipmentLoad {
    private final File file;
    private ConfigParser configParser;
    private boolean exists = false;

    public EquipmentLoad(Epidemic instance, String filename) {
        String filePathString = Epidemic.instance().getDataFolder().getAbsolutePath() + File.separator + "equipment" + File.separator + filename;
        file = new File(filePathString);
        if (file.exists()) {
            exists = true;
            configParser = new ConfigParser(
                    Epidemic.instance(),
                    file
            );
        }
    }

    /**
     * Returns the file
     * @return File
     */
    public File file() { return file; }

    /**
     * Returns true if the file exists
     * @return If true, file exists
     */
    public boolean exists() { return exists; }

    /**
     * Returns the current ConfigParser
     * @return ConfigParser
     */
    public ConfigParser configParser() { return configParser; }

    /**
     * Returns the display name from the Equipment config
     * @param defaultName Default name
     * @return Display Name
     */
    public String name(String defaultName) {
        ConfigParser.ConfigReturn crName = configParser.getStringValue(
                "name",
                defaultName,
                false
        );
        if (crName.isSuccess()) {
            return StringFunctions.colorToString(crName.getString());
        } else {
            return defaultName;
        }
    }

    /**
     * Returns the lore from the Equipment config
     * @return Lore
     */
    public List<String> lore() {
        List<String> lore = new ArrayList<>();
        ConfigParser.ConfigReturn crLore = configParser.getStringList(
                "lore",
                false
        );
        if (crLore.isSuccess()) {
            lore.addAll(crLore.getStringList());
            lore = StringFunctions.colorStringList(lore);
        }
        return lore;
    }

    /**
     * Returns the item from the Equipment config with custom item name
     * @param customItemName Custom item name (ie. helmet.item or item)
     * @param defaultItem Default item
     * @return ItemStack for the Equipment
     */
    public ItemStack item(String customItemName, ItemStack defaultItem) {
        ItemStack item;
        ConfigParser.ConfigReturn crItem = configParser.getStringList(
                customItemName,
                false
        );
        if (crItem.isSuccess()) {
            StringBuilder itemString = new StringBuilder();
            for (String itemLine : crItem.getStringList()) {
                itemString.append(itemLine);
                itemString.append("\n");
            }
            item = InventoryFunctions.stringBlobToItem(itemString.toString());
            if (item == null) {
                item = defaultItem;
            }
        } else {
            item = defaultItem;
        }
        return item;
    }

    /**
     * Returns the item from the Equipment config with standard item name
     * @param defaultItem Default item
     * @return ItemStack for the Equipment
     */
    public ItemStack item(ItemStack defaultItem) {
        return item("item", defaultItem);
    }

    /**
     * Returns the material from the Equipment config with custom material name
     * @param customMaterialName Custom material name (ie. helmet.material or material)
     * @param defaultMaterial Default material
     * @return Material for the Equipment
     */
    public Material material(String customMaterialName, Material defaultMaterial) {
        ConfigParser.ConfigReturn crMaterial = configParser.getMaterialValue(
                customMaterialName,
                defaultMaterial,
                false
        );
        if (crMaterial.isSuccess()) {
            return crMaterial.getMaterial();
        } else {
            return defaultMaterial;
        }
    }

    /**
     * Returns the material from the Equipment config with standard material name
     * @param defaultMaterial Default material
     * @return Material for the Equipment
     */
    public Material material(Material defaultMaterial) {
        return material("material", defaultMaterial);
    }

    /**
     * Returns the Base64 Texture with a custom name
     * @param customBase64Name Custom Base 64 Texture name
     * @param defaultBase64 Default material
     * @return Base64 Texture
     */
    public String base64Texture(String customBase64Name, String defaultBase64) {
        ConfigParser.ConfigReturn crBase64 = configParser.getStringValue(
                customBase64Name,
                defaultBase64,
                false
        );
        if (crBase64.isSuccess()) {
            return crBase64.getString();
        } else {
            return defaultBase64;
        }
    }

    /**
     * Returns the Base64 Texture with standard name
     * @param defaultBase64 Default material
     * @return Base64 Texture
     */
    public String base64Texture(String defaultBase64) {
        return base64Texture("texture", defaultBase64);
    }


    /**
     * Returns the owner UID for the player head from the Equipment with custom owner name
     * @param customHeadUIDName Custom Head UID name (ie. helmet.head_uid or head_uid)
     * @return Owner UID
     */
    public UUID headUID(String customHeadUIDName) {
        ConfigParser.ConfigReturn crHeadUID = configParser.getStringValue(
                customHeadUIDName,
                "",
                false
        );
        if (crHeadUID.isSuccess()) {
            return StringFunctions.uuidFromString(crHeadUID.getString());
        } else {
            return null;
        }
    }

    /**
     * Returns the owner UID for the player head from the Equipment with standard owner name
     * @return Owner UID
     */
    public UUID headUID() {
        return headUID("head_uid");
    }

    /**
     * Returns the color from the Equipment
     * @param defaultColor Default color
     * @return Color
     */
    public Color color(Color defaultColor) {
        ConfigParser.ConfigReturn crPotionColor = configParser.getColor(
                "potion_color",
                defaultColor,
                false
        );
        if (crPotionColor.isSuccess()) {
            return crPotionColor.getColor();
        } else {
            return defaultColor;
        }
    }

    /**
     * Returns the item amount for the Equipment with custom amount name
     * @param customAmountName Custom Amount name (ie. helmet.amount or amount)
     * @param defaultAmount Default amount
     * @return Amount to apply to the ItemStack
     */
    public int amount(String customAmountName, int defaultAmount) {
        ConfigParser.ConfigReturn crAmount = configParser.getIntValue(
                customAmountName,
                defaultAmount,
                false
        );
        if (crAmount.isSuccess()) {
            return crAmount.getInt();
        } else {
            return defaultAmount;
        }
    }

    /**
     * Returns the item amount for the Equipment with standard amount name
     * @param defaultAmount Default amount
     * @return Amount to apply to the ItemStack
     */
    public int amount(int defaultAmount) {
        return amount("amount", defaultAmount);
    }

    /**
     * Returns the range for the Equipment
     * @param defaultRange Default range
     * @return Range in blocks
     */
    public int range(int defaultRange) {
        ConfigParser.ConfigReturn crRange = configParser.getIntValue(
                "range",
                defaultRange,
                false
        );
        if (crRange.isSuccess()) {
            return crRange.getInt();
        } else {
            return defaultRange;
        }
    }

    /**
     * Returns the Protection level for the Equipment
     * @param defaultProtection Default protection amount
     * @return Protection level
     */
    public int protection(int defaultProtection) {
        ConfigParser.ConfigReturn crProtection = configParser.getIntValue(
                "protection",
                defaultProtection,
                false
        );
        if (crProtection.isSuccess()) {
            return crProtection.getInt();
        } else {
            return defaultProtection;
        }
    }

    /**
     * Returns the chance of equipment being destroyed
     * @param defaultChance Default chance
     * @return Destroy Chance
     */
    public int destroyChance(int defaultChance) {
        ConfigParser.ConfigReturn crDestroyChance = configParser.getIntValue(
                "destroy_chance",
                defaultChance,
                false
        );
        if (crDestroyChance.isSuccess()) {
            return crDestroyChance.getInt();
        } else {
            return defaultChance;
        }
    }
}
