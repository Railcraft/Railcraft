/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingIterator;
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public class RockCrusherCraftingManager implements ICrusherCraftingManager {

    private static final RecipeList recipes = new RecipeList();

    @Nonnull
    @Override
    public Collection<? extends ICrusherRecipe> recipes() {
        return recipes;
    }

    @Override
    public IInputMatcher createInputMatcher(ItemStack input, boolean matchDamage, boolean matchNBT) {
        return new InputMatcher(input, matchDamage, matchNBT);
    }

    @Override
    public GenRule createGenRule() {
        return createGenRule(1.0F, -1);
    }

    @Override
    public GenRule createGenRule(float randomChance, String... groupNames) {
        return new GenRule(randomChance, -1, groupNames);
    }

    @Override
    public GenRule createGenRule(final float randomChance, final int maxItems, final String... groupNames) {
        return new GenRule(randomChance, maxItems, groupNames);
    }

    @Override
    public ICrusherRecipe createRecipe(@Nonnull IInputMatcher inputMatcher) {
        return new CrusherRecipe(inputMatcher);
    }

    @Override
    public ICrusherRecipe createRecipe(ItemStack input, boolean matchDamage, boolean matchNBT) {
        return createRecipe(createInputMatcher(input, matchDamage, matchNBT));
    }

    @Override
    public ICrusherRecipe createAndAddRecipe(@Nonnull IInputMatcher inputMatcher) {
        ICrusherRecipe recipe = createRecipe(inputMatcher);
        recipes.add(recipe);
        return recipe;
    }

    @Override
    public ICrusherRecipe createAndAddRecipe(ItemStack input, boolean matchDamage, boolean matchNBT) {
        ICrusherRecipe recipe = createRecipe(input, matchDamage, matchNBT);
        recipes.add(recipe);
        return recipe;
    }

    @Override
    public ICrusherRecipe getRecipe(ItemStack input) {
        if (input == null) return null;
        for (ICrusherRecipe r : recipes.high()) {
            if (r.getInputMatcher().apply(input))
                return r;
        }
        for (ICrusherRecipe r : recipes.medium()) {
            if (r.getInputMatcher().apply(input))
                return r;
        }
        for (ICrusherRecipe r : recipes.low()) {
            if (r.getInputMatcher().apply(input))
                return r;
        }
        return null;
    }

    private static class InputMatcher implements IInputMatcher {
        private final ItemStack input;
        private final Priority priority;
        private final boolean matchDamage, matchNBT;

        InputMatcher(ItemStack input, boolean matchDamage, boolean matchNBT) {
            this(input, genPriority(matchDamage, matchNBT), matchDamage, matchNBT);
        }

        InputMatcher(ItemStack input, Priority priority, boolean matchDamage, boolean matchNBT) {
            this.input = input.copy();
            this.priority = priority;
            this.matchDamage = matchDamage;
            this.matchNBT = matchNBT;
        }

        private static Priority genPriority(boolean matchDamage, boolean matchNBT) {
            if (matchNBT)
                return Priority.HIGH;
            if (matchDamage)
                return Priority.MEDIUM;
            return Priority.LOW;
        }

        @Override
        public ItemStack getDisplayStack() {
            return input;
        }

        @Override
        public Priority getPriority() {
            return priority;
        }

        @Override
        public boolean apply(@Nullable ItemStack stack) {
            return InvTools.isItemEqual(stack, input, matchDamage, matchNBT);
        }
    }

    private static class GenRule implements IGenRule {
        private final float randomChance;
        private final int maxItems;
        private final String[] groupNames;
        private List<ITextComponent> toolTip;

        GenRule(final float randomChance, final int maxItems, final String... groupNames) {
            this.randomChance = randomChance;
            this.maxItems = maxItems;
            this.groupNames = groupNames;
        }

        @Override
        public boolean apply(@Nullable List<IOutputEntry> previousEntries) {
            if (previousEntries != null) {
                if (maxItems > 0 && previousEntries.size() >= maxItems)
                    return false;

                for (IOutputEntry entry : previousEntries) {
                    if (entry instanceof GenRule) {
                        GenRule genRule = (GenRule) entry;
                        for (String group : groupNames) {
                            if (ArrayUtils.contains(genRule.groupNames, group))
                                return false;
                        }
                    }
                }
            }

            return MiscTools.RANDOM.nextFloat() <= randomChance;
        }

        //TODO: test this!
        @Override
        public List<ITextComponent> getToolTip() {
            if (toolTip == null) {
                toolTip = new ArrayList<ITextComponent>();
                toolTip.add(new TextComponentString(new DecimalFormat("(###.###% chance)").format(randomChance)));
                toolTip.add(new TextComponentString("Max Items: " + maxItems));
                toolTip.add(new TextComponentString("Groups: " + Arrays.toString(groupNames)));
            }
            return toolTip;
        }
    }

    private static class OutputEntry implements IOutputEntry {
        private final ItemStack output;
        private final IGenRule genRule;

        private OutputEntry(ItemStack output, IGenRule genRule) {
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

        private final IInputMatcher inputMatcher;
        private final List<IOutputEntry> outputs = new ArrayList<IOutputEntry>();

        CrusherRecipe(IInputMatcher inputMatcher) {
            this.inputMatcher = inputMatcher;
        }

        @Override
        public IInputMatcher getInputMatcher() {
            return inputMatcher;
        }

        @Override
        public void addOutput(ItemStack output, IGenRule genRule) {
            if (output == null) return;
            outputs.add(new OutputEntry(output, genRule));
        }

        @Override
        public void addOutput(ItemStack output, float chance, int maxItems, String... groupNames) {
            addOutput(output, RailcraftCraftingManager.rockCrusher.createGenRule(chance, maxItems, groupNames));
        }

        @Override
        public void addOutput(ItemStack output, float chance) {
            addOutput(output, RailcraftCraftingManager.rockCrusher.createGenRule(chance));
        }

        @Override
        public void addOutput(ItemStack output) {
            addOutput(output, RailcraftCraftingManager.rockCrusher.createGenRule());
        }

        @Override
        public List<IOutputEntry> getOutputs() {
            return outputs;
        }

        @Override
        public List<ItemStack> getPossibleOutputs() {
            List<ItemStack> list = new ArrayList<ItemStack>();
            for (IOutputEntry entry : outputs) {
                ItemStack output = entry.getOutput();
                for (ItemStack saved : list) {
                    if (InvTools.isItemEqual(saved, output)) {
                        if (saved.stackSize + output.stackSize <= saved.getMaxStackSize()) {
                            saved.stackSize += output.stackSize;
                            output = null;
                        } else {
                            int diff = saved.getMaxStackSize() - saved.stackSize;
                            saved.stackSize = saved.getMaxStackSize();
                            output.stackSize -= diff;
                        }
                        break;
                    }
                }
                if (output != null) {
                    list.add(output);
                }
            }
            return list;
        }

        @Override
        public List<ItemStack> getProcessedOutputs() {
            List<ItemStack> list = new ArrayList<ItemStack>();
            List<IOutputEntry> outputs = new ArrayList<IOutputEntry>();
            for (IOutputEntry entry : outputs) {
                if (entry.getGenRule().apply(outputs)) {
                    list.add(entry.getOutput());
                    outputs.add(entry);
                }
            }
            return list;
        }

    }

    private static class RecipeList extends ForwardingCollection<ICrusherRecipe> {
        private final List<ICrusherRecipe> recipes = new ArrayList<ICrusherRecipe>();
        private final List<ICrusherRecipe> recipesHigh = new ArrayList<ICrusherRecipe>();
        private final List<ICrusherRecipe> recipesMedium = new ArrayList<ICrusherRecipe>();
        private final List<ICrusherRecipe> recipesLow = new ArrayList<ICrusherRecipe>();

        @Override
        protected Collection<ICrusherRecipe> delegate() {
            return recipes;
        }

        @Nonnull
        @Override
        public Iterator<ICrusherRecipe> iterator() {
            return new ForwardingIterator<ICrusherRecipe>() {
                ICrusherRecipe current;

                @Override
                protected Iterator<ICrusherRecipe> delegate() {
                    return recipes.iterator();
                }

                @Override
                public ICrusherRecipe next() {
                    current = super.next();
                    return current;
                }

                @Override
                public void remove() {
                    super.remove();
                    recipesHigh.remove(current);
                    recipesMedium.remove(current);
                    recipesLow.remove(current);
                }
            };
        }

        Collection<ICrusherRecipe> high() {
            return Collections.unmodifiableCollection(recipesHigh);
        }

        Collection<ICrusherRecipe> medium() {
            return Collections.unmodifiableCollection(recipesMedium);
        }

        Collection<ICrusherRecipe> low() {
            return Collections.unmodifiableCollection(recipesLow);
        }

        @Override
        public boolean add(@Nonnull ICrusherRecipe recipe) {
            boolean added = recipes.add(recipe);
            if (added)
                switch (recipe.getInputMatcher().getPriority()) {
                    case HIGH:
                        recipesHigh.add(recipe);
                    case MEDIUM:
                        recipesMedium.add(recipe);
                    case LOW:
                        recipesLow.add(recipe);
                }
            return added;
        }

        @Override
        public boolean remove(@Nonnull Object recipe) {
            recipesHigh.remove(recipe);
            recipesMedium.remove(recipe);
            recipesLow.remove(recipe);
            return recipes.remove(recipe);
        }

        @Override
        public boolean addAll(@Nonnull Collection<? extends ICrusherRecipe> c) {
            return standardAddAll(c);
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            return standardRemoveAll(c);
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return standardRetainAll(c);
        }

        @Override
        public void clear() {
            recipes.clear();
            recipesHigh.clear();
            recipesMedium.clear();
            recipesLow.clear();
        }
    }
}
