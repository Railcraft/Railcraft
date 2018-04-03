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
import mods.railcraft.common.blocks.single.TileForceTrackEmitter;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Created by CovertJaguar on 8/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTrackForce extends RailcraftTileEntity {

    @Nullable
    private TileForceTrackEmitter emitter;

    private boolean zAxis = true;
    private EnumColor color = EnumColor.CYAN;

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeEnum(color);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        color = data.readEnum(EnumColor.VALUES);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Z-Axis", zAxis);
        data.setInteger("Color", color.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("Z-Axis", Constants.NBT.TAG_BYTE))
            zAxis = data.getBoolean("Z-Axis");
        if (data.hasKey("Color", Constants.NBT.TAG_INT))
            color = EnumColor.VALUES[data.getInteger("Color")];
    }

    public void checkForEmitter() {
        assert emitter != null;
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
        if (!WorldPlugin.isBlockAt(world, pos, Blocks.LAPIS_BLOCK/* TODO EnumMachineEpsilon.FORCE_TRACK_EMITTER.block())*/))
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
