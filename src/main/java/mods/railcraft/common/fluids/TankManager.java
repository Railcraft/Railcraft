/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import com.google.common.collect.ForwardingList;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankManager extends ForwardingList<StandardTank> implements IFluidHandler {
    public static final TankManager NIL = new TankManager() {
        @Override
        protected List<StandardTank> delegate() {
            return Collections.emptyList();
        }
    };
    public static final BiFunction<TileEntity, EnumFacing, Boolean> TANK_FILTER = (t, f) -> t.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
    private final List<StandardTank> tanks = new ArrayList<>();

    @Override
    protected List<StandardTank> delegate() {
        return tanks;
    }

    @Override
    public boolean add(StandardTank tank) {
        tanks.add(tank);
        int index = tanks.indexOf(tank);
        tank.setTankIndex(index);
        return true;
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
        List<NBTTagCompound> tagList = NBTPlugin.getNBTList(data, "tanks", NBTTagCompound.class);
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
    public IFluidTankProperties[] getTankProperties() {
        return stream().filter(tank -> !tank.isHidden()).flatMap(t -> Arrays.stream(t.getTankProperties())).toArray(IFluidTankProperties[]::new);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tanks.stream().mapToInt(tank -> tank.fill(resource, doFill)).filter(filled -> filled > 0).findFirst().orElse(0);
    }

    public int fill(int tankIndex, @Nullable FluidStack resource, boolean doFill) {
        if (tankIndex < 0 || tankIndex >= tanks.size() || resource == null)
            return 0;

        return tanks.get(tankIndex).fill(resource, doFill);
    }

    @Override
    public @Nullable FluidStack drain(FluidStack resource, boolean doDrain) {
        return tanks.stream().map(tank -> tank.drain(resource, doDrain)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
        return tanks.stream().map(tank -> tank.drain(maxDrain, doDrain)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public @Nullable FluidStack drain(int tankIndex, FluidStack resource, boolean doDrain) {
        if (tankIndex < 0 || tankIndex >= tanks.size())
            return null;

        return tanks.get(tankIndex).drain(resource, doDrain);
    }

    public @Nullable FluidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
        if (tankIndex < 0 || tankIndex >= tanks.size())
            return null;

        return tanks.get(tankIndex).drain(maxDrain, doDrain);
    }

    @Override
    public StandardTank get(int tankIndex) {
        if (tankIndex < 0 || tankIndex >= tanks.size())
            throw new IllegalArgumentException("No Fluid Tank exists for index " + tankIndex);
        return tanks.get(tankIndex);
    }

    public void setCapacity(int tankIndex, int capacity) {
        StandardTank tank = get(tankIndex);
        tank.setCapacity(capacity);
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null && fluidStack.amount > capacity)
            fluidStack.amount = capacity;
    }

    public void pull(AdjacentTileCache cache, Predicate<? super TileEntity> filter, EnumFacing[] sides, int tankIndex, int amount) {
        Collection<IFluidHandler> targets = FluidTools.findNeighbors(cache, filter, sides);
        pull(targets, tankIndex, amount);
    }

    public void push(AdjacentTileCache cache, Predicate<? super TileEntity> filter, EnumFacing[] sides, int tankIndex, int amount) {
        Collection<IFluidHandler> targets = FluidTools.findNeighbors(cache, filter, sides);
        push(targets, tankIndex, amount);
    }

    public void pull(Collection<IFluidHandler> targets, int tankIndex, int amount) {
        transfer(targets, tankIndex, (me, them) -> FluidUtil.tryFluidTransfer(me, them, amount, true));
    }

    public void push(Collection<IFluidHandler> targets, int tankIndex, int amount) {
        transfer(targets, tankIndex, (me, them) -> FluidUtil.tryFluidTransfer(them, me, amount, true));
    }

    public void transfer(Collection<IFluidHandler> targets, int tankIndex, BiConsumer<IFluidHandler, IFluidHandler> transfer) {
        targets.forEach(them -> transfer.accept(get(tankIndex), them));
    }
}
