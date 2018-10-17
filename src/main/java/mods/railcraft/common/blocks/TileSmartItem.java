/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.interfaces.ITileInventory;
import mods.railcraft.common.util.inventory.ItemHandlerFactory;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 *
 */
public abstract class TileSmartItem extends RailcraftTileEntity implements ISmartTile, ITileInventory {

    private final StandaloneInventory inv;

    protected TileSmartItem() {
        inv = new StandaloneInventory(0, (IInventory) this);
    }

    protected TileSmartItem(int invSize) {
        inv = new StandaloneInventory(invSize, (IInventory) this);
    }

    @Override
    public StandaloneInventory getInventory() {
        return inv;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("Items", data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("Items", data);
        return data;
    }

    @Override
    public Object getBackingObject() {
        return this;
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
