/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IBlastFurnaceCraftingManager;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

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
                    return !InvTools.isEmpty(e) && super.add(e);
                }

            };
            // TODO: Thaumcraft
//            fuel.add(ThaumcraftPlugin.ITEMS.get("alumentum", 0));
            fuel.add(RailcraftItems.COKE.getStack());
            fuel.add(EnumGeneric.BLOCK_COKE.getStack());
            fuel.add(new ItemStack(Items.COAL, 1, 1));
            fuel.add(RailcraftItems.FIRESTONE_REFINED.getWildcard());
            fuel.add(RailcraftItems.FIRESTONE_CRACKED.getWildcard());
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
            return (InvTools.isEmpty(outputSlot) || InvTools.isEmpty(output) || (InvTools.isItemEqual(outputSlot, output) && sizeOf(outputSlot) + sizeOf(output) <= output.getMaxStackSize()));
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
            return sizeOf(output);
        }

        @Override
        public int getCookTime() {
            return cookTime;
        }

    }

    @Override
    public void addRecipe(@Nullable ItemStack input, boolean matchDamage, boolean matchNBT, int cookTime, @Nullable ItemStack output) {
        if (!InvTools.isEmpty(input) && !InvTools.isEmpty(output))
            recipes.add(new BlastFurnaceRecipe(input, matchDamage, matchNBT, cookTime, output));
    }

    @Override
    public IBlastFurnaceRecipe getRecipe(ItemStack input) {
        if (InvTools.isEmpty(input)) return null;
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
