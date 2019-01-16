/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 12/21/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Ingredients {
    public static Ingredient from(Object obj) {
        Ingredient ingredient;
        if (obj instanceof IIngredientSource)
            ingredient = ((IIngredientSource) obj).getIngredient();
        else if (obj instanceof FluidStack)
            ingredient = new FluidIngredient((FluidStack) obj);
        else if (obj instanceof ItemStack && InvTools.isEmpty((ItemStack) obj))
            ingredient = Ingredient.EMPTY;
        else
            ingredient = CraftingHelper.getIngredient(obj);
        if (ingredient == null)
            return Ingredient.EMPTY;
        return ingredient;
    }

    public static Ingredient catalyst(Item item) {
        return catalyst(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public static Ingredient catalyst(Block block) {
        return catalyst(Item.getItemFromBlock(block));
    }

    public static Ingredient catalyst(ItemStack... stacks) {
        return new CatalystIngredient(stacks);
    }

    public static Ingredient consumingContainer(Item item) {
        return consumingContainer(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public static Ingredient consumingContainer(Block block) {
        return consumingContainer(Item.getItemFromBlock(block));
    }

    public static Ingredient consumingContainer(ItemStack... stacks) {
        return new ContainerConsumingIngredient(stacks);
    }

    public static Ingredient from(Object... obj) {
        return new CompoundIngredient(Stream.of(obj).map(Ingredients::from).collect(Collectors.toList()));
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
