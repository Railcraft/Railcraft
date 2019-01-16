/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;

public class TileRFLoader extends TileRFManipulator {

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

        if (energyStorage.getEnergyStored() > 0 && rfCart.getEnergyStorage().getEnergyStored() < rfCart.getEnergyStorage().getMaxEnergyStored()) {
            int injection = TRANSFER_RATE;
            if (injection > energyStorage.getEnergyStored()) {
                injection = energyStorage.getEnergyStored();
            }
            int used = rfCart.getEnergyStorage().receiveEnergy(injection, false);
            if (used > 0) {
                energyStorage.extractEnergy(used, false);
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
                return rfCart.getEnergyStorage().getEnergyStored() < rfCart.getEnergyStorage().getMaxEnergyStored();
            case PARTIAL:
                return rfCart.getEnergyStorage().getEnergyStored() <= 0;
        }
        return false;
    }
}
