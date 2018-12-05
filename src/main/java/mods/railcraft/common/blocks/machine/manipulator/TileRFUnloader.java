/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
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
import mods.railcraft.common.plugins.forge.EnergyPlugin;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;

public class TileRFUnloader extends TileRFManipulator {
    private static final int AMOUNT_TO_PUSH_TO_TILES = 2000;

    @Override
    public IEnumMachine<?> getMachineType() {
        return ManipulatorVariant.RF_UNLOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.MANIPULATOR_RF, player, world, getX(), getY(), getZ());
        return true;
    }

    @Override
    public void upkeep() {
        super.upkeep();
        EnergyPlugin.pushToTiles(this, AMOUNT_TO_PUSH_TO_TILES);
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        EntityCartRF rfCart = (EntityCartRF) cart;

        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() && rfCart.getRF() > 0) {
            int request = TRANSFER_RATE;

            int room = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
            if (room < request) {
                request = room;
            }

            int extracted = rfCart.removeRF(request);
            energyStorage.receiveEnergy(extracted,false);
            setProcessing(extracted > 0);
        }
    }

    @Override
    protected boolean hasWorkForCart(EntityMinecart cart) {
        if (!(cart instanceof EntityCartRF))
            return false;
        EntityCartRF rfCart = (EntityCartRF) cart;
        switch (redstoneController().getButtonState()) {
            case COMPLETE:
                return rfCart.getRF() > 0;
            case PARTIAL:
                return rfCart.getRF() >= rfCart.getMaxRF();
        }
        return false;
    }
}
