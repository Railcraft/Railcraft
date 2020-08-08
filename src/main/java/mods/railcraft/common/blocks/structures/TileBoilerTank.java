/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.util.misc.Predicates;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerTank extends TileBoiler {

    private static final Predicate<TileEntity> OUTPUT_FILTER = Predicates.notInstanceOf(TileBoiler.class);

    private boolean isConnected;

    protected TileBoilerTank() {
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        if (!isStructureValid())
            return state;
        char marker = getPatternMarker();
        if (marker == 'O')
            return state;
        BlockPos patternPos = getPatternPosition();
        StructurePattern pattern = requireNonNull(getCurrentPattern());
        state = state
                .withProperty(BlockBoilerTank.NORTH, pattern.getPatternMarker(patternPos.offset(EnumFacing.NORTH)) == marker)
                .withProperty(BlockBoilerTank.SOUTH, pattern.getPatternMarker(patternPos.offset(EnumFacing.SOUTH)) == marker)
                .withProperty(BlockBoilerTank.EAST, pattern.getPatternMarker(patternPos.offset(EnumFacing.EAST)) == marker)
                .withProperty(BlockBoilerTank.WEST, pattern.getPatternMarker(patternPos.offset(EnumFacing.WEST)) == marker);
        return state;
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
