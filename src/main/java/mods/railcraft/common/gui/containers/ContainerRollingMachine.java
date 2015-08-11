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
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.alpha.TileRollingMachine;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotUnshiftable;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.gui.slots.SlotUntouchable;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;

public class ContainerRollingMachine extends RailcraftContainer {

    private final TileRollingMachine tile;
    private final InventoryCrafting craftMatrix;
    private final IInventory craftResult;
    private int lastProgress;
    private ItemStack prevOutput;
    private final RFEnergyIndicator energyIndicator;

    public ContainerRollingMachine(final InventoryPlayer inventoryplayer, final TileRollingMachine tile) {
        super(tile);
        this.tile = tile;
        craftMatrix = tile.getCraftMatrix();
        craftResult = new InventoryCraftResult() {
            @Override
            public void setInventorySlotContents(int slot, ItemStack stack) {
                super.setInventorySlotContents(slot, stack);
                if (stack != null && Game.isNotHost(tile.getWorldObj()))
                    InvTools.addItemToolTip(stack, LocalizationPlugin.translate("railcraft.gui.rolling.machine.tip.craft"));
            }

        };

        energyIndicator = new RFEnergyIndicator(tile);
        addWidget(new IndicatorWidget(energyIndicator, 157, 19, 176, 12, 6, 48));

        addSlot(new SlotRollingMachine(craftResult, 0, 93, 27));
        addSlot(new SlotOutput(tile, 0, 124, 35));

        for (int l = 0; l < 3; l++) {
            for (int k1 = 0; k1 < 3; k1++) {
                addSlot(new SlotUnshiftable(craftMatrix, k1 + l * 3, 30 + k1 * 18, 17 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 3; i1++) {
            for (int l1 = 0; l1 < 9; l1++) {
                addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
            }
        }

        for (int j1 = 0; j1 < 9; j1++) {
            addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 142));
        }

        onCraftMatrixChanged(craftMatrix);
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, tile.getProgress());
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
            if (lastProgress != tile.getProgress())
                icrafting.sendProgressBarUpdate(this, 0, tile.getProgress());
            if (storage != null)
                icrafting.sendProgressBarUpdate(this, 2, storage.getEnergyStored());
        }

        ItemStack output = tile.getStackInSlot(0);
        if (!InvTools.isItemEqualStrict(output, prevOutput)) {
            onCraftMatrixChanged(craftMatrix);
            prevOutput = output != null ? output.copy() : null;
        }

        lastProgress = tile.getProgress();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tile.setProgress(data);
                break;
            case 1:
                energyIndicator.setEnergy(data);
                break;
            case 2:
                energyIndicator.updateEnergy(data);
                break;
        }
    }

    @Override
    public final void onCraftMatrixChanged(IInventory inv) {
        ItemStack output = RollingMachineCraftingManager.getInstance().findMatchingRecipe(craftMatrix, tile.getWorldObj());
        craftResult.setInventorySlotContents(0, output);
    }

    @Override
    public ItemStack slotClick(int i, int j, int modifier, EntityPlayer entityplayer) {
        ItemStack stack = super.slotClick(i, j, modifier, entityplayer);
        onCraftMatrixChanged(craftMatrix);
        return stack;
    }

    private class SlotRollingMachine extends SlotUntouchable {

        public SlotRollingMachine(IInventory contents, int id, int x, int y) {
            super(contents, id, x, y);
        }

        @Override
        public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {
            tile.useLast = true;
        }

    }
}
