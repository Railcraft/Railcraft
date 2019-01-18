/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartWork;
import mods.railcraft.common.gui.slots.SlotUnshiftable;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public final class ContainerCartWork extends RailcraftContainer {

    /**
     * The crafting matrix inventory (3x3).
     */
    public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public final IInventory craftResult = new InventoryCraftResult();
    private final EntityCartWork cart;

    public ContainerCartWork(InventoryPlayer inv, EntityCartWork cart) {
        this.cart = cart;
        addSlot(new SlotCrafting(inv.player, craftMatrix, craftResult, 0, 124, 35));
        int var6;
        int var7;

        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 3; ++var7) {
                addSlot(new SlotUnshiftable(craftMatrix, var7 + var6 * 3, 30 + var7 * 18, 17 + var6 * 18));
            }
        }

        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 9; ++var7) {
                addSlot(new Slot(inv, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
            }
        }

        for (var6 = 0; var6 < 9; ++var6) {
            addSlot(new Slot(inv, var6, 8 + var6 * 18, 142));
        }

        onCraftMatrixChanged(craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        craftResult.setInventorySlotContents(0, CraftingManager.findMatchingResult(craftMatrix, cart.world));
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!cart.world.isRemote) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = craftMatrix.removeStackFromSlot(i);

                if (!InvTools.isEmpty(itemstack)) {
                    player.dropItem(itemstack, false);
                }
            }
        }
    }

}
