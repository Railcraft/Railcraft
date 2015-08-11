/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import mods.railcraft.common.blocks.machine.alpha.TileSteamOven;
import mods.railcraft.common.gui.slots.SlotSmelting;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.fluids.TankManager;

public class ContainerSteamOven extends RailcraftContainer {

    private TileSteamOven tile;
    private int lastCookTime;

    public ContainerSteamOven(InventoryPlayer invPlayer, TileSteamOven tile) {
        super(tile);
        this.tile = tile;

        TankManager tMan = tile.getTankManager();
        if (tMan != null) {
            addWidget(new FluidGaugeWidget(tMan.get(0), 94, 20, 176, 0, 16, 47));
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotSmelting(tile, i * 3 + k, 8 + k * 18, 17 + i * 18));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotFurnace(invPlayer.player, tile, 9 + i * 3 + k, 116 + k * 18, 17 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(invPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(invPlayer, j, 8 + j * 18, 142));
        }

    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        TankManager tMan = tile.getTankManager();
        if (tMan != null) {
            tMan.updateGuiData(this, crafters, 0);
        }

        int cookTime = tile.cookTime;

        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);


            if (lastCookTime != cookTime) {
                icrafting.sendProgressBarUpdate(this, 10, cookTime);
            }
        }

        lastCookTime = cookTime;
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        TankManager tMan = tile.getTankManager();
        if (tMan != null) {
            tMan.initGuiData(this, icrafting, 0);
        }
        icrafting.sendProgressBarUpdate(this, 10, tile.cookTime);
    }

    @Override
    public void updateProgressBar(int id, int value) {
        TankManager tMan = tile.getTankManager();
        if (tMan != null) {
            tMan.processGuiUpdate(id, value);
        }
        switch (id) {
            case 10:
                tile.cookTime = value;
                break;
        }
    }
}
