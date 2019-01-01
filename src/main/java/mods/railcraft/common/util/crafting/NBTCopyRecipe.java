/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryIterator;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class NBTCopyRecipe extends BaseRecipe {

    private final Ingredient source;
    private final Ingredient blank;
    private final ItemStack output;

    public NBTCopyRecipe(String name, Ingredient source, Ingredient blank, ItemStack output) {
        super(name);
        this.source = source;
        this.blank = blank;
        this.output = output;
    }

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        int numBlank = 0;
        int numSource = 0;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (!InvTools.isEmpty(stack)) {
                if (numSource == 0 && source.test(stack)) {
                    numSource++;
                } else if (blank.test(stack)) {
                    numBlank++;
                } else {
                    return false;
                }
            }
        }
        return numSource == 1 && numBlank == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack source = InventoryIterator.get(grid).streamStacks()
                .filter(this.source)
                .findFirst().orElse(InvTools.emptyStack());
        if (!InvTools.isEmpty(source)) {
            ItemStack copy = getRecipeOutput();
            NBTTagCompound nbt = source.getTagCompound();
            if (nbt != null)
                copy.setTagCompound(nbt.copy());
            return copy;
        }
        return getRecipeOutput();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (source.test(stack)) {
                ret.set(i, stack.copy());
                break;
            }
        }
        return ret;
    }

    public Ingredient getSource() {
        return source;
    }

    public Ingredient getBlank() {
        return blank;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(getSource());
        ingredients.add(getBlank());
        return ingredients;
    }
}
