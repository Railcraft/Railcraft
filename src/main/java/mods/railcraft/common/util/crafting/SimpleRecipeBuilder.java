/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.crafting;

import com.google.common.base.Preconditions;
import mods.railcraft.api.crafting.ISimpleRecipeBuilder;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 12/26/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unchecked")
public abstract class SimpleRecipeBuilder<B extends ISimpleRecipeBuilder> implements ISimpleRecipeBuilder<B> {
    private final String recipeType;
    protected final Ingredient input;
    protected ResourceLocation name;
    protected Function<ItemStack, Integer> timeFunction;
    private boolean registered;

    protected SimpleRecipeBuilder(String recipeType, Ingredient input, Function<ItemStack, Integer> defaultTimeFunction) {
        this.recipeType = recipeType;
        this.input = input;
        this.timeFunction = defaultTimeFunction;
        CraftingPlugin.addBuilder(this);
    }

    @Override
    public B name(@Nullable ResourceLocation name) {
        this.name = Objects.requireNonNull(name);
        return (B) this;
    }

    @Override
    public B time(Function<ItemStack, Integer> tickFunction) {
        this.timeFunction = tickFunction;
        return (B) this;
    }

    public void register() {
        registered = true;
        try {
            checkArguments();
            registerRecipe();
        } catch (Throwable ex) {
            Game.log(Level.WARN,
                    "Tried, but failed to register {0} as a {1} recipe. Reason: {2}",
                    name, recipeType, ex.getMessage());
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void checkArguments() {
        Preconditions.checkArgument(input != null && !input.apply(ItemStack.EMPTY),
                "Input was null or empty.");
        Preconditions.checkArgument(name != null, "Recipe name not set.");
        Preconditions.checkArgument(timeFunction != null
                        && Stream.of(input.getMatchingStacks()).map(timeFunction).allMatch(time -> time > 0),
                "No time function set.");
    }

    protected abstract void registerRecipe();

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public boolean notRegistered() {
        return !registered;
    }
}
