/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import mods.railcraft.api.signals.DualSignalReceiver;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.common.util.misc.Game;

public class TileSignalDualHeadDistantSignal extends TileSignalBase implements IReceiverTile, IDualHeadSignal {

    private static final float SIZE = 0.15f;
    private final DualSignalReceiver receiver = new DualSignalReceiver(getLocalizationTag(), this);

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.DUAL_HEAD_DISTANT_SIGNAL;
    }

    @Override
    protected boolean isLit() {
        return isLit(getTopAspect()) || isLit(getBottomAspect());
    }

    @Override
    protected boolean isBlinking() {
        return getTopAspect().isBlinkAspect() || getBottomAspect().isBlinkAspect();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj)) {
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        getBlockType().setBlockBounds(0.15f, 0f, 0.15f, 0.85f, 1f, 0.85f);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i + 0.15f, j, k + 0.15f, i + 0.85f, j + 1f, k + 0.85f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i + SIZE, j, k + SIZE, i + 1 - SIZE, j + 1, k + 1 - SIZE);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        receiver.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        receiver.readFromNBT(data);

    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        receiver.writePacketData(data);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
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
