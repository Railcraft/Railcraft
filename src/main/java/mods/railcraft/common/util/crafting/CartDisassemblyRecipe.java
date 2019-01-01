/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InventoryIterator;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 3/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartDisassemblyRecipe extends BaseRecipe {
    private final ItemStack contents;
    private final Ingredient fullCart;
    private final Item emptyCart;

    public CartDisassemblyRecipe(String name, ItemStack contents, Item fullCart, Item emptyCart) {
        super(name);
        this.contents = contents;
        this.fullCart = Ingredients.from(fullCart);
        this.emptyCart = emptyCart;
    }

    @Override
    public boolean matches(InventoryCrafting grid, World worldIn) {
        return InventoryIterator.get(grid).streamStacks().count() == 1
                && InventoryIterator.get(grid).streamStacks().anyMatch(fullCart);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return getRecipeOutput().copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredients.from(fullCart));
        return ingredients;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return contents;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> grid = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (IInvSlot slot : InventoryIterator.get(inv)) {
            if (slot.matches(fullCart))
                grid.set(slot.getIndex(), new ItemStack(emptyCart));
        }

        return grid;
    }

    public static class RailcraftVariant extends CartDisassemblyRecipe {
        private final RailcraftCarts cart;

        public RailcraftVariant(RailcraftCarts cart) {
            super(cart.getDef().registryName.getPath() + "_recipe", cart.getContents(), cart.getItem(), Items.MINECART);
            this.cart = cart;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return cart.getContents();
        }
    }
}
