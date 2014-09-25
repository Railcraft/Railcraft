/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.fluids.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.fluids.FluidHelper;
import net.minecraftforge.fluids.FluidStack;

public class SlotWater extends Slot {

    public SlotWater(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        FluidStack fluidStack = FluidHelper.getFluidStackInContainer(itemstack);
        if (fluidStack != null && Fluids.WATER.is(fluidStack)) {
            return true;
        }
        return false;
    }

}
