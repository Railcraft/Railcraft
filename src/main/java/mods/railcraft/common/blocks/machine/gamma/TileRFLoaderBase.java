/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class TileRFLoaderBase extends TileLoaderBase {
    protected static final int TRANSFER_RATE = 8000;
    protected static final int TRANSFER_FADE = 20;
    private static final int RF_CAP = 4000000;
    protected int amountRF;
    protected ForgeDirection direction = ForgeDirection.NORTH;
    private int ticksSinceTransfer;

    public TileRFLoaderBase() {
        setInventorySize(0);
    }

    @Override
    public IIcon getIcon(int side) {
        if (side == direction.ordinal())
            return getMachineType().getTexture(isProcessing() ? 7 : 8);
        return getMachineType().getTexture(isProcessing() ? 0 : 6);
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        direction = MiscTools.getSideFacingTrack(worldObj, xCoord, yCoord, zCoord);
        if (direction == ForgeDirection.UNKNOWN)
            direction = MiscTools.getSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
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
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
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
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("rf", amountRF);
        data.setByte("direction", (byte) direction.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        amountRF = data.getInteger("rf");
        direction = ForgeDirection.getOrientation(data.getByte("direction"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
        data.writeBoolean(isProcessing());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        direction = ForgeDirection.getOrientation(data.readByte());

        boolean transfer = data.readBoolean();
        if (isProcessing() != transfer) {
            ticksSinceTransfer = transfer ? TRANSFER_FADE : 0;
            markBlockForUpdate();
        }
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
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
