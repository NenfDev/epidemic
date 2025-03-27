package com.ibexmc.epidemic.thirst;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.ParticleFunctions;
import com.ibexmc.epidemic.util.functions.SoundFunctions;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.ibexmc.epidemic.events.player.PlayerInteract;

import java.util.HashMap;

public class ThirstManager {

    /**
     * onPlayerConsume runs player item consume code for thirst.  It is looking specifically for players drinking
     * from regular water bottles.  It also uses a random number generator and the configured dirty water chance
     * to return if it is a clean water bottle or dirty.  It takes in the PlayerItemConsumeEvent from the listener
     * and returns a cancellation flag.  If true, cancel the event.
     * @param event PlayerItemConsumeEvent that was called
     * @return boolean If true, cancel the event
     */
    public static boolean onPlayerConsume(PlayerItemConsumeEvent event) {
        Thirst.WaterSource waterSource = Thirst.isWaterSource(
                event.getPlayer(),
                Action.PHYSICAL,
                event.getItem(),
                null
        );
        HashMap<String, String> placeHolder = new HashMap<>();
        boolean validDrink = false;
        if (waterSource != null) {
            if (waterSource != Thirst.WaterSource.NULL) {
                switch (waterSource) {
                    case CLEAN_BOTTLE:
                        SetThirst.add(event.getPlayer(), 10);
                        placeHolder.put("<%source%>", Locale.localeText("bottle", "the bottle", null));
                        validDrink = true;
                        break;
                    case DIRTY_BOTTLE:
                        SetThirst.add(event.getPlayer(), -30);
                        ParticleFunctions.vomit(event.getPlayer());
                        event.getPlayer().setFoodLevel(1);
                        PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, (5 * 20), 2);
                        event.getPlayer().addPotionEffect(potionEffect);
                        placeHolder.put("<%source%>", Locale.localeText("bottle", "the bottle", null));
                        SendMessage.Player(
                                "dirty_water",
                                "&cThat water had something nasty in it!",
                                event.getPlayer(),
                                true,
                                null
                        );
                        validDrink = true;
                        break;
                    default:
                        break;
                }

                if (Epidemic.instance().config.displayDrinkNotification() && validDrink) {
                    SendMessage.actionBar(
                            event.getPlayer(),
                            Locale.localeText("drink_notification", "You drink from <%source%>", placeHolder)
                    );
                }
            }
        }
        return false; // Never cancel the event
    }

    /**
     * onPlayerInteract runs player interaction code for thirst.  It takes in the PlayerInteractEvent
     * from the listener and returns a cancellation flag.  If true, cancel the event.
     * @param event PlayerInteractEvent that was called
     * @return PlayerInteract.PlayerInteractResult Contains the cancellation flags for PlayerInteractEvent
     */
    public static PlayerInteract.PlayerInteractResult onPlayerInteract(PlayerInteractEvent event) {
        // Check if it is a (non-remedy) water source
        Logging.debug(
                "ThirstManager",
                "onPlayerInteract",
                "Starting"
        );
        if (event.getHand() == null) {
            Logging.debug(
                    "ThirstManager",
                    "onPlayerInteract",
                    "Returning not cancelled - hand is null"
            );
            return new PlayerInteract.PlayerInteractResult(
                    false,
                    Event.Result.ALLOW,
                    Event.Result.ALLOW
            );
        }
        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            Logging.debug(
                    "ThirstManager",
                    "onPlayerInteract",
                    "Returning not cancelled - getHand is not HAND"
            );
            return new PlayerInteract.PlayerInteractResult(
                    false,
                    Event.Result.ALLOW,
                    Event.Result.ALLOW
            );
        }
        Thirst.WaterSource waterSource = Thirst.isWaterSource(
                event.getPlayer(),
                event.getAction(),
                event.getItem(),
                event.getClickedBlock()
        );
        HashMap<String, String> placeHolder = new HashMap<>();

        if (waterSource != null) {

            Logging.debug(
                    "ThirstManager",
                    "onPlayerInteract",
                    "Water source is not null - " + waterSource.name()
            );

            if (waterSource != Thirst.WaterSource.NULL) {
                PotionEffect poisonEffect = new PotionEffect(PotionEffectType.POISON, (5 * 20), 2);
                switch (waterSource) {
                    case CLEAN_HAND:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is clean hand for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), 2);
                        placeHolder.put("<%source%>", Locale.localeText("hands", "your cupped hands", null));
                        break;
                    case DIRTY_HAND:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is dirty hand for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), -25);
                        ParticleFunctions.vomit(event.getPlayer());
                        event.getPlayer().setFoodLevel(1);
                        event.getPlayer().addPotionEffect(poisonEffect);
                        SendMessage.Player(
                                "dirty_water",
                                "&cThat water had something nasty in it!",
                                event.getPlayer(),
                                true,
                                null
                        );
                        placeHolder.put("<%source%>", Locale.localeText("hands", "your cupped hands", null));
                        break;
                    case CLEAN_BOWL:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is clean bowl for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), 5);
                        placeHolder.put("<%source%>", Locale.localeText("bowl", "the bowl", null));
                        break;
                    case DIRTY_BOWL:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is dirty bowl for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), -25);
                        ParticleFunctions.vomit(event.getPlayer());
                        event.getPlayer().setFoodLevel(1);
                        event.getPlayer().addPotionEffect(poisonEffect);
                        SendMessage.Player(
                                "dirty_water",
                                "&cThat water had something nasty in it!",
                                event.getPlayer(),
                                true,
                                null
                        );
                        placeHolder.put("<%source%>", Locale.localeText("bowl", "the bowl", null));
                        break;
                    case CAULDRON:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is cauldron for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), 10);
                        placeHolder.put("<%source%>", Locale.localeText("cauldron", "the cauldron", null));
                        break;
                    case BOILED_CAULDRON:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is boiled cauldron for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), 50);
                        placeHolder.put("<%source%>", Locale.localeText("cauldron", "the cauldron", null));
                        break;
                    case DIRTY_CAULDRON:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is dirty cauldron for " + event.getPlayer().getName()
                        );
                        SetThirst.add(event.getPlayer(), -50);
                        ParticleFunctions.vomit(event.getPlayer());
                        event.getPlayer().setFoodLevel(1);
                        event.getPlayer().addPotionEffect(poisonEffect);
                        SendMessage.Player(
                                "dirty_water",
                                "&cThat water had something nasty in it!",
                                event.getPlayer(),
                                true,
                                null
                        );
                        placeHolder.put("<%source%>", Locale.localeText("cauldron", "the cauldron", null));
                        break;
                    default:
                        Logging.debug(
                                "ThirstManager",
                                "onPlayerInteract",
                                "Water source is unexpected for " + event.getPlayer().getName() + " - " + waterSource.name()
                        );
                        break;
                }

                if (Epidemic.instance().config.displayDrinkNotification()) {
                    SendMessage.actionBar(
                            event.getPlayer(),
                            Locale.localeText("drink_notification", "You drink from <%source%>", placeHolder)
                    );
                }

                SoundFunctions.playerSoundPlayer(event.getPlayer(), Sound.ENTITY_GENERIC_DRINK, 0.2f, 1.2f);
            } else {
                Logging.debug(
                        "ThirstManager",
                        "onPlayerInteract",
                        "Water source is returned as WaterSource.NULL"
                );
            }
        }

        Logging.debug(
                "ThirstManager",
                "onPlayerInteract",
                "Returning not cancelled - end of code"
        );
        // Never cancel the event
        return new PlayerInteract.PlayerInteractResult(
                false,
                Event.Result.ALLOW,
                Event.Result.ALLOW
        );
    }
}
