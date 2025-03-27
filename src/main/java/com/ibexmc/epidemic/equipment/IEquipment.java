package com.ibexmc.epidemic.equipment;

import com.ibexmc.epidemic.recipe.RecipeItem;
import com.ibexmc.epidemic.recipe.RecipePosition;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface IEquipment {
    String key();
    String name();
    List<String> lore();
    ItemStack get();
    boolean throwable();
    Map<RecipePosition, RecipeItem> recipe();
}
