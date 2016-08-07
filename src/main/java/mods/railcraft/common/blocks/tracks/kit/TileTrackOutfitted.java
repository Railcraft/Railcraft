/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kit;

import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.tracks.TrackTypes;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileTrackOutfitted extends RailcraftTileEntity implements ITrackTile, IGuiReturnHandler {
    @Nonnull
    public ITrackKit track = new TrackKitDefault(false);
    private TrackTypes trackType = TrackTypes.IRON;

    public TileTrackOutfitted() {
    }

    public TrackTypes getTrackType() {
        return trackType;
    }

    @Override
    public String getLocalizationTag() {
        return "tile." + track.getTrackKitSpec().getTrackTag().replace(':', '.') + ".name";
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        data.setString("trackTag", getTrackKit().getTrackKitSpec().getTrackTag());

        track.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("trackTag")) {
            TrackKitSpec spec = TrackRegistry.getTrackSpec(data.getString("trackTag"));
            track = spec.createInstanceFromSpec();
        } else
            track = TrackRegistry.getDefaultTrackSpec().createInstanceFromSpec();

        track.setTile(this);
        track.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        track.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        track.readPacketData(data);
    }

    @Override
    public short getId() {
        return -1;
    }

    @Override
    public ITrackKit getTrackKit() {
        return track;
    }

    @Override
    public void writeGuiData(@Nonnull RailcraftOutputStream data) throws IOException {
        if (track instanceof IGuiReturnHandler)
            ((IGuiReturnHandler) track).writeGuiData(data);
    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
        if (track instanceof IGuiReturnHandler)
            ((IGuiReturnHandler) track).readGuiData(data, sender);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
