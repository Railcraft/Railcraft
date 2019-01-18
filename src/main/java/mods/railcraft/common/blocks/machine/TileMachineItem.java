/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.interfaces.ITileInventory;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.ItemHandlerFactory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public abstract class TileMachineItem extends TileMachineBase implements ITileInventory {

    private InventoryAdvanced inv;

    protected TileMachineItem() {
        inv = new InventoryAdvanced(0).callbackInv(this);
    }

    protected TileMachineItem(int invSize) {
        inv = new InventoryAdvanced(invSize).callbackInv(this);
    }

    protected void setInventorySize(int invSize) {
        this.inv = new InventoryAdvanced(invSize).callbackInv(this);
    }

    @Override
    public InventoryAdvanced getInventory() {
        return inv;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("Items", data);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("Items", data);
        return data;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerFactory.wrap(this, facing));
        }
        return super.getCapability(capability, facing);
    }
}
