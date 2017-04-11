/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import cofh.api.energy.EnergyStorage;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

public class ContainerRollingMachinePowered extends ContainerRollingMachine {

    private final TileRollingMachinePowered tile;
    private final RFEnergyIndicator energyIndicator;

    public ContainerRollingMachinePowered(final InventoryPlayer inventoryplayer, final TileRollingMachinePowered tile) {
        super(inventoryplayer, tile);
        this.tile = tile;

        energyIndicator = new RFEnergyIndicator(tile);
        addWidget(new IndicatorWidget(energyIndicator, 157, 19, 176, 12, 6, 48));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendProgressBarUpdate(this, 0, tile.getProgress());
        EnergyStorage storage = tile.getEnergyStorage();
        if (storage != null)
            listener.sendProgressBarUpdate(this, 1, storage.getEnergyStored());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        EnergyStorage storage = tile.getEnergyStorage();
        for (Object crafter : listeners) {
            IContainerListener listener = (IContainerListener) crafter;
            if (storage != null)
                listener.sendProgressBarUpdate(this, 2, storage.getEnergyStored());
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        switch (id) {
            case 1:
                energyIndicator.setEnergy(data);
                break;
            case 2:
                energyIndicator.updateEnergy(data);
                break;
        }
    }
}
