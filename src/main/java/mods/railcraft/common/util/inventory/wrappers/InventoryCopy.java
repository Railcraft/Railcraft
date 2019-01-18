/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.wrappers;

import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.InventoryIterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Creates a deep copy of an existing IInventory.
 * <p/>
 * Useful for performing inventory manipulations and then examining the results
 * without affecting the original inventory.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryCopy extends InvWrapperBase {

    private final InventoryAdvanced copy;

    public InventoryCopy(IInventory original) {
        super(original);
        this.copy = new InventoryAdvanced(original.getSizeInventory());
        for (IInvSlot slot : InventoryIterator.get(original)) {
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack)) {
                copy.setInventorySlotContents(slot.getIndex(), stack.copy());
            }
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        copy.setInventorySlotContents(slot, itemstack);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return copy.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return copy.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return copy.removeStackFromSlot(slot);
    }

    @Override
    public void markDirty() {
    }
}
