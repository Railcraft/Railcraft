/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class SlotWaterLimited extends SlotWater {

    public SlotWaterLimited(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        setStackLimit(4);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        FluidStack fluidStack = FluidItemHelper.getFluidStackInContainer(stack);
        return !(fluidStack != null && fluidStack.amount > FluidTools.BUCKET_VOLUME)
                && super.isItemValid(stack);
    }
}
