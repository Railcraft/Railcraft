/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.gui.slots.SlotRailcraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by CovertJaguar on 1/15/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ContainerRCChest extends RailcraftContainer {
    /**
     * On the server, this may be a {@link net.minecraft.tileentity.TileEntityChest} (corresponding to a single chest
     * block) or an {@link net.minecraft.inventory.InventoryLargeChest} (corresponding to a large chest); chests larger
     * than 2 chest blocks are represented by several nested InventoryLargeChests. See {@link
     * net.minecraft.block.BlockChest#getContainer)} for more information. On the client, this is an InventoryBasic.
     */
    private final IInventory inv;

    public ContainerRCChest(InventoryPlayer invPlayer, IInventory chest) {
        super(chest);
        this.inv = chest;
        int numRows = chest.getSizeInventory() / 9;
        chest.openInventory(invPlayer.player);
        int i = (numRows - 4) * 18;

        for (int j = 0; j < numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                addSlotToContainer(new SlotRailcraft(chest, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                addSlotToContainer(new Slot(invPlayer, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            addSlotToContainer(new Slot(invPlayer, i1, 8 + i1 * 18, 161 + i));
        }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        inv.closeInventory(playerIn);
    }

    public IInventory getInv() {
        return inv;
    }
}
