/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IGenRule;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

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
    public IRecipeBuilder makeRecipe(@Nullable ResourceLocation name, Ingredient input) {
        Objects.requireNonNull(name);
        if (!input.apply(ItemStack.EMPTY)) {
            return new RecipeBuilder(name, input);
        } else {
            Game.log(Level.WARN, "Tried, but failed to register {0} as a rock crusher recipe", name);
        }
        return new IRecipeBuilder() {};
    }

    private class RecipeBuilder implements IRecipeBuilder {
        private final ResourceLocation name;
        private final Ingredient input;
        private List<IOutputEntry> outputs = new ArrayList<>();
        private int processTime = PROCESS_TIME;

        public RecipeBuilder(ResourceLocation name, Ingredient input) {
            this.name = name;
            this.input = input;
        }

        @Override
        public IRecipeBuilder setProcessTime(int processTime) {
            this.processTime = processTime;
            return this;
        }

        @Override
        public IRecipeBuilder addOutput(IOutputEntry entry) {
            this.outputs.add(entry);
            return this;
        }

        @Override
        public IRecipeBuilder addOutput(ItemStack output, IGenRule rule) {
            if (output.isEmpty())
                return this;
            return addOutput(new OutputEntry(output, rule));
        }

        @Override
        public IRecipeBuilder addOutput(ItemStack output, float chance) {
            return addOutput(output, new RandomChanceGenRule(chance));
        }

        @Override
        public void register() throws IllegalArgumentException {
            checkArgument(input != null, "input");
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
                    return name;
                }

                @Override
                public int getTickTime() {
                    return processTime;
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
