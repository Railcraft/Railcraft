/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.force;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.epsilon.TileForceTrackEmitter;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.BlockRailBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 8/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTrackForce extends RailcraftTileEntity {
    public TileForceTrackEmitter emitter;

    public void checkForEmitter() {
        World world = theWorld();
        assert world != null;
        BlockRailBase.EnumRailDirection meta = TrackTools.getTrackDirectionRaw(world, getPos());
        BlockPos checkPos = getPos().down();
        if (meta == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (isValidEmitterTile(emitter, EnumFacing.NORTH, EnumFacing.SOUTH))
                return;
            else
                setEmitter(null);
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.NORTH, i);
                if (isValidEmitter(pos, EnumFacing.SOUTH))
                    return;
            }
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.SOUTH, i);
                if (isValidEmitter(pos, EnumFacing.NORTH))
                    return;
            }
        } else {
            if (isValidEmitterTile(emitter, EnumFacing.EAST, EnumFacing.WEST))
                return;
            else
                setEmitter(null);
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.EAST, i);
                if (isValidEmitter(pos, EnumFacing.WEST))
                    return;
            }
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.WEST, i);
                if (isValidEmitter(pos, EnumFacing.EAST))
                    return;
            }
        }
        WorldPlugin.setBlockToAir(world, getPos());
    }

    @Nullable
    public TileForceTrackEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(@Nullable TileForceTrackEmitter emitter) {
        this.emitter = emitter;
    }

    private boolean isValidEmitter(BlockPos pos, EnumFacing facing) {
        World world = theWorld();
        assert world != null;
        if (!WorldPlugin.isBlockAt(world, pos, EnumMachineEpsilon.FORCE_TRACK_EMITTER.block()))
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileForceTrackEmitter && isValidEmitterTile((TileForceTrackEmitter) tile, facing)) {
            setEmitter(emitter);
            return true;
        }
        return false;
    }

    private boolean isValidEmitterTile(TileForceTrackEmitter tile, EnumFacing... facing) {
        if (tile.isInvalid())
            return false;
        BlockPos expected = getPos().down();
        if (!expected.equals(tile.getPos())) return false;
        EnumFacing emitterFacing = tile.getFacing();
        for (EnumFacing f : facing) {
            if (f == emitterFacing)
                return true;
        }
        return false;
    }

}
