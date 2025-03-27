package com.ibexmc.epidemic.thirst;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.MaterialGroups;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.MathUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.boss.BarColor;

import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import com.ibexmc.epidemic.util.functions.BlockFunctions;

import java.util.*;

public class Thirst {

    //region Enums
    public enum WaterSource {
        CLEAN_BOTTLE,
        DIRTY_BOTTLE,
        POTION_BOTTLE,
        CLEAN_HAND,
        DIRTY_HAND,
        CLEAN_BOWL,
        DIRTY_BOWL,
        CAULDRON,
        BOILED_CAULDRON,
        DIRTY_CAULDRON,
        NULL
    }
    //endregion
    //region Objects
    private UUID uuid;
    private double thirstAmount;
    private BossBar thirstBar;
    //endregion
    //region Constructors
    public Thirst(UUID uuid, double thirstAmount) {
        this.uuid = uuid;
        this.thirstAmount = thirstAmount;
        if (Epidemic.instance().config.useThirstBar()) {
            this.thirstBar = Bukkit.createBossBar(Locale.localeText("thirst_bar_title", "Thirst", null), BarColor.BLUE, BarStyle.SEGMENTED_20);
            Player player = Bukkit.getPlayer(uuid);
            List<Player> lstThirstBar = thirstBar.getPlayers();
            if (player != null) {
                if (lstThirstBar != null) {
                    for (Player p : lstThirstBar) {
                        if (p.getUniqueId().equals(player.getUniqueId())) {
                            Logging.dev("Thirst", "Constructor", "Removing bossbar from " + player.getUniqueId());
                            thirstBar.removePlayer(player);
                        }
                    }
                }
                Logging.dev("Thirst", "Constructor", "Adding bossbar from " + player.getUniqueId());
                thirstBar.addPlayer(player);
            }
        }
    }
    //endregion
    //region Getters & Setters

    /**
     * Gets the unique identifier for the player
     * @return
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the player with their unique identifier
     * @param uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the thirst amount for the player
     * @return Thirst amount
     */
    public double getThirstAmount() {
        return thirstAmount;
    }

    /**
     * Sets the thirst amount for the player
     * @param thirstAmount Thist amount
     */
    public void setThirstAmount(double thirstAmount) {
        this.thirstAmount = thirstAmount;
    }

    /**
     * Gets the Thirst BossBar for the player
     * @return BossBar to use for Thirst
     */
    public BossBar getThirstBar() {
        if (Epidemic.instance().config.useThirstBar()) {
            return thirstBar;
        } else {
            return null;
        }
    }

    /**
     * Sets the Thirst BossBar
     * @param thirstBar BossBar to set
     */
    public void setThirstBar(BossBar thirstBar) {
        if (Epidemic.instance().config.useThirstBar()) {
            setThirstBar(thirstBar, false);
        }
    }

    /**
     * Sets the Thirst BossBar, can disable it (sets the bar to white)
     * @param thirstBar BossBar to set
     * @param disabled If true, disables the thirst bar
     */
    public void setThirstBar(BossBar thirstBar, boolean disabled) {
        if (Epidemic.instance().config.useThirstBar()) {
            this.thirstBar = thirstBar;
            if (!disabled) {
                if (this.thirstAmount < 10) {
                    this.thirstBar.setColor(BarColor.RED);
                } else if (this.thirstAmount >= 10 && this.thirstAmount < 20) {
                    this.thirstBar.setColor(BarColor.YELLOW);
                } else {
                    this.thirstBar.setColor(BarColor.BLUE);
                }
            } else {
                this.thirstBar.setColor(BarColor.WHITE);
            }
        }
    }
    //endregion
    //region Methods

