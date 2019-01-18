/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static net.minecraft.util.EnumFacing.*;

public class TileBoxInterlock extends TileBoxBase implements IControllerTile, IReceiverTile, IAspectProvider {

    private static final EnumFacing[] SIDES = {NORTH, WEST, SOUTH, EAST};
    private final SimpleSignalController controller = new SimpleSignalController(getLocalizationTag(), this);
    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);
    private Interlock interlock = new Interlock(this);
    private SignalAspect overrideAspect = SignalAspect.RED;

    @Override
    public IEnumMachine<?> getMachineType() {
        return SignalBoxVariant.INTERLOCK;
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
    }

    @Override
    public void update() {
        super.update();
        if (world.isRemote) {
            controller.tickClient();
            receiver.tickClient();
            return;
        }

        controller.tickServer();
        receiver.tickServer();

        overrideAspect = getOverrideAspect();

        mergeInterlocks();

        interlock.tick(this);

        SignalAspect prevAspect = controller.getAspect();
        if (receiver.isBeingPaired() || controller.isBeingPaired())
            controller.setAspect(SignalAspect.BLINK_YELLOW);
        else if (controller.isPaired() && receiver.isPaired())
            controller.setAspect(determineAspect());
        else
            controller.setAspect(SignalAspect.BLINK_RED);

        if (prevAspect != controller.getAspect())
            sendUpdateToClient();
    }

    private void mergeInterlocks() {
        for (EnumFacing side : SIDES) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (tile instanceof TileBoxInterlock) {
                TileBoxInterlock box = (TileBoxInterlock) tile;
                if (box.interlock != interlock) {
                    box.interlock.merge(interlock);
                    return;
                }
            }
        }
    }

    private SignalAspect getOverrideAspect() {
        SignalAspect newAspect = SignalAspect.GREEN;
        for (int side = 2; side < 6; side++) {
            EnumFacing forgeSide = EnumFacing.VALUES[side];
            TileEntity t = tileCache.getTileOnSide(forgeSide);
            if (t instanceof TileBoxBase) {
                TileBoxBase tile = (TileBoxBase) t;
                if (tile.canTransferAspect())
                    newAspect = SignalAspect.mostRestrictive(newAspect, tile.getBoxSignalAspect(forgeSide.getOpposite()));
            }
        }
        return newAspect;
    }

    private SignalAspect determineAspect() {
        interlock.requestLock(this, receiver.getAspect().ordinal() <= SignalAspect.YELLOW.ordinal());
        return interlock.getAspect(this, receiver.getAspect());
    }

    @Override
    public SignalAspect getBoxSignalAspect(@Nullable EnumFacing side) {
        return controller.getAspect();
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        controller.writeToNBT(data);
        receiver.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        controller.readFromNBT(data);
        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        controller.writePacketData(data);
        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        controller.readPacketData(data);
        receiver.readPacketData(data);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isConnected(EnumFacing side) {
        TileEntity tile = tileCache.getTileOnSide(side);
        if (tile instanceof TileBoxInterlock)
            return true;
        if (tile instanceof TileBoxBase)
            return ((TileBoxBase) tile).canTransferAspect();
        return false;
    }

    @Override
    public boolean canTransferAspect() {
        return false;
    }

    @Override
    public boolean canReceiveAspect() {
        return true;
    }

    @Override
    public SignalController getController() {
        return controller;
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getTriggerAspect() {
        return getBoxSignalAspect(null);
    }

    @Override
    public List<String> getDebugOutput() {
        List<String> debug = super.getDebugOutput();
        debug.add("Interlock Obj: " + interlock);
        debug.add("Interlock Pool: " + interlock.interlocks);
        debug.add("Lock Requests: " + interlock.lockRequests);
        debug.add("Active: " + interlock.active);
        debug.add("Delay: " + interlock.delay);
        debug.add("In Aspect: " + receiver.getAspect().name());
        debug.add("Out Aspect: " + controller.getAspect().name());
        debug.add("Override Aspect: " + overrideAspect.name());
        return debug;
    }

    private static class TileComparator implements Comparator<TileBoxInterlock> {
        public static final TileComparator INSTANCE = new TileComparator();

        @Override
        public int compare(TileBoxInterlock o1, TileBoxInterlock o2) {
            if (o1.getX() != o2.getX())
                return o1.getX() - o2.getX();
            if (o1.getZ() != o2.getZ())
                return o1.getZ() - o2.getZ();
            if (o1.getY() != o2.getY())
                return o1.getY() - o2.getY();
            return 0;
        }
    }

    private class Interlock {
        private static final int DELAY = 20 * 10;
        private final TreeSet<TileBoxInterlock> interlocks = new TreeSet<>(TileComparator.INSTANCE);
        private final TreeSet<TileBoxInterlock> lockRequests = new TreeSet<>(TileComparator.INSTANCE);
        private @Nullable TileBoxInterlock active;
        private int delay;

        public Interlock(TileBoxInterlock tile) {
            interlocks.add(tile);
        }

        void merge(Interlock interlock) {
            interlocks.addAll(interlock.interlocks);
            for (TileBoxInterlock box : interlocks) {
                box.interlock = this;
            }
        }

        public void tick(TileBoxInterlock host) {
            interlocks.removeIf(TileEntity::isInvalid);
            if (delay < DELAY) {
                delay++;
                return;
            }
            if (active != null && active.isInvalid())
                active = null;
            if (active == null && !lockRequests.isEmpty() && interlocks.first() == host) {
                active = lockRequests.last();
                lockRequests.clear();
            }
        }

        void requestLock(TileBoxInterlock host, boolean request) {
            if (request)
                lockRequests.add(host);
            else if (active == host)
                active = null;
        }

        public SignalAspect getAspect(TileBoxInterlock host, SignalAspect requestedAspect) {
            if (host == active) {
                SignalAspect overrideAspect = SignalAspect.GREEN;
                for (TileBoxInterlock box : interlocks) {
                    overrideAspect = SignalAspect.mostRestrictive(overrideAspect, box.overrideAspect);
                }
                return SignalAspect.mostRestrictive(overrideAspect, requestedAspect);
            }
            return SignalAspect.RED;
        }
    }
}
