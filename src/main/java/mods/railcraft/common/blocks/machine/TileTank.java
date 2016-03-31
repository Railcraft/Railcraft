/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.slots.SlotLiquidContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static net.minecraft.util.EnumFacing.UP;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileTank extends TileMultiBlockInventory implements IFluidHandler, ITankTile, ISidedInventory {

    protected final TankManager tankManager = new TankManager();

    public TileTank(String name, int invNum, List<? extends MultiBlockPattern> patterns) {
        super(name, invNum, patterns);
    }

    @Override
    public IInventory getInventory() {
        return this;
    }

    @Override
    public TankManager getTankManager() {
        TileTank mBlock = (TileTank) getMasterBlock();
        if (mBlock != null) {
            return mBlock.tankManager;
        }
        return null;
    }

    @Override
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
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.fill(from, resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.drain(from, maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.drain(from, resource, doDrain);
        }
        return null;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing side) {
        TankManager tMan = getTankManager();
        if (tMan != null) {
            return tMan.getTankInfo();
        }
        return FakeTank.INFO;
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
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        tankManager.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        tankManager.readPacketData(data);
    }
}
