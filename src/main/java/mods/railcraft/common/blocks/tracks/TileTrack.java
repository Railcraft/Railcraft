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
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
        } else if (data.hasKey("trackId")) {
            TrackSpec spec = TrackRegistry.getTrackSpec(data.getInteger("trackId"));
            track = spec.createInstanceFromSpec();
        }
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
    public void updateEntity() {
        super.updateEntity();
        track.updateEntity();
    }

    @Override
    public short getId() {
        return (short) track.getTrackSpec().getTrackId();
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
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
        return oldBlock != newBlock;
    }
}
