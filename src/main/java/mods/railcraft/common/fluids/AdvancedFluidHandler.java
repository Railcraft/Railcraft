/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * This class provides some convenience functions for IFluidHandler
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class AdvancedFluidHandler implements IFluidHandler {

    private final IFluidHandler tankContainer;

    public AdvancedFluidHandler(IFluidHandler c) {
        tankContainer = c;
    }

    public int getFluidQty(@Nullable FluidStack fluid) {
        if (fluid == null)
            return 0;
        return Arrays.stream(getTankProperties())
                .map(IFluidTankProperties::getContents)
                .filter(content -> FluidTools.matches(fluid, content))
                .mapToInt(content -> content.amount).sum();
    }

    public boolean isTankEmpty(@Nullable FluidStack fluid) {
        if (fluid == null)
            return areTanksEmpty();
        return getFluidQty(fluid) <= 0;
    }

    public boolean isTankFull(@Nullable FluidStack fluid) {
        if (fluid == null)
            return areTanksFull();
        int fill = fill(new FluidStack(fluid, 1), false);
        return fill <= 0;
    }

    public boolean areTanksFull() {
        return Arrays.stream(getTankProperties())
                .noneMatch(tank -> tank.getContents() == null || tank.getContents().amount < tank.getCapacity());
    }

    public boolean areTanksEmpty() {
        return !isFluidInTank();
    }

    public boolean isFluidInTank() {
        for (IFluidTankProperties tank : getTankProperties()) {
            boolean empty = tank.getContents() == null || tank.getContents().amount <= 0;
            if (!empty)
                return true;
        }
        return false;
    }

    public float getFluidLevel() {
        int amount = 0;
        int capacity = 0;
        for (IFluidTankProperties tank : getTankProperties()) {
            FluidStack liquid = tank.getContents();
            amount += liquid == null ? 0 : liquid.amount;
            capacity += tank.getCapacity();
        }
        return capacity == 0 ? 0 : ((float) amount) / capacity;
    }

    public float getFluidLevel(FluidStack fluid) {
        int amount = 0;
        int capacity = 0;
        for (IFluidTankProperties tank : getTankProperties()) {
            FluidStack liquid = tank.getContents();
            if (liquid == null || !FluidTools.matches(liquid, fluid))
                continue;
            amount += liquid.amount;
            capacity += tank.getCapacity();
        }
        return capacity == 0 ? 0 : amount / (float) capacity;
    }

    public boolean canPutFluid(@Nullable FluidStack fluid) {
        return fluid != null && fill(fluid, false) > 0;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return tankContainer.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tankContainer.fill(resource, doFill);
    }

    @Override
    public @Nullable FluidStack drain(FluidStack resource, boolean doDrain) {
        return tankContainer.drain(resource, doDrain);
    }

    @Override
    public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
        return tankContainer.drain(maxDrain, doDrain);
    }
}
