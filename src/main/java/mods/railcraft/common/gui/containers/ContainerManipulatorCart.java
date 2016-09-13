/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.manipulator.TileManipulatorCart;
import mods.railcraft.common.gui.slots.SlotMinecartFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

/**
 * Created by CovertJaguar on 9/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ContainerManipulatorCart extends RailcraftContainer {
    public TileManipulatorCart tile;

    public ContainerManipulatorCart(InventoryPlayer inventoryplayer, TileManipulatorCart tile) {
        super(tile);
        this.tile = tile;

        addSlot(new SlotMinecartFilter(this.tile.getCartFilters(), 0, 71, 26));
        addSlot(new SlotMinecartFilter(this.tile.getCartFilters(), 1, 89, 26));

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
