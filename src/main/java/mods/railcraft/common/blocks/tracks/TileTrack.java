/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackTile;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileTrack extends RailcraftTileEntity implements ITrackTile, IGuiReturnHandler {
    public ITrackInstance track;

    public TileTrack() {
    }

    public TileTrack(ITrackInstance t) {
        track = t;
        track.setTile(this);
    }

    @Override
    public String getLocalizationTag() {
        return "tile." + track.getTrackSpec().getTrackTag().replace(':', '.') + ".name";
    }

    @Nonnull
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setString("trackTag", getTrackInstance().getTrackSpec().getTrackTag());

        track.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("trackTag")) {
            TrackSpec spec = TrackRegistry.getTrackSpec(data.getString("trackTag"));
            track = spec.createInstanceFromSpec();
        }
        if (track == null)
            track = TrackRegistry.getDefaultTrackSpec().createInstanceFromSpec();

        track.setTile(this);

        track.readFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        if (track != null)
            track.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        if (track != null)
            track.readPacketData(data);
    }

    @Override
    public boolean canUpdate() {
        return track.canUpdate();
    }

    @Override
    public void update() {
        super.update();
        track.update();
    }

    @Override
    public short getId() {
        return track.getTrackSpec().getTrackId();
    }

    @Override
    public ITrackInstance getTrackInstance() {
        track.setTile(this);
        return track;
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        if (track instanceof IGuiReturnHandler)
            ((IGuiReturnHandler) track).writeGuiData(data);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        if (track instanceof IGuiReturnHandler)
            ((IGuiReturnHandler) track).readGuiData(data, sender);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
