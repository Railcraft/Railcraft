/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.common.base.Preconditions;
import mods.railcraft.api.crafting.ISimpleRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 12/26/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unchecked")
public abstract class SingleInputRecipeBuilder<B extends ISimpleRecipeBuilder> extends SimpleRecipeBuilder<B> {
    protected final Ingredient input;

    protected SingleInputRecipeBuilder(String recipeType, Ingredient input, Function<ItemStack, Integer> defaultTimeFunction) {
        super(recipeType, defaultTimeFunction);
        this.input = input;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void checkArguments() {
        super.checkArguments();
        Preconditions.checkArgument(input != null && !input.apply(ItemStack.EMPTY),
                "Input was null or empty.");
        Preconditions.checkArgument(Stream.of(input.getMatchingStacks()).map(timeFunction).allMatch(time -> time > 0),
                "Time set to zero.");
    }

}
