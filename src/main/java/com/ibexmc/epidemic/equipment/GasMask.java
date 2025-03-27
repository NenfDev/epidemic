package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.SkullCreator;
import com.ibexmc.epidemic.util.functions.FileFunctions;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import com.ibexmc.epidemic.util.functions.PlayerFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class GasMask implements IEquipment {

    private final String key = "gas_mask";
    private String name = "&bGas Mask";
    private final String defaultBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAzNjJmY2JmMGRjMzEyMDk0ZmVjNDQ1NjRkYWJjYzczNzNlNTVhMDFkYmFkMWZhY2I2ZTg3YjBmMzkzYmQyMSJ9fX0=";
    private List<String> lore = StringFunctions.stringToLore("&fA gas mask which filters out harmful particles");
    private ItemStack item;
    private int amount = 1;
    private int protection = 25000;
    private Map<RecipePosition, RecipeItem> recipe;

    /**
     * Constructor
     */
    public GasMask() {
        load();
        Epidemic.instance().gameData().recipes().put(this.key(), this.recipe());
    }

    /**
     * Gets the key
     *
     * @return Key
     */
    @Override
    public String key() {
        return this.key;
    }

    /**
     * Gets the display name of the gas mask
     *
     * @return Display name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the lore for the Gas Mask
     *
     * @return String list containing the lore
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets a gas mask
     *
     * @return Gas Mask ItemStack
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
     * Returns the recipe for the equipment
     * @return Recipe
     */
    @Override
    public Map<RecipePosition, RecipeItem> recipe() {
        return this.recipe;
    }

    /**
     * Returns the amount of items the player should get
     * @return Amount
     */
    public int amount() { return this.amount; }
    /**
     * Loads the gas mask from file
     */
    private void load() {
        try {
            String fileName = "gas_mask.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                Material material = loader.material(Material.AIR);
                if (material == Material.AIR) {
                    item = this.defaultItem();
                } else {
                    item = new ItemStack(material, 1);
                    if (material == Material.PLAYER_HEAD) {
                        UUID headUID = loader.headUID();
                        if (headUID != null) {
                            item = PlayerFunctions.head(headUID);
                        } else {
                            String base64 = loader.base64Texture(defaultBase64);
                            item = SkullCreator.itemFromBase64(base64);
                        }
                    }
                }
                this.item.setAmount(this.amount);
                this.protection = loader.protection(25000);
                this.recipe = RecipeManager.recipe(loader.file());
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/gas_mask.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "GasMask.load.001",
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
     * Called when a player uses a gas mask
     * @param player Player to put the Gas Mask on
     * @param equipmentSlot Equipment slot gas mask is in
     */
    public void apply(Player player, EquipmentSlot equipmentSlot) {
        if (player != null) {

            IEquipment eGasMask = Epidemic.instance().data().equipment.get("gas_mask");
            GasMask gasMask = (GasMask) eGasMask;
            if (gasMask != null) {
                if (gasMask.equipped(player)) {
                    gasMask.unapply(player, true);
                } else {
                    ItemStack currentHelmet = player.getInventory().getHelmet();
                    if (currentHelmet != null) {
                        InventoryFunctions.addItemsToPlayerInventory(player, currentHelmet, true);
                    }
                }
            }


            player.getInventory().setHelmet(get());
            switch (equipmentSlot) {
                case HAND:
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                    break;
                case OFF_HAND:
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Called when a player takes off a gas mask
     * @param player Player to take the Gas Mask off
     */
    public void unapply(Player player, boolean addToInventory) {
        if (player != null) {
            player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
            if (addToInventory) {
                InventoryFunctions.addItemsToPlayerInventory(player, get(), true);
            }
        }
    }

    /**
     * Checks if the gas mask is equipped
     * @param player Player to check
     * @return If true, Gas mask is on
     */
    public boolean equipped(Player player) {
        if (player != null) {
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet == null) {
                return false;
            }
            if (EquipmentManager.getEquipmentKey(helmet).equals(this.key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the protection level for the gas mask
     * @return Protection amount
     */
    public int protection() {
        return this.protection;
    }

    /**
     * Gets the default item
     * @return Default item
     */
    private ItemStack defaultItem() {
        ItemStack defaultItem = SkullCreator.itemFromBase64(defaultBase64);
        if (defaultItem != null) {
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