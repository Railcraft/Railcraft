/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.CartBaseFiltered;
import mods.railcraft.common.carts.IRailcraftCartContainer;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartFilterRecipe implements IRecipe {

    public enum FilterType {
        Cargo(RailcraftCarts.CARGO),
        Tank(RailcraftCarts.TANK) {
            @Override
            public boolean isAllowedFilterItem(ItemStack stack) {
                return FluidItemHelper.isFluidInContainer(stack);
            }
        };
        public static FilterType[] VALUES = values();
        public final IRailcraftCartContainer cartType;

        FilterType(IRailcraftCartContainer cartType) {
            this.cartType = cartType;
        }

        public boolean isAllowedFilterItem(ItemStack stack) {
            return true;
        }

        @Nullable
        public static FilterType fromCartType(IRailcraftCartContainer cartType) {
            if (cartType == null)
                return null;
            for (FilterType t : VALUES) {
                if (t.cartType == cartType)
                    return t;
            }
            return null;
        }
    }

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        return getCraftingResult(grid) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack cartItem = InvTools.emptyStack();
        ItemStack filterItem = InvTools.emptyStack();
        FilterType filterType = null;
        int cartSlot = -1;
        int itemCount = 0;
        int filterCartCount = 0;
        for (IInvSlot slot : InventoryIterator.getVanilla(grid)) {
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack)) {
                itemCount++;
                FilterType type = FilterType.fromCartType(RailcraftCarts.getCartType(stack));
                if (type != null) {
                    cartSlot = slot.getIndex();
                    filterType = type;
                    cartItem = stack.copy();
                    filterCartCount++;
                }
            }
        }
        if (filterType == null || itemCount > 2 || filterCartCount > 1)
            return InvTools.emptyStack();
        for (IInvSlot slot : InventoryIterator.getVanilla(grid)) {
            if (slot.getIndex() == cartSlot)
                continue;
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && filterType.isAllowedFilterItem(stack)) {
                filterItem = stack.copy();
                break;
            }
        }
        if (InvTools.isEmpty(cartItem) || InvTools.isEmpty(filterItem))
            return InvTools.emptyStack();

        filterItem.stackSize = 1;
        return CartBaseFiltered.addFilterToCartItem(cartItem, filterItem);
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
        ItemStack[] grid = new ItemStack[inv.getSizeInventory()];

        for (IInvSlot slot : InventoryIterator.getVanilla(inv)) {
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && FilterType.fromCartType(RailcraftCarts.getCartType(stack)) == null) {
                grid[slot.getIndex()] = stack.copy();
            }
        }

        return grid;
    }
}
