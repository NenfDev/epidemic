package com.ibexmc.epidemic.remedy;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.helpers.PlayerPotionEffect;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.TimeFunctions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.events.player.PlayerInteract;
import org.bukkit.potion.PotionEffect;

import java.sql.Timestamp;
import java.util.Map;

public class RemedyManager {

    public static boolean onPlayerInteractEntity(PlayerInteractEntityEvent event, Player clickedPlayer) {

        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return false;
        }
        ItemStack inHand = event.getPlayer().getInventory().getItemInMainHand();
        // Check if the item in hand is a remedy
        Remedy remedy = RemedyManager.getRemedy(
                event.getPlayer(),
                event.getHand(),
                inHand
        );
        if (remedy != null) {
            Logging.debug(
                    "RemedyManager",
                    "onPlayerInteractEntity",
                    "Remedy found - " + remedy.getDisplayName()
            );

            // Check if the remedy can be applied to others
            if (!remedy.getApplyOthers()) {
                SendMessage.Player(
                        "remedy_others_fail",
                        "&cSorry, that remedy cannot be applied to other players",
                        event.getPlayer(),
                        true,
                        null
                );
                return false;
            }

            // If not a food and not a drink, then interact with the Remedy
            if (!remedy.isFood() && !remedy.isDrink()) {
                boolean apply = true;

                if (apply) {
                    RemedyManager.interactOther(remedy, event.getPlayer(), clickedPlayer, event.getHand(), inHand);
                }
                // It was a remedy, cancel the event
                return true;
            } else {
                Logging.debug(
                        "RemedyManager",
                        "onPlayerInteractEntity",
                        "Remedy is food or drink - do not cancel event"
                );
            }
        } else {
            Logging.debug(
                    "RemedyManager",
                    "onPlayerInteractEntity",
                    "No Remedy found"
            );
        }
        return false;
    }

    /**
     * onPlayerInteract runs player interaction code for remedies.  It takes in the PlayerInteractEvent
     * from the listener and returns a cancellation flag.  If true, cancel the event.
     * @param event PlayerInteractEvent that was called
     * @return PlayerInteract.PlayerInteractResult Contains the cancellation flags for PlayerInteractEvent
     */
    public static PlayerInteract.PlayerInteractResult onPlayerInteract(PlayerInteractEvent event) {
        boolean processItem = false;
        ItemStack inHand = event.getItem();
        if (inHand != null && event.getItem() != null) {
            if (event.getHand().equals(EquipmentSlot.HAND)) {
                if (inHand.hashCode() == event.getPlayer().getInventory().getItemInMainHand().hashCode()) {
                    processItem = true;
                }
            }
            if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                if (inHand.hashCode() == event.getPlayer().getInventory().getItemInOffHand().hashCode()) {
                    processItem = true;
                }
            }
        }

        if (event.getItem() != null) {
            if (processItem) {
                // Check to make sure it is a right click event
                if (
                        event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                                event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                ) {
                    // Check if the item in hand is a remedy
                    Remedy remedy = RemedyManager.getRemedy(
                            event.getPlayer(),
                            event.getHand(),
                            event.getItem()
                    );
                    if (remedy != null) {
                        Logging.debug(
                                "RemedyInteract",
                                "onPlayerInteract",
                                "Remedy found - " + remedy.getDisplayName()
                        );
                        // If not a food and not a drink, then interact with the Remedy
                        if (!remedy.isFood() && !remedy.isDrink()) {
                            RemedyManager.interact(remedy, event.getPlayer(), event.getHand(), null);
                            // It was a remedy, cancel the event
                            return new PlayerInteract.PlayerInteractResult(
                                    true,
                                    Event.Result.DENY,
                                    Event.Result.DENY
                            );
                        } else {
                            Logging.debug(
                                    "RemedyInteract",
                                    "onPlayerInteract",
                                    "Remedy is food or drink - do not cancel event"
                            );
                        }

                    } else {
                        Logging.debug(
                                "RemedyInteract",
                                "onPlayerInteract",
                                "No Remedy found"
                        );
                    }
                }
            }
        }
        return new PlayerInteract.PlayerInteractResult(
                false,
                Event.Result.ALLOW,
                Event.Result.ALLOW
        );
    }

    /**
     * onPlayerItemConsume runs player item consume code for remedies.  It takes in the PlayerItemConsume
     * from the listener and returns a cancellation flag.  If true, cancel the event.
     * @param event PlayerItemConsume that was called
     * @return boolean If true, cancel the PlayerItemConsume
     */
    public static boolean onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return true;
        }
        if (event.getItem() != null) {
            Logging.debug(
                    "RemedyInteract",
                    "onPlayerItemConsume",
                    "Checking for remedy"
            );
            Remedy remedy = RemedyManager.getRemedy(
                    event.getPlayer(),
                    null,
                    event.getItem()
            );
            if (remedy != null) {
                if (remedy.isFood() || remedy.isDrink()) {
                    Logging.debug(
                            "RemedyInteract",
                            "onPlayerItemConsume",
                            "Item was a remedy and is either food or drink"
                    );
                    int itemHash = event.getItem().hashCode();
                    int mainHandHash = 0;
                    int offHandHash = 0;
                    ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
                    ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();
                    if (mainHand != null) {
                        mainHandHash = mainHand.hashCode();
                    }
                    if (offHand != null) {
                        offHandHash = offHand.hashCode();
                    }
                    EquipmentSlot equipmentSlot = null;
                    if (itemHash == mainHandHash) {
                        equipmentSlot = EquipmentSlot.HAND;
                    }
                    else if (itemHash == offHandHash) {
                        equipmentSlot = EquipmentSlot.OFF_HAND;
                    }
                    RemedyManager.interact(remedy, event.getPlayer(), equipmentSlot, event.getItem());

                    if (remedy.getItemUses() > 0) {
                        // cancel the event if it is a multi-use item
                        return true;
                    }
                    if (remedy.isItemConsume()) {
                        // Do not cancel the event - it would prevent the item
                        // being taken from the player
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return false; // Never cancel the event for Remedies
    }

    /**
     * getRemedy gets the remedy that player has in hand.  If it is not a remedy, it
     * returns null
     * @param player The Player to check
     * @param equipmentSlot The EquipmentSlot (hand) to check
     * @param itemStack The ItemStack to check
     * @return Remedy in hand (null if not a remedy)
     */
    public static Remedy getRemedy(Player player, EquipmentSlot equipmentSlot, ItemStack itemStack) {
        Epidemic plugin = Epidemic.instance();
        if (itemStack != null) {
            if (itemStack.getType() != Material.AIR) {
                // Item exists in hand
                for (Map.Entry<String, Remedy> entry : plugin.data().getRemedies().entrySet()) {
                    if (entry.getValue().isMatch(itemStack)) {
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * interact runs the Remedy use() function for the player and equipment slot
     * It requires that we already know the Remedy
     * @param remedy The Remedy to use
     * @param player The Player to use the remedy for
     * @param equipmentSlot The EquipmentSlot to use
     * @param consumedItem The consumed ItemStack
     */
    public static void interact(Remedy remedy, Player player, EquipmentSlot equipmentSlot, ItemStack consumedItem) {
        remedy.use(player, player, equipmentSlot, consumedItem);
    }

    /**
     * interactOther runs the Remedy use() function for the player and equipment slot
     * It requires that we already know the Remedy
     * @param remedy The Remedy to use
     * @param player The Player using the remedy
     * @param targetPlayer The player to use the remedy for
     * @param equipmentSlot The EquipmentSlot to use
     * @param consumedItem The consumed ItemStack
     */
    public static void interactOther(Remedy remedy, Player player, Player targetPlayer, EquipmentSlot equipmentSlot, ItemStack consumedItem) {
        remedy.use(targetPlayer, player, equipmentSlot, consumedItem);
    }

    /**
     * Applies symptom relief to a player
     * @param player Player to apply the relief to
     * @param seconds Number of seconds to apply the relief for
     */
    public static void symptomRelief(Player player, int seconds) {
        Epidemic plugin = Epidemic.instance();

        // Get current timestamp for our starting point
        Timestamp reliefUntil = TimeFunctions.now();

        // Check if the player already has symptom relief
        if (plugin.data().hasSymptomRelief(player.getUniqueId())) {
            // If the player does have symptom relief, get the end timestamp
            Timestamp existingReliefUntil = plugin.data().getSymptomReliefUntil(player.getUniqueId());
            // Check if the end timestamp is in the future
            if (TimeFunctions.inFuture(existingReliefUntil)) {
                // If the relief lasts for an excessive amount of time, don't apply
                if (TimeFunctions.secondsFromNow(existingReliefUntil) > 1800) {
                    Logging.debug(
                            "RemedyManager",
                            "symptomRelief",
                            "Player already has relief applied that lasts > 1800 seconds - Not applying"
                    );
                    return;
                }
                // If it is in the future, set that timestamp as our starting point
                reliefUntil = plugin.data().getSymptomReliefUntil(player.getUniqueId());
            }

        }

        // Add the number of seconds to the timestamp (either now, or end date of existing relief)
        reliefUntil = TimeFunctions.addSeconds(reliefUntil, seconds);
        // Set the new timestamp on player symptom relief
        plugin.data().addSymptomRelief(player.getUniqueId(), reliefUntil);

        // Loop through any current afflictions and remove the potion effects
        if (Epidemic.instance().data().getPlayerAfflictions().containsKey(player.getUniqueId())) {
            for (Afflicted afflicted : Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId())) {
                for (PotionEffect effect : player.getActivePotionEffects())
                {
                    if (afflicted.getAilment().getAilmentEffects() != null) {
                        for (PlayerPotionEffect ppe : afflicted.getAilment().getAilmentEffects()) {
                            player.removePotionEffect(ppe.getPotionEffectType());
                        }
                    } else {
                        player.removePotionEffect(effect.getType());
                    }
                }
            }
        }

    }
}
