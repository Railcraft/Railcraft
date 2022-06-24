/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

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

    public ContainerRCChest(InventoryPlayer invPlayer, IInventoryImplementor chest) {
        super(chest);
        this.inv = chest.getInventory();
        int numRows = inv.getSizeInventory() / 9;
        chest.openInventory(invPlayer.player);
        int i = (numRows - 4) * 18;

        for (int j = 0; j < numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                addSlot(new SlotRailcraft(inv, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        addPlayerSlots(invPlayer, 185 + i);
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
