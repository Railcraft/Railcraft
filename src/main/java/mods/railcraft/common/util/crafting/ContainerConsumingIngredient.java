/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import net.minecraft.item.ItemStack;

/**
 * An ingredient that consumes the container of an item stack.
 */
class ContainerConsumingIngredient extends RailcraftIngredient {

    ContainerConsumingIngredient(ItemStack... stacks) {
        super(stacks);
    }

    @Override
    public ItemStack getRemaining(ItemStack original) {
        return ItemStack.EMPTY;
    }
}