    /**
     * Gets the thirst modifier based on Biome and time spent there
     * @param biome Biome to lookup
     * @param time Time spent
     * @return Thirst modifier
     */
    public static double getThirstModifier(Biome biome, int time) {
        double thirstModifier = 1;

        if (biome.name().contains("JUNGLE")) {
            if (time >= 14000 && time <= 23999) {
                // Hot at night
                thirstModifier = 1.5;
            }
        }

        if (biome.name().contains("BADLANDS") || biome.name().contains("DESERT")) {
            if (time >= 0 && time <= 12000) {
                // Hot in day
                thirstModifier = 2;
            }
        }

        if (biome.name().contains("NETHER")) {
            thirstModifier = 3;
        }

        return thirstModifier;
    }

    /**
     * Gets the chance of contaminated water by biome
     * @param biome Biome to lookup
     * @return Chance of contaminated water
     */
    public static int getContaminatedWaterChanceByBiome(Biome biome) {
        int chance = 5;

        if (biome.name().contains("JUNGLE")) {
            chance = 10;
        }

        if (biome.name().contains("SWAMP")) {
            chance = 50;
        }

        return chance;
    }

    /**
     * Applies thirst to the player
     * @param player Player to apply thirst for
     */
    public static void applyThirst(Player player) {
        //Logging.log("applyThirst", "applyThirst started for " + player.getUniqueId());
        if (Epidemic.instance().config.isEnableThirst()) {
            //Logging.log("applyThirst", "Thirst is enabled");
            if (Epidemic.instance().data().getThirstBar() != null) {
                if (Epidemic.instance().data().getThirstBar().containsKey(player.getUniqueId())) {
                    Thirst thirst = Epidemic.instance().data().getThirstBar().get(player.getUniqueId());
                    if (thirst != null) {
                        boolean skipPlayer = thirst.disabled(player);

                        if (!skipPlayer) {
                            if (player != null) {
                                double oldThirstAmount = thirst.getThirstAmount();
                                double thirstAmount = thirst.getThirstAmount();

                                World world= player.getWorld();
                                if (thirstAmount > 0) {

                                    double thirstModifier = getThirstModifier(player.getLocation().getBlock().getBiome(), java.lang.Math.toIntExact(world.getTime()));

                                    // Skip if player is in water
                                    Material m = player.getLocation().getBlock().getType();
                                    if (m == Material.WATER) {
                                        thirstModifier = 0;
                                    }

                                    // Skip if it is storming
                                    if (player.getLocation().getWorld().hasStorm()) {
                                        thirstModifier = 0;
                                    }

                                    Logging.dev(
                                            "Thirst",
                                            "applyThirst(Player)",
                                            "Updating Player: " +
                                                    player.getName() +
                                                    " | Biome: " +
                                                    player.getLocation().getBlock().getBiome().name() +
                                                    " | Time: " +
                                                    java.lang.Math.toIntExact(world.getTime()) +
                                                    " | Player Location Block: " +
                                                    player.getLocation().getBlock().getType() +
                                                    " | Storming: " +
                                                    player.getLocation().getWorld().hasStorm() +
                                                    " | Current Thirst: " +
                                                    thirstAmount +
                                                    " | New Thirst: " +
                                                    (thirstAmount - thirstModifier) +
                                                    " | Thirst Modifier: " +
                                                    thirstModifier
                                    );

                                    if ((thirstAmount - thirstModifier) >= 0) {
                                        thirstAmount = thirstAmount - thirstModifier;
                                    } else {
                                        thirstAmount = 0;
                                    }

                                    if (Epidemic.instance().config.displayThirstWarning()) {
                                        if (oldThirstAmount > thirstAmount) {
                                            if (MathUtil.between(thirstAmount, 0, 10) && oldThirstAmount > 10) {
                                                // Display Warning
                                                SendMessage.title(
                                                        player,
                                                        Locale.localeText("thirst_warning_title_10", "&4You're dangerously thirsty!", null),
                                                        Locale.localeText("thirst_warning_10", "&4If you don't drink soon, you might die!", null),
                                                        10,
                                                        60,
                                                        10
                                                );
                                            } else if ((MathUtil.between(thirstAmount, 10, 20)) && oldThirstAmount > 20) {
                                                // Display Warning
                                                SendMessage.title(
                                                        player,
                                                        Locale.localeText("thirst_warning_title_20", "&6You're parched!", null),
                                                        Locale.localeText("thirst_warning_20", "&6You need to drink soon", null),
                                                        10,
                                                        60,
                                                        10
                                                );
                                            } else if ((MathUtil.between(thirstAmount, 40, 50)) && oldThirstAmount > 50) {
                                                // Display Warning
                                                SendMessage.title(
                                                        player,
                                                        Locale.localeText("thirst_warning_title_50", "&bYou're starting to get thirsty", null),
                                                        Locale.localeText("thirst_warning_50", "&bYou should drink as soon as possible", null),
                                                        10,
                                                        60,
                                                        10
                                                );
                                            }
                                        }
                                    }

                                    SetThirst.set(player, thirstAmount);

                                }
                                else {
                                    player.damage(Epidemic.instance().config.thirstDamage());
                                }
                            }
                        } else {
                            Logging.dev(
                                    "Thirst",
                                    "ApplyThirst",
                                    "Skipped for " + player.getName()
                            );
                            BossBar thirstBar = thirst.getThirstBar();
                            thirst.setThirstBar(thirstBar, true);
                            //Epidemic.ThirstBar.put(player.getUniqueId(), thirst);
                            Epidemic.instance().data().setThirstBar(player.getUniqueId(), thirst);
                        }
                    }
                } else {
                    Logging.dev(
                            "Thirst",
                            "applyThirst()",
                            "No Thirst bar in live data for " +
                                    player.getName() + " (" + player.getUniqueId() + ")"
                    );
                }
            } else {
                Logging.dev(
                        "Thirst",
                        "applyThirst()",
                        "Thirst bar in live data returning null"
                );
            }
        } else {
            Logging.dev(
                    "Thirst",
                    "applyThirst()",
                    "Thirst is disabled"
            );
        }
    }

