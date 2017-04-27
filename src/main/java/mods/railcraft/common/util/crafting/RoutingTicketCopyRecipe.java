/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.stream.IntStream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RoutingTicketCopyRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        int numPaper = 0;
        int numTickets = 0;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (!InvTools.isEmpty(stack)) {
                if (RailcraftItems.TICKET_GOLD.isEqual(stack)) {
                    numTickets++;
                } else if (stack.getItem() == Items.PAPER || RailcraftItems.TICKET.isEqual(stack)) {
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
        ItemStack ticket = IntStream.range(0, grid.getSizeInventory())
                .mapToObj(grid::getStackInSlot)
                .filter(RailcraftItems.TICKET_GOLD::isEqual)
                .findFirst().orElse(InvTools.emptyStack());
        if (!InvTools.isEmpty(ticket)) {
            return ItemTicket.copyTicket(ticket);
        }
        return InvTools.emptyStack();
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return RailcraftItems.TICKET.getStack();
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return CraftingPlugin.emptyContainers(inv);
    }
}
