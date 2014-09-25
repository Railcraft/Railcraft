/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerTank extends TileBoiler {

    private final static ITileFilter OUTPUT_FILTER = new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            if (tile instanceof TileBoiler)
                return false;
            else if (tile instanceof IFluidHandler)
                return true;
            return false;
        }

    };
    private boolean isConnected;

    protected TileBoilerTank() {
        super();
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(isStructureValid());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        isConnected = data.readBoolean();
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public ITileFilter getOutputFilter() {
        return OUTPUT_FILTER;
    }

}
