/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import com.google.common.collect.ForwardingList;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin.NBTList;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankManager extends ForwardingList<StandardTank> implements IFluidHandler {
    public static final Predicate<TileEntity> TANK_FILTER = tile -> tile instanceof IFluidHandler;
    private static final byte NETWORK_DATA = 3;
    private final List<StandardTank> tanks = new ArrayList<>();

    public TankManager() {
    }

    public TankManager(StandardTank... tanks) {
        addAll(Arrays.asList(tanks));
    }

    @Nullable
    public static IFluidHandler getTankFromTile(@Nullable TileEntity tile) {
        IFluidHandler tank = null;
        if (tile instanceof IFluidHandler)
            tank = (IFluidHandler) tile;
        return tank;
    }

    @Override
    protected List<StandardTank> delegate() {
        return tanks;
    }

    @Override
    public boolean add(StandardTank tank) {
        boolean added = tanks.add(tank);
        int index = tanks.indexOf(tank);
        tank.setTankIndex(index);
        return added;
    }

    public void writeTanksToNBT(NBTTagCompound data) {
        NBTTagList tagList = new NBTTagList();
        for (byte slot = 0; slot < tanks.size(); slot++) {
            StandardTank tank = tanks.get(slot);
            if (tank.getFluid() != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("tank", slot);
                tank.writeToNBT(tag);
                tagList.appendTag(tag);
            }
        }
        data.setTag("tanks", tagList);
    }

    public void readTanksFromNBT(NBTTagCompound data) {
        NBTList<NBTTagCompound> tagList = NBTPlugin.getNBTList(data, "tanks", NBTPlugin.EnumNBTType.COMPOUND);
        for (NBTTagCompound tag : tagList) {
            int slot = tag.getByte("tank");
            if (slot >= 0 && slot < tanks.size())
                tanks.get(slot).readFromNBT(tag);
        }
    }

    public void writePacketData(RailcraftOutputStream data) throws IOException {
        for (StandardTank tank : tanks) {
            data.writeFluidStack(tank.getFluid());
        }
    }

    public void readPacketData(RailcraftInputStream data) throws IOException {
        for (StandardTank tank : tanks) {
            tank.setFluid(data.readFluidStack());
        }
    }

    @Override
    public int fill(EnumFacing from, @Nullable FluidStack resource, boolean doFill) {
        return fill(0, resource, doFill);
    }

    public int fill(int tankIndex, @Nullable FluidStack resource, boolean doFill) {
        if (tankIndex < 0 || tankIndex >= tanks.size() || resource == null)
            return 0;

        return tanks.get(tankIndex).fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return drain(0, maxDrain, doDrain);
    }

    @Nullable
    public FluidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
        if (tankIndex < 0 || tankIndex >= tanks.size())
            return null;

        return tanks.get(tankIndex).drain(maxDrain, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        for (StandardTank tank : tanks) {
            if (tankCanDrainFluid(tank, resource))
                return tank.drain(resource.amount, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(@Nullable EnumFacing direction) {
        List<FluidTankInfo> info = stream().filter(tank -> !tank.isHidden()).map(FluidTank::getInfo).collect(Collectors.toList());
        return info.toArray(new FluidTankInfo[info.size()]);
    }

    public FluidTankInfo[] getTankInfo() {
        return getTankInfo(null);
    }

    @Override
    @Nullable
    public StandardTank get(int tankIndex) {
        if (tankIndex < 0 || tankIndex >= tanks.size())
            return null;
        return tanks.get(tankIndex);
    }

    public void setCapacity(int tankIndex, int capacity) {
        StandardTank tank = get(tankIndex);
        if (tank == null)
            return;
        tank.setCapacity(capacity);
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null && fluidStack.amount > capacity)
            fluidStack.amount = capacity;
    }

    public void outputLiquid(AdjacentTileCache cache, Predicate<TileEntity> filter, EnumFacing[] sides, int tankIndex, int amount) {
        for (EnumFacing side : sides) {
            TileEntity tile = cache.getTileOnSide(side);
            if (tile == null) continue;
            if (!filter.test(tile)) continue;
            IFluidHandler tank = getTankFromTile(tile);
            if (tank == null) continue;
            outputLiquid(tank, side, tankIndex, amount);
        }
    }

    public void outputLiquid(IFluidHandler[] outputs, int tankIndex, int amount) {
        for (int side = 0; side < 6; side++) {
            IFluidHandler nearbyTank = outputs[side];
            if (nearbyTank != null)
                outputLiquid(nearbyTank, EnumFacing.VALUES[side], tankIndex, amount);
        }
    }

    private void outputLiquid(IFluidHandler tank, EnumFacing side, int tankIndex, int amount) {
        FluidStack available = drain(tankIndex, amount, false);
        if (available != null) {
            int used = tank.fill(side.getOpposite(), available, true);
            if (used > 0)
                drain(tankIndex, used, true);
        }
    }

    private boolean tankAcceptsFluid(StandardTank tank, @Nullable FluidStack fluidStack) {
        return fluidStack != null && tank.fill(fluidStack, false) > 0;
    }

    private boolean tankCanDrain(StandardTank tank) {
        FluidStack drained = tank.drain(1, false);
        return drained != null && drained.amount > 0;
    }

    private boolean tankCanDrainFluid(StandardTank tank, @Nullable FluidStack fluidStack) {
        return fluidStack != null && Fluids.areEqual(tank.getFluidType(), fluidStack) && tankCanDrain(tank);
    }
}
