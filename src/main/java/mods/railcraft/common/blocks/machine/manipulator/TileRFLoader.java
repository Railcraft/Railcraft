/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import cofh.api.energy.IEnergyReceiver;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.io.IOException;

public class TileRFLoader extends TileRFManipulator implements IGuiReturnHandler, IEnergyReceiver {
    private boolean waitTillFull = true;
    private boolean waitIfEmpty = true;

    @Override
    public IEnumMachine getMachineType() {
        return ManipulatorVariant.RF_LOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOADER_RF, player, worldObj, getX(), getY(), getZ());
        return true;
    }

    @Override
    protected boolean processCart() {
        boolean transferred = false;

        EntityMinecart cart = CartToolsAPI.getMinecartOnSide(worldObj, getPos(), 0.1f, direction);

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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("WaitIfEmpty", waitIfEmpty());
        data.setBoolean("WaitTillFull", waitTillFull());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        setWaitIfEmpty(nbttagcompound.getBoolean("WaitIfEmpty"));
        setWaitTillFull(nbttagcompound.getBoolean("WaitTillFull"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        byte bits = 0;
        bits |= waitIfEmpty ? 1 : 0;
        bits |= waitTillFull ? 2 : 0;
        data.writeByte(bits);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        byte bits = data.readByte();
        waitIfEmpty = (bits & 1) != 0;
        waitTillFull = (bits & 2) != 0;
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeBoolean(waitIfEmpty);
        data.writeBoolean(waitTillFull);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
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
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return addRF(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return amountRF;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaxRF();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }
}
