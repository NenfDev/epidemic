package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipePosition;

import com.ibexmc.epidemic.util.functions.FileFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class InfectedSample implements IEquipment {

    private final String key = "infected_sample";
    private String name = "&cInfected Sample";
    private List<String> lore = StringFunctions.stringToLore("&fAn infected sample");
    private ItemStack item;
    private int amount = 1;

    /**
     * Constructor
     */
    public InfectedSample() {
        load();
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
     * Gets the display name of the equipment
     *
     * @return Display name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the lore for the equipment
     *
     * @return String list containing the lore
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets the infected sample
     *
     * @return Infected Sample ItemStack
     */
    @Override
    public ItemStack get() {
        ItemStack sample = this.item.clone();
        ItemMeta itemMeta = sample.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            sample.setItemMeta(itemMeta);
        }
        if (!EquipmentManager.hasEquipmentKey(sample)) {
            Epidemic.instance().persistentData().setString(sample, PersistentData.Key.EQUIPMENT, this.key);
        }
        return sample;
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
     * Returns a null recipe (Item is not craftable)
     * @return Null Recipe
     */
    @Override
    public Map<RecipePosition, RecipeItem> recipe() {
        return null;
    }

    /**
     * Loads the arrow from file
     */
    private void load() {
        try {
            String fileName = "infected_sample.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                this.item = loader.item(this.defaultItem());
                this.item.setAmount(this.amount);
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/infected_sample.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "InfectedSample.load.001",
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
        ItemStack defaultItem = new ItemStack(Material.ROTTEN_FLESH, this.amount);
        ItemMeta itemMeta = defaultItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.name);
            itemMeta.setLore(this.lore);
            defaultItem.setItemMeta(itemMeta);
        }
        return defaultItem;
    }
}
