/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted;

import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.items.IMagnifiable;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class TileTrackOutfitted extends TileRailcraft implements IOutfittedTrackTile, IGuiReturnHandler, IMagnifiable {

    private ITrackKitInstance trackKitInstance = new TrackKitMissing(false);
    private TrackType trackType = TrackTypes.IRON.getTrackType();

    public TileTrackOutfitted() {
        trackKitInstance.setTile(this);
    }

    @Override
    public TrackType getTrackType() {
        return trackType;
    }

    public void setTrackType(TrackType trackType) {
        this.trackType = trackType;
    }

    @Override
    public ITrackKitInstance getTrackKitInstance() {
        return trackKitInstance;
    }

    public void setTrackKitInstance(ITrackKitInstance trackKit) {
        this.trackKitInstance = trackKit;
        trackKitInstance.setTile(this);
    }

    @Override
    public String getLocalizationTag() {
        return "tile.railcraft.track_outfitted." + trackType.getName() + "." + trackKitInstance.getTrackKit().getName();
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setString(TrackType.NBT_TAG, getTrackType().getName());
        data.setString(TrackKit.NBT_TAG, getTrackKitInstance().getTrackKit().getName());

        trackKitInstance.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey(TrackType.NBT_TAG)) {
            trackType = TrackRegistry.TRACK_TYPE.get(data);
        } else
            trackType = TrackTypes.IRON.getTrackType();

        if (data.hasKey(TrackKit.NBT_TAG)) {
            TrackKit trackKit = TrackRegistry.TRACK_KIT.get(data);
            setTrackKitInstance(trackKit.createInstance());
        } else
            setTrackKitInstance(TrackRegistry.getMissingTrackKit().createInstance());

        trackKitInstance.setTile(this);
        trackKitInstance.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeInt(TrackRegistry.TRACK_TYPE.getId(getTrackType()));
        data.writeInt(TrackRegistry.TRACK_KIT.getId(getTrackKitInstance().getTrackKit()));
        trackKitInstance.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        boolean needsUpdate = false;
        TrackType type = TrackRegistry.TRACK_TYPE.get(data.readInt());
        if (trackType != type) {
            trackType = type;
            needsUpdate = true;
        }
        TrackKit kit = TrackRegistry.TRACK_KIT.get(data.readInt());
        if (getTrackKitInstance().getTrackKit() != kit) {
            setTrackKitInstance(kit.createInstance());
            needsUpdate = true;
        }
        if (needsUpdate)
            markBlockForUpdate();
        trackKitInstance.readPacketData(data);
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        if (trackKitInstance instanceof IGuiReturnHandler)
            ((IGuiReturnHandler) trackKitInstance).writeGuiData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        if (trackKitInstance instanceof IGuiReturnHandler)
            ((IGuiReturnHandler) trackKitInstance).readGuiData(data, sender);
    }

    @Override
    public void onMagnify(EntityPlayer viewer) {
        if (trackKitInstance instanceof IMagnifiable) {
            ((IMagnifiable) trackKitInstance).onMagnify(viewer);
        }
    }
}
