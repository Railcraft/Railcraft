/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.collections.TimerBag;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by CovertJaguar on 4/13/2015 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileSignalToken extends TileSignalBase implements IControllerTile, ISignalTileToken {
    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private UUID tokenRingUUID = UUID.randomUUID();
    private BlockPos centroid;
    private final TrackLocator trackLocator = new TrackLocator(this);
    private final TimerBag<UUID> cartTimers = new TimerBag<>(8);

    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalVariant.TOKEN;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
            controller.tickClient();
            return;
        }

        TokenRing tokenRing = getTokenRing();
        if (!Objects.equals(centroid, tokenRing.centroid())) {
            centroid = tokenRing.centroid();
            sendUpdateToClient();
        }

        cartTimers.tick();
        if (trackLocator.getTrackStatus() == TrackLocator.Status.VALID) {
            BlockPos trackPos = trackLocator.getTrackLocation();
            if (trackPos != null) {
                List<EntityMinecart> carts = EntitySearcher.findMinecarts().around(trackPos).in(world);
                carts.stream().filter(c -> !cartTimers.contains(c.getUniqueID())).forEach(tokenRing::markCart);
                carts.forEach(c -> cartTimers.add(c.getUniqueID()));
            }
        }


        controller.tickServer();
        SignalAspect prevAspect = controller.getAspect();
        if (controller.isBeingPaired()) {
            controller.setAspect(SignalAspect.BLINK_YELLOW);
        } else {
            controller.setAspect(tokenRing.getAspect());
        }
        if (prevAspect != controller.getAspect()) {
            sendUpdateToClient();
        }
    }

    @Override
    public SignalAspect getSignalAspect() {
        return controller.getAspect();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data = super.writeToNBT(data);
        controller.writeToNBT(data);
        NBTPlugin.writeUUID(data, "tokenRingUUID", tokenRingUUID);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        controller.readFromNBT(data);
        tokenRingUUID = NBTPlugin.readUUID(data, "tokenRingUUID");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        controller.writePacketData(data);
        data.writeBlockPos(getTokenRing().centroid());
        data.writeUUID(tokenRingUUID);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        controller.readPacketData(data);
        centroid = data.readBlockPos();
        tokenRingUUID = data.readUUID();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        getTokenRing().removeSignal(getPos());
    }

    public UUID getTokenRingUUID() {
        return tokenRingUUID;
    }

    public void setTokenRingUUID(UUID tokenRingUUID) {
        this.tokenRingUUID = tokenRingUUID;
    }

    public BlockPos getTokenRingCentroid() {
        if (centroid == null)
            return getPos();
        return centroid;
    }

    @Override
    public SimpleSignalController getController() {
        return controller;
    }

    @Override
    public TokenRing getTokenRing() {
        return TokenManager.getManager(world).getTokenRing(tokenRingUUID, getPos());
    }

    @Override
    public TrackLocator getTrackLocator() {
        return trackLocator;
    }
}
