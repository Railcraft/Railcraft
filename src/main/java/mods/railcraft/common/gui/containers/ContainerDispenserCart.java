/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.items.IMinecartItem;
import mods.railcraft.common.blocks.machine.manipulator.TileDispenserCart;
import mods.railcraft.common.gui.slots.SlotMinecart;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerDispenserCart extends RailcraftContainer {

    public TileDispenserCart tile;

    public ContainerDispenserCart(InventoryPlayer inventoryplayer, TileDispenserCart tile) {
        super(tile);
        this.tile = tile;
        addSlot(new SlotDispenserCart(tile, 0, 62, 24));
        addSlot(new SlotDispenserCart(tile, 1, 80, 24));
        addSlot(new SlotDispenserCart(tile, 2, 98, 24));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }

    private class SlotDispenserCart extends SlotMinecart {

        public SlotDispenserCart(IInventory iinventory, int slotIndex, int posX, int posY) {
            super(iinventory, slotIndex, posX, posY);
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            if (InvTools.isEmpty(stack))
                return false;
            if (stack.getItem() instanceof IMinecartItem)
                return ((IMinecartItem) stack.getItem()).canBePlacedByNonPlayer(stack);
            return super.isItemValid(stack);
        }

    }
}
