/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import net.minecraft.inventory.ICrafting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.items.RailcraftPartItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerTurbine extends RailcraftContainer {

    public TileSteamTurbine tile;
    private Slot rotor;
    private int lastOutput;

    public ContainerTurbine(InventoryPlayer inventoryplayer, TileSteamTurbine tile) {
        super(tile.getInventory());
        this.tile = tile;
        addSlot(rotor = new SlotTurbine(tile.getInventory(), 0, 60, 24));
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (lastOutput != Math.round(tile.output))
                icrafting.sendProgressBarUpdate(this, 0, Math.round(tile.output));
        }
        lastOutput = Math.round(tile.output);
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, Math.round(tile.output));
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tile.output = data;
                break;
        }
    }

    private class SlotTurbine extends SlotRailcraft {

        public SlotTurbine(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
            setStackLimit(1);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && stack.getItem() == RailcraftPartItems.itemTurbineRotor;
        }

    }
}
