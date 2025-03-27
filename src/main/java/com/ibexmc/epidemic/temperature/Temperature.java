package com.ibexmc.epidemic.temperature;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.symptoms.Hurt;
import com.ibexmc.epidemic.symptoms.PotionEffects;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.Permission;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.SpigotVersion;
import com.ibexmc.epidemic.util.functions.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.epidemic.thirst.Thirst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Temperature {
    /*
    Temperature is calculated by:
        Core temperature (armor modifier)
        Ambient temperature (biome and nearby block modifier)
        Ailment temperature (fever/chills from ailment)
        Relief - prevents symptoms

    Symptoms are based on Temperature level (HOT or COLD) but also
    on the actual temperature as a modifier, and also the number of
    cycles in this temperature level.  The longer the player is there
    the higher the modifier

    Temperature Values run from:

    < -5 = COLD - Symptoms
    -5 to +5 - No Symptoms
    > +5 HOT - Symptoms

    If there is relief applied, then temperature will default back to base

    */

    //region Enums
    public enum Level {
        NORMAL,
        HOT,
        COLD
    }
    //endregion
    //region Objects
    Player player;
    //endregion
    //region Constructors
    public Temperature(Player player) {
        this.player = player;
    }
    //endregion
    //region Methods

    /**
     * Gets Temperature
     * @return Current temperature for the player
     */
    public double getTemperature() {
        double temp = 0; // Base core temperature is 0
        double core = getCoreTemperature();
        double ambient = getAmbientTemperature();
        double ailment = getAilmentTemperature();
        temp = core + ambient + ailment;
        Logging.debug(
                "Temperature",
                "getTemperature()",
                "Core = " + core + " " +
                        "Ambient = " + ambient + " " +
                        "Ailment = " + ailment + " " +
                        "Total = " + temp
        );
        return temp;
    }

    /**
     * Gets the temperature level as a String
     * @return Temperature level
     */
    public String getLevelText() {
        Level level = getLevelFromTemperature(getTemperature());
        String levelText = Locale.localeText("normal", "Normal", null);
        if (level.equals(Level.HOT)) {
            levelText = Locale.localeText("hot", "&cHot", null);
        }
        if (level.equals(Level.COLD)) {
            levelText = Locale.localeText("cold", "&9Cold", null);
        }
        return levelText;
    }

    /**
     * Gets thermometer text for the player
     * @return Thermometer text
     */
    public String getThermometerText() {
        String thermH = Locale.localeText("thermometer_hot", "H", null);
        String thermC = Locale.localeText("thermometer_cold", "C", null);
        double temp = getTemperature();
        String therm = "";
        switch ((int) Math.round(temp)) {
            case -10:
                therm = "&1" + thermC +" =====&7|&1=====&f 0 =====&f|===== &c" + thermH;
                break;
            case -9:
                therm = "&1" + thermC +" &7=&1====&7|&1=====&f 0 =====&f|===== &c" + thermH;
                break;
            case -8:
                therm = "&1" + thermC +" &7==&1===&7|&1=====&f 0 =====&f|===== &c" + thermH;
                break;
            case -7:
                therm = "&1" + thermC +" &7===&1==&7|&1=====&f 0 =====&f|===== &c" + thermH;
                break;
            case -6:
                therm = "&1" + thermC +" &7====&1=&7|&1=====&f 0 =====&f|===== &c" + thermH;
                break;
            case -5:
                therm = "&9" + thermC +" &7=====|&9=====&f 0 =====&f|===== &c" + thermH;
                break;
            case -4:
                therm = "&9" + thermC +" &7=====|=&9====&f 0 =====&f|===== &c" + thermH;
                break;
            case -3:
                therm = "&9" + thermC +" &7=====|==&9===&f 0 =====&f|===== &c" + thermH;
                break;
            case -2:
                therm = "&9" + thermC +" &7=====|===&9==&f 0 =====&f|===== &c" + thermH;
                break;
            case -1:
                therm = "&9" + thermC +" &7=====|====&9=&f 0 =====&f|===== &c" + thermH;
                break;
            case 0:
                therm = "&9" + thermC +" &7=====|=====&f 0 =====&f|===== &c" + thermH;
                break;
            case 1:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c=&f====&7|&f===== &c" + thermH;
                break;
            case 2:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c==&f===&7|&f===== &c" + thermH;
                break;
            case 3:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c===&f==&7|&f===== &c" + thermH;
                break;
            case 4:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c====&f=&7|&f===== &c" + thermH;
                break;
            case 5:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c=====&7|===== &c" + thermH;
                break;
            case 6:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c=====&7|&4=&7==== &4" + thermH;
                break;
            case 7:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c=====&7|&4==&7=== &4" + thermH;
                break;
            case 8:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c=====&7|&4===&7== &4" + thermH;
                break;
            case 9:
                therm = "&9" + thermC +" &7=====|=====&f 0 &c=====&7|&4====&7= &4" + thermH;
                break;
            case 10:
                therm = "&9" + thermC + " &7=====|=====&f 0 &c=====&7|&4===== &4" + thermH;
                break;
            default:
                if (temp < -10) {
                    therm = "&1!!! &1" + thermC + " =====&7|&1=====&f 0 &f=====&7|&f===== &c" + thermH + " &1!!!";
                }
                if (temp > 10) {
                    therm = "&4!!! &9" + thermC + " &f=====&7|&f=====&f 0 &4=====&7|&4===== &4" + thermH + " !!!";
                }
        }
        return "&f[ " + therm + "&f ]";
    }

    /**
     * Gets temperature information for the player
     * @return Temperature information
     */
    public String getTemperatureInfo() {
        double temp = 0; // Base core temperature is 0
        double core = getCoreTemperature();
        double ambient = getAmbientTemperature();
        double ailment = getAilmentTemperature();
        temp = core + ambient + ailment;

        HashMap<String, String> placeHolder = new HashMap<>();
        placeHolder.put("<%level%>", getLevelText());
        placeHolder.put("<%temp%>", "" + MathUtil.round(temp, 1));
        placeHolder.put("<%core%>", "" + MathUtil.round(core, 1));
        placeHolder.put("<%ambient%>", "" + MathUtil.round(ambient, 1));
        placeHolder.put("<%ailments%>", "" + MathUtil.round(ailment, 1));

        return Locale.localeText(
                "temp_info",
                "Overall: <%level%>,Temperature: <%temp%>, Core: <%core%>, Ambient: <%ambient%>, Ailment(s): <%ailments%>",
                placeHolder
        );
    }

    /**
     * Applies temperature warnings and symptoms to the player
     */
    public void applyTemperature() {

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            Logging.debug("Temperature", "applyTemperature", "Skipping player as they are in creative");
            return; // This player has the invincible permission or is in creative mode
        }
        if (Permission.inBypass(player)) {
            Logging.debug("Temperature", "applyTemperature", "Skipping player as they have bypass mode on");
            return; // This player is in bypass mode
        }
        if (Epidemic.instance().config.getPreventTempWorlds() != null) {
            for (String worldName : Epidemic.instance().config.getPreventTempWorlds()) {
                if (worldName.equalsIgnoreCase(player.getLocation().getWorld().getName())) {
                    Logging.debug("Temperature", "applyTemperature", "Skipping as player is in a blocked world");
                    return; // This player is in a world that is excluded from temperature checks
                }
            }
        }

        // Get the players temperature
        double temp = getTemperature();

        // Get the stored temperature
        if (Epidemic.instance().data().getPlayerTemp(player.getUniqueId()) == null) {
            Logging.debug("Temperature", "applyTemperature", "Adding a blank player temperature");
            Epidemic.instance().data().setPlayerTemp(player.getUniqueId(), getLevelFromTemperature(temp), temp);
        }
        PlayerTemperature playerTemperature = Epidemic.instance().data().getPlayerTemp(player.getUniqueId());
        if (playerTemperature != null) {

            // Get the current temperature level
            Level level = getLevelFromTemperature(temp);

            Logging.debug(
                    "Temperature",
                    "applyTemperature",
                    "Getting Temperature: Level = " + level + " " +
                            "Cycle = " + playerTemperature.getCycles()
            );

            // Check if the current temperature level is what was stored
            if (level != playerTemperature.level) {
                // If the level has changed, recreate the PlayerTemperature object
                // which will reset the number of cycles
                Logging.debug("Temperature", "applyTemperature", "Resetting playerTemperature with new level");
                playerTemperature = new PlayerTemperature(level, temp);
            }

            // Display alerts to the player
            if (Epidemic.instance().config.isTempWarnPlayers()) {
                if (playerTemperature.getCycles() == 1 && playerTemperature.getLevel() == Level.HOT) {
                    // starting to get hot
                    SendMessage.actionBar(player, Locale.localeText("temp_getting_hot", "&cYou are starting to get too hot", null));
                }
                if (playerTemperature.getCycles() == 1 && playerTemperature.getLevel() == Level.COLD) {
                    // starting to get cold
                    SendMessage.actionBar(player, Locale.localeText("temp_getting_cold", "&9You are starting to get too cold", null));
                }
                if (playerTemperature.getCycles() == 4 && playerTemperature.getLevel() == Level.HOT) {
                    // symptom text - cold
                    SendMessage.actionBar(player, Locale.localeText("temp_hot_symptom", "&4You are too hot!", null));
                }
                if (playerTemperature.getCycles() == 4 && playerTemperature.getLevel() == Level.COLD) {
                    // symptom text - cold
                    SendMessage.actionBar(player, Locale.localeText("temp_cold_symptom", "&1You are too cold!", null));
                }
            }

            // If the player has been in this temperature level for more than
            // 3 cycles, start to apply symptoms
            if (playerTemperature.getCycles() > 3) {
                applySymptom(playerTemperature);
            }

            playerTemperature.setRecord(level, temp);
            Epidemic.instance().data().putPlayerTemp(player.getUniqueId(), playerTemperature);
        }
    }

    /**
     * Applies a temperature symptom to the player
     * @param playerTemperature PlayerTemperature item used to determine symptom
     */
    private void applySymptom(PlayerTemperature playerTemperature) {
        /*
            Symptoms are based on temperature level initially, with
            temperature and number of cycles used as a modifier.
            temperature modifier kicks in at COLD + <= -5, HOT + >= 5
        */

        // Check if the player has any relief for heat or cold
        // If so, exit out and do not apply symptoms
        if (getLevelFromTemperature(playerTemperature.getTemp()) == Level.COLD) {
            if (Epidemic.instance().data().getCurrentColdRelief(player.getUniqueId())) {
                Logging.debug("Temperature", "applySymptom(PlayerTemperature)", "Not applying cold symptoms due to relief");
                return;
            }
        }
        if (getLevelFromTemperature(playerTemperature.getTemp()) == Level.HOT) {
            if (Epidemic.instance().data().getCurrentHeatRelief(player.getUniqueId())) {
                Logging.debug("Temperature", "applySymptom(PlayerTemperature)", "Not applying heat symptoms due to relief");
                return;
            }
        }

        double damage = 0;
        double modifier = 0;
        if (playerTemperature.getTemp() <= -5) {
            // temp (as a positive integer) * 0.2
            // example:
            //      -5 = 5 * 0.2 = 1
            //      -6 = 6 * 0.2 = 1.2
            //      -7 = 7 * 0.2 = 1.4
            modifier = modifier + ((playerTemperature.getTemp() * 0.2d) * -1); //
        }
        if (playerTemperature.getTemp() >= 5) {
            // temp * 0.2
            // example:
            //      5 = 5 * 0.2 = 1
            //      6 = 6 * 0.2 = 1.2
            //      7 = 7 * 0.2 = 1.4
            modifier = modifier + ((playerTemperature.getTemp() * 0.2d) * -1); //
        }

        // Cycle modifier
        // existing modifier from hot/cold + cycles * 0.2
        // example:
        //      level = hot, temp = 6, modifier = 1.2, cycles = 4
        //      (4 * 0.2) = 0.8 + 1.2 [modifier] = 2.0
        //      level = hot, temp = 6, modifier = 1.2, cycles = 5
        //      (5 * 0.2) = 1.0 + 1.2 [modifier] = 2.4
        //      level = hot, temp = 6, modifier = 1.2, cycles = 6
        //      (6 * 0.2) = 1.2 + 1.2 [modifier] = 2.4
        //      level = hot, temp = 6, modifier = 1.2, cycles = 7
        //      (7 * 0.2) = 1.4 + 1.2 [modifier] = 2.6
        modifier = modifier + (playerTemperature.getCycles() * 0.2d); // Cycle modifier

        // Getting damage with added modifier
        damage = damage + modifier;

        switch (playerTemperature.getLevel()) {
            case HOT:
                // Player sweating
                ParticleFunctions.sweat(player);
                // Damage the player based on damage + modifier
                Hurt.hurt(player, damage, false);
                // Remove from players thirst bar
                removeThirst(10);
                break;
            case COLD:
                // slow
                double time = 20 * playerTemperature.getCycles();
                if (time > 400) {
                    time = 400; // max out at 400 ticks
                }
                int multiplier = (int) modifier;
                if (multiplier < 1) {
                    multiplier = 1;
                }
                if (multiplier > 3) {
                    multiplier = 3;
                }
                PotionEffects.slow(player, (int) Math.round(time), multiplier);
                // Damage the player based on damage + modifier
                Hurt.hurt(player, damage, false);
                // panda sneeze sound
                SoundFunctions.playSoundWorld(player.getLocation(), 3.0f, 1.0f, Sound.ENTITY_PANDA_SNEEZE);
                break;
            default:
                break;
        }
    }

    /**
     * Gets the players core temperature
     * @return Players core temperature
     */
    private double getCoreTemperature() {
        double temp = 0;
        if (player != null) {

            // Check if the player is on fire, no other checks needed if so
            if (player.getFireTicks() > 0) {
                temp = temp + 10; // Player is on fire
                return temp;
            }

            // Armor
            if (player.getInventory().getHelmet() != null) {
                temp = temp + getTemperatureFromArmor(player.getInventory().getHelmet());
            }
            if (player.getInventory().getChestplate() != null) {
                temp = temp + getTemperatureFromArmor(player.getInventory().getChestplate());
            }
            if (player.getInventory().getLeggings() != null) {
                temp = temp + getTemperatureFromArmor(player.getInventory().getLeggings());
            }
            if (player.getInventory().getBoots() != null) {
                temp = temp + getTemperatureFromArmor(player.getInventory().getBoots());
            }
            if (player.getInventory().getItemInMainHand() != null) {
                temp = temp + getTemperatureFromHand(player.getInventory().getItemInMainHand().getType());
            }
            if (player.getInventory().getItemInOffHand() != null) {
                temp = temp + getTemperatureFromHand(player.getInventory().getItemInOffHand().getType());
            }

            // If the player is inside, normalize their temperature by 1
            if (temp < 0 && BlockFunctions.isUnderBlock(player.getLocation())) {
                temp = temp + 1;
            }
            if (temp > 0 && BlockFunctions.isUnderBlock(player.getLocation())) {
                temp = temp - 1;
            }


        } else {
            return temp;
        }
        return temp;
    }

    /**
     * Gets the ambient temperature for the player
     * @return Players ambient temperature
     */
    private double getAmbientTemperature() {
        double temp = 0;
        if (player != null) {

            // Biome
            double biometemp = getTemperatureFromBiome();
            temp = temp + biometemp;

            // Elevation
            double elevation = getTemperatureFromElevation();
            temp = temp + elevation;

            // Nearby blocks - 2 blocks
            double totalNearbyTemp = 0;
            double nearbytemp = 0;
            double nearbyBlocks = 0; // number of blocks nearby that have an effect
            List<Block> blocks = BlockFunctions.getBlocks(player.getLocation().getBlock(), 2);
            if (blocks != null) {
                for (Block block : blocks) {
                    if (block != null) {


                        if (block.getType() != Material.AIR) {
                            double actualdistance = player.getLocation().add(0.5, 0, 0.5).distance(
                                    block.getLocation().add(0.5, 0, 0.5)
                            );

                            int distance = (int) Math.round(actualdistance);
                            if (distance == 0) {
                                distance = 1;
                            }

                            nearbytemp = getTemperatureFromBlock(
                                    block, distance
                            );

                            if (nearbytemp != 0) {
                                nearbyBlocks++;
                                totalNearbyTemp = totalNearbyTemp + nearbytemp;
                            }
                        }
                    }
                }
            }

            // Max out the values for nearby blocks
            if (totalNearbyTemp > 10) {
                totalNearbyTemp =  10;
            }
            if (totalNearbyTemp < -10) {
                totalNearbyTemp = -10;
            }
            temp = temp + totalNearbyTemp;

            Logging.debug("Temperature",
                    "getAmbientTemperature",
                    "Ambient Temperature - Player: " + player.getName() + "\n" +
                            "Biome Temp: " + biometemp + " Total: " + temp + "\n" +
                            "Elevation Temp: " + biometemp + " Total: " + temp + "\n" +
                            "# Blocks that have an effect: " + nearbyBlocks + " Nearby Temp: " + totalNearbyTemp + " Total: " + temp
            );
        }
        return temp;
    }

    /**
     * Gets the temperature from elevation for the player
     * @return Elevation temperature
     */
    private double getTemperatureFromElevation() {
        double temp = 0;
        // Get player location height
        if (player.getLocation().getBlockY() <= 15) {
            temp = temp + 1; // Warmer below 15
        }
        if (player.getLocation().getBlockY() < 30 && player.getLocation().getBlockY() > 15) {
            temp = temp - 1; // Colder underground
        }
        if (player.getLocation().getBlockY() > 80) {
            temp = temp - 1; // Colder higher up
        }
        if (player.getLocation().getBlockY() > 150) {
            temp = temp - 2; // Even colder higher up (cumulative temp drops)
        }
        return temp;
    }

    /**
     * Gets temperature from ailments for the player
     * @return Ailment temperature
     */
    private double getAilmentTemperature() {
        // Gets a temperature modifier from any afflicted ailments
        double temp = 0;
        Set<Afflicted> afflictedSet = Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId());
        if (afflictedSet != null) {
            for (Afflicted affliction : afflictedSet) {
                Logging.debug("", "", affliction.getAilment().getDisplayName());

                if (affliction.getAilment().isFever()) {
                    Logging.debug("Temperature", "getAilmentTemperature(Player)", "Temp increased from fever because of " + affliction.getAilment().getDisplayName());
                    temp = temp + 3;
                }
                if (affliction.getAilment().isChills()) {
                    Logging.debug("Temperature", "getAilmentTemperature(Player)", "Temp lowered from fever because of " + affliction.getAilment().getDisplayName());
                    temp = temp - 3;
                }

            }
        }
        return temp;
    }

    /**
     * Gets the player temperature
     * @return Player temperature
     */
    public PlayerTemperature getPlayerTemperature() {
        return Epidemic.instance().data().getPlayerTemp(player.getUniqueId());
    }

    /**
     * Gets temperature level for the player based on provided temperature
     * @param temp Temperature
     * @return Temperature level for the player
     */
    private Level getLevelFromTemperature(double temp) {
        if (temp < -4) {
            return Level.COLD;
        }
        if (temp > 5) {
            return Level.HOT;
        }
        return Level.NORMAL;
    }

    /**
     * Gets temperature from players worn-armor
     * @param itemStack ItemStack of players armor
     * @return Armor temperature
     */
    private double getTemperatureFromArmor(ItemStack itemStack) {

        if (itemStack == null) {
            return 0;
        }

        Material material = itemStack.getType();
        if (material == null) {
            return 0;
        }
        double temp = 0;

        switch (material) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case TURTLE_HELMET:
                temp = 0.5;
                break;
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
                temp = 1;
                break;
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                temp = -0.5;
                break;
            default:
                break;
        }

        if (SpigotVersion.is116Safe()) {
            switch (material) {
                case NETHERITE_HELMET:
                case NETHERITE_CHESTPLATE:
                case NETHERITE_LEGGINGS:
                case NETHERITE_BOOTS:
                    temp = 0;
                    break;
                default:
                    break;
            }
        }

        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                if (enchantment.getKey().equals(Enchantment.FROST_WALKER)) {
                    temp = temp -2; // -2 temperature for frost walker boots
                }
            }
        }

        return temp;
    }

    /**
     * Gets temperature from item in players hand
     * @param material Material of item in players hand
     * @return Item in-hand temperature
     */
    private double getTemperatureFromHand(Material material) {
        if (material == null) {
            return 0;
        }
        double temp = 0;
        switch (material) {
            case BLAZE_POWDER:
            case BLAZE_ROD:
            case FIRE_CHARGE:
            case LANTERN:
            case LAVA_BUCKET:
            case MAGMA_CREAM:
            case TORCH:
                temp = 0.5;
                break;
            case ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
            case SNOW_BLOCK:
            case SNOWBALL:
                temp = -0.5;
                break;
            default:
                break;
        }
        return temp;
    }

    /**
     * Gets the temperature from the biome the player is in
     * @return Biome temperature
     */
    private double getTemperatureFromBiome() {
        Biome biome = player.getLocation().getBlock().getBiome();
        if (biome == null) {
            return 0;
        }

        double temp = 0;
        TimeFunctions.TimeOfDay timeOfDay = TimeFunctions.getTimeOfDay(player.getLocation().getWorld());

        if (
                biome.name().contains("NETHER") ||
                biome.name().contains("CRIMSON") ||
                biome.name().contains("SOUL") ||
                biome.name().contains("WARPED")
        ) {
            temp = 4;
            return temp;
        }


        if (biome.name().contains("BADLANDS")) {
            if (timeOfDay == TimeFunctions.TimeOfDay.DAY) {
                temp = 3;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NOONISH) {
                temp = 4;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NIGHT) {
                temp = -1;
            }
            return temp;
        }

        if (biome.name().contains("DESERT")) {
            if (timeOfDay == TimeFunctions.TimeOfDay.DAY) {
                temp = 2;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NOONISH) {
                temp = 3;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NIGHT) {
                temp = -1;
            }
            return temp;
        }

        if (biome.name().contains("JUNGLE")) {
            if (timeOfDay == TimeFunctions.TimeOfDay.DAY) {
                temp = 1;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NOONISH) {
                temp = 1.5;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NIGHT) {
                temp = 2.5;
            }
            return temp;
        }

        if (biome.name().equals("BEACH")) {
            temp = 1;
            return temp;
        }

        if (
                biome.name().contains("SWAMP") ||
                biome.name().equals("WARM_OCEAN") ||
                biome.name().equals("DEEP_WARM_OCEAN")
        ) {
            temp = 1.5;
            return temp;
        }

        if (
                biome.name().equals("WOODED_BADLANDS_PLATEAU") ||
                biome.name().equals("MODIFIED_WOODED_BADLANDS_PLATEAU") ||
                biome.name().equals("LUKEWARM_OCEAN")
        ) {
            temp = 1.5;
            return temp;
        }

        if (biome.name().equals("RIVER")) {
            temp = -0.5;
            return temp;
        }

        if (
                biome.name().equals("TAIGA") ||
                biome.name().equals("TAIGA_HILLS") ||
                biome.name().equals("TAIGA_MOUNTAINS") ||
                biome.name().equals("MOUNTAIN_EDGE") ||
                biome.name().equals("MOUNTAINS") ||
                biome.name().equals("GRAVELLY_MOUNTAINS") ||
                biome.name().equals("MODIFIED_GRAVELLY_MOUNTAINS")
        ) {
            temp = -1;
            return temp;
        }

        if (biome.name().contains("COLD_OCEAN")) {
            if (timeOfDay == TimeFunctions.TimeOfDay.DAY) {
                temp = -1;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NOONISH) {
                temp = -0.5;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NIGHT) {
                temp = -2;
            }
            return temp;
        }

        if (biome.name().contains("FROZEN")) {
            temp = -2.5;
            return temp;
        }

        if (biome.name().contains("SNOWY")) {
            temp = -3.0;
            return temp;
        }

        if (
                biome.name().equals("DEEP_COLD_OCEAN") ||
                biome.name().equals("DEEP_FROZEN_OCEAN") ||
                biome.name().equals("DEEP_LUKEWARM_OCEAN") ||
                biome.name().equals("DEEP_OCEAN") ||
                biome.name().equals("ICE_SPIKES")
        ) {
            if (timeOfDay == TimeFunctions.TimeOfDay.DAY) {
                temp = -2.5;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NOONISH) {
                temp = -3;
            }
            if (timeOfDay == TimeFunctions.TimeOfDay.NIGHT) {
                temp = -3.5;
            }
            return temp;
        }

        if (
                biome.name().equals("THE_END") ||
                biome.name().equals("END_BARRENS") ||
                biome.name().equals("END_HIGHLANDS") ||
                biome.name().equals("END_MIDLANDS")
        ) {
            temp = -4;
            return temp;
        }

        if (biome.name().contains("THE_VOID")) {
            temp = -10;
            return temp;
        }

        return temp;
    }

    /**
     * Removes a set amount of hydration from the player
     * @param amount Amount of hydration to remove
     */
    private void removeThirst(double amount) {
        double newThirstAmount = 0;
        if (Epidemic.instance().data().getThirstBar().containsKey(player.getUniqueId())) {
            Thirst thirst = Epidemic.instance().data().getThirstBar().get(player.getUniqueId());
            if (thirst != null) {
                try {
                    newThirstAmount = thirst.getThirstAmount() - amount;
                    if (newThirstAmount < 0) {
                        newThirstAmount = 0;
                    }
                    if (newThirstAmount > 100) {
                        newThirstAmount = 100;
                    }
                    thirst.setThirstAmount(newThirstAmount);
                    BossBar thirstBar = thirst.getThirstBar();
                    thirstBar.setProgress(thirst.getThirstAmount() / 100);
                    thirst.setThirstAmount(thirst.getThirstAmount());
                    thirst.setThirstBar(thirstBar);
                    Epidemic.instance().data().setThirstBar(player.getUniqueId(), thirst);
                } catch (Exception ex) {
                    Logging.debug(
                            "Temperature",
                            "removeThirst(double)",
                            "Unexpected error removing thirst.  Amount to remove: " +
                                    amount +
                                    " New Amount = " +
                                    newThirstAmount +
                                    " Exception: " +
                                    ex.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Get temperature from block and distance from player
     * @param block Block to lookup
     * @param distance Distance from player
     * @return Block temperature
     */
    private double getTemperatureFromBlock(Block block, int distance) {
        double returnValue = 0;
        BlockData blockData = block.getBlockData();
        switch (block.getType()) {
            case LAVA:

                if (blockData instanceof Levelled) {
                    Levelled levelled = (Levelled) blockData;
                    Logging.debug("", "", "Level: " + levelled.getLevel());
                    if (levelled.getLevel() == 0) {
                        // Lava source
                        if (distance == 1) {
                            returnValue = 2;
                        } else {
                            returnValue = 1;
                        }
                    } else {
                        // Lava flow
                        if (distance == 1) {
                            returnValue = 1;
                        } else {
                            returnValue = 0.5;
                        }
                    }
                }
                break;
            case FIRE:
                if (distance == 1) {
                    returnValue = 2;
                } else {
                    returnValue = 0.5;
                }
                break;
            case CAMPFIRE:
                if (blockData instanceof Lightable) {
                    Lightable lightable = (Lightable) blockData;
                    if (lightable.isLit()) {
                        if (distance == 1) {
                            returnValue = 5;
                        } else {
                            returnValue = 0.5;
                        }
                    }
                }
                break;
            case FURNACE:
            case FURNACE_MINECART:
            case SMOKER:
            case BLAST_FURNACE:
                if (blockData instanceof Lightable) {
                    Lightable lightable = (Lightable) blockData;
                    if (lightable.isLit()) {
                        if (distance == 1) {
                            returnValue = 2;
                        } else {
                            returnValue = 1;
                        }
                    }
                }
                break;
            case BLUE_ICE:
            case PACKED_ICE:
            case FROSTED_ICE:
            case SNOW_BLOCK:
                if (distance == 1) {
                    returnValue = -1.5;
                } else {
                    returnValue = -0.5;
                }
                break;
            case ICE:
                if (distance == 1) {
                    returnValue = -0.75;
                } else {
                    returnValue = -0.25;
                }
            case SNOW:
                if (distance == 1) {
                    returnValue = -0.1;
                } else {
                    returnValue = -0.05;
                }
            default:
                break;
        }

        return returnValue;
    }

    //endregion
    //region Sub-Classes
    public static class PlayerTemperature {
        //region Objects
        private Level level;
        private double temp = 0;
        private int cycles = 0;
        //endregion
        //region Constructors
        public PlayerTemperature(Level level, double temp) {
            this.level = level;
            this.temp = temp;
            this.cycles = 0;
        }
        //endregion
        //region Getters

        /**
         * Gets the temperature level
         * @return Temperature Level
         */
        public Level getLevel() {
            return level;
        }

        /**
         * Gets the temperature
         * @return Temperature
         */
        public double getTemp() {
            return temp;
        }

        /**
         * Gets the number of cycles at this temperature level
         * @return Temperature cycles
         */
        public int getCycles() {
            return cycles;
        }
        //endregion
        //region Methods

        /**
         * Sets the level and temperature
         * @param level Level to set
         * @param temp Temperature to set
         */
        public void setRecord(Level level, double temp) {
            this.temp = temp;
            if (this.level == level) {
                this.cycles++;
            } else {
                this.level = level;
                this.cycles = 0;
            }
        }
        //endregion
    }
    //endregion
}
