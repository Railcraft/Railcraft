/******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016                                      *
 * http://railcraft.info                                                      *
 * *
 * This code is the property of CovertJaguar                                  *
 * and may only be used with explicit written                                 *
 * permission unless otherwise specified on the                               *
 * license page at http://railcraft.info/wiki/info:license.                   *
 ******************************************************************************/

package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 3/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartUncraftingRecipe implements IRecipe {
    private final ItemStack contents, fullCart, emptyCart;

    public CartUncraftingRecipe(ItemStack contents, ItemStack fullCart, ItemStack emptyCart) {
        this.contents = contents;
        this.fullCart = fullCart;
        this.emptyCart = emptyCart;
    }

    @Override
    public boolean matches(InventoryCrafting grid, World worldIn) {
        int itemCount = 0;
        boolean foundCart = false;
        for (IInvSlot slot : InventoryIterator.getIterable(grid).notNull()) {
            if (InvTools.isItemEqual(slot.getStackInSlot(), fullCart))
                foundCart = true;
            itemCount++;
        }
        return itemCount == 1 && foundCart;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return getRecipeOutput().copy();
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return contents;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] grid = new ItemStack[inv.getSizeInventory()];

        for (IInvSlot slot : InventoryIterator.getIterable(inv).notNull()) {
            ItemStack stack = slot.getStackInSlot();
            if (InvTools.isItemEqual(stack, fullCart))
                grid[slot.getIndex()] = emptyCart.copy();
        }

        return grid;
    }

    public static class EnumCartUncraftingRecipe extends CartUncraftingRecipe {
        private final EnumCart cart;

        public EnumCartUncraftingRecipe(EnumCart cart) {
            super(cart.getContents(), cart.getCartItem(), new ItemStack(Items.minecart));
            this.cart = cart;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return cart.getContents();
        }
    }
}
