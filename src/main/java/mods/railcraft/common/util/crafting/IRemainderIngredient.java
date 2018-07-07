package mods.railcraft.common.util.crafting;

import net.minecraft.item.ItemStack;

/**
 *
 */
public interface IRemainderIngredient {

    ItemStack getRemaining(ItemStack original);
}
