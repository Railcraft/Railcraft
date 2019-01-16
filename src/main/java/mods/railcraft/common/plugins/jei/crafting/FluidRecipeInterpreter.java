/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.crafting;

import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class FluidRecipeInterpreter {

    static IStackHelper helper;
    static IIngredientRegistry ingredientRegistry;

    public static void init(IStackHelper helper, IIngredientRegistry registry) {
        FluidRecipeInterpreter.helper = helper;
        FluidRecipeInterpreter.ingredientRegistry = registry;
    }

//    public static List<List<ItemStack>> expand(List<Object> inputs) {
//        return inputs.stream()
//                .map(FluidRecipeInterpreter::getPossibilities)
//                .collect(Collectors.toList());
//    }
//
//    public static List<ItemStack> getPossibilities(Object input) {
//        if (input instanceof ItemStack) {
//            ItemStack stack = (ItemStack) input;
//            if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
//                return helper.getSubtypes(stack);
//            }
//            return Collections.singletonList(stack);
//        } else if (input instanceof String) {
//            return OreDictionary.getOres((String) input);
//        } else if (input instanceof Iterable) {
//            return StreamSupport.stream(((Iterable<?>) input).spliterator(), false).flatMap((obj) -> getPossibilities(obj).stream()).collect(Collectors.toList());
//        } else if (input instanceof FluidStack) {
//            return getAllContainersFilledWith((FluidStack) input);
//        }
//        return Collections.emptyList();
//    }

    public static List<ItemStack> getAllContainersFilledWith(@Nullable FluidStack fluid) {
        if (fluid == null)
            return Collections.emptyList();
        Collection<ItemStack> ingredients = ingredientRegistry.getAllIngredients(ItemStack.class);
        List<ItemStack> ret = new ArrayList<>();

        for (ItemStack ingredient : ingredients) {
            ItemStack toTest = ingredient.copy();
            if (ingredient.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IFluidHandler emptyCapability = toTest.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (emptyCapability == null) {
                    continue;
                }
                FluidStack drain = emptyCapability.drain(fluid.amount, false);
                if (drain != null && drain.isFluidStackIdentical(fluid)) {
                    ret.add(toTest);
                }
            }
        }
        return ret;
    }

    private FluidRecipeInterpreter() {
    }

}
