/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.manipulator.TileDispenserTrain;
import mods.railcraft.common.gui.slots.SlotDispensableCart;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerDispenserTrain extends RailcraftContainer {

    public ContainerDispenserTrain(InventoryPlayer playerInv, TileDispenserTrain tile) {
        super(tile);

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotDispensableCart(tile.getPattern(), i, 8 + i * 18, 31).setPhantom().setStackLimit(1));
        }

        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new SlotStackFilter(StandardStackFilters.MINECART,
                        tile, k + i * 9, 8 + k * 18, 67 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInv, k + i * 9 + 9, 8 + k * 18, 111 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(playerInv, j, 8 + j * 18, 169));
        }
    }
}
