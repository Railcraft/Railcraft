/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IItemTransfer;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.inventory.ISidedInventory;

/**
 *
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

        EntityMinecart link_A = LinkageManager.instance().getLinkedCartA(this);
        EntityMinecart link_B = LinkageManager.instance().getLinkedCartB(this);

        if (stackStock == null || stackStock.stackSize < stackStock.getMaxStackSize()) {
            ItemStack stack = null;
            if (link_A instanceof IItemTransfer) {
                stack = ((IItemTransfer) link_A).requestItem(this, stackReplace);
                if (stack != null)
                    if (stackStock == null)
                        setInventorySlotContents(slotStock, stack);
                    else
                        stackStock.stackSize++;
            }
            if (stack == null && link_B instanceof IItemTransfer) {
                stack = ((IItemTransfer) link_B).requestItem(this, stackReplace);
                if (stack != null)
                    if (stackStock == null)
                        setInventorySlotContents(slotStock, stack);
                    else
                        stackStock.stackSize++;
            }
        }
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
