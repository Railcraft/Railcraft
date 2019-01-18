/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

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
