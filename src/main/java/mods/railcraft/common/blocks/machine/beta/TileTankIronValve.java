/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.IComparatorValueProvider;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankIronValve extends TileTankBase implements IFluidHandler, IComparatorValueProvider {

    private final static ITileFilter FLUID_OUTPUT_FILTER = new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            if (tile instanceof TileTankBase)
                return false;
            else if (tile instanceof IFluidHandler)
                return true;
            return false;
        }

    };
    private final static ForgeDirection[] FLUID_OUTPUTS = {ForgeDirection.DOWN};
    private static final int FLOW_RATE = FluidHelper.BUCKET_VOLUME;
    private static final byte FILL_INCREMENT = 1;
    private final StandardTank fillTank = new StandardTank(20);
    private int previousComparatorValue = 0;

    private boolean previousStructureValidity;

    public TileTankIronValve() {
        fillTank.setHidden(true);
        tankManager.add(fillTank);
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
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.TANK_IRON_VALVE;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(worldObj))
            return;
        decrementFilling();

        if (isMaster) {
            TileEntity tileBelow = tileCache.getTileOnSide(ForgeDirection.DOWN);

            TileTankIronValve valveBelow = null;
            if (tileBelow instanceof TileTankIronValve) {
                valveBelow = (TileTankIronValve) tileBelow;
                if (valveBelow.isStructureValid() && valveBelow.getPatternMarker() == 'T') {
                    StandardTank tankBelow = valveBelow.getTankManager().get(0);
                    FluidStack liquid = tankBelow.getFluid();
                    if (liquid != null && liquid.amount >= tankBelow.getCapacity() - FluidHelper.BUCKET_VOLUME) {
                        valveBelow = null;

                        FluidStack fillStack = liquid.copy();
                        fillStack.amount = FluidHelper.BUCKET_VOLUME - (tankBelow.getCapacity() - liquid.amount);
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
                FluidStack available = tankManager.drain(0, FluidHelper.BUCKET_VOLUME, false);
                if (available != null && available.amount > 0) {
                    int used = valveBelow.fill(ForgeDirection.UP, available, true);
                    tankManager.drain(0, used, true);
                }
            }
        }

        if (getPatternPositionY() - getPattern().getMasterOffsetY() == 0) {
            TankManager tMan = getTankManager();
            if (tMan != null)
                tMan.outputLiquid(tileCache, FLUID_OUTPUT_FILTER, FLUID_OUTPUTS, 0, FLOW_RATE);
        }

        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock instanceof TileTankBase) {
            TileTankBase masterTileTankBase = (TileTankBase) masterBlock;
            int compValue = masterTileTankBase.getComparatorValue();
            if (previousComparatorValue != compValue) {
                previousComparatorValue = compValue;
                getWorld().func_147453_f(getX(), getY(), getZ(), null);
            }
        }

        if (previousStructureValidity != isStructureValid())
            getWorld().func_147453_f(getX(), getY(), getZ(), null);
        previousStructureValidity = isStructureValid();
    }

    @Override
    public IIcon getIcon(int side) {
        if (!isStructureValid() || getPattern() == null)
            return getMachineType().getTexture(side);
        ForgeDirection s = ForgeDirection.getOrientation(side);
        char markerSide = getPattern().getPatternMarkerChecked(MiscTools.getXOnSide(getPatternPositionX(), s), MiscTools.getYOnSide(getPatternPositionY(), s), MiscTools.getZOnSide(getPatternPositionZ(), s));

        if (!isMapPositionOtherBlock(markerSide)) {
            if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal())
                return getMachineType().getTexture(6);
            return getMachineType().getTexture(7);
        }
        return getMachineType().getTexture(side);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!canFill(from, null))
            return 0;
        if (resource == null || resource.amount <= 0) return 0;
        TankManager tMan = getTankManager();
        if (tMan == null)
            return 0;
        resource = resource.copy();
//        resource.amount = Math.min(resource.amount, FLOW_RATE);
        int filled = tMan.fill(0, resource, doFill);
        if (filled > 0 && doFill)
            setFilling(resource.copy());
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (getPatternPositionY() - getPattern().getMasterOffsetY() != 1)
            return null;
        TankManager tMan = getTankManager();
        if (tMan != null)
            //            maxDrain = Math.min(maxDrain, FLOW_RATE);
            return tMan.drain(0, maxDrain, doDrain);
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        TankManager tMan = getTankManager();
        if (tMan != null && tMan.get(0).getFluidType() == resource.getFluid())
            return drain(from, resource.amount, doDrain);
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return getPatternPositionY() - getPattern().getMasterOffsetY() > 0;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return getPatternPositionY() - getPattern().getMasterOffsetY() <= 1;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        TankManager tMan = getTankManager();
        if (tMan != null)
            return tMan.getTankInfo();
        return FakeTank.INFO;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock instanceof TileTankBase)
            return ((TileTankBase) masterBlock).getComparatorValue();
        return 0;
    }

}
