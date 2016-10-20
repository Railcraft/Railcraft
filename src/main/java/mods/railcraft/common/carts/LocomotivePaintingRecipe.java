/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LocomotivePaintingRecipe implements IRecipe {

    private final ItemStack locomotive;

    public LocomotivePaintingRecipe(ItemStack locomotive) {
        this.locomotive = locomotive;
        InvTools.addNBTTag(locomotive, "gregfix", "get the hell off my lawn!");
    }

    private boolean isDye(@Nullable ItemStack stack) {
        return getDye(stack) != null;
    }

    @Nullable
    private EnumColor getDye(@Nullable ItemStack stack) {
        if (stack == null)
            return null;
        for (EnumColor color : EnumColor.VALUES) {
            if (InvTools.isItemEqual(stack, color.getDyesStacks()))
                return color;
        }
        return null;
    }

    private boolean isLocomotive(@Nullable ItemStack loco) {
        return InvTools.isItemEqualIgnoreNBT(locomotive, loco);
    }

    @Override
    public boolean matches(InventoryCrafting craftingGrid, World var2) {
        if (craftingGrid.getSizeInventory() < getRecipeSize())
            return false;
        ItemStack dyePrimary = craftingGrid.getStackInRowAndColumn(1, 0);
        if (!isDye(dyePrimary))
            return false;
        ItemStack cart = craftingGrid.getStackInRowAndColumn(1, 1);
        if (!isLocomotive(cart))
            return false;
        ItemStack dyeSecondary = craftingGrid.getStackInRowAndColumn(1, 2);
        return isDye(dyeSecondary);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGrid) {

        ItemStack dyePrimary = craftingGrid.getStackInRowAndColumn(1, 0);
        ItemStack loco = craftingGrid.getStackInRowAndColumn(1, 1);
        ItemStack dyeSecondary = craftingGrid.getStackInRowAndColumn(1, 2);

        if (loco == null)
            return null;

        EnumColor colorPrimary = getDye(dyePrimary);
        EnumColor colorSecondary = getDye(dyeSecondary);

        ItemStack result = loco.copy();
        if (colorPrimary != null && colorSecondary != null)
            ItemLocomotive.setItemColorData(result, colorPrimary, colorSecondary);
        return result;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return CraftingPlugin.emptyContainers(inv);
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return locomotive;
    }

}
