/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.api.core.IIngredientSource;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 12/21/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Ingredients {
    public static Ingredient from(Object obj) {
        if (obj instanceof IIngredientSource)
            return ((IIngredientSource) obj).getIngredient();
        if (obj instanceof FluidStack)
            return new IngredientFluid((FluidStack) obj);
        Ingredient ing = CraftingHelper.getIngredient(obj);
        if (ing == null)
            return Ingredient.EMPTY;
        return ing;
    }

    public static Ingredient from(Item item, int meta) {
        return from(new ItemStack(item, 1, meta));
    }

    public static Ingredient from(Block block, int meta) {
        return from(new ItemStack(block, 1, meta));
    }

    public static Ingredient from(@Nullable String ore) {
        if (Strings.isEmpty(ore)) {
            return Ingredient.EMPTY;
        }
        return new OreIngredient(ore);
    }

}
