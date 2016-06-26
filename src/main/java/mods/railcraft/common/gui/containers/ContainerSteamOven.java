/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.alpha.TileSteamOven;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.gui.slots.SlotSmelting;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

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
                addSlot(new SlotFurnaceOutput(invPlayer.player, tile, 9 + i * 3 + k, 116 + k * 18, 17 + i * 18));
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
        int cookTime = tile.cookTime;

        for (IContainerListener listener : listeners) {
            if (lastCookTime != cookTime) {
                listener.sendProgressBarUpdate(this, 0, cookTime);
            }
        }

        lastCookTime = cookTime;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendProgressBarUpdate(this, 0, tile.cookTime);
    }

    @Override
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                tile.cookTime = value;
                break;
        }
    }
}
