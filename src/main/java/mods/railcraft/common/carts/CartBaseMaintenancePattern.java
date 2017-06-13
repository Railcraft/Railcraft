/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseMaintenancePattern extends CartBaseMaintenance implements ISidedInventory {
    protected final StandaloneInventory patternInv = new StandaloneInventory(6, this);

    protected CartBaseMaintenancePattern(World world) {
        super(world);
    }

    protected CartBaseMaintenancePattern(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public IInventory getPattern() {
        return patternInv;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return isItemValidForSlot(slot, stack);
    }

    protected void stockItems(int slotReplace, int slotStock) {
        ItemStack stackReplace = patternInv.getStackInSlot(slotReplace);

        ItemStack stackStock = getStackInSlot(slotStock);

        if (stackStock != null && !InvTools.isItemEqual(stackReplace, stackStock)) {
            CartToolsAPI.transferHelper.offerOrDropItem(this, stackStock);
            setInventorySlotContents(slotStock, null);
            stackStock = null;
        }

        if (stackReplace == null)
            return;

        if (stackStock == null)
            setInventorySlotContents(slotStock, CartToolsAPI.transferHelper.pullStack(this, StackFilters.of(stackReplace)));
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
