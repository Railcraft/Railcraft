/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Map;
import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IWorldspike extends IInventoryImplementor, INeedsFuel {

    @Override
    default IInventory getInventory() {
        return this;
    }

    @Override
    default int getSizeInventory() {
        return usesFuel() ? 1 : 0;
    }

    default boolean usesFuel() {
        return !getFuelMap().isEmpty();
    }

    @Override
    default boolean needsFuel() {
        if (!usesFuel())
            return false;
        ItemStack stack = getStackInSlot(0);
        return InvTools.isEmpty(stack) || (stack.getMaxStackSize() > 1 && InvTools.sizeOf(stack) <= 1);
    }

    default boolean hasFuel() {
        return !usesFuel() || getFuelAmount() > 0;
    }

    long getFuelAmount();

    Map<Ingredient, Float> getFuelMap();

    default Optional<Float> getFuelValue(ItemStack stack) {
        return getFuelMap().entrySet().stream()
                .filter(e -> e.getKey().apply(stack))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    default void testGUI() {
        if (!usesFuel())
            throw new IllegalStateException("Worldspike should not open GUI if it doesn't need fuel.");
    }
}
