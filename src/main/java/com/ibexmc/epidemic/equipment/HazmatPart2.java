package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.functions.FileFunctions;
import com.ibexmc.epidemic.util.functions.PlayerFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class HazmatPart2 implements IEquipment {

    private final String key = "hazmat_part2";
    private String name = "&bHazmat Suit Material";
    private List<String> lore = StringFunctions.stringToLore("&fSturdy germ proof material for a Hazmat Suit");
    private ItemStack item;
    private int amount = 1;
    private Map<RecipePosition, RecipeItem> recipe;

    /**
     * Constructor
     */
    public HazmatPart2() {
        load();
        Epidemic.instance().gameData().recipes().put(this.key(), this.recipe());
    }

    /**
     * Gets the key
     * @return Key
     */
    @Override
    public String key() {
        return this.key;
    }

    /**
     * Gets the display name of the Hazmat suit material
     *
     * @return Display name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the lore for the Hazmat suit material
     *
     * @return String list containing the lore
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets the Hazmat suit material
     *
     * @return Hazmat suit material ItemStack
     */
    @Override
    public ItemStack get() {
        ItemStack mask = this.item.clone();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
        itemMeta.setLore(StringFunctions.colorStringList(this.lore));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (!EquipmentManager.hasEquipmentKey(mask)) {
            Epidemic.instance().persistentData().setString(itemMeta, PersistentData.Key.EQUIPMENT, this.key);
        }
        mask.setItemMeta(itemMeta);
        return mask;
    }

    /**
     * Checks if the equipment is throwable
     * @return If true, equipment is throwable
     */
    @Override
    public boolean throwable() {
        return false;
    }

    /**
     * Gets the Recipe for this equipment
     * @return Recipe
     */
    @Override
    public Map<RecipePosition, RecipeItem> recipe() {
        return this.recipe;
    }

    /**
     * Loads the gas mask from file
     */
    private void load() {
        try {
            String fileName = "hazmat_part2.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                this.item = loader.item(null);
                if (this.item == null) {
                    Material material = loader.material(Material.AIR);
                    if (material == Material.AIR) {
                        item = this.defaultItem();
                    } else {
                        item = new ItemStack(material, 1);
                        if (material == Material.PLAYER_HEAD) {
                            UUID headUID = loader.headUID();
                            if (headUID != null) {
                                item = PlayerFunctions.head(headUID);
                            }
                        }
                    }
                }
                this.item.setAmount(this.amount);
                this.recipe = RecipeManager.recipe(loader.file());
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/hazmat_part2.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "HazmatPart2.load.001",
                    "Epidemic",
                    "load()",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Gets the default item
     * @return Default item
     */
    private ItemStack defaultItem() {
        ItemStack defaultItem = new ItemStack(Material.YELLOW_CARPET, this.amount);
        ItemMeta itemMeta = defaultItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.name);
            itemMeta.setLore(this.lore);
            defaultItem.setItemMeta(itemMeta);
        }
        return defaultItem;
    }

}