/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerTank extends TileBoiler {

    private static final Predicate<TileEntity> OUTPUT_FILTER = tile -> {
        if (tile instanceof TileBoiler)
            return false;
        else if (tile instanceof IFluidHandler)
            return true;
        return false;
    };
    private boolean isConnected;

    protected TileBoilerTank() {
    }

    @Override
    public void writePacketData( RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(isStructureValid());
    }

    @Override
    public void readPacketData( RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        isConnected = data.readBoolean();
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public Predicate<TileEntity> getOutputFilter() {
        return OUTPUT_FILTER;
    }
}
