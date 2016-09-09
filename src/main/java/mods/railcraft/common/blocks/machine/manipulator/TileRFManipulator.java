/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

//TODO: standardize with other loaders
public abstract class TileRFManipulator extends TileCartManipulator implements ITileRotate {
    protected static final int TRANSFER_RATE = 8000;
    protected static final int TRANSFER_FADE = 20;
    private static final int RF_CAP = 4000000;
    protected int amountRF;
    protected EnumFacing direction = EnumFacing.NORTH;
    private int ticksSinceTransfer;

    protected TileRFManipulator() {
        setInventorySize(0);
    }

//    @Override
//    public IIcon getIcon(int side) {
//        if (side == direction.ordinal())
//            return getMachineType().getTexture(isProcessing() ? 7 : 8);
//        return getMachineType().getTexture(isProcessing() ? 0 : 6);
//    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        direction = MiscTools.getSideFacingTrack(worldObj, getPos());
        if (direction == null)
            direction = MiscTools.getSideFacingPlayer(getPos(), placer);
    }

    @Override
    public boolean isManualMode() {
        return false;
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return !isSendCartGateAction() && cart instanceof EntityCartRF;
    }

    @Override
    public boolean isProcessing() {
        return ticksSinceTransfer > 0;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld()))
            return;

        int capacity = getMaxRF();
        if (amountRF > capacity)
            amountRF = capacity;

        boolean wasProcessing = isProcessing();
        if (processCart())
            ticksSinceTransfer = TRANSFER_FADE;
        else if (ticksSinceTransfer > 0)
            ticksSinceTransfer--;

        if (isProcessing() != wasProcessing)
            sendUpdateToClient();
    }

    protected abstract boolean processCart();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("rf", amountRF);
        data.setByte("direction", (byte) direction.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        amountRF = data.getInteger("rf");
        direction = EnumFacing.getFront(data.getByte("direction"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
        data.writeBoolean(isProcessing());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        direction = EnumFacing.getFront(data.readByte());

        boolean transfer = data.readBoolean();
        if (isProcessing() != transfer) {
            ticksSinceTransfer = transfer ? TRANSFER_FADE : 0;
            markBlockForUpdate();
        }
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    public int addRF(int rf, boolean simulate) {
        if (rf <= 0)
            return 0;
        if (amountRF >= RF_CAP)
            return 0;
        if (RF_CAP - amountRF >= rf) {
            if (!simulate)
                amountRF += rf;
            return rf;
        }
        int used = RF_CAP - amountRF;
        if (!simulate)
            amountRF = RF_CAP;
        return used;
    }

    public int removeRF(int request, boolean simulate) {
        if (request <= 0)
            return 0;
        if (amountRF >= request) {
            if (!simulate)
                amountRF -= request;
            return request;
        }
        int ret = amountRF;
        if (!simulate)
            amountRF = 0;
        return ret;
    }

    public int getRF() {
        return amountRF;
    }

    public void setRF(int energy) {
        this.amountRF = energy;
    }

    public int getMaxRF() {
        return RF_CAP;
    }

}
