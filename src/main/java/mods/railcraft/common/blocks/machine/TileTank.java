/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.slots.SlotLiquidContainer;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static net.minecraft.util.EnumFacing.UP;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileTank extends TileMultiBlockInventory implements IFluidHandler, ITankTile, ISidedInventory {

    protected final TankManager tankManager = new TankManager();

    protected TileTank(String name, int invNum, List<? extends MultiBlockPattern> patterns) {
        super(name, invNum, patterns);
    }

    @Override
    public IInventory getInventory() {
        return this;
    }

    @Override
    @Nullable
    public TankManager getTankManager() {
        TileTank mBlock = (TileTank) getMasterBlock();
        if (mBlock != null) {
            return mBlock.tankManager;
        }
        return null;
    }

    @Override
    @Nullable
    public StandardTank getTank() {
        TileTank mBlock = (TileTank) getMasterBlock();
        if (mBlock != null) {
            return mBlock.tankManager.get(0);
        }
        return null;
    }

    @Override
    public Slot getInputSlot(IInventory inv, int id, int x, int y) {
        return new SlotLiquidContainer(inv, id, x, y);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.fill(resource, doFill);
        }
        return 0;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    @Nullable
    public FluidStack drain(@Nullable FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.drain(resource, doDrain);
        }
        return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.getTankProperties();
        }
        return FakeTank.PROPERTIES;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == UP) {
            return new int[]{0};
        }
        return new int[]{1};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this;
        return super.getCapability(capability, facing);
    }
}
