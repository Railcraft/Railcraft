/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import cofh.redstoneflux.api.IEnergyReceiver;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class TileRFLoader extends TileRFManipulator implements IEnergyReceiver {

    @Override
    public IEnumMachine<?> getMachineType() {
        return ManipulatorVariant.RF_LOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.MANIPULATOR_RF, player, world, getX(), getY(), getZ());
        return true;
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        EntityCartRF rfCart = (EntityCartRF) cart;

        if (energyStorage.getEnergyStored() > 0 && rfCart.getRF() < rfCart.getMaxRF()) {
            int injection = TRANSFER_RATE;
            if (injection > energyStorage.getEnergyStored()) {
                injection = energyStorage.getEnergyStored();
            }
            int used = rfCart.addRF(injection);
            if (used > 0) {
                energyStorage.modifyEnergyStored(-used);
                setProcessing(true);
            }
        }

//        if (!isProcessing())
//            setResetTimer(TRANSFER_FADE);
    }

    @Override
    protected void sendCart(@Nullable EntityMinecart cart) {
        super.sendCart(cart);
        if (resetTimer == 0)
            setResetTimer(TRANSFER_FADE);
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        if (!(cart instanceof EntityCartRF))
            return false;
        EntityCartRF rfCart = (EntityCartRF) cart;
        switch (redstoneController().getButtonState()) {
            case COMPLETE:
                return rfCart.getRF() < rfCart.getMaxRF();
            case PARTIAL:
                return rfCart.getRF() <= 0;
        }
        return false;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }
}
