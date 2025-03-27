package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;

import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipeManager;
import com.ibexmc.epidemic.recipe.RecipePosition;
import com.ibexmc.epidemic.util.Logging;
import com.ibexmc.epidemic.util.SendMessage;
import com.ibexmc.epidemic.util.functions.FileFunctions;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import com.ibexmc.epidemic.util.log.Error;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EmptySyringe implements IEquipment {

    private final String key = "empty_syringe";
    private String name = "&bEmpty Syringe";
    private List<String> lore = StringFunctions.stringToLore("&fAn empty syringe");
    private int amount = 1;
    private ItemStack item;
    private Map<RecipePosition, RecipeItem> recipe;

    /**
     * Constructor
     */
    public EmptySyringe() {
        load();
        Epidemic.instance().gameData().recipes().put(this.key(), this.recipe());
    }

    /**
     * Gets the key
     *
     * @return Key
     */
    @Override
    public String key() {
        return this.key;
    }

    /**
     * Gets the display name of the syringe
     *
     * @return Display name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the lore for the Syringe
     *
     * @return String list containing the lore
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets a syringe
     *
     * @return Syringe ItemStack
     */
    @Override
    public ItemStack get() {
        ItemStack emptySyringe = this.item.clone();
        ItemMeta itemMeta = emptySyringe.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(StringFunctions.colorToString(this.name));
            itemMeta.setLore(StringFunctions.colorStringList(this.lore));
            emptySyringe.setItemMeta(itemMeta);
        }
        if (!EquipmentManager.hasEquipmentKey(emptySyringe)) {
            Epidemic.instance().persistentData().setString(emptySyringe, PersistentData.Key.EQUIPMENT, this.key);
        }
        return emptySyringe;
    }

    /**
     * Checks if the equipment is throwable
     * @return If true, equipment is throwable
     */
    @Override
    public boolean throwable() {
        return false;
    }

    /**
     * Returns the recipe for the equipment
     * @return Recipe
     */
    @Override
    public Map<RecipePosition, RecipeItem> recipe() {
        return this.recipe;
    }

    /**
     * Loads the syringe from file
     */
    private void load() {
        try {
            String fileName = "empty_syringe.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                this.item = loader.item(this.defaultItem());
                this.item.setAmount(amount);
                this.recipe = RecipeManager.recipe(loader.file());
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/empty_syringe.yml");
            }
        } catch (Exception ex) {
            Error.save(
                    "EmptySyringe.load.001",
                    "Epidemic",
                    "load()",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Called when syringe is used on an entity
     *
     * @param entity Entity used on
     */
    public ItemStack use(Entity entity) {
        Logging.debug("EmptySyringe", "use", "Using an empty syringe");
        FullSyringe fullSyringe = (FullSyringe) Epidemic.instance().data().equipment.get("full_syringe");
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Logging.debug("EmptySyringe", "use", "Player being poked = " + player.getDisplayName());
            SendMessage.Player(
                    "injected_by_player",
                    "You feel a sharp pinch",
                    player,
                    true,
                    null
            );
            if (Epidemic.instance().data().getPlayerAfflictions() != null) {
                Set<Afflicted> afflictedSet = Epidemic.instance().data().getPlayerAfflictions().get(player.getUniqueId());
                Set<Afflicted> infectiousAfflictedSet = new HashSet<>();
                if (afflictedSet != null) {
                    for (Afflicted affliction : afflictedSet) {
                        if (affliction.getAilment().isInfectious()) {
                            Logging.debug("EmptySyringe", "use", "Adding infectious ailment " + affliction.getAilment().getDisplayName());
                            infectiousAfflictedSet.add(affliction);
                        }
                    }
                }
                if (infectiousAfflictedSet.size() > 0) {
                    Afflicted affliction = infectiousAfflictedSet.stream().skip(new Random().nextInt(infectiousAfflictedSet.size())).findFirst().orElse(null);
                    if (affliction != null) {
                        // return dirty blood
                        fullSyringe.ailment(affliction.getAilment());
                        return EquipmentManager.applyAilmentToFullSyringe(fullSyringe.get(), affliction.getAilment());
                    }
                } else {
                    Logging.debug("EmptySyringe", "use", "No infectious ailment found");
                }

            }
        }
        //  Return clean blood
        return fullSyringe.get();
    }

    /**
     * Gets the default item
     * @return Default item
     */
    private ItemStack defaultItem() {
        ItemStack defaultItem = new ItemStack(Material.TRIPWIRE_HOOK, this.amount);
        ItemMeta itemMeta = defaultItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.name);
            itemMeta.setLore(this.lore);
            defaultItem.setItemMeta(itemMeta);
        }
        return defaultItem;
    }

}