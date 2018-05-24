/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerEnergyLoader extends RailcraftContainer {

    private final TileIC2Manipulator device;
    private int lastEnergy, lastTransferRate;
    private short lastStorage, lastLapotron;

    public ContainerEnergyLoader(InventoryPlayer inventoryplayer, TileIC2Manipulator device) {
        super(device);
        this.device = device;
        addSlot(new SlotEnergy(device, 0, 8, 17));
        addSlot(new SlotEnergy(device, 1, 8, 53));

        addSlot(new SlotUpgrade(device, 2, 152, 8));
        addSlot(new SlotUpgrade(device, 3, 152, 26));
        addSlot(new SlotUpgrade(device, 4, 152, 44));
        addSlot(new SlotUpgrade(device, 5, 152, 62));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener player) {
        super.addListener(player);
        PacketBuilder.instance().sendGuiIntegerPacket(player, windowId, 0, (int) device.getEnergy());
        player.sendWindowProperty(this, 1, device.storageUpgrades);
        player.sendWindowProperty(this, 2, device.lapotronUpgrades);
        player.sendWindowProperty(this, 3, device.transferRate);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastEnergy != device.getEnergy())
                PacketBuilder.instance().sendGuiIntegerPacket(listener, windowId, 0, (int) device.getEnergy());

            if (lastStorage != device.storageUpgrades)
                listener.sendWindowProperty(this, 1, device.storageUpgrades);

            if (lastLapotron != device.lapotronUpgrades)
                listener.sendWindowProperty(this, 2, device.lapotronUpgrades);

            if (lastTransferRate != device.transferRate)
                listener.sendWindowProperty(this, 3, device.transferRate);
        }

        lastEnergy = (int) device.getEnergy();
        lastStorage = device.storageUpgrades;
        lastLapotron = device.lapotronUpgrades;
        lastTransferRate = device.transferRate;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            device.setEnergy(data);
        if (id == 1)
            device.storageUpgrades = (short) data;
        if (id == 2)
            device.lapotronUpgrades = (short) data;
        if (id == 3)
            device.transferRate = data;
    }

}
