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
import mods.railcraft.api.crafting.IGenRule;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
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
    public IRecipeBuilder makeRecipe(Object input) {
        RecipeBuilder builder = new RecipeBuilder(Ingredients.from(input));
        CraftingPlugin.tryGuessName(input, builder);
        return builder;
    }

    private class RecipeBuilder implements IRecipeBuilder {
        private final Ingredient input;
        private ResourceLocation name;
        private List<IOutputEntry> outputs = new ArrayList<>();
        private int processTime = PROCESS_TIME;
        private boolean registered;

        public RecipeBuilder(Ingredient input) {
            this.input = input;
            CraftingPlugin.addBuilder(this);
        }

        @Override
        public IRecipeBuilder name(@Nullable ResourceLocation name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        @Override
        public IRecipeBuilder time(int ticks) {
            this.processTime = ticks;
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
        public void register() {
            registered = true;
            try {
                Preconditions.checkArgument(input != null && !input.apply(ItemStack.EMPTY),
                        "Input was null or empty.");
                Preconditions.checkArgument(name != null, "Recipe name not set.");
                Preconditions.checkArgument(processTime > 0, "Process time was zero.");
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
            } catch (Throwable ex) {
                Game.log(Level.WARN,
                        "Tried, but failed to register {0} as a Rock Crusher recipe. Reason: {1}",
                        name, ex.getMessage());
            }
        }

        @Override
        public ResourceLocation getName() {
            return name;
        }

        @Override
        public boolean notRegistered() {
            return !registered;
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
