/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.wrappers;

import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * Wrapper class used to specify part of an existing inventory to be treated as
 * a complete inventory. Used primarily to map a side of an ISidedInventory, but
 * it is also helpful for complex inventories such as the Tunnel Bore.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryMapper extends InvWrapperBase {

    private final IInventory inv;
    private final int start;
    private final int size;
    private int stackSizeLimit = -1;
    private Predicate<ItemStack> filter = StackFilters.ALL;

    public static InventoryMapper make(IInventory inv) {
        return new InventoryMapper(inv, 0, inv.getSizeInventory());
    }

    public static InventoryMapper make(IInventory inv, int start, int size) {
        return new InventoryMapper(inv, start, size);
    }

    /**
     * Creates a new InventoryMapper
     *
     * @param inv        The backing inventory
     * @param start      The starting index
     * @param size       The size of the new inventory, take care not to exceed the
     *                   end of the backing inventory
     */
    public InventoryMapper(IInventory inv, int start, int size) {
        super(inv);
        this.inv = inv;
        this.start = start;
        this.size = size;
    }

    /**
     * If called the inventory will ignore isItemValidForSlot checks.
     */
    public InventoryMapper ignoreItemChecks() {
        checkItems = false;
        return this;
    }

    @SafeVarargs
    public final InventoryMapper withFilters(Predicate<ItemStack>... filters) {
        this.filter = Predicates.and(filter, filters);
        return this;
    }

    public Predicate<ItemStack> filter() {
        return filter;
    }

    public InventoryMapper withStackSizeLimit(int limit) {
        stackSizeLimit = limit;
        return this;
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validSlot(slot);
        return inv.getStackInSlot(start + slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        validSlot(slot);
        return inv.decrStackSize(start + slot, amount);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        validSlot(slot);
        inv.setInventorySlotContents(start + slot, itemstack);
    }

    @Override
    public int getInventoryStackLimit() {
        return stackSizeLimit > 0 ? stackSizeLimit : inv.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        validSlot(slot);
        return !checkItems() || (filter.test(stack) && inv.isItemValidForSlot(start + slot, stack));
    }

    public boolean containsSlot(int absoluteIndex) {
        return absoluteIndex >= start && absoluteIndex < start + size;
    }

    private void validSlot(int slot) {
        if (slot < 0 || slot >= size) throw new IllegalArgumentException("Slot index out of bounds.");
    }

}
