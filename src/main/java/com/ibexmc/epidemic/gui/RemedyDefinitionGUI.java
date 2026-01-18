package com.ibexmc.epidemic.gui;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.helpers.recipe.EpidemicCraftRecipe;
import com.ibexmc.epidemic.helpers.recipe.EpidemicFurnaceRecipe;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RemedyDefinitionGUI implements InvGUI {

    //region Objects
    private Remedy remedy;
    //endregion
    //region Constructors
    public RemedyDefinitionGUI(Remedy remedy) {
        Epidemic plugin = Epidemic.instance();
        this.remedy = remedy;
    }
    //endregion
    //region Events

    /**
     * Called from InventoryClick for any clicks in an inventory window of this type
     * @param event InventoryClickEvent to process
     */
    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.LEFT) {
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                if (event.getRawSlot() >= 0 && event.getRawSlot() <= 27) {
                    switch (event.getRawSlot()) {
                        case 9:
                            RemedyListGUI listGUI = new RemedyListGUI(
                                    remedy.getDisplayName(),
                                    9
                            );
                            Inventory listInventory = listGUI.getInventory();
                            player.openInventory(listInventory);
                            break;
                        case 15:
                            if (Permission.hasPermission(player, Permission.PermissionType.REMEDY_TAKE) || Permission.hasPermission(player, Permission.PermissionType.CURES_TAKE)) {
                                if (!InventoryFunctions.addItemsToPlayerInventory(
                                        player,
                                        remedy.getItemStack(1),
                                        false
                                )) {
                                    SendMessage.Player(
                                            "remedy_take_fail_001",
                                            "&cUnable to get take this remedy",
                                            player,
                                            true,
                                            null
                                    );
                                    return;
                                }
                            } else {
                                SendMessage.Player(
                                        "remedy_gui_perm_001",
                                        "&cYou do not have permission to take this remedy",
                                        player,
                                        true,
                                        null
                                );
                                return;
                            }
                        default:
                            break;
                    }
                }
            }
        }
    }
    //endregion
    //region Methods
    private ItemStack getIngredientItemStack(EpidemicCraftRecipe.Ingredient ingredient) {
        if (ingredient == null || ingredient.isAir()) {
            return new ItemStack(Material.AIR);
        }
        if (ingredient.isItemsAdder()) {
            ItemStack iaItem = com.ibexmc.epidemic.util.functions.ItemFunctions.getItemsAdderItem(ingredient.getItemsAdderID());
            if (iaItem != null) {
                return iaItem;
            }
            return new ItemStack(Material.BARRIER);
        }
        ItemStack item = new ItemStack(ingredient.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null && ingredient.hasCustomModelData()) {
            meta.setCustomModelData(ingredient.getCustomModelData());
        }
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionType(PotionType.WATER);
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    /**
     * Gets the inventory being used in this GUI
     */
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(
                this, 
                27, ChatColor.stripColor(StringFunctions.colorToString(remedy.getDisplayName()))
        );

        // Black/Red Glass
        ItemStack itemStackNA = new ItemStack(Epidemic.instance().config().getGuiRemedyRecipeBorderItem(), 1);
        ItemMeta itemMetaNA = itemStackNA.getItemMeta();
        if (itemMetaNA != null) {
            itemMetaNA.setDisplayName(" ");
            if (Epidemic.instance().config().getGuiRemedyRecipeBorderCMD() > 0) {
                itemMetaNA.setCustomModelData(Epidemic.instance().config().getGuiRemedyRecipeBorderCMD());
            }
            itemMetaNA.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStackNA.setItemMeta(itemMetaNA);
        }
        ItemStack itemStackBack = new ItemStack(Epidemic.instance().config().getGuiRemedyRecipeBackItem(), 1);
        ItemMeta itemMetaBack = itemStackBack.getItemMeta();
        if (itemMetaBack != null) {
            itemMetaBack.setDisplayName(Locale.localeText(
                    "remedy_def_gui_back",
                    "Back",
                    null));
            String backLoreText = Locale.localeText(
                    "remedy_def_gui_back_lore",
                    "Back to Cures List",
                    null);
            List<String> backLore = StringFunctions.stringToLore(backLoreText);
            if (backLore == null) {
                backLore = new ArrayList<>();
            }
            itemMetaBack.setLore(backLore);
            if (Epidemic.instance().config().getGuiRemedyRecipeBackCMD() > 0) {
                itemMetaBack.setCustomModelData(Epidemic.instance().config().getGuiRemedyRecipeBackCMD());
            }
            itemMetaBack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStackBack.setItemMeta(itemMetaBack);
        }

        if (remedy.getRecipe() == null) {

            ItemStack noRecipe = new ItemStack(Material.BARRIER, 1);
            ItemMeta noRecipeMeta = noRecipe.getItemMeta();
            if (noRecipeMeta != null) {
                noRecipeMeta.setDisplayName(Locale.localeText(
                        "uncraftable_recipe",
                        "This recipe is not craftable",
                        null)
                );
                noRecipeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                noRecipe.setItemMeta(noRecipeMeta);
            }
            inventory.setItem(0, noRecipe);

            ItemStack barrier = new ItemStack(Material.BARRIER, 1);
            ItemMeta barrierMeta = barrier.getItemMeta();
            if (barrierMeta != null) {
                barrierMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                barrier.setItemMeta(barrierMeta);
            }
            inventory.setItem(2, barrier); // Top - Left
            inventory.setItem(3, barrier); // Top - Center
            inventory.setItem(4, barrier); // Top - Right
            inventory.setItem(11, barrier); // Middle - Left
            inventory.setItem(12, barrier); // Middle - Center
            inventory.setItem(13, barrier); // Middle - Right
            inventory.setItem(20, barrier); // Bottom - Left
            inventory.setItem(21, barrier); // Bottom - Center
            inventory.setItem(22, barrier); // Bottom - Right
        } else {

            if (remedy.getRecipe() instanceof EpidemicCraftRecipe) {
                EpidemicCraftRecipe epidemicCraftRecipe = (EpidemicCraftRecipe) remedy.getRecipe();

                ItemStack craftRecipe = new ItemStack(Material.CRAFTING_TABLE, 1);
                ItemMeta craftRecipeMeta = craftRecipe.getItemMeta();
                if (craftRecipeMeta != null) {
                    craftRecipeMeta.setDisplayName(Locale.localeText(
                            "craft_recipe",
                            "This is a Crafting Table recipe",
                            null)
                    );
                    craftRecipeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    craftRecipe.setItemMeta(craftRecipeMeta);
                }
                inventory.setItem(0, craftRecipe);

                // Top - Left
                inventory.setItem(2, getIngredientItemStack(epidemicCraftRecipe.getTop().getLeft()));
                // Top - Center
                inventory.setItem(3, getIngredientItemStack(epidemicCraftRecipe.getTop().getCenter()));
                // Top - Right
                inventory.setItem(4, getIngredientItemStack(epidemicCraftRecipe.getTop().getRight()));
                // Middle - Left
                inventory.setItem(11, getIngredientItemStack(epidemicCraftRecipe.getMiddle().getLeft()));
                // Middle - Center
                inventory.setItem(12, getIngredientItemStack(epidemicCraftRecipe.getMiddle().getCenter()));
                // Middle - Right
                inventory.setItem(13, getIngredientItemStack(epidemicCraftRecipe.getMiddle().getRight()));
                // Bottom - Left
                inventory.setItem(20, getIngredientItemStack(epidemicCraftRecipe.getBottom().getLeft()));
                // Bottom - Center
                inventory.setItem(21, getIngredientItemStack(epidemicCraftRecipe.getBottom().getCenter()));
                // Bottom - Right
                inventory.setItem(22, getIngredientItemStack(epidemicCraftRecipe.getBottom().getRight()));
            } else if (remedy.getRecipe() instanceof EpidemicFurnaceRecipe) {

                EpidemicFurnaceRecipe epidemicFurnaceRecipe = (EpidemicFurnaceRecipe) remedy.getRecipe();
                ItemStack furnaceRecipe = new ItemStack(Material.FURNACE, 1);
                ItemMeta furnaceRecipeMeta = furnaceRecipe.getItemMeta();
                if (furnaceRecipeMeta != null) {
                    furnaceRecipeMeta.setDisplayName(Locale.localeText(
                            "furnace_recipe",
                            "This is a Furnace recipe",
                            null)
                    );
                    furnaceRecipeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    furnaceRecipe.setItemMeta(furnaceRecipeMeta);
                }
                inventory.setItem(0, furnaceRecipe);

                ItemStack furnace = new ItemStack(Material.FURNACE, 1);
                ItemMeta furnaceMeta = furnace.getItemMeta();
                if (furnaceMeta != null) {
                    furnaceMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    furnace.setItemMeta(furnaceMeta);
                }

                inventory.setItem(2, itemStackNA);
                inventory.setItem(3, itemStackNA);
                inventory.setItem(4, itemStackNA);
                inventory.setItem(11, furnace);
                inventory.setItem(12, itemStackNA);
                ItemStack itemStackSource = new ItemStack(epidemicFurnaceRecipe.getSource(), 1);
                ItemMeta itemMetaSource = itemStackSource.getItemMeta();
                if (itemMetaSource != null) {
                    itemMetaSource.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackSource.setItemMeta(itemMetaSource);
                }
                if (itemMetaSource instanceof PotionMeta) {
                    PotionMeta potionMetaMid2 = (PotionMeta) itemMetaSource;
                    potionMetaMid2.setBasePotionType(PotionType.WATER);
                    itemStackSource.setItemMeta(potionMetaMid2);
                }
                inventory.setItem(13, itemStackSource);

                inventory.setItem(14, itemStackNA);
                inventory.setItem(20, itemStackNA);
                inventory.setItem(21, itemStackNA);
                inventory.setItem(22, itemStackNA);
            }

        }


        inventory.setItem(9, itemStackBack);


        inventory.setItem(1, itemStackNA);
        inventory.setItem(5, itemStackNA);
        inventory.setItem(6, itemStackNA);
        inventory.setItem(7, itemStackNA);
        inventory.setItem(8, itemStackNA);

        inventory.setItem(10, itemStackNA);
        inventory.setItem(14, itemStackNA);
        inventory.setItem(16, itemStackNA);
        inventory.setItem(17, itemStackNA);

        inventory.setItem(18, itemStackNA);
        inventory.setItem(19, itemStackNA);
        inventory.setItem(23, itemStackNA);
        inventory.setItem(24, itemStackNA);
        inventory.setItem(25, itemStackNA);
        inventory.setItem(26, itemStackNA);

        // Return Item
        ItemStack itemStack = remedy.getItemStack(1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        inventory.setItem(15, itemStack);

        return inventory;

    }
    //endregion
}
