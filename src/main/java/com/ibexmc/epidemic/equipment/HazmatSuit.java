package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SkullCreator;
import com.ibexmc.epidemic.util.functions.*;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HazmatSuit implements IEquipment {

    private final String key = "hazmat_suit";
    private String name = "&bHazmat Suit";
    private final String defaultBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRlM2UzNmQyOGRmMTQ3NGEzZjQ2ZWVkZTBkMzNiNGQxYzNmMzhkYjE1MjM4ZTYzN2RiNDZhMzE4MWRlZjBhMSJ9fX0=";
    private List<String> lore = StringFunctions.stringToLore("&fA bright yellow suit that keeps out all contagious diseases");
    private int amount = 1;
    private ItemStack helmet;
    private ItemStack chestPlate;
    private ItemStack leggings;
    private ItemStack boots;
    private int protection = 1000000;
    private int destroyChance = 0;
    private Map<RecipePosition, RecipeItem> recipe;

    /**
     * Constructor
     */
    public HazmatSuit() {
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
     * Gets the display name of the hazmat suit
     * @return Display name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the lore for the hazmat suit
     * @return String list containing the lore
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets a hazmat suit
     * @return Hazmat Suit ItemStack
     */
    @Override
    public ItemStack get() {
        // Returns just the helmet
        ItemStack helmet = this.helmet.clone();
        ItemMeta itemMeta = helmet.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            helmet.setItemMeta(itemMeta);
            if (!EquipmentManager.hasEquipmentKey(helmet)) {
                Epidemic.instance().persistentData().setString(helmet, PersistentData.Key.EQUIPMENT, this.key);
            }
        }
        return helmet;
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
     * Gets the chest plate
     * @return Chest plate
     */
    private ItemStack getChestPlate() {
        // Returns just the chestplate
        ItemStack chest = this.chestPlate.clone();
        ItemMeta itemMeta = chest.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (GameFunctions.isVersionSafe(1.17)) {
                Logging.debug("HazmatSuit", "getChestPlate", "Version 1.17 safe");
                ItemFlag itemFlag = ItemFunctions.itemFlagFromName("HIDE_DYE");
                if (itemFlag != null) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                }
            }
            chest.setItemMeta(itemMeta);
            if (!EquipmentManager.hasEquipmentKey(chest)) {
                Epidemic.instance().persistentData().setString(chest, PersistentData.Key.EQUIPMENT, this.key);
            }
        }
        return chest;
    }

    /**
     * Gets the leggings
     * @return Leggings
     */
    private ItemStack getLeggings() {
        // Returns just the leggings
        ItemStack leggings = this.leggings.clone();
        ItemMeta itemMeta = leggings.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (GameFunctions.isVersionSafe(1.17)) {
                Logging.debug("HazmatSuit", "getLeggings", "Version 1.17 safe");
                ItemFlag itemFlag = ItemFunctions.itemFlagFromName("HIDE_DYE");
                if (itemFlag != null) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                }
            }
            leggings.setItemMeta(itemMeta);
            if (!EquipmentManager.hasEquipmentKey(leggings)) {
                Epidemic.instance().persistentData().setString(leggings, PersistentData.Key.EQUIPMENT, this.key);
            }
        }
        return leggings;
    }

    /**
     * Gets the boots
     * @return Boots
     */
    private ItemStack getBoots() {
        // Returns just the boots
        ItemStack boots = this.boots.clone();
        ItemMeta itemMeta = boots.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (GameFunctions.isVersionSafe(1.17)) {
                Logging.debug("HazmatSuit", "getBoots", "Version 1.17 safe");
                ItemFlag itemFlag = ItemFunctions.itemFlagFromName("HIDE_DYE");
                if (itemFlag != null) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
                }
            }
            boots.setItemMeta(itemMeta);
            if (!EquipmentManager.hasEquipmentKey(boots)) {
                Epidemic.instance().persistentData().setString(boots, PersistentData.Key.EQUIPMENT, this.key);
            }
        }
        return boots;
    }

    /**
     * Loads the Hazmat suit from file
     */
    private void load() {
        try {
            String fileName = "hazmat_suit.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                Material helmetMaterial = loader.material("helmet.material", Material.AIR);
                if (helmetMaterial == Material.AIR) {
                    this.helmet = this.defaultHelmet();
                } else {
                    this.helmet = new ItemStack(helmetMaterial, 1);
                    if (helmetMaterial == Material.PLAYER_HEAD) {
                        UUID headUID = loader.headUID("helmet.head_uid");
                        if (headUID != null) {
                            this.helmet = PlayerFunctions.head(headUID);
                        } else {
                            String base64 = loader.base64Texture("helmet.texture", defaultBase64);
                            this.helmet = SkullCreator.itemWithBase64(this.helmet, base64);
                        }
                    }
                }
                this.chestPlate = loader.item("chestplate.item", null);
                if (this.chestPlate == null) {
                    Material material = loader.material("chestplate.material", Material.AIR);
                    if (material == Material.AIR) {
                        this.chestPlate = this.defaultChestplate();
                    } else {
                        this.chestPlate = new ItemStack(material, 1);
                        if (material == Material.PLAYER_HEAD) {
                            UUID headUID = loader.headUID("chestplate.head_uid");
                            if (headUID != null) {
                                this.chestPlate = PlayerFunctions.head(headUID);
                            }
                        }
                    }
                }
                this.leggings = loader.item("leggings.item", null);
                if (this.leggings == null) {
                    Material material = loader.material("leggings.material", Material.AIR);
                    if (material == Material.AIR) {
                        this.leggings = this.defaultLeggings();
                    } else {
                        this.leggings = new ItemStack(material, 1);
                        if (material == Material.PLAYER_HEAD) {
                            UUID headUID = loader.headUID("leggings.head_uid");
                            if (headUID != null) {
                                this.leggings = PlayerFunctions.head(headUID);
                            }
                        }
                    }
                }
                this.boots = loader.item("boots.item", null);
                if (this.boots == null) {
                    Material material = loader.material("boots.material", Material.AIR);
                    if (material == Material.AIR) {
                        this.boots = this.defaultBoots();
                    } else {
                        this.boots = new ItemStack(material, 1);
                        if (material == Material.PLAYER_HEAD) {
                            UUID headUID = loader.headUID("boots.head_uid");
                            if (headUID != null) {
                                this.boots = PlayerFunctions.head(headUID);
                            }
                        }
                    }
                }
                this.helmet.setAmount(this.amount);
                this.chestPlate.setAmount(this.amount);
                this.leggings.setAmount(this.amount);
                this.boots.setAmount(this.amount);
                this.protection = loader.protection(1000000);
                this.destroyChance = loader.destroyChance(0);
                this.recipe = RecipeManager.recipe(loader.file());
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/hazmat_suit.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "HazmatSuit.load.001",
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
     * Sets the name of the Hazmat suit
     * @param name Name to set
     */
    public void name(String name) {
        this.name = name;
    }

    /**
     * Sets the lore of the Hazmat suit
     * @param lore Lore to set
     */
    public void lore(List<String> lore) {
        this.lore = lore;
    }

    /**
     * Gets the protection level from the hazmat suit
     * @return Protection amount
     */
    public int protection() {
        return this.protection;
    }

    public int destroyChance() { return this.destroyChance; }

    /**
     * Checks if all 4 pieces of the Hazmat suit are equipped
     * @param player Player to check
     * @return If true, Hazmat suit is completely equipped
     */
    public boolean equipped(Player player) {
        if (player != null) {
            ItemStack helmet = player.getInventory().getHelmet();
            ItemStack chestPlate = player.getInventory().getChestplate();
            ItemStack leggings = player.getInventory().getLeggings();
            ItemStack boots = player.getInventory().getBoots();
            if (helmet == null || chestPlate == null || leggings == null || boots == null) {
                Logging.debug("HazmatSuit", "equipped", "At least one piece of armor is null - return false");
                return false;
            }
            return EquipmentManager.getEquipmentKey(helmet).equals(this.key) &&
                    EquipmentManager.getEquipmentKey(chestPlate).equals(this.key) &&
                    EquipmentManager.getEquipmentKey(leggings).equals(this.key) &&
                    EquipmentManager.getEquipmentKey(boots).equals(this.key);
        }
        return false;
    }

    /**
     * Called when a player uses a hazmat suit
     * @param player Player to put the Hazmat suit on
     */
    public void apply(Player player) {
        if (player != null) {

            // Take existing armor off
            ItemStack playerHelmet = player.getInventory().getHelmet();
            if (playerHelmet != null) {
                InventoryFunctions.addItemsToPlayerInventory(player, playerHelmet, true);
            }
            ItemStack playerChestPlate = player.getInventory().getChestplate();
            if (playerChestPlate != null) {
                InventoryFunctions.addItemsToPlayerInventory(player, playerChestPlate, true);
            }
            ItemStack playerLeggings = player.getInventory().getLeggings();
            if (playerLeggings != null) {
                InventoryFunctions.addItemsToPlayerInventory(player, playerLeggings, true);
            }
            ItemStack playerBoots = player.getInventory().getBoots();
            if (playerBoots != null) {
                InventoryFunctions.addItemsToPlayerInventory(player, playerBoots, true);
            }

            player.getInventory().setHelmet(get());
            player.getInventory().setChestplate(getChestPlate());
            player.getInventory().setLeggings(getLeggings());
            player.getInventory().setBoots(getBoots());
        }
    }

    /**
     * Called when a player takes off a hazmat suit
     * @param player Player to take the Hazmat suit off
     */
    public void unapply(Player player, boolean addToInventory) {
        if (player != null) {
            player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
            player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
            player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
            player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
            if (addToInventory) {
                InventoryFunctions.addItemsToPlayerInventory(player, get(), true);
            }
        }
    }

    /**
     * Gets the default helmet
     * @return Default item
     */
    private ItemStack defaultHelmet() {
        ItemStack defaultItem = SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), defaultBase64);
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

    /**
     * Gets the default Chestplate
     * @return Default item
     */
    private ItemStack defaultChestplate() {
        ItemStack defaultItem = InventoryFunctions.stringBlobToItem(
                "i:\n" +
                        "                 ==: org.bukkit.inventory.ItemStack\n" +
                        "                 v: 1519\n" +
                        "                 type: LEATHER_CHESTPLATE\n" +
                        "                 meta:\n" +
                        "                   ==: ItemMeta\n" +
                        "                   meta-type: LEATHER_ARMOR\n" +
                        "                   color:\n" +
                        "                     ==: Color\n" +
                        "                     RED: 254\n" +
                        "                     BLUE: 61\n" +
                        "                     GREEN: 216"
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

    /**
     * Gets the default leggings
     * @return Default item
     */
    private ItemStack defaultLeggings() {
        ItemStack defaultItem = InventoryFunctions.stringBlobToItem(
                "i:\n" +
                        "               ==: org.bukkit.inventory.ItemStack\n" +
                        "               v: 1519\n" +
                        "               type: LEATHER_LEGGINGS\n" +
                        "               meta:\n" +
                        "                 ==: ItemMeta\n" +
                        "                 meta-type: LEATHER_ARMOR\n" +
                        "                 color:\n" +
                        "                   ==: Color\n" +
                        "                   RED: 254\n" +
                        "                   BLUE: 61\n" +
                        "                   GREEN: 216"
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

    /**
     * Gets the default boots
     * @return Default item
     */
    private ItemStack defaultBoots() {
        ItemStack defaultItem = InventoryFunctions.stringBlobToItem(
                "i:\n" +
                        "            ==: org.bukkit.inventory.ItemStack\n" +
                        "            v: 1519\n" +
                        "            type: LEATHER_BOOTS\n" +
                        "            meta:\n" +
                        "              ==: ItemMeta\n" +
                        "              meta-type: LEATHER_ARMOR\n" +
                        "              color:\n" +
                        "                ==: Color\n" +
                        "                RED: 254\n" +
                        "                BLUE: 61\n" +
                        "                GREEN: 216"
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
