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
import mods.railcraft.common.carts.IIC2EnergyCart;
import mods.railcraft.common.gui.slots.SlotEnergy;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerCartEnergy extends RailcraftContainer {

    private IIC2EnergyCart cart;
    private int lastEnergy;

    public ContainerCartEnergy(InventoryPlayer inventoryplayer, IIC2EnergyCart device) {
        super(device);
        this.cart = device;
        addSlot(new SlotEnergy(device, 0, 56, 17));
        addSlot(new SlotEnergy(device, 1, 56, 53));
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
        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 0, (int) cart.getEnergy());
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int i = 0; i < crafters.size(); ++i) {
            ICrafting player = (ICrafting) crafters.get(i);

            if (lastEnergy != cart.getEnergy())
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) player, windowId, 0, (int) cart.getEnergy());
        }

        lastEnergy = (int) cart.getEnergy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            cart.setEnergy(data);
    }

}
