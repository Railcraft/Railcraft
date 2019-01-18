/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 * Created by CovertJaguar on 12/19/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftIngredient extends Ingredient {
    public RailcraftIngredient(int size) {
        super(size);
    }

    public RailcraftIngredient(ItemStack... stacks) {
        super(stacks);
    }

    public ItemStack getRemaining(ItemStack original) {
        return InvTools.depleteItem(original);
    }
}