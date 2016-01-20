package uristqwerty.CraftGuide.api;

import net.minecraft.item.ItemStack;


/**
 * Contains two forgotten methods from CraftGuideRecipe. Allows
 *  searching for items, limiting the search to a specific
 *  SlotType.
 */
public interface CraftGuideRecipeExtra1
{
	boolean containsItem(ItemStack filter, SlotType type);
	boolean containsItem(ItemFilter filter, SlotType type);
}
