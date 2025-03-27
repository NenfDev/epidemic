package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import com.ibexmc.epidemic.ailments.Afflicted;

public class Rust {
    /**
     * Applies rust to a players iron armor, tools and weapons
     * @param player Player to apply rust to
     * @param rustAmount Amount of damage to apply to each item
     */
    public static void rust(Player player, int rustAmount) {
        int i = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null) {
                switch (itemStack.getType()) {
                    case CHAINMAIL_HELMET:
                    case CHAINMAIL_CHESTPLATE:
                    case CHAINMAIL_LEGGINGS:
                    case CHAINMAIL_BOOTS:
                    case IRON_HELMET:
                    case IRON_CHESTPLATE:
                    case IRON_LEGGINGS:
                    case IRON_BOOTS:
                    case IRON_SWORD:
                    case IRON_AXE:
                    case IRON_SHOVEL:
                    case IRON_PICKAXE:
                    case IRON_HOE:
                    case IRON_HORSE_ARMOR:
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (!itemMeta.isUnbreakable()) {
                            if (itemMeta instanceof Damageable) {
                                Damageable damageable = (Damageable) itemMeta;
                                Logging.debug("Rust", "applyRust", "Item Type: " + itemStack.getType().name());
                                Logging.debug("Rust", "applyRust", "Item Display Name: " + itemMeta.getDisplayName());
                                Logging.debug("Rust", "applyRust", "Max Damage: " + itemStack.getType().getMaxDurability());
                                Logging.debug("Rust", "applyRust", "Damage: " + damageable.getDamage());

                                Logging.debug("Rust", "applyRust", "Calc: " + (itemStack.getType().getMaxDurability() - damageable.getDamage() - rustAmount));
                                if (itemStack.getType().getMaxDurability() - damageable.getDamage() - rustAmount > 0) {
                                    damageable.setDamage(damageable.getDamage() + rustAmount);
                                }
                                itemStack.setItemMeta(itemMeta);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            i++;
        }
    }

    /**
     * Applies rust to the player for the specified afflcition
     * @param player Player to apply rust for
     * @param afflicted Affliction causing rust
     */
    public static void applyRust(Player player, Afflicted afflicted) {
        if (player != null) {
            if (!Permission.inBypass(player)  || player.getGameMode().equals(GameMode.CREATIVE)) {
                if (afflicted.getAilment().isRust()) {
                    if (afflicted.getAilment().getRustAmount() > 0) {
                        rust(player, afflicted.getAilment().getRustAmount());
                    }
                }
            }
        }
    }
}
