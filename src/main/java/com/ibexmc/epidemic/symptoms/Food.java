package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.ItemFunctions;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.ailments.Afflicted;

public class Food {
    /**
     * Rots food in players inventory
     * @param player Player to rot food for
     */
    public static void rotFood(Player player) {
        boolean foodFound = false;
        int i = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null) {
                Logging.debug("Food", "rotFood", itemStack.getType().name() + " x" + itemStack.getAmount());
                if (itemStack.getType().isEdible()) {
                    ItemStack blank = new ItemStack(ItemFunctions.emptyFoodContainer(itemStack.getType()), itemStack.getAmount());
                    player.getInventory().setItem(i, blank);
                    foodFound = true;
                }
            }
            i++;
        }
        if (foodFound) {
            SendMessage.Player("food_rot", "Your food has been tainted and rotted away", player, true, null);
        }
    }

    /**
     * Applies food rot to the player
     * @param player Player to rot food for
     * @param afflicted Affliction causing food rot
     */
    public static void applyRotFood(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player)  || player.getGameMode().equals(GameMode.CREATIVE)) {
                if (afflicted.getAilment().isFoodRot()) {
                    rotFood(player);
                }
            }
        }
    }
}
