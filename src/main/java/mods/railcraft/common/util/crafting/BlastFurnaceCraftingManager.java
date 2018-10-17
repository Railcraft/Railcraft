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
import mods.railcraft.api.crafting.IBlastFurnaceFuel;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BlastFurnaceCraftingManager implements IBlastFurnaceCraftingManager {

    private static final BlastFurnaceCraftingManager INSTANCE = new BlastFurnaceCraftingManager();
    private final List<IBlastFurnaceRecipe> recipes = new ArrayList<>();
    private final List<IBlastFurnaceFuel> fuels = new ArrayList<>();

    public static BlastFurnaceCraftingManager getInstance() {
        return INSTANCE;
    }

    private BlastFurnaceCraftingManager() {
        List<ItemStack> fuel = new ArrayList<>();
        fuel.add(ThaumcraftPlugin.ITEMS.get("alumentum", 0));
        fuel.add(EnumGeneric.BLOCK_COKE.getStack());
        fuel.add(new ItemStack(Items.COAL, 1, 1));
        fuel.add(RailcraftItems.FIRESTONE_REFINED.getWildcard());
        fuel.add(RailcraftItems.FIRESTONE_CRACKED.getWildcard());
        fuel.add(OreDictPlugin.getOre("blockCharcoal", 1));
        for (ItemStack each : fuel) {
            fuels.add(createFuel(Ingredient.fromStacks(each), FuelPlugin.getBurnTime(each)));
        }
    }

    @Override
    public IBlastFurnaceFuel createFuel(Ingredient matcher, int cookTime) {
        return new IBlastFurnaceFuel() {
            @Override
            public Ingredient getInput() {
                return matcher;
            }

            @Override
            public int getCookTime() {
                return cookTime;
            }
        };
    }

    @Override
    public IBlastFurnaceRecipe createRecipe(Ingredient matcher, int cookTime, ItemStack output, ItemStack secondOutput) {
        return new IBlastFurnaceRecipe() {
            @Override
            public Ingredient getInput() {
                return matcher;
            }

            @Override
            public int getCookTime() {
                return cookTime;
            }

            @Override
            public ItemStack getOutput() {
                return output.copy();
            }

            @Override
            public ItemStack getSecondOutput() {
                return secondOutput.copy();
            }
        };
    }

    @Override
    public void addRecipe(IBlastFurnaceRecipe recipe) {
        if (!recipe.getInput().apply(ItemStack.EMPTY)) {
            recipes.add(recipe);
        } else {
            Game.logTrace(Level.ERROR, 10, "Tried to register an invalid blast furnace recipe");
        }
    }

    @Override
    public void addFuel(IBlastFurnaceFuel fuel) {
        if (!fuel.getInput().apply(ItemStack.EMPTY)) {
            fuels.add(fuel);
        } else {
            Game.logTrace(Level.ERROR, 10, "Tried to register an invalid blast furnace fuel");
        }
    }

    @Override
    public List<IBlastFurnaceRecipe> getRecipes() {
        return recipes;
    }

    @Override
    public List<IBlastFurnaceFuel> getFuels() {
        return fuels;
    }

    @Override
    public int getCookTime(ItemStack stack) {
        return fuels.stream()
                .filter(fuel -> fuel.getInput().test(stack))
                .findFirst()
                .map(IBlastFurnaceFuel::getCookTime)
                .orElse(0);
    }

    @Override
    public @Nullable IBlastFurnaceRecipe getRecipe(ItemStack stack) {
        return recipes.stream()
                .filter(recipe -> recipe.getInput().test(stack))
                .findFirst()
                .orElse(null);
    }
}
