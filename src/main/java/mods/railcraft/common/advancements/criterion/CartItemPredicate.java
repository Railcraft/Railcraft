package mods.railcraft.common.advancements.criterion;

import mods.railcraft.api.items.IMinecartItem;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;

/**
 *
 */
final class CartItemPredicate extends ItemPredicate {

    @Override
    public boolean test(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof IMinecartItem || item instanceof ItemMinecart;
    }
}
