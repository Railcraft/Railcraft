/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import mods.railcraft.api.crafting.IBlastFurnaceCraftingManager;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.OreDictionary;

public class BlastFurnaceCraftingManager implements IBlastFurnaceCraftingManager {

    private final List<BlastFurnaceRecipe> recipes = new ArrayList<BlastFurnaceRecipe>();
    private List<ItemStack> fuels;

    public static IBlastFurnaceCraftingManager getInstance() {
        return RailcraftCraftingManager.blastFurnace;
    }

    @Override
    public List<ItemStack> getFuels() {
        if (fuels == null) {
            List<ItemStack> fuel = new ArrayList<ItemStack>() {
                @Override
                public boolean add(ItemStack e) {
                    if (e == null)
                        return false;
                    return super.add(e);
                }

            };
            fuel.add(ThaumcraftPlugin.getItem("itemResource", 0));
            fuel.add(RailcraftToolItems.getCoalCoke());
            fuel.add(EnumCube.COKE_BLOCK.getItem());
            fuel.add(new ItemStack(Items.coal, 1, 1));
            fuel.add(InvTools.makeStack(ItemFirestoneRefined.item, 1, OreDictionary.WILDCARD_VALUE));
            fuel.add(InvTools.makeStack(ItemFirestoneCracked.item, 1, OreDictionary.WILDCARD_VALUE));
            fuels = Collections.unmodifiableList(fuel);
        }
        return fuels;
    }

    @Override
    public List<? extends IBlastFurnaceRecipe> getRecipes() {
        return recipes;
    }

    public static class BlastFurnaceRecipe implements IBlastFurnaceRecipe {

        private final ItemStack input;
        private final boolean matchDamage;
        private final boolean matchNBT;
        private final int cookTime;
        private final ItemStack output;

        public BlastFurnaceRecipe(ItemStack input, boolean matchDamage, boolean matchNBT, int cookTime, ItemStack output) {
            this.input = input.copy();
            this.matchDamage = matchDamage;
            this.matchNBT = matchNBT;
            this.cookTime = cookTime;
            this.output = output.copy();
        }

        @Override
        public boolean isRoomForOutput(ItemStack outputSlot) {
            if ((outputSlot == null || output == null || (InvTools.isItemEqual(outputSlot, output) && outputSlot.stackSize + output.stackSize <= output.getMaxStackSize())))
                return true;
            return false;
        }

        @Override
        public ItemStack getInput() {
            return input.copy();
        }

        public boolean matchDamage() {
            return matchDamage;
        }

        public boolean matchNBT() {
            return matchNBT;
        }

        @Override
        public ItemStack getOutput() {
            return output.copy();
        }

        @Override
        public int getOutputStackSize() {
            if (output == null)
                return 0;
            return output.stackSize;
        }

        @Override
        public int getCookTime() {
            return cookTime;
        }

    }

    @Override
    public void addRecipe(ItemStack input, boolean matchDamage, boolean matchNBT, int cookTime, ItemStack output) {
        if (input != null && output != null)
            recipes.add(new BlastFurnaceRecipe(input, matchDamage, matchNBT, cookTime, output));
    }

    @Override
    public IBlastFurnaceRecipe getRecipe(ItemStack input) {
        if (input == null) return null;
        for (BlastFurnaceRecipe r : recipes) {
            if (!r.matchDamage || InvTools.isWildcard(r.input)) continue;
            if (InvTools.isItemEqual(input, r.input, true, r.matchNBT))
                return r;
        }
        for (BlastFurnaceRecipe r : recipes) {
            if (InvTools.isItemEqual(input, r.input, r.matchDamage, r.matchNBT))
                return r;
        }
        return null;
    }

}
