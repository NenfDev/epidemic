package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.functions.FileFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class InfectedArrow implements IEquipment {

    private final String key = "infected_arrow";
    private String name = "&bInfected Arrow";
    private List<String> lore = StringFunctions.stringToLore("&fAn infected arrow");
    private ItemStack item;
    private int amount = 1;
    private Map<RecipePosition, RecipeItem> recipe;

    /**
     * Constructor
     */
    public InfectedArrow() {
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
     * Gets the infected arrow
     *
     * @return Arrow ItemStack
     */
    @Override
    public ItemStack get() {
        ItemStack arrow = this.item.clone();
        ItemMeta itemMeta = arrow.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            arrow.setItemMeta(itemMeta);
        }
        if (!EquipmentManager.hasEquipmentKey(arrow)) {
            Epidemic.instance().persistentData().setString(arrow, PersistentData.Key.EQUIPMENT, this.key);
        }
        return arrow;
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
     * Returns the uninfected version of the arrows
     * If we create an UninfectedArrow we'd return that instead of just ARROW
     * @param amount Amount of arrows to return
     * @return Arrows
     */
    public ItemStack getUninfected(int amount) {
        return new ItemStack(Material.ARROW, amount);
    }

    /**
     * Loads the arrow from file
     */
    private void load() {
        try {

            String fileName = "infected_arrow.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(4);
                this.item = loader.item(defaultItem()); // MUST be a tipped arrow
                this.item.setAmount(this.amount);
                this.recipe = RecipeManager.recipe(loader.file());
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/infected_arrow.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "InfectedArrow.load.001",
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
        ItemStack defaultItem = new ItemStack(Material.TIPPED_ARROW, this.amount);
        ItemMeta itemMeta = defaultItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.name);
            itemMeta.setLore(this.lore);
            defaultItem.setItemMeta(itemMeta);
        }
        return defaultItem;
    }

}
