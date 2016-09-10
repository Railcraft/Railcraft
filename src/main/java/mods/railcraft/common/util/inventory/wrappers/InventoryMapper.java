/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Wrapper class used to specify part of an existing inventory to be treated as
 * a complete inventory. Used primarily to map a side of an ISidedInventory, but
 * it is also helpful for complex inventories such as the Tunnel Bore.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryMapper extends InvWrapperBase implements IInventory {

    private final IInventory inv;
    private final int start;
    private final int size;
    private int stackSizeLimit = -1;

    public InventoryMapper(IInventory inv) {
        this(inv, 0, inv.getSizeInventory(), true);
    }

    public InventoryMapper(IInventory inv, boolean checkItems) {
        this(inv, 0, inv.getSizeInventory(), checkItems);
    }

    /**
     * Creates a new InventoryMapper
     *
     * @param inv   The backing inventory
     * @param start The starting index
     * @param size  The size of the new inventory, take care not to exceed the
     *              end of the backing inventory
     */
    public InventoryMapper(IInventory inv, int start, int size) {
        this(inv, start, size, true);
    }

    public InventoryMapper(IInventory inv, int start, int size, boolean checkItems) {
        super(inv, checkItems);
        this.inv = inv;
        this.start = start;
        this.size = size;
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(start + slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return inv.decrStackSize(start + slot, amount);
    }

    @Override
    public void setInventorySlotContents(int slot, @Nullable ItemStack itemstack) {
        inv.setInventorySlotContents(start + slot, itemstack);
    }

    public void setStackSizeLimit(int limit) {
        stackSizeLimit = limit;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackSizeLimit > 0 ? stackSizeLimit : inv.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return !checkItems() || inv.isItemValidForSlot(start + slot, stack);
    }

}
