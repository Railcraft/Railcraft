/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import mods.railcraft.api.crafting.IRockCrusherCraftingManager;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;

public class RockCrusherCraftingManager implements IRockCrusherCraftingManager {

    private final List<CrusherRecipe> recipes = new ArrayList<CrusherRecipe>();

    public static IRockCrusherCraftingManager getInstance() {
        return RailcraftCraftingManager.rockCrusher;
    }

    @Override
    public List<? extends IRockCrusherRecipe> getRecipes() {
        return recipes;
    }

    public static class CrusherRecipe implements IRockCrusherRecipe {

        private final ItemStack input;
        private final boolean matchDamage;
        private final boolean matchNBT;
        private final List<Map.Entry<ItemStack, Float>> outputs = new ArrayList<Map.Entry<ItemStack, Float>>();

        public CrusherRecipe(ItemStack input, boolean matchDamage, boolean matchNBT) {
            this.input = input.copy();
            this.matchDamage = matchDamage;
            this.matchNBT = matchNBT;
        }

        @Override
        public ItemStack getInput() {
            return input.copy();
        }

        @Override
        public void addOutput(ItemStack output, float chance) {
            if (output == null) return;
            outputs.add(Maps.immutableEntry(output, chance));
        }

        @Override
        public List<Map.Entry<ItemStack, Float>> getOutputs() {
            return outputs;
        }

        @Override
        public List<ItemStack> getPossibleOuputs() {
            List<ItemStack> list = new ArrayList<ItemStack>();
            for (Map.Entry<ItemStack, Float> entry : outputs) {
                ItemStack output = entry.getKey();
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
                    list.add(output.copy());
                }
            }
            return list;
        }

        @Override
        public List<ItemStack> getRandomizedOuputs() {
            List<ItemStack> list = new ArrayList<ItemStack>();
            for (Map.Entry<ItemStack, Float> entry : outputs) {
                if (MiscTools.getRand().nextFloat() <= entry.getValue()) {
                    list.add(entry.getKey().copy());
                }
            }
            return list;
        }

        public int getNumberOfOutputs() {
            return outputs.size();
        }
    }

    /**
     *
     * @param input
     * @param matchDamage
     * @param matchNBT
     * @return 
     */
    @Override
    public IRockCrusherRecipe createNewRecipe(ItemStack input, boolean matchDamage, boolean matchNBT) {
        CrusherRecipe recipe = new CrusherRecipe(input, matchDamage, matchNBT);
        recipes.add(recipe);
        return recipe;
    }

    @Override
    public CrusherRecipe getRecipe(ItemStack input) {
        if (input == null) return null;
        for (CrusherRecipe r : recipes) {
            if (!r.matchDamage || InvTools.isWildcard(r.input)) continue;
            if (InvTools.isItemEqual(input, r.input, true, r.matchNBT))
                return r;
        }
        for (CrusherRecipe r : recipes) {
            if (InvTools.isItemEqual(input, r.input, r.matchDamage, r.matchNBT))
                return r;
        }
        return null;
    }
}
