/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Map;
import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IWorldspike extends IInventory {

    long getFuelAmount();

    Map<Ingredient, Float> getFuelMap();

    default Optional<Float> getFuelValue(ItemStack stack) {
        return getFuelMap().entrySet().stream()
                .filter(e -> e.getKey().apply(stack))
                .map(Map.Entry::getValue)
                .findFirst();
    }

}
