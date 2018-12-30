/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.util.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Iterator;

/**
 *
 */
public abstract class InventoryLogic extends Logic implements IItemHandlerImplementor {

    protected final InventoryAdvanced inventory;
    protected final IInventoryComposite composite;

    protected InventoryLogic(Adapter adapter, int sizeInv) {
        super(adapter);
        this.inventory = new InventoryAdvanced(sizeInv).callback(adapter.getContainer());
        this.composite = InventoryComposite.of(inventory);
    }

    protected void dropItem(ItemStack stack) {
        InvTools.dropItem(stack, theWorldAsserted(), getX(), getY(), getZ());
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return adapter.isUsableByPlayer(player);
    }

    @Override
    public Iterator<InventoryAdaptor> iterator() {
        return composite.iterator();
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        inventory.writeToNBT("inv", data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inventory.readFromNBT("inv", data);
    }
}
