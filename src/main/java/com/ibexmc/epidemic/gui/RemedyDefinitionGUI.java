package com.ibexmc.epidemic.gui;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.helpers.recipe.EpidemicCraftRecipe;
import com.ibexmc.epidemic.helpers.recipe.EpidemicFurnaceRecipe;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;

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
        ItemStack itemStackNA = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itemMetaNA = itemStackNA.getItemMeta();
        if (itemMetaNA != null) {
            itemMetaNA.setDisplayName(" ");
            itemMetaNA.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStackNA.setItemMeta(itemMetaNA);
        }
        ItemStack itemStackBack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta itemMetaBack = itemStackNA.getItemMeta();
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
                ItemStack itemStackTop1 = new ItemStack(epidemicCraftRecipe.getTop().getLeft(), 1);
                ItemMeta itemMetaTop1 = itemStackTop1.getItemMeta();
                if (itemMetaTop1 != null) {
                    itemMetaTop1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackTop1.setItemMeta(itemMetaTop1);
                }
                if (itemMetaTop1 instanceof PotionMeta) {
                    PotionMeta potionMetaTop1 = (PotionMeta) itemMetaTop1;
                    PotionData potionDataTop1 = new PotionData(PotionType.WATER);
                    potionMetaTop1.setBasePotionData(potionDataTop1);
                    itemStackTop1.setItemMeta(potionMetaTop1);
                }
                inventory.setItem(2, itemStackTop1);
                // Top - Center
                ItemStack itemStackTop2 = new ItemStack(epidemicCraftRecipe.getTop().getCenter(), 1);
                ItemMeta itemMetaTop2 = itemStackTop2.getItemMeta();
                if (itemMetaTop2 != null) {
                    itemMetaTop2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackTop2.setItemMeta(itemMetaTop2);
                }
                if (itemMetaTop2 instanceof PotionMeta) {
                    PotionMeta potionMetaTop2 = (PotionMeta) itemMetaTop2;
                    PotionData potionDataTop2 = new PotionData(PotionType.WATER);
                    potionMetaTop2.setBasePotionData(potionDataTop2);
                    itemStackTop2.setItemMeta(potionMetaTop2);
                }
                inventory.setItem(3, itemStackTop2);
                // Top - Right
                ItemStack itemStackTop3 = new ItemStack(epidemicCraftRecipe.getTop().getRight(), 1);
                ItemMeta itemMetaTop3 = itemStackTop3.getItemMeta();
                if (itemMetaTop3 != null) {
                    itemMetaTop3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackTop3.setItemMeta(itemMetaTop3);
                }
                if (itemMetaTop3 instanceof PotionMeta) {
                    PotionMeta potionMetaTop3 = (PotionMeta) itemMetaTop3;
                    PotionData potionDataTop3 = new PotionData(PotionType.WATER);
                    potionMetaTop3.setBasePotionData(potionDataTop3);
                    itemStackTop3.setItemMeta(potionMetaTop3);
                }
                inventory.setItem(4, itemStackTop3);
                // Middle - Left
                ItemStack itemStackMid1 = new ItemStack(epidemicCraftRecipe.getMiddle().getLeft(), 1);
                ItemMeta itemMetaMid1 = itemStackMid1.getItemMeta();
                if (itemMetaMid1 != null) {
                    itemMetaMid1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackMid1.setItemMeta(itemMetaMid1);
                }
                if (itemMetaMid1 instanceof PotionMeta) {
                    PotionMeta potionMetaMid1 = (PotionMeta) itemMetaMid1;
                    PotionData potionDataMid1 = new PotionData(PotionType.WATER);
                    potionMetaMid1.setBasePotionData(potionDataMid1);
                    itemStackMid1.setItemMeta(potionMetaMid1);
                }
                inventory.setItem(11, itemStackMid1);
                // Middle - Center
                ItemStack itemStackMid2 = new ItemStack(epidemicCraftRecipe.getMiddle().getCenter(), 1);
                ItemMeta itemMetaMid2 = itemStackMid2.getItemMeta();
                if (itemMetaMid2 != null) {
                    itemMetaMid2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackMid2.setItemMeta(itemMetaMid2);
                }
                if (itemMetaMid2 instanceof PotionMeta) {
                    PotionMeta potionMetaMid2 = (PotionMeta) itemMetaMid2;
                    PotionData potionDataMid2 = new PotionData(PotionType.WATER);
                    potionMetaMid2.setBasePotionData(potionDataMid2);
                    itemStackMid2.setItemMeta(potionMetaMid2);
                }
                inventory.setItem(12, itemStackMid2);
                // Middle - Right
                ItemStack itemStackMid3 = new ItemStack(epidemicCraftRecipe.getMiddle().getRight(), 1);
                ItemMeta itemMetaMid3 = itemStackMid3.getItemMeta();
                if (itemMetaMid3 != null) {
                    itemMetaMid3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackMid3.setItemMeta(itemMetaMid3);
                }
                if (itemMetaMid3 instanceof PotionMeta) {
                    PotionMeta potionMetaMid3 = (PotionMeta) itemMetaMid3;
                    PotionData potionDataMid3 = new PotionData(PotionType.WATER);
                    potionMetaMid3.setBasePotionData(potionDataMid3);
                    itemStackMid3.setItemMeta(potionMetaMid3);
                }
                inventory.setItem(13, itemStackMid3);
                // Bottom - Left
                ItemStack itemStackBottom1 = new ItemStack(epidemicCraftRecipe.getBottom().getLeft(), 1);
                ItemMeta itemMetaBottom1 = itemStackBottom1.getItemMeta();
                if (itemMetaBottom1 != null) {
                    itemMetaBottom1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackBottom1.setItemMeta(itemMetaBottom1);
                }
                if (itemMetaBottom1 instanceof PotionMeta) {
                    PotionMeta potionMetaBottom1 = (PotionMeta) itemMetaBottom1;
                    PotionData potionDataBottom1 = new PotionData(PotionType.WATER);
                    potionMetaBottom1.setBasePotionData(potionDataBottom1);
                    itemStackBottom1.setItemMeta(potionMetaBottom1);
                }
                inventory.setItem(20, itemStackBottom1);
                // Bottom - Center
                ItemStack itemStackBottom2 = new ItemStack(epidemicCraftRecipe.getBottom().getCenter(), 1);
                ItemMeta itemMetaBottom2 = itemStackBottom2.getItemMeta();
                if (itemMetaBottom2 != null) {
                    itemMetaBottom2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackBottom2.setItemMeta(itemMetaBottom2);
                }
                if (itemMetaBottom2 instanceof PotionMeta) {
                    PotionMeta potionMetaBottom2 = (PotionMeta) itemMetaBottom2;
                    PotionData potionDataBottom2 = new PotionData(PotionType.WATER);
                    potionMetaBottom2.setBasePotionData(potionDataBottom2);
                    itemStackBottom2.setItemMeta(potionMetaBottom2);
                }
                inventory.setItem(21, itemStackBottom2);
                // Bottom - Right
                ItemStack itemStackBottom3 = new ItemStack(epidemicCraftRecipe.getBottom().getRight(), 1);
                ItemMeta itemMetaBottom3 = itemStackBottom3.getItemMeta();
                if (itemMetaBottom3 != null) {
                    itemMetaBottom3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    itemStackBottom3.setItemMeta(itemMetaBottom3);
                }
                if (itemMetaBottom3 instanceof PotionMeta) {
                    PotionMeta potionMetaBottom3 = (PotionMeta) itemMetaBottom3;
                    PotionData potionDataBottom3 = new PotionData(PotionType.WATER);
                    potionMetaBottom3.setBasePotionData(potionDataBottom3);
                    itemStackBottom3.setItemMeta(potionMetaBottom3);
                }
                inventory.setItem(22, itemStackBottom3);
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
                    PotionData potionDataMid2 = new PotionData(PotionType.WATER);
                    potionMetaMid2.setBasePotionData(potionDataMid2);
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
