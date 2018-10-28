/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class EggInfoCopyRecipe extends BaseRecipe {

    public EggInfoCopyRecipe() {
        super("egg_info_copy");
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return calculate(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        Tuple<Integer, Integer> result = calculate(inv);
        if (result == null) {
            return ItemStack.EMPTY;
        }
        return NBTPlugin.copyTag(inv.getStackInSlot(result.getFirst()), inv.getStackInSlot(result.getSecond()));
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        Tuple<Integer, Integer> tuple = calculate(inv);
        if (tuple != null) {
            final int place = tuple.getSecond();
            list.set(place, InvTools.copy(inv.getStackInSlot(place)));
        }
        return list;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return RailcraftCarts.SPAWNER.getStack();
    }

    private @Nullable Tuple<Integer, Integer> calculate(InventoryCrafting inv) {
        int cart = -1;
        int egg = -1;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (RailcraftCarts.SPAWNER.isEqual(stack)) {
                if (cart >= 0) {
                    return null;
                } else {
                    cart = i;
                }
            }
            if (stack.getItem() instanceof ItemMonsterPlacer) {
                if (egg >= 0) {
                    return null;
                } else {
                    egg = i;
                }
            }
        }
        if (egg < 0 || cart < 0) {
            return null;
        }
        return new Tuple<>(cart, egg);
    }
}
