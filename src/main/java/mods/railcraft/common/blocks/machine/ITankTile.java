/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.interfaces.ITileTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITankTile extends ITileTank {
    @Nullable
    StandardTank getTank();

    IInventory getInventory();

    String getTitle();

    default Predicate<ItemStack> getInputFilter() {
        return StandardStackFilters.FLUID_CONTAINER;
    }

}
