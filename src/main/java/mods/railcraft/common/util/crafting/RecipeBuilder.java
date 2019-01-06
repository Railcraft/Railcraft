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
import mods.railcraft.api.crafting.IRecipeBuilder;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 12/26/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unchecked")
public abstract class RecipeBuilder<B extends IRecipeBuilder> implements IRecipeBuilder<B> {
    private final String recipeType;
    protected @Nullable ResourceLocation name;
    private boolean registered;
    private final List<Feature> features = new ArrayList<>();

    protected RecipeBuilder(String recipeType) {
        this.recipeType = recipeType;
        CraftingPlugin.addBuilder(this);
    }

    protected B addFeature(Feature<B> feature) {
        features.add(feature);
        return (B) this;
    }

    public <F> Optional<F> getFeature(Class<F> feature) {
        return features.stream().flatMap(Streams.toType(feature)).findFirst();
    }

    @Override
    public B name(@Nullable Object obj) {
        ResourceLocation nameGuess = CraftingPlugin.guessName(obj);
        if (nameGuess != null)
            name(nameGuess);
        return (B) this;
    }

    @Override
    public B name(@Nullable ResourceLocation name) {
        this.name = Objects.requireNonNull(name);
        return (B) this;
    }

    public final void register() {
        registered = true;
        try {
            checkArguments();
            registerRecipe();
        } catch (Throwable ex) {
            if (name == null)
                Game.log().throwable(Level.WARN, 10, ex,
                        "Tried, but failed to register a {0} recipe. Reason: {1}",
                        recipeType, ex.getMessage());
            else
                Game.log().msg(Level.WARN,
                        "Tried, but failed to register {0} as a {1} recipe. Reason: {2}",
                        name, recipeType, ex.getMessage());
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void checkArguments() {
        features.forEach(Feature::checkArguments);
        Preconditions.checkArgument(name != null, "Recipe name not set.");
    }

    protected abstract void registerRecipe();

    @Override
    public ResourceLocation getName() {
        return name != null ? name : Game.getActiveModResource("unknown");
    }

    @Override
    public boolean notRegistered() {
        return !registered;
    }

    /**
     * @author CovertJaguar <http://www.railcraft.info>
     */
    public abstract static class Feature<B extends IRecipeBuilder> implements IFeature {
        protected Feature(RecipeBuilder builder) {
            this.builder = builder;
        }

        @Override
        public final <F> Optional<F> getFeature(Class<F> feature) {
            return builder.getFeature(feature);
        }

        protected final RecipeBuilder<B> builder;

        protected abstract void checkArguments();
    }

    /**
     * @author CovertJaguar <http://www.railcraft.info>
     */
    public class TimeFeature<B extends IRecipeBuilder> extends Feature<B> implements ITimeFeature<B> {
        protected ToIntFunction<ItemStack> timeFunction;

        protected TimeFeature(RecipeBuilder<B> builder, ToIntFunction<ItemStack> defaultTimeFunction) {
            super(builder);
            this.timeFunction = defaultTimeFunction;
        }

        @Override
        public B time(ToIntFunction<ItemStack> tickFunction) {
            this.timeFunction = tickFunction;
            return (B) builder;
        }

        @Override
        public ToIntFunction<ItemStack> getTimeFunction() {
            return timeFunction;
        }

        @Override
        @OverridingMethodsMustInvokeSuper
        protected void checkArguments() {
            Preconditions.checkArgument(timeFunction != null, "Time function not set");
            getFeature(SingleInputFeature.class).ifPresent(feature -> {
                Stream<ItemStack> ingredients = Stream.of(feature.input.getMatchingStacks());
                Preconditions.checkArgument(ingredients.mapToInt(timeFunction).allMatch(time -> time > 0),
                        "Time set to zero.");
            });
        }

    }

    /**
     * @author CovertJaguar <http://www.railcraft.info>
     */
    public static class SingleInputFeature<B extends IRecipeBuilder> extends Feature<B> implements ISingleInputFeature {
        protected final Ingredient input;

        protected SingleInputFeature(RecipeBuilder<B> builder, Ingredient input) {
            super(builder);
            this.input = input;
        }

        @Override
        public Ingredient getInput() {
            return input;
        }

        @Override
        @OverridingMethodsMustInvokeSuper
        protected void checkArguments() {
            Preconditions.checkArgument(input != null && !input.apply(ItemStack.EMPTY),
                    "Input was null or empty.");
        }

    }

    /**
     * @author CovertJaguar <http://www.railcraft.info>
     */
    public static class SingleItemStackOutputFeature<B extends IRecipeBuilder> extends Feature<B> implements ISingleItemStackOutputFeature<B> {
        protected ItemStack output = ItemStack.EMPTY;
        private final boolean doChecks;

        protected SingleItemStackOutputFeature(RecipeBuilder<B> builder, boolean doChecks) {
            super(builder);
            this.doChecks = doChecks;
        }

        @Override
        public B output(@Nullable ItemStack output) {
            this.output = InvTools.copy(output);
            return (B) builder;
        }

        @Override
        public ItemStack getOutput() {
            return output.copy();
        }

        @Override
        @OverridingMethodsMustInvokeSuper
        protected void checkArguments() {
            if (doChecks) Preconditions.checkArgument(InvTools.nonEmpty(output),
                    "Output was null or empty.");
        }

    }
}
