/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.RailcraftTileEntity;
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

public class TileTrack extends RailcraftTileEntity implements ITrackTile, IGuiReturnHandler {
    @Nonnull
    public ITrackInstance track = new TrackInstanceDefault(false);

    public TileTrack() {
    }

    public void makeTrackInstance(@Nonnull TrackSpec trackSpec) {
        this.track = trackSpec.createInstanceFromSpec(this);
    }

    @Override
    public String getLocalizationTag() {
        return "tile." + track.getTrackSpec().getTrackTag().replace(':', '.') + ".name";
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        data.setString("trackTag", getTrackInstance().getTrackSpec().getTrackTag());

        track.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        ITrackInstance trackInstance;
        if (data.hasKey("trackTag")) {
            TrackSpec spec = TrackRegistry.getTrackSpec(data.getString("trackTag"));
            track = spec.createInstanceFromSpec(this);
        } else
            track = TrackRegistry.getDefaultTrackSpec().createInstanceFromSpec(this);

        track.readFromNBT(data);
    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        track.writePacketData(data);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
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
