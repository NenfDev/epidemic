package com.ibexmc.epidemic.events.craft;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.equipment.EquipmentManager;
import com.ibexmc.epidemic.equipment.IEquipment;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.remedy.Remedy;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.ItemFunctions;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CraftItem implements Listener {

    /**
     * Called when a crafted item is being taken from the result box.  If the resulting item is a Remedy, it will
     * check if craft permissions are required, if so, then it will check for the permission before allowing the
     * player to complete the crafting process.  If permission is not found, the event is cancelled.
     * @param event CraftItemEvent
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Logging.debug("CraftItem", "onCraftItem", "Start");
        Epidemic plugin = Epidemic.instance();

        ItemStack itemStack = event.getRecipe().getResult();

        //Logging.debug("CraftItem", "onCraftItem", ItemFunctions.itemToStringBlob(itemStack));

        HumanEntity humanEntity = event.getWhoClicked();
        Player player = (Player) humanEntity;
        if (player == null) {
            return;
        }

        if (Epidemic.instance().persistentData().hasString(itemStack, PersistentData.Key.EQUIPMENT)) {
            Logging.debug("CraftItem", "onCraftItem", "Equipment picked up - clear inventory, clear result, give equipment");
            event.setCancelled(true);
        }

        Remedy remedy = null;
        if (itemStack != null) {
            for (Map.Entry<String, Remedy> remedyEntry : plugin.data().getRemedies().entrySet()) {
                if (remedyEntry.getValue().isMatch(itemStack)) {
                    remedy = remedyEntry.getValue();
                    break;
                }
            }

            if (remedy != null) {
                if (remedy.isRequireCraftPerm()) {
                    if (!Permission.hasPermission(
                            player, "epidemic.craft." + remedy.getNamespacedKey().getKey())
                    ) {
                        SendMessage.Player(
                                "no_craft_perm",
                                "&cYou do not have permission to craft this item",
                                player,
                                true,
                                null
                        );
                        Logging.debug(
                                "CraftItemEvent",
                                "onCraftItem",
                                "Cancelling event"
                        );
                        event.setCancelled(true);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onPrepareCraftItem(PrepareItemCraftEvent event) {
        Logging.debug("CraftItem", "onPrepareCraftItem", "Start");

        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();
        if (matrix.length == 9) {

            Map<RecipePosition, ItemStack> craftRecipe = RecipeManager.populateCraftItemMap(matrix);

            for (Map.Entry<String, Map<RecipePosition, RecipeItem>> equipmentRecipe : Epidemic.instance().gameData().recipes().get().entrySet()) {
                if (RecipeManager.match(craftRecipe, equipmentRecipe.getValue(), equipmentRecipe.getKey())) {
                    Set<Ailment> ailments = new HashSet<>();
                    if (equipmentRecipe.getKey().equals("infected_arrow") || equipmentRecipe.getKey().equals("infected_splash_potion")) {
                        for (RecipePosition position : RecipePosition.values()) {
                            if (Epidemic.instance().persistentData().hasString(craftRecipe.get(position), PersistentData.Key.AILMENT)) {
                                ailments.add(
                                        AilmentManager.getAilmentByInternalName(
                                                Epidemic.instance().persistentData().getString(craftRecipe.get(position),
                                                        PersistentData.Key.AILMENT)
                                        )
                                );
                            }
                        }
                    }
                    Logging.debug("CraftItem", "PrepareItemCraftEvent", "Recipe found!");
                    IEquipment equipment = Epidemic.instance().data().equipment.get(equipmentRecipe.getKey());
                    if (equipment != null) {
                        ItemStack output = equipment.get();
                        for (Ailment ailment : ailments) {
                            if (equipmentRecipe.getKey().equals("infected_arrow")) {
                                output = EquipmentManager.applyAilmentToArrow(output, ailment);
                            }
                            if (equipmentRecipe.getKey().equals("infected_splash_potion")) {
                                output = EquipmentManager.applyAilmentToSplashPotion(output, ailment);
                            }
                        }
                        Logging.debug("CraftItem", "PrepareItemCraftEvent", "Returning: " + ItemFunctions.itemToStringBlob(output));
                        event.getInventory().setResult(output);
                    }

                }
            }
        }
        // ABC
        // DEF
        // GHI
        // A=0 B=1 C=2 D=3 E=4 F=5 G=6 H=7 I=8
    }
}
