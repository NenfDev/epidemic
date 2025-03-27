package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.util.*;
import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.events.player.PlayerInteract;
import com.ibexmc.epidemic.util.functions.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class EquipmentManager {

    /**
     * Runs when the player interacts with something
     * @param event PlayerInteractEvent event
     * @return PlayerInteractResult
     */
    public static PlayerInteract.PlayerInteractResult onPlayerInteract(PlayerInteractEvent event) {
        boolean processItem = false;
        ItemStack inHand = event.getItem();
        if (inHand != null && event.getItem() != null) {
            if (event.getHand() != null) {
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
        }
        if (event.getHand() != null) {
            if (event.getHand().equals(EquipmentSlot.HAND) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                ItemStack input = event.getPlayer().getInventory().getItemInMainHand();
                if (Epidemic.instance().persistentData().hasString(input, PersistentData.Key.AILMENT)) {
                    String ailmentKey = Epidemic.instance().persistentData().getString(input, PersistentData.Key.AILMENT);
                    Ailment ailment = AilmentManager.getAilmentByInternalName(ailmentKey);
                    if (ailment != null) {
                        IEquipment infectedSample = Epidemic.instance().data().equipment.get("infected_sample");
                        if (infectedSample != null) {
                            ItemStack output = infectedSample.get();
                            ItemMeta itemMeta = output.getItemMeta();
                            if (itemMeta != null) {
                                setAilment(itemMeta, ailment);
                                output.setItemMeta(itemMeta);

                                if (event.getClickedBlock() != null) {
                                    Block block = event.getClickedBlock();
                                    if (block.getType() == Material.GLASS) {
                                        Block blockBelow = block.getLocation().clone().add(0, -1, 0).getBlock();
                                        if (blockBelow.getType() == Material.IRON_BLOCK) {
                                            Block blockAbove = block.getLocation().clone().add(0, 1, 0).getBlock();
                                            if (blockAbove.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                                                IEquipment eHazmatSuit = Epidemic.instance().data().equipment.get("hazmat_suit");
                                                HazmatSuit hazmatSuit = (HazmatSuit) eHazmatSuit;
                                                if (hazmatSuit != null) {
                                                    if (hazmatSuit.equipped(event.getPlayer())) {
                                                        Logging.log("EquipmentManager.onPlayerInteract", "Accessing Incubation Chamber");
                                                        InventoryFunctions.addItemStackAmount(input, -1);
                                                        SoundFunctions.playSoundWorld(block.getLocation(), 1.0f, 1.0f, Sound.BLOCK_BREWING_STAND_BREW);
                                                        EquipmentManager.incubationChamber(blockBelow.getLocation().clone(), event.getPlayer(), output);

                                                    } else {
                                                        SendMessage.Player(
                                                                "incubation_hazmat_required",
                                                                "&4Sorry, you must wear a Hazmat suit to use the Incubation chamber",
                                                                event.getPlayer(),
                                                                true,
                                                                null
                                                        );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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
                    // Check if the item in hand is equipment
                    IEquipment equipment = getEquipment(
                            event.getPlayer(),
                            event.getHand(),
                            event.getItem()
                    );
                    if (equipment != null) {
                        Logging.debug(
                                "EquipmentManager",
                                "onPlayerInteract",
                                "Equipment found - " + equipment.name()
                        );
                        // do stuff

                        // If throwable, do not cancel the event
                        if (equipment.throwable()) {
                            return new PlayerInteract.PlayerInteractResult(
                                    false,
                                    Event.Result.ALLOW,
                                    Event.Result.ALLOW
                            );
                        }
                        interact(event.getPlayer(), equipment, event.getHand());
                        // Equipment task performed, cancel the event
                        return new PlayerInteract.PlayerInteractResult(
                                true,
                                Event.Result.DENY,
                                Event.Result.DENY
                        );
                    } else {
                        Logging.debug(
                                "EquipmentManager",
                                "onPlayerInteract",
                                "No Equipment found"
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
     * Runs when the player interacts with an entity
     * @param event PlayerInteractEntityEvent event
     * @return If true, interacted successfully
     */
    public static boolean onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        ItemStack inHand = null;
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            inHand = event.getPlayer().getInventory().getItemInMainHand();
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            inHand = event.getPlayer().getInventory().getItemInOffHand();
        }
        if (inHand == null) {
            return false;
        }
        if (EquipmentManager.hasEquipmentKey(inHand)) {
            if (EquipmentManager.getEquipmentKey(inHand).equalsIgnoreCase("empty_syringe")) {
                EmptySyringe emptySyringe = new EmptySyringe();
                ItemStack fullSyringe = emptySyringe.use(event.getRightClicked());
                if (fullSyringe != null) {
                    inHand.setAmount(inHand.getAmount() - 1);
                    if (event.getHand().equals(EquipmentSlot.HAND)) {
                        event.getPlayer().getInventory().setItemInMainHand(inHand);
                    } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                        event.getPlayer().getInventory().setItemInOffHand(inHand);
                    }
                    InventoryFunctions.addItemsToPlayerInventory(event.getPlayer(), fullSyringe, true);

                }
                event.setCancelled(true);
                return true;
            }
            if (EquipmentManager.getEquipmentKey(inHand).equalsIgnoreCase("full_syringe")) {
                // Get ailment key from persistent data container and get ailment, if not found, null
                Ailment ailment = Epidemic.instance().data().getAvailableAilmentByInternalName(
                        Epidemic.instance().persistentData().getString(inHand, PersistentData.Key.AILMENT)
                );
                FullSyringe fullSyringe = new FullSyringe();
                inHand = fullSyringe.use(event.getRightClicked(), ailment);
                event.getPlayer().getInventory().setItemInMainHand(inHand);
                SoundFunctions.playSoundWorld(event.getPlayer().getLocation(), 2, 3, Sound.BLOCK_BREWING_STAND_BREW);
                event.setCancelled(true);
                return true;
            }
        }

        return false;

    }

    /**
     * Gets an infected sample from the incubation chamber
     * @param blockLocation Block location
     * @param player Player
     * @param output Output required
     */
    public static void incubationChamber(Location blockLocation, Player player, ItemStack output) {
        new BukkitRunnable() {
            private int step = 0;
            Location location = blockLocation.clone().add(0.5,1,0.5);
            public void run() {
                step++;
                int r = 212;
                int g = 53;
                int b = 19;

                if (step >= 0 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.01,
                            location.getZ());
                    ParticleFunctions.displayParticleDust(
                            location,
                            5,  // count
                            r, // red
                            g,   // green
                            b,   // blue
                            2    // size
                    );
                }
                if (step >= 5 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.02,
                            location.getZ());
                    ParticleFunctions.displayParticleDust(
                            location,
                            3,  // count
                            r + 5, // red
                            g,   // green
                            b,   // blue
                            1    // size
                    );
                }
                if (step >= 10 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.03,
                            location.getZ());
                    ParticleFunctions.displayParticleDust(
                            location,
                            1,  // count
                            r + 10, // red
                            g,   // green
                            b,   // blue
                            1    // size
                    );
                }


                if (step >= 15) {
                    InventoryFunctions.addItemsToPlayerInventory(player, output, true);
                    this.cancel();
                    SoundFunctions.playSoundWorld(blockLocation, 1.0f, 1.0f, Sound.BLOCK_FIRE_EXTINGUISH);
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Gets Equipment the player has in hand, returns null if not equipment
     * @param player The Player to check
     * @param equipmentSlot The EquipmentSlot (hand) to check
     * @param itemStack The ItemStack to check
     * @return IEquipment in hand (null if not a remedy)
     */
    public static IEquipment getEquipment(Player player, EquipmentSlot equipmentSlot, ItemStack itemStack) {
        Epidemic plugin = Epidemic.instance();
        if (itemStack != null) {
            if (EquipmentManager.hasEquipmentKey(itemStack)) {
                String equipmentKey = EquipmentManager.getEquipmentKey(itemStack);
                if (plugin.data().equipment.containsKey(equipmentKey)) {
                    return plugin.data().equipment.get(equipmentKey);
                }
            }
        }
        return null;
    }

    /**
     * Handles the interaction of Epidemic equipment
     * @param player
     * @param equipment
     * @param equipmentSlot
     */
    public static void interact(Player player, IEquipment equipment, EquipmentSlot equipmentSlot) {
        // Hazmat suit


        if (equipment instanceof HazmatSuit) {
            HazmatSuit hazmatSuit = (HazmatSuit) equipment;
            if (hazmatSuit != null) {
                hazmatSuit.apply(player);
                switch (equipmentSlot) {
                    case HAND:
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        break;
                    case OFF_HAND:
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
                        break;
                    default:
                        break;
                }
            }
        }

        if (equipment instanceof GasMask) {
            GasMask gasMask = (GasMask) equipment;
            gasMask.apply(player, equipmentSlot);
        }

        // Empty syringe
        if (equipment instanceof EmptySyringe) {
            EmptySyringe emptySyringe = (EmptySyringe) equipment;
            if (emptySyringe != null) {
                if (player.isSneaking()) {
                    player.getInventory().setItemInMainHand(emptySyringe.use(player));
                }
            }
        }
    }

    /**
     * Checks if the ItemStack has the epidemic equipment key
     * @param itemStack ItemStack to check
     * @return If true, ItemStack has the equipment tag
     */
    public static boolean hasEquipmentKey(ItemStack itemStack) {
        Logging.debug("EquipmentManager", "hasEquipmentKey", "Starting");
        return Epidemic.instance().persistentData().hasString(itemStack, PersistentData.Key.EQUIPMENT);
    }

    /**
     * Gets the equipment key value from the ItemStack provided
     * @param itemStack ItemStack to get key value from
     * @return Equipment key value, returns blank string if not found
     */
    public static String getEquipmentKey(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }
        if (itemStack.getType() == Material.AIR) {
            return "";
        }
        if (Epidemic.instance().persistentData().hasString(itemStack, PersistentData.Key.EQUIPMENT)) {
            return StringFunctions.isNull(Epidemic.instance().persistentData().getString(itemStack, PersistentData.Key.EQUIPMENT), "");
        } else {
            return "";
        }
    }

    /**
     * Sets the ailment on an ItemMeta
     * @param itemMeta ItemMeta
     * @param ailment Ailment
     */
    public static void setAilment(ItemMeta itemMeta, Ailment ailment) {
        itemMeta.setLore(StringFunctions.addLineToStringList(itemMeta.getLore(), "&fInfected with:&c " + ailment.getDisplayName()));
        Epidemic.instance().persistentData().setString(itemMeta, PersistentData.Key.AILMENT, ailment.getInternalName());
    }

    /**
     * Applies an ailment to a full syringe
     * @param syringe
     * @param ailment
     * @return
     */
    public static ItemStack applyAilmentToFullSyringe(ItemStack syringe, Ailment ailment) {
        if (hasEquipmentKey(syringe)) {
            if (!getEquipmentKey(syringe).equalsIgnoreCase("full_syringe")) {
                return syringe;
            }
        } else {
            return syringe;
        }
        ItemMeta itemMeta = syringe.getItemMeta();
        if (itemMeta != null) {
            if (ailment != null) {
                setAilment(itemMeta, ailment);
            } else {
                // no ailment in blood
                itemMeta.setLore(StringFunctions.addLineToStringList(itemMeta.getLore(), "&fNot infected"));
            }
            syringe.setItemMeta(itemMeta);
        }
        return syringe;
    }

    /**
     * Applies an ailment to an infected arrow
     * @param arrow Arrow
     * @param ailment Ailment
     * @return Arrow with infection
     */
    public static ItemStack applyAilmentToArrow(ItemStack arrow, Ailment ailment) {
        if (hasEquipmentKey(arrow)) {
            if (!getEquipmentKey(arrow).equalsIgnoreCase("infected_arrow")) {
                return arrow;
            }
        } else {
            return arrow;
        }
        ItemMeta itemMeta = arrow.getItemMeta();
        if (itemMeta != null) {
            if (ailment != null) {
                setAilment(itemMeta, ailment);
            } else {
                // no ailment in blood, return plain arrow
                 InfectedArrow infectedArrow = (InfectedArrow) Epidemic.instance().data().equipment.get("infected_arrow");
                 return infectedArrow.getUninfected(arrow.getAmount());
            }
        }
        arrow.setItemMeta(itemMeta);
        return arrow;
    }

    /**
     * Applies an ailment to an infected splash potion
     * @param splashPotion Splash Potion
     * @param ailment Ailment
     * @return Splash Potion with infection
     */
    public static ItemStack applyAilmentToSplashPotion(ItemStack splashPotion, Ailment ailment) {
        if (hasEquipmentKey(splashPotion)) {
            if (!getEquipmentKey(splashPotion).equalsIgnoreCase("infected_splash_potion")) {
                return splashPotion;
            }
        } else {
            return splashPotion;
        }
        ItemMeta itemMeta = splashPotion.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                if (ailment != null) {
                    setAilment(itemMeta, ailment);
                } else {
                    // no ailment in blood, return plain arrow
                    InfectedSplashPotion infectedSplashPotion = (InfectedSplashPotion) Epidemic.instance().data().equipment.get("infected_splash_potion");
                    return infectedSplashPotion.getUninfected(splashPotion.getAmount());
                }
                splashPotion.setItemMeta(potionMeta);
            } else {
                if (ailment != null) {
                    setAilment(itemMeta, ailment);
                } else {
                    // no ailment in blood, return plain arrow
                    InfectedSplashPotion infectedSplashPotion = (InfectedSplashPotion) Epidemic.instance().data().equipment.get("infected_splash_potion");
                    return infectedSplashPotion.getUninfected(splashPotion.getAmount());
                }
                splashPotion.setItemMeta(itemMeta);
            }
        }
        return splashPotion;
    }

    /**
     * Checks if a hazmat suit should be destroyed
     * Gets the destroy-chance from Hazmat suit (0-10) and a random number
     * and if the destroy-chance is less than the random number, returns true
     * @return If true, the hazmat suit should be destroyed
     */
    public static boolean checkDestroyHazmatSuit() {
        HazmatSuit hazmatSuit = (HazmatSuit) Epidemic.instance().data().equipment.get("hazmat_suit");
        if (hazmatSuit != null) {
            int destroyChance = hazmatSuit.destroyChance();
            int random = NumberFunctions.random(10);
            return (destroyChance < random);
        } else {
            return false;
        }
    }

    /**
     * Loads the equipment items
     */
    public static void load() {

        Epidemic plugin = Epidemic.instance();

        if (plugin.config.isDisableEpidemicEquipment()) {
            return;
        }
        IEquipment emptySyringe = new EmptySyringe();
        plugin.data().equipment.put(emptySyringe.key(), emptySyringe);

        IEquipment fullSyringe = new FullSyringe();
        plugin.data().equipment.put(fullSyringe.key(), fullSyringe);

        IEquipment gasMask = new GasMask();
        plugin.data().equipment.put(gasMask.key(), gasMask);

        IEquipment hazmatPart1 = new HazmatPart1();
        plugin.data().equipment.put(hazmatPart1.key(), hazmatPart1);

        IEquipment hazmatPart2 = new HazmatPart2();
        plugin.data().equipment.put(hazmatPart2.key(), hazmatPart2);

        IEquipment hazmatSuit = new HazmatSuit();
        plugin.data().equipment.put(hazmatSuit.key(), hazmatSuit);

        IEquipment infectedArrow = new InfectedArrow();
        plugin.data().equipment.put(infectedArrow.key(), infectedArrow);

        IEquipment infectedSplashPotion = new InfectedSplashPotion();
        plugin.data().equipment.put(infectedSplashPotion.key(), infectedSplashPotion);

        IEquipment infectedSample = new InfectedSample();
        plugin.data().equipment.put(infectedSample.key(), infectedSample);


    }
}
