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
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("rawtypes")
public abstract class TileBoilerTank<T extends TileBoilerTank<T, F>, F extends TileBoilerFirebox<F>> extends TileBoiler<T, F> {

    private static final Predicate<TileEntity> OUTPUT_FILTER = Predicates.notInstanceOf(TileBoiler.class);

    private boolean isConnected;

    protected TileBoilerTank() {
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        if (getCurrentPattern() == null)
            return state;
        BlockPos patternPos = getPatternPosition();
        if (patternPos == null)
            return state;
        char marker = getPatternMarker();
        if (marker == 'O')
            return state;
        state = state
                .withProperty(BlockBoilerTank.NORTH, getCurrentPattern().getPatternMarker(patternPos.offset(EnumFacing.NORTH)) == marker)
                .withProperty(BlockBoilerTank.SOUTH, getCurrentPattern().getPatternMarker(patternPos.offset(EnumFacing.SOUTH)) == marker)
                .withProperty(BlockBoilerTank.EAST, getCurrentPattern().getPatternMarker(patternPos.offset(EnumFacing.EAST)) == marker)
                .withProperty(BlockBoilerTank.WEST, getCurrentPattern().getPatternMarker(patternPos.offset(EnumFacing.WEST)) == marker);
        return state;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<F> defineMasterClass() {
        return (Class<F>) TileBoilerFirebox.class; // Idea glitched on this cast
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
