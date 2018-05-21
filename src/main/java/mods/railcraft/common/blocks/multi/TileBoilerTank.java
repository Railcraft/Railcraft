/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.util.misc.Predicates;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.tileentity.TileEntity;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerTank<S extends TileBoilerTank<S>> extends TileBoiler<S> {

    private static final Predicate<TileEntity> OUTPUT_FILTER = Predicates.notInstanceOf(TileBoiler.class);

    private boolean isConnected;

    protected TileBoilerTank() {
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(isStructureValid());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        isConnected = data.readBoolean();
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public Predicate<TileEntity> getOutputFilter() {
        return OUTPUT_FILTER;
    }
}
