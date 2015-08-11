/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderEnergyBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.gui.slots.SlotEnergy;
import mods.railcraft.common.gui.slots.SlotUpgrade;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;

public class ContainerEnergyLoader extends RailcraftContainer {

    private TileLoaderEnergyBase device;
    private int lastEnergy, lastTransferRate;
    private short lastStorage, lastLapo;

    public ContainerEnergyLoader(InventoryPlayer inventoryplayer, TileLoaderEnergyBase device) {
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
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);
        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 0, (int) device.getEnergy());
        player.sendProgressBarUpdate(this, 1, device.storageUpgrades);
        player.sendProgressBarUpdate(this, 2, device.lapotronUpgrades);
        player.sendProgressBarUpdate(this, 3, device.transferRate);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int i = 0; i < crafters.size(); ++i) {
            ICrafting player = (ICrafting) crafters.get(i);

            if (lastEnergy != device.getEnergy())
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 0, (int) device.getEnergy());

            if (lastStorage != device.storageUpgrades)
                player.sendProgressBarUpdate(this, 1, device.storageUpgrades);

            if (lastLapo != device.lapotronUpgrades)
                player.sendProgressBarUpdate(this, 2, device.lapotronUpgrades);

            if (lastLapo != device.transferRate)
                player.sendProgressBarUpdate(this, 3, device.transferRate);
        }

        lastEnergy = (int) device.getEnergy();
        lastStorage = device.storageUpgrades;
        lastLapo = device.lapotronUpgrades;
        lastTransferRate = device.transferRate;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            this.device.setEnergy(data);
        if (id == 1)
            this.device.storageUpgrades = (short) data;
        if (id == 2)
            this.device.lapotronUpgrades = (short) data;
        if (id == 3)
            this.device.transferRate = data;
    }

}
