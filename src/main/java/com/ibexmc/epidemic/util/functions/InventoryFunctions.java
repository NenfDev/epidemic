package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.util.SendMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryFunctions {

    /**
     * Adds an ItemStack to a players inventory, fails if inventory is full
     * @param player Player to add to
     * @param itemStack ItemStack to add
     * @return If true, item was added
     */
    public static boolean addItemsToPlayerInventory(Player player, ItemStack itemStack) {
        return addItemsToPlayerInventory(player, itemStack, true);
    }

    /**
     * Adds an ItemStack to a player inventory, can drop if the inventory is full
     * @param player Player to add to
     * @param itemStack ItemStack to add
     * @param drop If true, drop the item in front of the player if inventory is full
     * @return If true, item was added or dropped
     */
    public static boolean addItemsToPlayerInventory(Player player, ItemStack itemStack, boolean drop) {
        // Add Items to a players inventory if there is enough space
        // Should add as much of the item to already filled slots as possible
        // if there is any remainder, it should place that in the first available
        // free slot if there is one.  If there are no free slots and no space
        // remaining elsewhere, it will drop the remainder to the ground

        if (itemStack == null) {
            return false;
        }
        if (player == null) {
            return false;
        }
        int amount = itemStack.getAmount(); // Amount to add
        int i = 0; // Position in the inventory
        Inventory inventory = player.getInventory(); // Get the players inventory
        // Loop through the inventory - only looking at slots with items in them
        for (ItemStack inventoryItem : inventory.getStorageContents()) {
            if (inventoryItem != null) {
                // Not sure if we can have AIR, but if so, it can be considered a free slot
                if (inventoryItem.getType() == Material.AIR) {
                    break;
                }
                if (inventoryItem.getMaxStackSize() > 1) {
                    if (inventoryItem.getType() == itemStack.getType()) {
                        // First check if the base types are the same
                        // Next check that the slot isn't completely full
                        if (inventoryItem.getAmount() < inventoryItem.getMaxStackSize()) {
                            // Clone the items and set their amounts to 1 - they'll then be
                            // serialized and comapared
                            ItemStack invItem1 = inventoryItem.clone();
                            invItem1.setAmount(1);
                            ItemStack itemStack1 = itemStack.clone();
                            itemStack1.setAmount(1);
                            // Serialize
                            if (itemToStringBlob(invItem1).equals(itemToStringBlob(itemStack1))) {
                                // Item is exactly the same (not including amount)
                                // Get the free space in this slot
                                int freeSpace = inventoryItem.getMaxStackSize() - inventoryItem.getAmount();
                                if (freeSpace >= amount) {
                                    // Free space is more or equal to what we need
                                    inventoryItem.setAmount(inventoryItem.getAmount() + amount);
                                    amount = 0;
                                    break;
                                } else {
                                    // Free space is not enough to hold the entire item stack
                                    inventoryItem.setAmount(inventoryItem.getMaxStackSize()); // Max out the slot
                                    amount = amount - freeSpace; // Lower the needed amount
                                }
                            } else {
                                // Items do not match
                            }
                            invItem1 = null; // cleanup
                            itemStack1 = null;
                        }
                    } else {
                        // Material types do not match
                    }
                } else {
                    // Max stack size is 1 and there is already an item here
                }
            }
        } // End of for loop

        // Check if amount is still more than 0
        if (amount > 0) {
            int freeSlot = firstEmptySlot(player); // Get the first completely empty slot
            itemStack.setAmount(amount); // Set the itemstack to the amount still needed
            if (freeSlot >= 0) {
                // There is a free slot, set that slot to the remaining amount
                inventory.setItem(freeSlot, itemStack);
            } else {
                // There are no free slots, drop the remaining amount to the ground
                // if allowed
                if (drop) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                } else {
                    SendMessage.Player(
                            "inv_give_fail_nodrop_001",
                            "&cYou are unable to accept the incoming item as your inventory is full",
                            player,
                            true,
                            null
                    );
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Converts an ItemStack into a String Blob
     * @param itemStack ItemStack to convert
     * @return String blob of the ItemStack
     */
    public static String itemToStringBlob(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    /**
     * Converts a String Blob into an ItemStack
     * @param stringBlob String Blob to convert
     * @return ItemStack from String Blob
     */
    public static ItemStack stringBlobToItem(String stringBlob) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringBlob);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }

    /**
     * Gets the first empty slot the player has
     * @param player Player to lookup
     * @return First empty slot, -1 if no slot free
     */
    public static int firstEmptySlot(Player player) {
        int slot = -1;
        if (player != null) {
            if (player.getPlayer().getInventory().firstEmpty() >= 0) {
                slot = player.getPlayer().getInventory().firstEmpty();
            }
        }
        return slot;
    }

    /**
     * Sets the amount of items in an ItemStack
     * @param itemStack ItemStack to set amount for
     * @param setAmount Amount to set to
     * @return ItemStack with updated amount
     */
    public static ItemStack setItemStackAmount(ItemStack itemStack, int setAmount) {
        if (itemStack != null) {
            itemStack.setAmount(setAmount);
            if (itemStack.getAmount() == 0) {
                return new ItemStack(Material.AIR, 1);
            }
            return itemStack;
        } else {
            return null;
        }
    }

    /**
     * Adds an amount to an existing ItemStack
     * @param itemStack ItemStack to add amount to
     * @param addAmount Amount to add
     * @return ItemStack with updated amount
     */
    public static ItemStack addItemStackAmount(ItemStack itemStack, int addAmount) {
        if (itemStack != null) {
            itemStack.setAmount(itemStack.getAmount() + addAmount);
            if (itemStack.getAmount() == 0) {
                return new ItemStack(Material.AIR, 1);
            }
        }
        return itemStack;
    }

    /**
     * Compares two ItemStack objects
     * @param item1 ItemStack to compare
     * @param item2 ItemStack to compare against
     * @param ignoreQuantity If quantity should be ignored during comparison
     * @return If true, ItemStacks are the same
     */
    public static boolean compareItems(ItemStack item1, ItemStack item2, boolean ignoreQuantity, boolean minQuantity1to2) {
        if (item1 == null) {
            item1 = new ItemStack(Material.AIR, 1);
        }
        if (item2 == null) {
            item2 = new ItemStack(Material.AIR, 1);
        }
        ItemStack itemStack1 = item1.clone();
        ItemStack itemStack2 = item2.clone();
        if (minQuantity1to2) {
            if (itemStack1.getAmount() < itemStack2.getAmount()) {
                return false;
            }
        }
        if (ignoreQuantity) {
            itemStack1.setAmount(1);
            itemStack2.setAmount(1);
        }
        String item1Blob = ItemFunctions.itemToStringBlob(itemStack1);
        String item2Blob = ItemFunctions.itemToStringBlob(itemStack2);
        if (item1Blob.equals(item2Blob)) {
            return true;
        } else {
            return false;
        }
    }
}
