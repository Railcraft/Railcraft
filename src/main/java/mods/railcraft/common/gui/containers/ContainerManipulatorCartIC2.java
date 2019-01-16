/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.manipulator.TileIC2Manipulator;
import mods.railcraft.common.gui.slots.SlotEnergy;
import mods.railcraft.common.gui.slots.SlotUpgrade;
import mods.railcraft.common.gui.widgets.ChargeBatteryIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerManipulatorCartIC2 extends RailcraftContainer {

    private final TileIC2Manipulator device;
    private int lastTransferRate;
    private short lastStorage, lastLapotron;

    public ContainerManipulatorCartIC2(InventoryPlayer inventoryplayer, TileIC2Manipulator device) {
        super(device);
        this.device = device;

        addWidget(new IndicatorWidget(new ChargeBatteryIndicator(device.getBattery()), 31, 28, 176, 0, 24, 9, false));

        addSlot(new SlotEnergy(device, 0, 8, 17));
        addSlot(new SlotEnergy(device, 1, 8, 53));

        addSlot(new SlotUpgrade(device, 2, 152, 8));
        addSlot(new SlotUpgrade(device, 3, 152, 26));
        addSlot(new SlotUpgrade(device, 4, 152, 44));
        addSlot(new SlotUpgrade(device, 5, 152, 62));

        addPlayerSlots(inventoryplayer);
    }

    @Override
    public void addListener(IContainerListener player) {
        super.addListener(player);
        player.sendWindowProperty(this, 1, device.storageUpgrades);
        player.sendWindowProperty(this, 2, device.lapotronUpgrades);
        player.sendWindowProperty(this, 3, (int) device.transferRate);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastStorage != device.storageUpgrades)
                listener.sendWindowProperty(this, 1, device.storageUpgrades);

            if (lastLapotron != device.lapotronUpgrades)
                listener.sendWindowProperty(this, 2, device.lapotronUpgrades);

            if (lastTransferRate != device.transferRate)
                listener.sendWindowProperty(this, 3, (int) device.transferRate);
        }

        lastStorage = device.storageUpgrades;
        lastLapotron = device.lapotronUpgrades;
        lastTransferRate = (int) device.transferRate;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 1)
            device.storageUpgrades = (short) data;
        if (id == 2)
            device.lapotronUpgrades = (short) data;
        if (id == 3)
            device.transferRate = data;
    }

}
