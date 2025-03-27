package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.data.PersistentData;
import com.ibexmc.epidemic.recipe.RecipeItem;
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

import java.util.List;
import java.util.Map;

public class FullSyringe implements IEquipment {

    private final String key = "full_syringe";
    private String name = StringFunctions.colorToString("&bFull Syringe");
    private List<String> lore = StringFunctions.colorStringList(StringFunctions.stringToLore("&fA syringe filled with &4blood&f"));
    private Ailment ailment = null;
    private int amount = 1;
    private ItemStack item;

    /**
     * Constructor
     */
    public FullSyringe() {
        load();
    }

    /**
     * Gets the key
     * @return Key
     */
    @Override
    public String key() {
        return this.key;
    }

    /**
     * Gets the display name of the syringe
     * @return Display name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the lore for the Syringe
     * @return String list containing the lore
     */
    @Override
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets a syringe
     * @return Syringe ItemStack
     */
    @Override
    public ItemStack get() {
        assert this.item != null;
        ItemStack fullSyringe = this.item.clone();
        ItemMeta itemMeta = fullSyringe.getItemMeta();
        if (itemMeta != null) {
            ItemMeta syringeMeta = itemMeta.clone();
            syringeMeta.setDisplayName(StringFunctions.colorToString(this.name));
            syringeMeta.setLore(StringFunctions.colorStringList(this.lore));
            fullSyringe.setItemMeta(syringeMeta);
        }
        if (!EquipmentManager.hasEquipmentKey(fullSyringe)) {
            Epidemic.instance().persistentData().setString(fullSyringe, PersistentData.Key.EQUIPMENT, this.key);
        }
        return fullSyringe;
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
     * Returns a null recipe (Item is not craftable)
     * @return Null Recipe
     */
    @Override
    public Map<RecipePosition, RecipeItem> recipe() {
        return null;
    }

    /**
     * Loads the syringe from file
     */
    private void load() {
        try {
            String fileName = "full_syringe.yml";
            EquipmentLoad loader = new EquipmentLoad(Epidemic.instance(), fileName);
            if (loader.exists()) {
                this.name = loader.name(this.name);
                this.lore = loader.lore();
                this.amount = loader.amount(1);
                this.item = loader.item(this.defaultItem());
                this.item.setAmount(this.amount);
            } else {
                FileFunctions.saveResourceIfNotExists("equipment/full_syringe.yml");
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
     * Returns the ailment assigned to this syringe
     * @param ailment
     */
    public void ailment(Ailment ailment) {
        this.ailment = ailment;
    }

    /**
     * Gets the ailment assigned to this syringe
     * @return Ailment
     */
    public Ailment ailment() {
        return this.ailment;
    }

    /**
     * Called when syringe is used on an entity
     * @param entity Entity used on
     * @return Used syringe
     */
    public ItemStack use(Entity entity, Ailment ailment) {
        // If the entity is a player
        // Check if there is an ailment, if so, transfer the ailment to the entity
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (ailment == null) {
                Logging.debug("FullSyringe", "use", "Using full syringe with no ailment on " + player.getDisplayName());
            } else {
                Logging.debug("FullSyringe", "use", "Using full syringe with " + ailment.getDisplayName() + " on " + player.getDisplayName());
            }
            SendMessage.Player(
                    "injected_by_player",
                    "You feel a sharp pinch",
                    player,
                    true,
                    null
            );
            if (ailment != null) {
                if (ailment.checkDeliberateTransmission(player)) {
                    ailment.afflict(player, null);
                }
            }
        }
        // TODO: If entity is not a player, check if pig/cow/sheep, if so infect them
        // TODO: so they can be used for vaccines
        // Replace full syringe with empty syringe
        EmptySyringe emptySyringe = new EmptySyringe();
        return emptySyringe.get();
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
