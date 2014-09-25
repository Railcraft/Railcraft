/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.ItemTicketGold;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RoutingTicketCopyRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        int numPaper = 0;
        int numTickets = 0;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (stack != null) {
                if (stack.getItem() == ItemTicketGold.item) {
                    numTickets++;
                } else if (stack.getItem() == Items.paper || stack.getItem() == ItemTicket.item) {
                    numPaper++;
                } else {
                    return false;
                }
            }
        }
        return numTickets == 1 && numPaper == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack ticket = null;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (stack != null) {
                if (stack.getItem() == ItemTicketGold.item) {
                    ticket = stack;
                    break;
                }
            }
        }
        if (ticket != null) {
            return ItemTicket.copyTicket(ticket);
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemTicket.getTicket();
    }
}
