/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.inventory.IInventory;

public class SlotBlockFilter extends SlotRailcraft {

    public SlotBlockFilter(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        setFilter(StackFilters.BLOCK);
        setPhantom();
        setStackLimit(1);
    }

}
