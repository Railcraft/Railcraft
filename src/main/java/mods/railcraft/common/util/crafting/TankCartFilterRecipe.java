/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.fluids.FluidItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankCartFilterRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        boolean hasTankCart = false;
        boolean hasContainer = false;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (stack == null)
                continue;
            if (EnumCart.getCartType(stack) == EnumCart.TANK)
                hasTankCart = true;
            else if (FluidItemHelper.isContainer(stack))
                hasContainer = true;
            else
                return false;
        }
        return hasContainer && hasTankCart;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack cart = null;
        ItemStack container = null;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (stack == null)
                continue;
            if (EnumCart.getCartType(stack) == EnumCart.TANK)
                cart = stack.copy();
            else if (FluidItemHelper.isContainer(stack))
                container = stack.copy();
            else
                return null;
        }
        if (cart == null || container == null)
            return null;

        container.stackSize = 1;
        return EntityCartTank.getCartItemForFilter(cart, container);
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
