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
import mods.railcraft.api.crafting.IGenRule;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.ToIntFunction;

public enum RockCrusherCrafter implements IRockCrusherCrafter {
    INSTANCE;
    private final List<IRecipe> recipes = new ArrayList<>();

    @Override
    public List<IRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack input) {
        if (InvTools.isEmpty(input)) {
            return Optional.empty();
        }
        return recipes.stream().filter(r -> r.getInput().apply(input)).findFirst();
    }

    @Override
    public IRockCrusherRecipeBuilder makeRecipe(Object input) {
        return new RockCrusherRecipeBuilder(Ingredients.from(input)).name(input);
    }

    private class RockCrusherRecipeBuilder extends RecipeBuilder<IRockCrusherRecipeBuilder> implements IRockCrusherRecipeBuilder {
        private final List<IOutputEntry> outputs = new ArrayList<>();

        public RockCrusherRecipeBuilder(Ingredient input) {
            super("Rock Crusher");
            addFeature(new SingleInputFeature<>(this, input));
            addFeature(new TimeFeature<>(this, stack -> PROCESS_TIME));
        }

        @Override
        public IRockCrusherRecipeBuilder addOutput(IOutputEntry entry) {
            outputs.add(entry);
            return this;
        }

        @Override
        public IRockCrusherRecipeBuilder addOutput(ItemStack output, IGenRule rule) {
            if (output.isEmpty())
                return this;
            return addOutput(new OutputEntry(output, rule));
        }

        @Override
        public IRockCrusherRecipeBuilder addOutput(ItemStack output, float chance) {
            return addOutput(output, new RandomChanceGenRule(chance));
        }

        @Override
        protected void checkArguments() {
            super.checkArguments();
            Preconditions.checkArgument(!outputs.stream().map(IOutputEntry::getOutput).allMatch(InvTools::isEmpty),
                    "No outputs defined");
        }

        @Override
        protected void registerRecipe() {
            final Ingredient input = getInput();
            final ToIntFunction<ItemStack> timeFunction = getTimeFunction();
            recipes.add(new IRecipe() {
                @Override
                public Ingredient getInput() {
                    return input;
                }

                @Override
                public List<IOutputEntry> getOutputs() {
                    return outputs;
                }

                @Override
                public ResourceLocation getName() {
                    return Objects.requireNonNull(name);
                }

                @Override
                public int getTickTime(ItemStack input) {
                    return timeFunction.applyAsInt(input);
                }
            });
        }
    }

    private static class RandomChanceGenRule implements IGenRule {
        private final float randomChance;
        private List<ITextComponent> toolTip;

        RandomChanceGenRule(float randomChance) {
            this.randomChance = randomChance;
        }

        @Override
        public boolean test(Random random) {
            return random.nextFloat() < randomChance;
        }

        //TODO: test this!
        @Override
        public List<ITextComponent> getToolTip() {
            if (toolTip == null) {
                toolTip = Collections.singletonList(new TextComponentString(new DecimalFormat("(###.###% chance)").format(randomChance)));
            }
            return toolTip;
        }
    }

    private static class OutputEntry implements IOutputEntry {
        private final ItemStack output;
        private final IGenRule genRule;

        OutputEntry(ItemStack output, IGenRule genRule) {
            this.output = output.copy();
            this.genRule = genRule;
        }

        @Override
        public ItemStack getOutput() {
            return output.copy();
        }

        @Override
        public IGenRule getGenRule() {
            return genRule;
        }
    }
}
