/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.epsilon.TileForceTrackEmitter;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class TrackForce extends TrackUnsupported {

    public TileForceTrackEmitter emitter;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.FORCE;
    }

    @Override
    public List<ItemStack> getDrops(int fortune) {
        return null;
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        if (Game.isHost(getWorld()))
            checkForEmitter();
    }

    public void checkForEmitter() {
        EnumTrackMeta meta = EnumTrackMeta.fromMeta(tileEntity.getBlockMetadata());
        int y = getY() - 1;
        Block emitterBlock = EnumMachineEpsilon.FORCE_TRACK_EMITTER.getBlock();
        if (meta == EnumTrackMeta.NORTH_SOUTH) {
            if (isValidEmitterTile(emitter, EnumFacing.NORTH, EnumFacing.SOUTH))
                return;
            else
                setEmitter(null);
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                int x = getX() + i * EnumFacing.NORTH.offsetX;
                int z = getZ() + i * EnumFacing.NORTH.offsetZ;
                if (isValidEmitter(x, y, z, emitterBlock, EnumFacing.SOUTH))
                    return;
            }
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                int x = getX() + i * EnumFacing.SOUTH.offsetX;
                int z = getZ() + i * EnumFacing.SOUTH.offsetZ;
                if (isValidEmitter(x, y, z, emitterBlock, EnumFacing.NORTH))
                    return;
            }
        } else {
            if (isValidEmitterTile(emitter, EnumFacing.EAST, EnumFacing.WEST))
                return;
            else
                setEmitter(null);
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                int x = getX() + i * EnumFacing.EAST.offsetX;
                int z = getZ() + i * EnumFacing.EAST.offsetZ;
                if (isValidEmitter(x, y, z, emitterBlock, EnumFacing.WEST))
                    return;
            }
            for (int i = 1; i <= TileForceTrackEmitter.MAX_TRACKS; i++) {
                int x = getX() + i * EnumFacing.WEST.offsetX;
                int z = getZ() + i * EnumFacing.WEST.offsetZ;
                if (isValidEmitter(x, y, z, emitterBlock, EnumFacing.EAST))
                    return;
            }
        }
        WorldPlugin.setBlockToAir(getWorld(), getX(), getY(), getZ());
    }

    public TileForceTrackEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(TileForceTrackEmitter emitter) {
        this.emitter = emitter;
    }

    private boolean isValidEmitter(int x, int y, int z, Block emitterBlock, EnumFacing facing) {
        if (WorldPlugin.getBlock(getWorld(), x, y, z) != emitterBlock)
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(getWorld(), x, y, z);
        if (tile instanceof TileForceTrackEmitter && isValidEmitterTile((TileForceTrackEmitter) tile, facing)) {
            setEmitter(emitter);
            return true;
        }
        return false;
    }

    private boolean isValidEmitterTile(TileForceTrackEmitter tile, EnumFacing... facing) {
        if (tile == null || tile.isInvalid())
            return false;
        if (getY() - 1 != tile.yCoord)
            return false;
        if (getX() != tile.xCoord && getZ() != tile.zCoord)
            return false;
        EnumFacing emitterFacing = tile.getFacing();
        for (EnumFacing f : facing) {
            if (f == emitterFacing)
                return true;
        }
        return false;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getExplosionResistance(double srcX, double srcY, double srcZ, Entity exploder) {
        return 6000000.0F;
    }

}
