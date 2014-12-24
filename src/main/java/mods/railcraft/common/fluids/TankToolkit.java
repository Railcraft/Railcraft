/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This class provides some convenience functions for ITankContainers
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankToolkit implements IFluidHandler {

    private final IFluidHandler tankContainer;

    public TankToolkit(IFluidHandler c) {
        tankContainer = c;
    }

    public int getFluidQty(Fluid fluid) {
        if (fluid == null)
            return 0;
        int amount = 0;
        for (FluidTankInfo tank : getTankInfo(ForgeDirection.UNKNOWN)) {
            if (tank.fluid != null && fluid == tank.fluid.getFluid())
                amount += tank.fluid.amount;
        }
        return amount;
    }

    public boolean isTankEmpty(Fluid fluid) {
        if (fluid == null)
            return areTanksEmpty();
        return getFluidQty(fluid) <= 0;
    }

    public boolean isTankFull(Fluid fluid) {
        if (fluid == null)
            return areTanksFull();
        int fill = fill(ForgeDirection.UNKNOWN, new FluidStack(fluid, 1), false);
        return fill <= 0;
    }

    public boolean areTanksFull() {
        for (FluidTankInfo tank : getTankInfo(ForgeDirection.UNKNOWN)) {
            if (tank.fluid == null || tank.fluid.amount < tank.capacity)
                return false;
        }
        return true;
    }

    public boolean areTanksEmpty() {
        return !isFluidInTank();
    }

    public boolean isFluidInTank() {
        for (FluidTankInfo tank : getTankInfo(ForgeDirection.UNKNOWN)) {
            boolean empty = tank.fluid == null || tank.fluid.amount <= 0;
            if (!empty)
                return true;
        }
        return false;
    }

    public float getFluidLevel() {
        int amount = 0;
        int capacity = 0;
        for (FluidTankInfo tank : getTankInfo(ForgeDirection.UNKNOWN)) {
            FluidStack liquid = tank.fluid;
            amount += liquid == null ? 0 : liquid.amount;
            capacity += tank.capacity;
        }
        return capacity == 0 ? 0 : amount / capacity;
    }

    public float getFluidLevel(Fluid fluid) {
        int amount = 0;
        int capacity = 0;
        for (FluidTankInfo tank : getTankInfo(ForgeDirection.UNKNOWN)) {
            FluidStack liquid = tank.fluid;
            if (liquid == null || liquid.getFluid() != fluid)
                continue;
            amount += liquid.amount;
            capacity += tank.capacity;
        }
        return capacity == 0 ? 0 : amount / (float) capacity;
    }

    public boolean canPutFluid(ForgeDirection from, FluidStack liquid) {
        if (liquid == null)
            return false;
        return fill(from, liquid, false) > 0;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tankContainer.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tankContainer.drain(from, maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return tankContainer.drain(from, resource, doDrain);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        return tankContainer.getTankInfo(side);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return tankContainer.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return tankContainer.canDrain(from, fluid);
    }

}
