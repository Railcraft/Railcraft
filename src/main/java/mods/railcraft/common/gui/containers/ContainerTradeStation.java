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
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.alpha.TileTradeStation;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;

public class ContainerTradeStation extends RailcraftContainer {

    public TileTradeStation tile;

    public ContainerTradeStation(InventoryPlayer inventoryplayer, TileTradeStation tile) {
        super(tile);
        this.tile = tile;

        for (int y = 0; y < 3; y++) {
            addSlot(new SlotRailcraft(tile.getRecipeSlots(), 0 + 3 * y, 8, 24 + 21 * y).setPhantom());
            addSlot(new SlotRailcraft(tile.getRecipeSlots(), 1 + 3 * y, 26, 24 + 21 * y).setPhantom());
            addSlot(new SlotRailcraft(tile.getRecipeSlots(), 2 + 3 * y, 71, 24 + 21 * y).setPhantom());
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 5; x++) {
                addSlot(new Slot(tile, x + y * 5, 8 + x * 18, 87 + y * 18));
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotOutput(tile, x + y * 3 + 10, 116 + x * 18, 87 + y * 18));
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(inventoryplayer, x + y * 9 + 9, 8 + x * 18, 132 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(inventoryplayer, x, 8 + x * 18, 190));
        }
    }

}
