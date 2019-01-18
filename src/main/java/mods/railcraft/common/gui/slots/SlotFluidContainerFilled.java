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
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SlotFluidContainerFilled extends Slot {

    public SlotFluidContainerFilled(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return !InvTools.isEmpty(stack) && FluidItemHelper.getFluidStackInContainer(stack) != null;
    }
}
