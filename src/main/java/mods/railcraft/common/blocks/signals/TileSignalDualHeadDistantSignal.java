/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.DualSignalReceiver;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileSignalDualHeadDistantSignal extends TileSignalBase implements IReceiverTile, IDualHeadSignal {

    private static final float SIZE = -0.15f;
    private final DualSignalReceiver receiver = new DualSignalReceiver(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.DUAL_HEAD_DISTANT_SIGNAL;
    }

    @Override
    public int getLightValue() {
        return Math.max(getTopAspect().getLightValue(), getBottomAspect().getLightValue());
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(worldObj)) {
            receiver.tickClient();
            return;
        }

        receiver.tickServer();
        int numPairs = receiver.getNumPairs();
        boolean changed = false;
        switch (numPairs) {
            case 0:
                changed |= receiver.setTopAspect(SignalAspect.BLINK_RED);
            case 1:
                changed |= receiver.setBottomAspect(SignalAspect.BLINK_RED);
        }
        if (changed) {
            sendUpdateToClient();
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
        sendUpdateToClient();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        getBlockType().setBlockBounds(0.15f, 0f, 0.15f, 0.85f, 1f, 0.85f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos) {
        return AABBFactory.start().createBoxForTileAt(pos).expandHorizontally(SIZE).build();
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        receiver.writeToNBT(data);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        receiver.readFromNBT(data);

    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        receiver.readPacketData(data);
    }

    @Override
    public DualSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public SignalAspect getTopAspect() {
        return receiver.getTopAspect();
    }

    @Override
    public SignalAspect getBottomAspect() {
        return receiver.getBottomAspect();
    }

    @Override
    public SignalAspect getSignalAspect() {
        return receiver.getTopAspect();
    }
}
