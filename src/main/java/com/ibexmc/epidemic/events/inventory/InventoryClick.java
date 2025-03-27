package com.ibexmc.epidemic.events.inventory;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.equipment.EquipmentManager;
import com.ibexmc.epidemic.equipment.GasMask;
import com.ibexmc.epidemic.equipment.HazmatSuit;
import com.ibexmc.epidemic.equipment.IEquipment;
import com.ibexmc.epidemic.gui.InvGUI;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.functions.InventoryFunctions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryClick implements Listener {

    /**
     * Called any time an inventory window is clicked.  Used to pass the event handler to
     * the Inventory GUI
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory().getHolder() instanceof InvGUI)
        {
            event.setCancelled(true);
            InvGUI clicked = (InvGUI) event.getInventory().getHolder();
            clicked.onClick(event);
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
            if(event.getSlotType() == InventoryType.SlotType.ARMOR) {
                if (EquipmentManager.hasEquipmentKey(event.getCurrentItem())) {
                    IEquipment equipment = Epidemic.instance().data().equipment.get(EquipmentManager.getEquipmentKey(event.getCurrentItem()));
                    Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Has equipment key");
                    event.setCancelled(true); // Cancel the event
                    event.setResult(Event.Result.DENY);
                    if (event.getRawSlot() == 5) {
                        switch (EquipmentManager.getEquipmentKey(event.getCurrentItem()))
                        {
                            case "hazmat_suit":
                                Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Hazmat suit found");
                                if (equipment instanceof HazmatSuit) {
                                    Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Removing Hazmat suit");
                                    HazmatSuit hazmatSuit = (HazmatSuit) equipment;
                                    hazmatSuit.unapply((Player) event.getWhoClicked(), true);
                                } else {
                                    Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Hazmat suit isn't a hazmat suit. weird.");
                                }
                                break;
                            case "gas_mask":
                                if (equipment instanceof GasMask) {
                                    Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Removing Gas Mask");
                                    GasMask gasMask = (GasMask) equipment;
                                    gasMask.unapply((Player) event.getWhoClicked(), true);
                                }
                            default:
                                Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Unexpected key = " + EquipmentManager.getEquipmentKey(event.getCurrentItem()));
                                break;
                        }
                    }
                } else {
                    Logging.debug("InventoryClick", "onInventoryClick.OnArmorClick", "Does not have equipment key");
                }
            }
        }

        // Enhanced recipes
        if (event.getResult() == Event.Result.ALLOW) {
            if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                if (event.getSlot() == 0) {
                    ItemStack itemStack = event.getInventory().getItem(event.getRawSlot());
                    if (itemStack != null) {
                        if (Epidemic.instance().persistentData().hasString(itemStack, PersistentData.Key.EQUIPMENT)) {
                            ItemStack output = Epidemic.instance().data().equipment.get(Epidemic.instance().persistentData().getString(itemStack, PersistentData.Key.EQUIPMENT)).get();
                            Map<RecipePosition, RecipeItem> recipe = Epidemic.instance().gameData().recipes().get(Epidemic.instance().persistentData().getString(itemStack, PersistentData.Key.EQUIPMENT));
                            if (recipe != null) {
                                Set<Ailment> ailments = new HashSet<>();
                                for (int i = 1; i < event.getInventory().getSize(); i++) {
                                    ItemStack item = event.getInventory().getItem(i);
                                    if (Epidemic.instance().persistentData().hasString(item, PersistentData.Key.AILMENT)) {
                                        ailments.add(
                                                AilmentManager.getAilmentByInternalName(
                                                        Epidemic.instance().persistentData().getString(item, PersistentData.Key.AILMENT)
                                                )
                                        );
                                    }
                                    if (item != null) {
                                        RecipeItem recipeItem;
                                        switch (i) {
                                            case 1:
                                                recipeItem = recipe.get(RecipePosition.TOP_LEFT);
                                                Logging.log("onInventoryClick", "Item: " + item.getType().name() + " x" + item.getAmount() + " - " + recipeItem.amount());
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 2:
                                                recipeItem = recipe.get(RecipePosition.TOP_CENTER);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 3:
                                                recipeItem = recipe.get(RecipePosition.TOP_RIGHT);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 4:
                                                recipeItem = recipe.get(RecipePosition.MIDDLE_LEFT);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 5:
                                                recipeItem = recipe.get(RecipePosition.MIDDLE_CENTER);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 6:
                                                recipeItem = recipe.get(RecipePosition.MIDDLE_RIGHT);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 7:
                                                recipeItem = recipe.get(RecipePosition.BOTTOM_LEFT);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 8:
                                                recipeItem = recipe.get(RecipePosition.BOTTOM_CENTER);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                            case 9:
                                                recipeItem = recipe.get(RecipePosition.BOTTOM_RIGHT);
                                                item.setAmount(item.getAmount() - recipeItem.amount());
                                                break;
                                        }
                                        event.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
                                        InventoryFunctions.addItemsToPlayerInventory(player, item);

                                    }
                                }
                                if (output != null) {
                                    if (Epidemic.instance().persistentData().hasString(output, PersistentData.Key.EQUIPMENT)) {
                                        String equipmentKey = Epidemic.instance().persistentData().getString(output, PersistentData.Key.EQUIPMENT);
                                        if (equipmentKey.equals("infected_arrow")) {
                                            for (Ailment ailment : ailments) {
                                                output = EquipmentManager.applyAilmentToArrow(output, ailment);
                                            }
                                        }
                                        if ( equipmentKey.equals("infected_splash_potion")) {
                                            for (Ailment ailment : ailments) {
                                                output = EquipmentManager.applyAilmentToSplashPotion(output, ailment);
                                            }
                                        }
                                    }
                                    InventoryFunctions.addItemsToPlayerInventory(player, output, true);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
