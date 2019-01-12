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
 * An ingredient that serve as a catalyst (i.e. not consumed).
 */
class CatalystIngredient extends RailcraftIngredient {

    CatalystIngredient(ItemStack... stacks) {
        super(stacks);
    }

    @Override
    public ItemStack getRemaining(ItemStack original) {
        return original;
    }
}
