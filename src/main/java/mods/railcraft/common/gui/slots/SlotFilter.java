/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFilter extends SlotRailcraft {

    public ISlotController controller = null;

    public SlotFilter(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        setPhantom();
    }

    public SlotFilter(IInventory iinventory, int slotIndex, int posX, int posY, ISlotController controller) {
        this(iinventory, slotIndex, posX, posY);
        this.controller = controller;
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return controller == null || controller.isSlotEnabled();
    }

}
