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

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IFluidHandlerImplementor extends IFluidHandler {
    TankManager getTankManager();

    @Override
    default IFluidTankProperties[] getTankProperties() {
        return getTankManager().getTankProperties();
    }

    @Override
    default int fill(FluidStack resource, boolean doFill) {
        return getTankManager().fill(resource, doFill);
    }

    @Nullable
    @Override
    default FluidStack drain(FluidStack resource, boolean doDrain) {
        return getTankManager().drain(resource, doDrain);
    }

    @Nullable
    @Override
    default FluidStack drain(int maxDrain, boolean doDrain) {
        return getTankManager().drain(maxDrain, doDrain);
    }
}
