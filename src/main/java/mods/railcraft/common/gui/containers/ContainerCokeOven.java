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
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.alpha.TileCokeOven;
import mods.railcraft.common.gui.slots.SlotFluidContainerEmpty;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;


public class ContainerCokeOven extends RailcraftContainer {

    private TileCokeOven tile;
    private int lastCookTime, lastCookTimeTotal;

    public ContainerCokeOven(InventoryPlayer inventoryplayer, TileCokeOven tile) {
        super(tile);
        lastCookTime = 0;
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 90, 24, 176, 0, 48, 47));

        addSlot(new SlotCokeOven(tile, 0, 16, 43));
        addSlot(new SlotOutput(tile, 1, 62, 43));
        addSlot(new SlotOutput(tile, 2, 149, 57));
        addSlot(new SlotFluidContainerEmpty(tile, 3, 149, 22));
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
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        TankManager tMan = tile.getTankManager();
        if (tMan != null)
            tMan.updateGuiData(this, crafters, 0);

        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            int cookTime = tile.getCookTime();
            if (lastCookTime != cookTime)
                icrafting.sendProgressBarUpdate(this, 10, cookTime);

            int cookTimeTotal = tile.getTotalCookTime();
            if (lastCookTimeTotal != cookTimeTotal)
                icrafting.sendProgressBarUpdate(this, 11, cookTimeTotal);
        }

        lastCookTime = tile.getCookTime();
        lastCookTimeTotal = tile.getTotalCookTime();
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);

        TankManager tMan = tile.getTankManager();
        if (tMan != null)
            tMan.initGuiData(this, icrafting, 0);

        icrafting.sendProgressBarUpdate(this, 10, tile.getCookTime());
        icrafting.sendProgressBarUpdate(this, 11, tile.getTotalCookTime());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        TankManager tMan = tile.getTankManager();
        if (tMan != null)
            tMan.processGuiUpdate(id, data);

        switch (id) {
            case 10:
                tile.setCookTime(data);
                break;
            case 11:
                tile.cookTimeTotal = data;
        }
    }

    private class SlotCokeOven extends SlotRailcraft {

        public SlotCokeOven(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack != null && !InvTools.isSynthetic(stack) && RailcraftCraftingManager.cokeOven.getRecipe(stack) != null)
                return true;
            return false;
        }

    }
}
