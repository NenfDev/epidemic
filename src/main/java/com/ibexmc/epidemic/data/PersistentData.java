package com.ibexmc.epidemic.data;

import com.ibexmc.epidemic.Epidemic;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentData {

    public enum Type {
        STRING,
        INT,
        DOUBLE
    }
    public enum Key {
        EQUIPMENT("epidemic_equipment"),
        AILMENT("epidemic_ailment");

        private final String text;

        Key(String text) {
            this.text = text;
        }

        /**
         * Gets the key text
         * @return Key Text
         */
        public String getKey() {
            return this.text;
        }

    }

    private final Epidemic plugin;
    public PersistentData(Epidemic plugin) {
        this.plugin = plugin;
    }

    //region Public Methods

    /**
     * Checks if the object has the string key provided
     * @param object Object to check
     * @param key String Key to check
     * @return If true, object has the key
     */
    public boolean hasString(Object object, String key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key);
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            return hasKey(itemStack.getItemMeta(), namedKey, Type.STRING);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            return hasKey(itemMeta, namedKey, Type.STRING);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            return hasKey(entity, namedKey, Type.STRING);
        }
        return false;
    }

    /**
     * Checks if the object has the string key provided
     * @param object Object to check
     * @param key Key to check
     * @return If true, object has the key
     */
    public boolean hasString(Object object, Key key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            return hasKey(itemStack.getItemMeta(), namedKey, Type.STRING);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            return hasKey(itemMeta, namedKey, Type.STRING);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            return hasKey(entity, namedKey, Type.STRING);
        }
        return false;
    }

    /**
     * Checks if the object has the integer key provided
     * @param object Object to check
     * @param key Key to check
     * @return If true, object has the key
     */
    public boolean hasInt(Object object, Key key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            return hasKey(itemStack.getItemMeta(), namedKey, Type.INT);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            return hasKey(itemMeta, namedKey, Type.INT);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            return hasKey(entity, namedKey, Type.INT);
        }
        return false;
    }

    /**
     * Checks if the object has the double key provided
     * @param object Object to check
     * @param key Key to check
     * @return If true, object has the key
     */
    public boolean hasDouble(Object object, Key key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            return hasKey(itemStack.getItemMeta(), namedKey, Type.DOUBLE);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            return hasKey(itemMeta, namedKey, Type.DOUBLE);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            return hasKey(entity, namedKey, Type.DOUBLE);
        }
        return false;
    }

    /**
     * Gets the key string value from the object provided
     * @param object Object to get value from
     * @param key Key to get value from
     * @return Key value.  If not found, returns blank string
     */
    public String getString(Object object, Key key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            return getString(itemStack.getItemMeta(), namedKey);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            return getString(itemMeta, namedKey);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            return getString(entity, namedKey);
        } else {
            return "";
        }
    }

    /**
     * Gets the key int value from the object provided
     * @param object Object to get value from
     * @param key Key to get value from
     * @return Key value.  If not found, returns -1
     */
    public int getInt(Object object, Key key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            return getInt(itemStack.getItemMeta(), namedKey);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            return getInt(itemMeta, namedKey);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            return getInt(entity, namedKey);
        } else {
            return -1;
        }
    }

    /**
     * Sets the key value to the object
     * @param object Object to set key value to
     * @param key Key to set
     * @param value Value to set
     */
    public void setString(Object object, Key key, String value) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            ItemMeta itemMeta = itemStack.getItemMeta();
            setString(itemMeta, namedKey, value);
            itemStack.setItemMeta(itemMeta);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            setString(itemMeta, namedKey, value);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            setString(entity, namedKey, value);
        }
    }

    /**
     * Removes a key value from the object
     * @param object Object to remove key value from
     * @param key Key to remove
     */
    public void remove(Object object, Key key) {
        NamespacedKey namedKey = new NamespacedKey(plugin, key.getKey());
        if (object instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) object;
            remove(itemStack.getItemMeta(), namedKey);
        } else if (object instanceof ItemMeta) {
            ItemMeta itemMeta = (ItemMeta) object;
            remove(itemMeta, namedKey);
        } else if (object instanceof Entity) {
            Entity entity = (Entity) object;
            remove(entity, namedKey);
        }
    }
    //endregion
    //region Private Methods
    /**
     * Checks if the ItemMeta has the NamespacedKey provided
     * @param itemMeta ItemMeta to check
     * @param namedKey NamespacedKey to check
     * @param type Data type to check for
     * @return If true, ItemMeta has the key
     */
    private boolean hasKey(ItemMeta itemMeta, NamespacedKey namedKey, Type type) {
        if (itemMeta != null) {
            switch (type) {
                case STRING:
                    return (itemMeta.getPersistentDataContainer().has(namedKey, PersistentDataType.STRING));
                case INT:
                    return (itemMeta.getPersistentDataContainer().has(namedKey, PersistentDataType.INTEGER));
                case DOUBLE:
                    return (itemMeta.getPersistentDataContainer().has(namedKey, PersistentDataType.DOUBLE));
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if the Entity has the NamedspacedKey provided
     * @param entity Entity to check
     * @param namedKey NamespacedKey to check
     * @param type Data type to check for
     * @return If true, Entity has the key
     */
    private boolean hasKey(Entity entity, NamespacedKey namedKey, Type type) {
        if (entity != null) {
            switch (type) {
                case STRING:
                    return (entity.getPersistentDataContainer().has(namedKey, PersistentDataType.STRING));
                case INT:
                    return (entity.getPersistentDataContainer().has(namedKey, PersistentDataType.INTEGER));
                case DOUBLE:
                    return (entity.getPersistentDataContainer().has(namedKey, PersistentDataType.DOUBLE));
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets the key string value from the ItemMeta
     * @param itemMeta ItemMeta to get key value from
     * @param namedKey Named key to lookup
     * @return Key value. If not found, returns blank string
     */
    private String getString(ItemMeta itemMeta, NamespacedKey namedKey) {
        if (itemMeta != null) {
            return (itemMeta.getPersistentDataContainer().get(namedKey, PersistentDataType.STRING));
        } else {
            return "";
        }
    }

    /**
     * Gets the key string value from the Entity
     * @param entity Entity to get key value from
     * @param namedKey Named key to lookup
     * @return Key value. If not found, returns blank string
     */
    private String getString(Entity entity, NamespacedKey namedKey) {
        if (entity != null) {
            return (entity.getPersistentDataContainer().get(namedKey, PersistentDataType.STRING));
        } else {
            return "";
        }
    }

    /**
     * Gets the key integer value from the ItemMeta
     * @param itemMeta ItemMeta to get key value from
     * @param namedKey Named key to lookup
     * @return Key value. If not found, returns -1
     */
    private int getInt(ItemMeta itemMeta, NamespacedKey namedKey) {
        if (itemMeta != null) {
            Integer intValue = itemMeta.getPersistentDataContainer().get(namedKey, PersistentDataType.INTEGER);
            if (intValue != null) {
                return intValue;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Gets the key integer value from the Entity
     * @param entity Entity to get key value from
     * @param namedKey Named key to lookup
     * @return Key value. If not found, returns null
     */
    private int getInt(Entity entity, NamespacedKey namedKey) {
        if (entity != null) {
            Integer intValue = entity.getPersistentDataContainer().get(namedKey, PersistentDataType.INTEGER);
            if (intValue != null) {
                return intValue;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Gets the key double value from the ItemMeta
     * @param itemMeta ItemMeta to get key value from
     * @param namedKey Named key to lookup
     * @return Key value. If not found, returns -1
     */
    private double getDouble(ItemMeta itemMeta, NamespacedKey namedKey) {
        if (itemMeta != null) {
            Double dblValue = itemMeta.getPersistentDataContainer().get(namedKey, PersistentDataType.DOUBLE);
            if (dblValue != null) {
                return dblValue;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Gets the key double value from the Entity
     * @param entity Entity to get key value from
     * @param namedKey Named key to lookup
     * @return Key value. If not found, returns null
     */
    private double getDouble(Entity entity, NamespacedKey namedKey) {
        if (entity != null) {
            Double dblValue = entity.getPersistentDataContainer().get(namedKey, PersistentDataType.DOUBLE);
            if (dblValue != null) {
                return dblValue;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Sets the string key value to the ItemMeta provided
     * @param itemMeta ItemMeta to set key value to
     * @param namedKey Key to set
     * @param value Value to set
     */
    private void setString(ItemMeta itemMeta, NamespacedKey namedKey, String value) {
        if (itemMeta != null) {
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                PersistentDataContainer container = potionMeta.getPersistentDataContainer();
                container.set(namedKey, PersistentDataType.STRING, value);
            } else {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                container.set(namedKey, PersistentDataType.STRING, value);
            }
        }
    }

    /**
     * Sets the string key value to the ItemMeta provided
     * @param entity Entity to set key value to
     * @param namedKey Key to set
     * @param value Value to set
     */
    private void setString(Entity entity, NamespacedKey namedKey, String value) {
        if (entity != null) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set(namedKey, PersistentDataType.STRING, value);
        }
    }

    /**
     * Sets the int key value to the ItemMeta provided
     * @param itemMeta ItemMeta to set key value to
     * @param namedKey Key to set
     * @param value Value to set
     */
    private void setInt(ItemMeta itemMeta, NamespacedKey namedKey, int value) {
        if (itemMeta != null) {
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                PersistentDataContainer container = potionMeta.getPersistentDataContainer();
                container.set(namedKey, PersistentDataType.INTEGER, value);
            } else {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                container.set(namedKey, PersistentDataType.INTEGER, value);
            }
        }
    }

    /**
     * Sets the int key value to the ItemMeta provided
     * @param entity Entity to set key value to
     * @param namedKey Key to set
     * @param value Value to set
     */
    private void setInt(Entity entity, NamespacedKey namedKey, int value) {
        if (entity != null) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set(namedKey, PersistentDataType.INTEGER, value);
        }
    }

    /**
     * Sets the double key value to the ItemMeta provided
     * @param itemMeta ItemMeta to set key value to
     * @param namedKey Key to set
     * @param value Value to set
     */
    private void setDouble(ItemMeta itemMeta, NamespacedKey namedKey, double value) {
        if (itemMeta != null) {
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                PersistentDataContainer container = potionMeta.getPersistentDataContainer();
                container.set(namedKey, PersistentDataType.DOUBLE, value);
            } else {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                container.set(namedKey, PersistentDataType.DOUBLE, value);
            }
        }
    }

    /**
     * Sets the double key value to the ItemMeta provided
     * @param entity Entity to set key value to
     * @param namedKey Key to set
     * @param value Value to set
     */
    private void setDouble(Entity entity, NamespacedKey namedKey, double value) {
        if (entity != null) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set(namedKey, PersistentDataType.DOUBLE, value);
        }
    }

    /**
     * Removes a key value from an ItemMeta
     * @param itemMeta ItemMeta to remove key value from
     * @param namedKey Key to remove
     */
    private void remove(ItemMeta itemMeta, NamespacedKey namedKey) {
        if (itemMeta != null) {
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                PersistentDataContainer container = potionMeta.getPersistentDataContainer();
                container.remove(namedKey);
            } else {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                container.remove(namedKey);
            }
        }
    }

    /**
     * Removes a key value from an Entity
     * @param entity Entity to remove key value from
     * @param namedKey Key to remove
     */
    private void remove(Entity entity, NamespacedKey namedKey) {
        if (entity != null) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.remove(namedKey);
        }
    }
    //endregion
}
