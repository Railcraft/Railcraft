/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.interfaces.ITileCompare;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronValve<T extends TileTankBase<T, M>, M extends TileTankBase<M, M>> extends TileTankBase<T, M> implements IFluidHandler, ITileCompare {

    private static final EnumFacing[] FLUID_OUTPUTS = {EnumFacing.DOWN};
    private static final int FLOW_RATE = FluidTools.BUCKET_VOLUME;
    private static final byte FILL_INCREMENT = 1;
    private final StandardTank fillTank = new StandardTank(20);
    private int previousComparatorValue;

    private boolean previousStructureValidity;

    public TileTankIronValve() {
        fillTank.setHidden(true);
        tankManager.add(fillTank);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> defineSelfClass() {
        return (Class<T>) (Class<?>) TileTankIronValve.class; // Intellij Bug
    }

    private void setFilling(FluidStack resource) {
        boolean needsUpdate = fillTank.isEmpty();
        resource = resource.copy();
        resource.amount = 20;
        fillTank.fill(resource, true);

        if (needsUpdate)
            sendUpdateToClient();
    }

    private void decrementFilling() {
        if (!fillTank.isEmpty()) {
            fillTank.drain(FILL_INCREMENT, true);
            if (fillTank.isEmpty())
                sendUpdateToClient();
        }
    }

    public StandardTank getFillTank() {
        return fillTank;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(world))
            return;

        if (isStructureValid()) {
            decrementFilling();

            if (isMaster) {
                TileEntity tileBelow = tileCache.getTileOnSide(EnumFacing.DOWN);

                TileTankIronValve<?, ?> valveBelow = null;
                if (tileBelow instanceof TileTankIronValve) {
                    valveBelow = (TileTankIronValve<?, ?>) tileBelow;
                    if (valveBelow.isStructureValid() && valveBelow.getPatternMarker() == 'T') {
                        //noinspection ConstantConditions
                        StandardTank tankBelow = valveBelow.getTankManager().get(0);
                        assert tankBelow != null;
                        FluidStack liquid = tankBelow.getFluid();
                        if (liquid != null && liquid.amount >= tankBelow.getCapacity() - FluidTools.BUCKET_VOLUME) {
                            valveBelow = null;

                            FluidStack fillStack = liquid.copy();
                            fillStack.amount = FluidTools.BUCKET_VOLUME - (tankBelow.getCapacity() - liquid.amount);
                            if (fillStack.amount > 0) {
                                int used = tank.fill(fillStack, false);
                                if (used > 0) {
                                    fillStack = tankBelow.drain(used, true);
                                    tank.fill(fillStack, true);
                                }
                            }
                        }
                    } else
                        valveBelow = null;
                }

                if (valveBelow != null) {
                    FluidStack available = tankManager.drain(0, FluidTools.BUCKET_VOLUME, false);
                    if (available != null && available.amount > 0) {
                        int used = valveBelow.fill(available, true);
                        tankManager.drain(0, used, true);
                    }
                }
            }

            if (getPatternPosition().getY() - getPattern().getMasterOffset().getY() == 0) {
                TankManager tMan = getTankManager();
                if (!tMan.isEmpty())
                    tMan.push(tileCache, Predicates.notInstanceOf(TileTankBase.class), FLUID_OUTPUTS, 0, FLOW_RATE);
            }

            M masterBlock = getMasterBlock();
            if (masterBlock != null) {
                int compValue = masterBlock.getComparatorValue();
                if (previousComparatorValue != compValue) {
                    previousComparatorValue = compValue;
                    getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
                }
            }
        }

        if (previousStructureValidity != isStructureValid())
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
        previousStructureValidity = isStructureValid();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFill())
            return 0;
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            int amount = tMan.fill(resource, doFill);
            if (amount > 0 && doFill) {
                setFilling(resource.copy());
            }
            return amount;
        }
        return 0;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrain())
            return null;
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    @Nullable
    public FluidStack drain(@Nullable FluidStack resource, boolean doDrain) {
        if (!canDrain())
            return null;
        if (resource == null)
            return null;
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.drain(resource, doDrain);
        }
        return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        TankManager tMan = getTankManager();
        if (!tMan.isEmpty()) {
            return tMan.getTankProperties();
        }
        return FakeTank.PROPERTIES;
    }

    public boolean canFill() {
        return isStructureValid() && getPatternPosition().getY() - getPattern().getMasterOffset().getY() > 0;
    }

    public boolean canDrain() {
        return isStructureValid() && getPatternPosition().getY() - getPattern().getMasterOffset().getY() <= 1;
    }

    @Override
    public int getComparatorInputOverride() {
        M masterBlock = getMasterBlock();
        if (masterBlock != null)
            return masterBlock.getComparatorValue();
        return 0;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <C> C getCapability(Capability<C> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, facing);
    }
}