    /**
     * Returns true if the players thirst bar should be disabled
     * @param player Player to check
     * @return If true, bar should be disabled
     */
    private static boolean disabled(Player player) {
        if (player != null) {

             // Disable if the player is in bypass
            if (Permission.inBypass(player)) {
                return true;
            }

            // Disable if the player is in a non-thirst world
            World playerWorld = player.getLocation().getWorld();
            if (Epidemic.instance().config.getPreventThirstWorlds() != null) {
                for (String worldName : Epidemic.instance().config.getPreventThirstWorlds()) {
                    if (worldName.equalsIgnoreCase(playerWorld.getName())) {
                        return true;
                    }
                }
            }

            // Disable if the player is not in survival
            if (player.getGameMode() != GameMode.SURVIVAL) {
                return true;
            }

        } else {
            return true;
        }

        return false;
    }

    /**
     * Checks if the supplied ItemStack or Block is a water source
     * @param player Player to check if water source for
     * @param action Action performed by player (clicked air (item), clicked block (item or block)
     * @param itemStack ItemStack to check
     * @param block Block to check
     * @return WaterSource type.  WaterSource.NULL if not a water source
     */
    public static WaterSource isWaterSource(Player player, Action action, ItemStack itemStack, Block block) {
        if (Epidemic.instance().config.isEnableThirst()) {
            if (player == null) {
                Logging.debug(
                        "Thirst",
                        "isWaterSource",
                        "Unable to return a water source - player is null"
                );
                return WaterSource.NULL;
            }
            if (action == null) {
                Logging.debug(
                        "Thirst",
                        "isWaterSource",
                        "Unable to return a water source - action is null"
                );
                return WaterSource.NULL;
            }

            // Get the item in the main hand so we don't have issues getting with a bowl if offhand
            itemStack = player.getInventory().getItemInMainHand();

            // Water bottle check
            Logging.debug(
                    "Thirst",
                    "isWaterSource",
                    "Water Bottle Check"
            );
            if (action == Action.PHYSICAL) {
                Logging.debug(
                        "Thirst",
                        "isWaterSource",
                        "Water Bottle Check - Action is PHYSICAL"
                );
                if (itemStack != null) {
                    if (itemStack.getType().equals(Material.POTION)) {
                        Logging.debug(
                                "Thirst",
                                "isWaterSource",
                                "Water Bottle Check - Item in hand is a potion"
                        );
                        if (Epidemic.instance().config.dirtyCheckPotionDisabled()) {
                            return WaterSource.CLEAN_BOTTLE;
                        }
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta instanceof PotionMeta) {
                            PotionMeta potionMeta = (PotionMeta) itemMeta;
                            if (potionMeta != null) {
                                PotionData potionData = potionMeta.getBasePotionData();
                                if (potionData != null) {
                                    if (potionData.getType().equals(PotionType.WATER)) {
                                        Logging.debug(
                                                "Thirst",
                                                "isWaterSource",
                                                "Water Bottle Check - Bottle contains water"
                                        );
                                        int dirtyWaterChance = 0;
                                        dirtyWaterChance = Epidemic.instance().config.getDirtyWaterChance();
                                        int dirtyWater = 0;
                                        dirtyWater = dirtyWaterChance + Thirst.getContaminatedWaterChanceByBiome(
                                                player.getLocation().getBlock().getBiome()
                                        );
                                        Random random = new Random();
                                        int randomChance = (random.nextInt(1000) + 1);
                                        if (randomChance < dirtyWater) {
                                            return WaterSource.DIRTY_BOTTLE;
                                        } else {
                                            return WaterSource.CLEAN_BOTTLE;
                                        }
                                    } else {
                                        Logging.debug(
                                                "Thirst",
                                                "isWaterSource",
                                                "Water Bottle Check - Bottle contains something other than water"
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
                return WaterSource.NULL;
            }


            // Water Hands Check
            Logging.debug(
                    "Thirst",
                    "isWaterSource",
                    "Water Hands Check"
            );
            if (itemStack.getType().equals(Material.AIR) && player.isSneaking()) {
                Logging.debug(
                        "Thirst",
                        "isWaterSource",
                        "Water Hands Check - Player is sneaking with empty hands"
                );
                if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                    if (!Epidemic.instance().config.isWaterHandsDisabled()) {
                        Block targetBlock = player.getTargetBlock(null, 5);
                        if (targetBlock != null) {
                            if (targetBlock.getType().equals(Material.WATER)) {
                                Logging.debug(
                                        "Thirst",
                                        "isWaterSource",
                                        "Water Hands Check - Target block is water"
                                );
                                int dirtyWaterChance = 0;
                                dirtyWaterChance = Epidemic.instance().config.getDirtyWaterChance();
                                int dirtyWater = 0;
                                dirtyWater = dirtyWaterChance + Thirst.getContaminatedWaterChanceByBiome(
                                        player.getLocation().getBlock().getBiome()
                                );
                                Random random = new Random();
                                int randomChance = (random.nextInt(1000) + 1);
                                if ((randomChance - 10) < dirtyWater) {
                                    return WaterSource.DIRTY_HAND;
                                } else {
                                    return WaterSource.CLEAN_HAND;
                                }
                            } else {
                                Logging.debug(
                                        "Thirst",
                                        "isWaterSource",
                                        "Water Hands Check - Target block is not water"
                                );
                            }
                        } else {
                            Logging.debug(
                                    "Thirst",
                                    "isWaterSource",
                                    "Water Hands Check - No target block"
                            );
                        }
                    }
                }
            }


            // Water Bowl Check
            Logging.debug(
                    "Thirst",
                    "isWaterSource",
                    "Water bowl check"
            );
            if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                Logging.debug(
                        "Thirst",
                        "isWaterSource",
                        "Water Bowl Check - Right click block or air - checking for water bowl"
                );
                if (itemStack != null) {

                    if (itemStack.getType().equals(Material.BOWL)) {
                        Logging.debug(
                                "Thirst",
                                "isWaterSource",
                                "Water Bowl Check - Item in hand is a bowl"
                        );
                        if (!Epidemic.instance().config.isWaterBowlDisabled()) {
                            Logging.debug(
                                    "Thirst",
                                    "isWaterSource",
                                    "Water Bowl Check - Water bowls are enabled in the config"
                            );
                            Block targetBlock = player.getTargetBlock(null, 5);
                            if (targetBlock != null) {
                                Logging.debug(
                                        "Thirst",
                                        "isWaterSource",
                                        "Water Bowl Check - Target block is not null"
                                );
                                if (targetBlock.getType().equals(Material.WATER)) {
                                    Logging.debug(
                                            "Thirst",
                                            "isWaterSource",
                                            "Water Bowl Check - Target block is water"
                                    );
                                    int dirtyWaterChance = 0;
                                    dirtyWaterChance = Epidemic.instance().config.getDirtyWaterChance();
                                    int dirtyWater = 0;
                                    dirtyWater = dirtyWaterChance + Thirst.getContaminatedWaterChanceByBiome(
                                            player.getLocation().getBlock().getBiome()
                                    );
                                    Random random = new Random();
                                    int randomChance = (random.nextInt(1000) + 1);
                                    if (randomChance < dirtyWater) {
                                        return WaterSource.DIRTY_BOWL;
                                    } else {
                                        return WaterSource.CLEAN_BOWL;
                                    }
                                } else {
                                    Logging.debug(
                                            "Thirst",
                                            "isWaterSource",
                                            "Water Bowl Check - Target block is not water"
                                    );
                                }
                            } else {
                                Logging.debug(
                                        "Thirst",
                                        "isWaterSource",
                                        "Water Bowl Check - Target block is null"
                                );
                            }
                        } else {
                            Logging.debug(
                                    "Thirst",
                                    "isWaterSource",
                                    "Water Bowl Check - Water bowls are disabled in the config"
                            );
                        }
                    } else {
                        Logging.debug(
                                "Thirst",
                                "isWaterSource",
                                "Water Bowl Check - Item in hand is not a bowl"
                        );
                    }
                } else {
                    Logging.debug(
                            "Thirst",
                            "isWaterSource",
                            "Water Bowl Check - No item in hand"
                    );
                }
            }

            // Cauldron Check
            Logging.debug(
                    "Thirst",
                    "isWaterSource",
                    "Cauldron check"
            );
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                    if (block != null) {
                        if (MaterialGroups.isWaterCauldron(block.getType().name())) {
                            Logging.debug(
                                    "Thirst",
                                    "isWaterSource",
                                    "Cauldron Check - Clicked block is a cauldron"
                            );
                            if (!Epidemic.instance().config.isCauldronDisabled()) {
                                Logging.debug(
                                        "Thirst",
                                        "isWaterSource",
                                        "Cauldron Check - Cauldrons are enabled in the config"
                                );
                                if (block.getBlockData() instanceof Levelled) {
                                    Levelled cauldronData = (Levelled) block.getBlockData();
                                    if (cauldronData.getLevel() > 0) {
                                        Block blockBelow = block.getLocation().subtract(0, 1, 0).getBlock();
                                        if (BlockFunctions.isHeatSource(blockBelow)) {
                                            Logging.debug(
                                                    "Thirst",
                                                    "isWaterSource",
                                                    "Cauldron Check - Heat source found below cauldron"
                                            );
                                            return WaterSource.BOILED_CAULDRON;
                                        } else {
                                            Logging.debug(
                                                    "Thirst",
                                                    "isWaterSource",
                                                    "Cauldron Check - No heat source below cauldron"
                                            );
                                            int dirtyWaterChance = 0;
                                            dirtyWaterChance = Epidemic.instance().config.getDirtyWaterChance();
                                            int dirtyWater = 0;
                                            dirtyWater = dirtyWaterChance + Thirst.getContaminatedWaterChanceByBiome(
                                                    player.getLocation().getBlock().getBiome()
                                            );
                                            Random random = new Random();
                                            int randomChance = (random.nextInt(1000) + 1);
                                            if (randomChance < dirtyWater) {
                                                return WaterSource.DIRTY_CAULDRON;
                                            } else {
                                                return WaterSource.CAULDRON;
                                            }
                                        }
                                    }
                                }
                            } else {
                                Logging.debug(
                                        "Thirst",
                                        "isWaterSource",
                                        "Cauldron Check - Caudrons are disabled in the config"
                                );
                            }
                        } else {
                            Logging.debug(
                                    "Thirst",
                                    "isWaterSource",
                                    "Cauldron Check - Right clicked block is not a cauldron - " + block.getType().name()
                            );
                        }
                    } else {
                        Logging.debug(
                                "Thirst",
                                "isWaterSource",
                                "Cauldron Check - block is null"
                        );
                    }
                } else {
                    Logging.debug(
                            "Thirst",
                            "isWaterSource",
                            "Cauldron Check - No item in hand"
                    );
                }

            }


            return WaterSource.NULL;

        } else {
            return WaterSource.NULL;
        }
    }

    /**
     * Returns the thirst text used from /thirst when use_thirst_text = true
     * @return
     */
    public String getThirstText() {

        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return "";
        }
        boolean disabled = disabled(player);

        HashMap<String, String> placeHolder = new HashMap<>();
        placeHolder.put("<%thirst%>", "" + this.thirstAmount);
        String returnVal = "";
        if (this.thirstAmount < 1) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_0", "Thirst: &f==========", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_0", "Thirst: &4==========", placeHolder);
            }
        } else if (this.thirstAmount > 1 && this.thirstAmount < 10) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_1_9", "Thirst: &c7=&f=========", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_1_9", "Thirst: &c=&7=========", placeHolder);
            }
        } else if (this.thirstAmount >= 10 && this.thirstAmount < 20) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_10_19", "Thirst: &7=&f=========", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_10_19", "Thirst: &e=&7=========", placeHolder);
            }
        } else if (this.thirstAmount >= 20 && this.thirstAmount < 30) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_20_29", "Thirst: &7==&f========", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_20_29", "Thirst: &3==&7========", placeHolder);
            }
        } else if (this.thirstAmount >= 30 && this.thirstAmount < 40) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_30_39", "Thirst: &7===&f=======", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_30_39", "Thirst: &3===&7=======", placeHolder);
            }
        } else if (this.thirstAmount >= 40 && this.thirstAmount < 50) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_40_49", "Thirst: &7====&f======", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_40_49", "Thirst: &3====&7======", placeHolder);
            }
        } else if (this.thirstAmount >= 50 && this.thirstAmount < 60) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_50_59", "Thirst: &7=====&f=====", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_50_59", "Thirst: &3=====&7=====", placeHolder);
            }
        } else if (this.thirstAmount >= 60 && this.thirstAmount < 70) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_60_69", "Thirst: &7======&f====", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_60_69", "Thirst: &3======&7====", placeHolder);
            }
        } else if (this.thirstAmount >= 70 && this.thirstAmount < 80) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_70_79", "Thirst: &7=======&f===", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_70_79", "Thirst: &3=======&7===", placeHolder);
            }
        } else if (this.thirstAmount >= 80 && this.thirstAmount < 90) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_80_89", "Thirst: &7========&f==", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_80_89", "Thirst: &3========&7==", placeHolder);
            }
        } else if (this.thirstAmount >= 90 && this.thirstAmount < 100) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_90_99", "Thirst: &7=========&f=", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_90_99", "Thirst: &3=========&7=", placeHolder);
            }
        } else if (this.thirstAmount == 100) {
            if (disabled) {
                returnVal = Locale.localeText("thirst_text_disabled_100", "Thirst: &f==========", placeHolder);
            } else {
                returnVal = Locale.localeText("thirst_text_100", "Thirst: &b==========", placeHolder);
            }
        }

        return returnVal;
    }
    //endregion
}
