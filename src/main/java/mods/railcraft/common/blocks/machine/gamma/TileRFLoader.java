/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import cofh.api.energy.IEnergyReceiver;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileRFLoader extends TileRFLoaderBase implements IGuiReturnHandler, IEnergyReceiver {
    private boolean waitTillFull = true;
    private boolean waitIfEmpty = true;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.RF_LOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_RF, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    protected boolean processCart() {
        boolean transferred = false;

        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0.1f, direction);

        if (cart != currentCart) {
            setPowered(false);
            currentCart = cart;
            cartWasSent();
        }

        if (cart == null)
            return false;

        if (!canHandleCart(cart)) {
            sendCart(cart);
            return false;
        }

        if (isPaused())
            return false;

        EntityCartRF rfCart = (EntityCartRF) cart;

        if (amountRF > 0 && rfCart.getRF() < rfCart.getMaxRF()) {
            int injection = TRANSFER_RATE;
            if (injection > amountRF) {
                injection = amountRF;
            }
            int used = rfCart.addRF(injection);
            if (used > 0) {
                amountRF -= used;
                transferred = true;
            }
        }

        if (!transferred && !isPowered() && shouldSendCart(cart))
            sendCart(cart);

        return transferred;
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof EntityCartRF))
            return true;
        EntityCartRF energyCart = (EntityCartRF) cart;
        if (!waitTillFull && energyCart.getRF() > 0)
            return true;
        else if (!waitIfEmpty && !waitTillFull && energyCart.getRF() == 0)
            return true;
        else if (energyCart.getRF() >= energyCart.getMaxRF())
            return true;
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("WaitIfEmpty", waitIfEmpty());
        nbttagcompound.setBoolean("WaitTillFull", waitTillFull());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        setWaitIfEmpty(nbttagcompound.getBoolean("WaitIfEmpty"));
        setWaitTillFull(nbttagcompound.getBoolean("WaitTillFull"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        byte bits = 0;
        bits |= waitIfEmpty ? 1 : 0;
        bits |= waitTillFull ? 2 : 0;
        data.writeByte(bits);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        byte bits = data.readByte();
        waitIfEmpty = (bits & 1) != 0;
        waitTillFull = (bits & 2) != 0;
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeBoolean(waitIfEmpty);
        data.writeBoolean(waitTillFull);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        waitIfEmpty = data.readBoolean();
        waitTillFull = data.readBoolean();
    }

    public boolean waitTillFull() {
        return waitTillFull;
    }

    public void setWaitTillFull(boolean waitTillFull) {
        this.waitTillFull = waitTillFull;
    }

    public boolean waitIfEmpty() {
        return waitIfEmpty;
    }

    public void setWaitIfEmpty(boolean waitIfEmpty) {
        this.waitIfEmpty = waitIfEmpty;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return addRF(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return amountRF;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxRF();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }
}
