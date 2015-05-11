/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.inventory.filters.ArrayStackFilter;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartMaintenancePatternBase extends CartMaintenanceBase implements ISidedInventory {
    protected final StandaloneInventory patternInv = new StandaloneInventory(6, this);

    public CartMaintenancePatternBase(World world) {
        super(world);
    }

    public IInventory getPattern() {
        return patternInv;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    protected void stockItems(int slotReplace, int slotStock) {
        ItemStack stackReplace = patternInv.getStackInSlot(slotReplace);

        ItemStack stackStock = getStackInSlot(slotStock);

        if (stackStock != null && !InvTools.isItemEqual(stackReplace, stackStock)) {
            CartTools.offerOrDropItem(this, stackStock);
            setInventorySlotContents(slotStock, null);
        }

        if (stackReplace == null)
            return;

        stackStock = getStackInSlot(slotStock);

        if (stackStock == null)
            setInventorySlotContents(slotStock, CartTools.transferHelper.pullStack(this, new ArrayStackFilter(stackReplace)));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        patternInv.writeToNBT("patternInv", data);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        patternInv.readFromNBT("patternInv", data);
    }
}
