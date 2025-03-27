package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.functions.FileFunctions;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;

public class InfectedSplashPotion implements IEquipment {
    private final String key = "infected_splash_potion";
    private String name = "&bInfected Splash Potion";
    private List<String> lore = StringFunctions.stringToLore("&fAn infected splash potion");
    private Color potionColor;
    private int range = 3;
    private int amount = 1;
    private ItemStack item;
    private Map<RecipePosition, RecipeItem> recipe;

    /**
     * Constructor
     */
    public InfectedSplashPotion() {
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
     * Gets the infected splash potion
     *
     * @return Splash Potion ItemStack
     */
    @Override
    public ItemStack get() {
        ItemStack splashPotion = this.item.clone();
        ItemMeta itemMeta = splashPotion.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setDisplayName(StringFunctions.colorToString(this.name));
                potionMeta.setLore(StringFunctions.colorStringList(this.lore));
                potionMeta.setBasePotionData(new PotionData(PotionType.AWKWARD));
                potionMeta.setColor(potionColor);
                splashPotion.setItemMeta(potionMeta);
            } else {
                itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
                itemMeta.setLore(StringFunctions.colorStringList(this.lore));
                splashPotion.setItemMeta(itemMeta);
            }
        }
        if (!EquipmentManager.hasEquipmentKey(splashPotion)) {
            Epidemic.instance().persistentData().setString(splashPotion, PersistentData.Key.EQUIPMENT, this.key);
        }
        return splashPotion;
    }

    /**
     * Checks if the equipment is throwable
     * @return If true, equipment is throwable
     */
    @Override
    public boolean throwable() {
        return true;
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
     * Returns the uninfected version of the splash potion
     * If we create an UninfectedArrow we'd return that instead of just SPLASH_POTION
     * @param amount Amount of potions to return
     * @return Splash Potions
     */
    public ItemStack getUninfected(int amount) {
        ItemStack item = defaultItem();
        item.setAmount(amount);
        return item;
    }

    /**
     * Returns the range in blocks of the splash potion
     * @return Range
     */
    public int range() {
        return this.range;
    }

    /**
     * Loads the splash potion from file
     */
    private void load() {
        try {
            String fileName = "infected_splash_potion.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                this.item = loader.item(defaultItem()); // MUST be a splash potion
                this.item.setAmount(this.amount);
                this.potionColor = loader.color(Color.WHITE);
                this.range = loader.range(3);
                this.recipe = RecipeManager.recipe(loader.file());
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/infected_splash_potion.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "InfectedSplashPotion.load.001",
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
        ItemStack defaultItem = InventoryFunctions.stringBlobToItem(
                "i:\n" +
                        "  ==: org.bukkit.inventory.ItemStack\n" +
                        "  v: 1519\n" +
                        "  type: SPLASH_POTION\n" +
                        "  meta:\n" +
                        "    ==: ItemMeta\n" +
                        "    meta-type: POTION\n" +
                        "    potion-type: minecraft:water"
        );
        if (defaultItem != null) {
            defaultItem.setAmount(this.amount);
            ItemMeta itemMeta = defaultItem.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(this.name);
                itemMeta.setLore(this.lore);
                defaultItem.setItemMeta(itemMeta);
            }
        }
        return defaultItem;
    }
}
