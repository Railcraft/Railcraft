/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RoutingTableCopyRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        ItemStack source = grid.getStackInSlot(0);
        if (source == null || RailcraftItems.ROUTING_TABLE.isEqual(source) || source.stackSize > 1) {
            return false;
        }
        int numCopies = 0;
        for (int slot = 1; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (stack != null) {
                if (RailcraftItems.ROUTING_TABLE.isEqual(stack)) {
                    numCopies++;
                } else {
                    return false;
                }
            }
        }
        return numCopies > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack source = grid.getStackInSlot(0);
        if (source != null && RailcraftItems.ROUTING_TABLE.isEqual(source) && source.stackSize == 1) {
            int copies = 0;
            for (int slot = 1; slot < grid.getSizeInventory(); slot++) {
                ItemStack stack = grid.getStackInSlot(slot);
                if (stack != null && RailcraftItems.ROUTING_TABLE.isEqual(stack)) {
                    copies++;
                }
            }
            if (copies > 0) {
                ItemStack dest = source.copy();
                dest.stackSize = copies + 1;
                return dest;
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return CraftingPlugin.emptyContainers(inv);
    }
}
