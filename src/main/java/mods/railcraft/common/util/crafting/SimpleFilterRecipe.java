/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.items.ItemFilterSimple;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SimpleFilterRecipe implements IRecipe {
    private static Predicate<ItemStack> FILTER = StackFilters.of(ItemFilterSimple.class);
    private static Predicate<ItemStack> PROTOTYPE = s -> !OreDictPlugin.getOreTags(s).isEmpty();

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        InventoryComposite inv = InventoryComposite.of(grid);
        int filterStacks = InvTools.countStacks(inv, FILTER);
        if (filterStacks != 1)
            return false;
        ItemStack filter = InvTools.findMatchingItem(inv, FILTER);
        if (InvTools.isEmpty(filter))
            return false;
        int prototype = InvTools.countStacks(inv, validPrototype(filter));
        return prototype == 1;
    }

    private Predicate<ItemStack> validPrototype(ItemStack filter) {
        return s -> !FILTER.test(s) && ((ItemFilterSimple) filter.getItem()).isValidPrototype(s);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        if (!matches(grid, null))
            return InvTools.emptyStack();
        InventoryComposite inv = InventoryComposite.of(grid);
        ItemStack filter = InvTools.findMatchingItem(inv, FILTER);
        if (InvTools.isEmpty(filter))
            return InvTools.emptyStack();
        ItemStack prototype = InvTools.findMatchingItem(inv, validPrototype(filter));
        if (!InvTools.isEmpty(prototype))
            return ItemFilterSimple.setPrototype(filter, prototype);
        return InvTools.emptyStack();
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return RailcraftItems.FILTER_ORE_DICT.getStack();
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] grid = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < grid.length; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!InvTools.isEmpty(stack)) {
                if (!FILTER.test(stack)) {
                    stack = stack.copy();
                    stack.stackSize = 1;
                    grid[i] = stack;
                }
            }
        }

        return grid;
    }
}
