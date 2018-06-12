/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.ICrusherRecipe;
import mods.railcraft.api.crafting.IGenRule;
import mods.railcraft.api.crafting.IOutputEntry;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public final class RockCrusherCraftingManager implements ICrusherCraftingManager {

    private final List<ICrusherRecipe> recipes = new ArrayList<>();
    public static final ICrusherRecipe NULL_RECIPE = new CrusherRecipe(Ingredient.EMPTY);
    private static final RockCrusherCraftingManager INSTANCE = new RockCrusherCraftingManager();

    public static RockCrusherCraftingManager getInstance() {
        return INSTANCE;
    }

    private RockCrusherCraftingManager() {
    }

    @Override
    public List<ICrusherRecipe> getRecipes() {
        return recipes;
    }

    @Override
    public IGenRule createGenRule(float randomChance) {
        return new RandomChanceGenRule(randomChance);
    }

    @Nullable
    @Override
    public ICrusherRecipe getRecipe(ItemStack input) {
        if (InvTools.isEmpty(input)) {
            return null;
        }
        for (ICrusherRecipe r : recipes) {
            if (r.getInput().apply(input))
                return r;
        }
        return null;
    }

    @Override
    public void addRecipe(ICrusherRecipe recipe) {
        if (!recipe.getInput().apply(ItemStack.EMPTY)) {
            recipes.add(recipe);
        } else {
            Game.logTrace(Level.ERROR, 10, "Tried to register an invalid rock crusher recipe");
        }
    }

    @Override
    public ICrusherRecipeBuilder createRecipeBuilder() {
        return new CrusherRecipeBuilderImpl();
    }

    private static class CrusherRecipeBuilderImpl implements ICrusherRecipeBuilder {
        private Ingredient input;
        private List<IOutputEntry> outputs = new ArrayList<>();

        @Override
        public ICrusherRecipeBuilder input(Ingredient input) {
            this.input = input;
            return this;
        }

        @Override
        public ICrusherRecipeBuilder addOutput(IOutputEntry entry) {
            this.outputs.add(entry);
            return this;
        }

        @Override
        public ICrusherRecipeBuilder addOutput(ItemStack output, IGenRule rule) {
            if (output.isEmpty())
                return this;
            return addOutput(new OutputEntry(output, rule));
        }

        @Override
        public ICrusherRecipeBuilder addOutput(ItemStack output, float chance) {
            return addOutput(output, new RandomChanceGenRule(chance));
        }

        @Override
        public ICrusherRecipe build() throws IllegalArgumentException {
            checkArgument(input != null, "input");
            return new CrusherRecipe(input, outputs);
        }

        @Override
        public void buildAndRegister() throws IllegalArgumentException {
            RockCrusherCraftingManager.getInstance().addRecipe(build());
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

    private static class CrusherRecipe implements ICrusherRecipe {

        private final Ingredient inputMatcher;
        private final List<IOutputEntry> outputs;

        CrusherRecipe(Ingredient inputMatcher) {
            this(inputMatcher, new ArrayList<>());
        }

        CrusherRecipe(Ingredient inputMatcher, List<IOutputEntry> entries) {
            this.inputMatcher = inputMatcher;
            this.outputs = entries;
        }

        @Override
        public Ingredient getInput() {
            return inputMatcher;
        }

        @Override
        public List<IOutputEntry> getOutputs() {
            return outputs;
        }
    }
}
