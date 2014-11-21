/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.util.crafting.DyeHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LocomotivePaintingRecipe implements IRecipe {

    private final ItemStack locomotive;

    public LocomotivePaintingRecipe(ItemStack locomotive) {
        this.locomotive = locomotive;
        InvTools.addNBTTag(locomotive, "gregfix", "get the hell off my lawn!");
    }

    private boolean isDye(ItemStack stack) {
        return getDye(stack) != -1;
    }

    private int getDye(ItemStack stack) {
        for (EnumColor color : EnumColor.VALUES) {
            if (InvTools.isItemEqual(stack, DyeHelper.getDyes().get(color)))
                return color.ordinal();
        }
        return -1;
    }

    private boolean isLocomotive(ItemStack loco) {
        return InvTools.isItemEqualIgnoreNBT(this.locomotive, loco);
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

        ItemStack result = loco.copy();
        ItemLocomotive.setItemColorData(result, getDye(dyePrimary), getDye(dyeSecondary));
        return result;
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
