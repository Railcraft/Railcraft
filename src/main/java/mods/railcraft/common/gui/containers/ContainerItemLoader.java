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
import mods.railcraft.common.blocks.machine.gamma.TileLoaderItemBase;
import mods.railcraft.common.gui.slots.SlotFilter;
import mods.railcraft.common.gui.slots.SlotMinecartFilter;

public class ContainerItemLoader extends RailcraftContainer {

    public TileLoaderItemBase tile;

    public ContainerItemLoader(InventoryPlayer inventoryplayer, TileLoaderItemBase tile) {
        super(tile);
        this.tile = tile;

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(new SlotFilter(tile.getItemFilters(), k + i * 3, 8 + k * 18, 26 + i * 18));
            }
        }

        addSlot(new SlotMinecartFilter(tile.getCartFilters(), 0, 71, 26));
        addSlot(new SlotMinecartFilter(tile.getCartFilters(), 1, 89, 26));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                addSlot(tile.getBufferSlot(k + i * 3, 116 + k * 18, 26 + i * 18));
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

}
