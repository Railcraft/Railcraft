/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kit.instances;

import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.epsilon.TileForceTrackEmitter;
import mods.railcraft.common.blocks.tracks.kit.TrackKits;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

//TODO: split to own block?
public class TrackForce extends TrackUnsupported {

    public TileForceTrackEmitter emitter;

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.FORCE;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        return Collections.emptyList();
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        if (Game.isHost(theWorldAsserted()))
            checkForEmitter();
    }

    public void checkForEmitter() {
        EnumRailDirection meta = getRailDirection();
        BlockPos checkPos = getPos().down();
        Block emitterBlock = EnumMachineEpsilon.FORCE_TRACK_EMITTER.getBlock();
        if (meta == EnumRailDirection.NORTH_SOUTH) {
            if (isValidEmitterTile(emitter, EnumFacing.NORTH, EnumFacing.SOUTH))
                return;
            else
                setEmitter(null);
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.NORTH, i);
                if (isValidEmitter(pos, emitterBlock, EnumFacing.SOUTH))
                    return;
            }
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.SOUTH, i);
                if (isValidEmitter(pos, emitterBlock, EnumFacing.NORTH))
                    return;
            }
        } else {
            if (isValidEmitterTile(emitter, EnumFacing.EAST, EnumFacing.WEST))
                return;
            else
                setEmitter(null);
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.EAST, i);
                if (isValidEmitter(pos, emitterBlock, EnumFacing.WEST))
                    return;
            }
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                BlockPos pos = checkPos.offset(EnumFacing.WEST, i);
                if (isValidEmitter(pos, emitterBlock, EnumFacing.EAST))
                    return;
            }
        }
        WorldPlugin.setBlockToAir(theWorldAsserted(), getPos());
    }

    @Nullable
    public TileForceTrackEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(@Nullable TileForceTrackEmitter emitter) {
        this.emitter = emitter;
    }

    private boolean isValidEmitter(BlockPos pos, Block emitterBlock, EnumFacing facing) {
        if (WorldPlugin.getBlock(theWorldAsserted(), pos) != emitterBlock)
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(theWorldAsserted(), pos);
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

    @Override
    public boolean isProtected() {
        return true;
    }

//    @Override
//    public float getExplosionResistance(Explosion explosion, Entity exploder) {
//        return 6000000.0F;
//    }

}
