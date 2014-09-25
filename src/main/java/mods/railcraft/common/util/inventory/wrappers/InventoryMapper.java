/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Wrapper class used to specify part of an existing inventory to be treated as
 * a complete inventory. Used primarily to map a side of an ISidedInventory, but
 * it is also helpful for complex inventories such as the Tunnel Bore.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryMapper implements IInventory {

    private final IInventory inv;
    private final int start;
    private final int size;
    private int stackSizeLimit = -1;
    private boolean checkItems = true;

    public InventoryMapper(IInventory inv, ForgeDirection side) {
        this(inv, getInventoryStart(inv, side), getInventorySize(inv, side));
    }

    public InventoryMapper(IInventory inv) {
        this(inv, 0, inv.getSizeInventory(), true);
    }

    public InventoryMapper(IInventory inv, boolean checkItems) {
        this(inv, 0, inv.getSizeInventory(), checkItems);
    }

    /**
     * Creates a new InventoryMapper
     *
     * @param inv The backing inventory
     * @param start The starting index
     * @param size The size of the new inventory, take care not to exceed the
     * end of the backing inventory
     */
    public InventoryMapper(IInventory inv, int start, int size) {
        this(inv, start, size, true);
    }

    public InventoryMapper(IInventory inv, int start, int size, boolean checkItems) {
        this.inv = inv;
        this.start = start;
        this.size = size;
        this.checkItems = checkItems;
    }

    protected static int getInventorySize(IInventory inv, ForgeDirection side) {
        return inv.getSizeInventory();
    }

    protected static int getInventoryStart(IInventory inv, ForgeDirection side) {
        return 0;
    }

    public IInventory getBaseInventory() {
        return inv;
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
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        inv.setInventorySlotContents(start + slot, itemstack);
    }

    @Override
    public String getInventoryName() {
        return inv.getInventoryName();
    }

    public void setStackSizeLimit(int limit) {
        stackSizeLimit = limit;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackSizeLimit > 0 ? stackSizeLimit : inv.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inv.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return inv.isUseableByPlayer(entityplayer);
    }

    @Override
    public void openInventory() {
        inv.openInventory();
    }

    @Override
    public void closeInventory() {
        inv.closeInventory();
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return inv.getStackInSlotOnClosing(start + slot);
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inv.hasCustomInventoryName();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (checkItems)
            return inv.isItemValidForSlot(start + slot, stack);
        return true;
    }

}
