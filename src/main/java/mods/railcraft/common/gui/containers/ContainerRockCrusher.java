/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cofh.api.energy.EnergyStorage;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.alpha.TileRockCrusher;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerRockCrusher extends RailcraftContainer {

    private final TileRockCrusher tile;
    private int lastProcessTime;
    private final RFEnergyIndicator energyIndicator;

    public ContainerRockCrusher(InventoryPlayer inventoryplayer, TileRockCrusher crusher) {
        super(crusher);
        this.tile = crusher;

        energyIndicator = new RFEnergyIndicator(tile);
        addWidget(new IndicatorWidget(energyIndicator, 157, 23, 176, 53, 6, 48));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotRockCrusher(crusher, i * 3 + k, 8 + k * 18, 21 + i * 18));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotOutput(crusher, 9 + i * 3 + k, 98 + k * 18, 21 + i * 18));
            }
        }
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
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, tile.getProcessTime());
        EnergyStorage storage = tile.getEnergyStorage();
        if (storage != null)
            icrafting.sendProgressBarUpdate(this, 1, storage.getEnergyStored());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        EnergyStorage storage = tile.getEnergyStorage();
        for (Object crafter : crafters) {
            ICrafting icrafting = (ICrafting) crafter;
            if (lastProcessTime != tile.getProcessTime())
                icrafting.sendProgressBarUpdate(this, 0, tile.getProcessTime());
            if (storage != null)
                icrafting.sendProgressBarUpdate(this, 2, storage.getEnergyStored());
        }

        lastProcessTime = tile.getProcessTime();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tile.setProcessTime(data);
                break;
            case 1:
                energyIndicator.setEnergy(data);
                break;
            case 2:
                energyIndicator.updateEnergy(data);
                break;
        }
    }

    public class SlotRockCrusher extends SlotRailcraft {

        public SlotRockCrusher(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack != null && RailcraftCraftingManager.rockCrusher.getRecipe(stack) != null)
                return true;
            return false;
        }

    }
}
