/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.ItemLocomotive;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LocomotivePaintingRecipe extends BaseRecipe implements IShapedRecipe {

    private final ItemStack locomotive;

    public LocomotivePaintingRecipe(ItemStack locomotive) {
        super(Objects.requireNonNull(locomotive.getItem().getRegistryName()).getPath() + "_painting");
        this.locomotive = locomotive;
        InvTools.getItemData(locomotive).setString("gregfix", "get the hell off my lawn!");
    }

    private boolean isDye(ItemStack stack) {
        return getDye(stack) != null;
    }

    private @Nullable EnumColor getDye(ItemStack stack) {
        if (InvTools.isEmpty(stack))
            return null;
        return EnumColor.dyeColorOf(stack).orElse(null);
    }

    private boolean isLocomotive(ItemStack loco) {
        return InvTools.isItemEqualIgnoreNBT(locomotive, loco);
    }

    @Override
    public boolean matches(InventoryCrafting craftingGrid, World var2) {
        for (int i = 0; i < 3; i++) {
            ItemStack dyePrimary = craftingGrid.getStackInRowAndColumn(i, 0);
            if (!isDye(dyePrimary))
                continue;
            ItemStack cart = craftingGrid.getStackInRowAndColumn(i, 1);
            if (!isLocomotive(cart))
                continue;
            ItemStack dyeSecondary = craftingGrid.getStackInRowAndColumn(i, 2);
            if (isDye(dyeSecondary))
                return true;
            // check other columns clear?
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGrid) {
        for (int i = 0; i < 3; i++) {
            ItemStack dyePrimary = craftingGrid.getStackInRowAndColumn(i, 0);
            ItemStack loco = craftingGrid.getStackInRowAndColumn(i, 1);
            ItemStack dyeSecondary = craftingGrid.getStackInRowAndColumn(i, 2);

            if (InvTools.isEmpty(loco))
                continue;

            EnumColor colorPrimary = getDye(dyePrimary);
            EnumColor colorSecondary = getDye(dyeSecondary);

            ItemStack result = loco.copy();
            if (colorPrimary != null && colorSecondary != null)
                ItemLocomotive.setItemColorData(result, colorPrimary, colorSecondary);
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 1 && height >= 3;
    }

    @Override
    public int getRecipeWidth() {
        return 1;
    }

    @Override
    public int getRecipeHeight() {
        return 3;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return locomotive;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
        Ingredient dye = Ingredients.from(Stream.of(EnumColor.VALUES).map(EnumColor::getIngredient).toArray(Object[]::new));
        ingredients.set(1, dye);
        ingredients.set(4, Ingredients.from(locomotive));
        ingredients.set(7, dye);
        return ingredients;
    }
}
