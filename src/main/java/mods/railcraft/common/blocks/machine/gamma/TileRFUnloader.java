/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import cofh.api.energy.IEnergyProvider;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.rf.RedstoneFluxPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileRFUnloader extends TileRFLoaderBase implements IEnergyProvider, IGuiReturnHandler {
    private boolean waitTillEmpty = true;
    private static final int AMOUNT_TO_PUSH_TO_TILES = 2000;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.RF_UNLOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.UNLOADER_RF, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(worldObj))
            return;

        RedstoneFluxPlugin.pushToTiles(this, tileCache, AMOUNT_TO_PUSH_TO_TILES);
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

        if (amountRF < getMaxRF() && rfCart.getRF() > 0) {
            int request = TRANSFER_RATE;

            int room = getMaxRF() - getRF();
            if (room < request) {
                request = room;
            }

            double extracted = rfCart.removeRF(request);
            amountRF += extracted;
            transferred = extracted > 0;
        }

        if (!transferred && !isPowered() && shouldSendCart(cart))
            sendCart(cart);
        return transferred;
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof EntityCartRF))
            return true;
        EntityCartRF rfCart = (EntityCartRF) cart;
        if (!waitTillEmpty)
            return true;
        else if (rfCart.getRF() <= 0)
            return true;
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("WaitTillEmpty", waitTillEmpty());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        setWaitTillEmpty(nbttagcompound.getBoolean("WaitTillEmpty"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(waitTillEmpty);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        waitTillEmpty = data.readBoolean();
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeBoolean(waitTillEmpty);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        waitTillEmpty = data.readBoolean();
    }

    public boolean waitTillEmpty() {
        return waitTillEmpty;
    }

    public void setWaitTillEmpty(boolean wait) {
        waitTillEmpty = wait;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return removeRF(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getRF();
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
