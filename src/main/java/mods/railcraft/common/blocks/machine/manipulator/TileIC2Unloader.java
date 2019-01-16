/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.ic2.IEmitterDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2EmitterDelegate;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class TileIC2Unloader extends TileIC2Manipulator implements IEmitterDelegate {

    private static final int[] OUTPUT_LEVELS = {512, 2048};
    private boolean waitTillEmpty = true;
    private TileEntity emitterDelegate;

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.ENERGY_UNLOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.UNLOADER_ENERGY, player, world, getPos());
        return true;
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;

        if (battery.needsCharging() && energyCart.getEnergy() > 0) {
            double usage = (energyCart.getTransferLimit() * Math.pow(1.5, overclockerUpgrades));
            double injection = (energyCart.getTransferLimit() * Math.pow(1.3, overclockerUpgrades));

            double room = battery.room();
            if (room < injection) {
                double ratio = room / injection;
                injection = room;
                usage = usage * ratio;
            }

            double extract = energyCart.extractEnergy(this, usage, getTier(), true, false, false);

            if (extract < usage) {
                double ratio = extract / usage;
//                usage = extract;
                injection = injection * ratio;
            }

            transferRate = injection;
            battery.addCharge(injection);
            setProcessing(extract > 0);
        }
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        if (!super.canHandleCart(cart))
            return false;
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;
        return energyCart.canExtractEnergy();
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        if (!(cart instanceof IEnergyTransfer))
            return false;
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;
        if (!waitTillEmpty)
            return false;
        else return !(energyCart.getEnergy() == 0);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("WaitTillEmpty", waitTillEmpty());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setWaitTillEmpty(data.getBoolean("WaitTillEmpty"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(waitTillEmpty);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        waitTillEmpty = data.readBoolean();
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeBoolean(waitTillEmpty);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        waitTillEmpty = data.readBoolean();
    }

    public boolean waitTillEmpty() {
        return waitTillEmpty;
    }

    public void setWaitTillEmpty(boolean wait) {
        waitTillEmpty = wait;
    }

    @Override
    public double getOfferedEnergy() {
        int emit = transformerUpgrades > 0 ? OUTPUT_LEVELS[1] : OUTPUT_LEVELS[0];
        return Math.min(battery.getAvailableCharge(), emit);
    }

    @Override
    public int getSourceTier() {
        return transformerUpgrades > 0 ? 4 : 3;
    }

    @Override
    public void drawEnergy(double amount) {
        battery.removeCharge(amount);
    }

    @Override
    public boolean emitsEnergyTo(Object receiver, EnumFacing direction) {
        return getFacing() != direction;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public TileEntity getIC2Delegate() {
        if (emitterDelegate == null)
            try {
                emitterDelegate = new TileIC2EmitterDelegate(this);
            } catch (Throwable error) {
                Game.log().api("IndustrialCraft", error);
            }
        return emitterDelegate;
    }
}
