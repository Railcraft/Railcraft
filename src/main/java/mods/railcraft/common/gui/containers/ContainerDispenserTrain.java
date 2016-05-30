/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.blocks.machine.gamma.TileDispenserTrain;
import mods.railcraft.common.gui.slots.SlotMinecart;
import mods.railcraft.common.gui.slots.SlotMinecartFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerDispenserTrain extends RailcraftContainer {

    public ContainerDispenserTrain(InventoryPlayer playerInv, TileDispenserTrain tile) {
        super(tile);

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotDispenserTrain(tile.getPattern(), i, 8 + i * 18, 31));
        }

        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new SlotMinecart(tile, k + i * 9, 8 + k * 18, 67 + i * 18));
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

    private class SlotDispenserTrain extends SlotMinecartFilter {

        public SlotDispenserTrain(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
            setStackLimit(1);
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            if (stack == null)
                return false;
            if (stack.getItem() instanceof IMinecartItem)
                return ((IMinecartItem) stack.getItem()).canBePlacedByNonPlayer(stack);
            return super.isItemValid(stack);
        }

    }
}
