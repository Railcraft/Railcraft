/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 3/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartDisassemblyRecipe implements IRecipe {
    private final ItemStack contents;
    private final Item fullCart, emptyCart;

    public CartDisassemblyRecipe(ItemStack contents, Item fullCart, Item emptyCart) {
        this.contents = contents;
        this.fullCart = fullCart;
        this.emptyCart = emptyCart;
    }

    @Override
    public boolean matches(InventoryCrafting grid, World worldIn) {
        int itemCount = 0;
        boolean foundCart = false;
        for (IInvSlot slot : InventoryIterator.getVanilla(grid)) {
            if (slot.hasStack()) {
                if (slot.containsItem(fullCart))
                    foundCart = true;
                itemCount++;
            }
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

        for (IInvSlot slot : InventoryIterator.getVanilla(inv)) {
            if (slot.containsItem(fullCart))
                grid[slot.getIndex()] = new ItemStack(emptyCart);
        }

        return grid;
    }

    public static class RailcraftVariant extends CartDisassemblyRecipe {
        private final RailcraftCarts cart;

        public RailcraftVariant(RailcraftCarts cart) {
            super(cart.getContents(), cart.getItem(), Items.MINECART);
            this.cart = cart;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return cart.getContents();
        }
    }
}
