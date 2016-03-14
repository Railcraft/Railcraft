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
import mods.railcraft.common.blocks.machine.gamma.TileFluidUnloader;
import mods.railcraft.common.gui.slots.SlotFluidContainerEmpty;
import mods.railcraft.common.gui.slots.SlotFluidFilter;
import mods.railcraft.common.gui.slots.SlotMinecartFilter;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerFluidUnloader extends RailcraftContainer {

    private final TileFluidUnloader tile;

    public ContainerFluidUnloader(InventoryPlayer inventoryplayer, TileFluidUnloader tile) {
        super(tile);
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 17, 23, 176, 0, 16, 47));

        addSlot(new SlotMinecartFilter(tile.getCartFilters(), 0, 44, 39));
        addSlot(new SlotMinecartFilter(tile.getCartFilters(), 1, 62, 39));
        addSlot(new SlotFluidFilter(tile.getFluidFilter(), 0, 89, 39));
        addSlot(new SlotFluidContainerEmpty(tile, 0, 134, 21));
        addSlot(new SlotOutput(tile, 1, 134, 56));

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
        tile.getTankManager().initGuiData(this, icrafting, 0);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        tile.getTankManager().updateGuiData(this, crafters, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        tile.getTankManager().processGuiUpdate(id, data);
    }

}
