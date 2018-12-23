/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.plugins.thaumcraft.ThaumcraftPlugin;
import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public enum BlastFurnaceCrafter implements IBlastFurnaceCrafter {
    INSTANCE;
    private final List<IRecipe> recipes = new ArrayList<>();
    private final List<IFuel> fuels = new ArrayList<>();

    public void postInit() {
        addFuel("thaumcraft:alumentum", ThaumcraftPlugin.ITEMS.get("alumentum", 0));
        addFuel("railcraft:blockCoke", EnumGeneric.BLOCK_COKE.getStack());
        addFuel("minecraft:charcoal", new ItemStack(Items.COAL, 1, 1));
        addFuel(RailcraftItems.FIRESTONE_REFINED);
        addFuel(RailcraftItems.FIRESTONE_CRACKED);
        addFuel("ore:blockCharcoal", OreDictPlugin.getOre("blockCharcoal", 1));
    }

    public void addFuel(IRailcraftObjectContainer<?> obj) {
        addFuel(obj.getRegistryName(), obj.getWildcard());
    }

    public void addFuel(String name, ItemStack stack) {
        addFuel(new ResourceLocation(name), stack);
    }

    @Override
    public void addFuel(@Nullable ResourceLocation name, ItemStack stack) {
        addFuel(name, stack, FuelPlugin.getBurnTime(stack));
    }

    @Override
    public void addFuel(@Nullable ResourceLocation name, Ingredient input, int cookTime) {
        Objects.requireNonNull(name);
        if (!input.apply(ItemStack.EMPTY)) {
            fuels.add(new IFuel() {
                @Override
                public ResourceLocation getName() {
                    return name;
                }

                @Override
                public Ingredient getInput() {
                    return input;
                }

                @Override
                public int getCookTime() {
                    return cookTime;
                }
            });
        } else {
            Game.log(Level.WARN, "Tried, but failed to register {0} as a blast furnace fuel", name);
        }
    }

    @Override
    public void addRecipe(@Nullable ResourceLocation name, Ingredient input, int cookTime, ItemStack output, int slagOutput) {
        Objects.requireNonNull(name);
        if (!input.apply(ItemStack.EMPTY)) {
            recipes.add(new IRecipe() {
                @Override
                public ResourceLocation getName() {
                    return name;
                }

                @Override
                public Ingredient getInput() {
                    return input;
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
                public int getSlagOutput() {
                    return slagOutput;
                }
            });
        } else {
            Game.log(Level.WARN, "Tried, but failed to register {0} as a blast furnace recipe", name);
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        return CollectionTools.removeOnlyList(recipes);
    }

    @Override
    public List<IFuel> getFuels() {
        return CollectionTools.removeOnlyList(fuels);
    }

    @Override
    public int getCookTime(ItemStack stack) {
        return fuels.stream()
                .filter(fuel -> fuel.getInput().test(stack))
                .findFirst()
                .map(IFuel::getCookTime)
                .orElse(0);
    }

    @Override
    public Optional<IRecipe> getRecipe(ItemStack stack) {
        return recipes.stream()
                .filter(recipe -> recipe.getInput().test(stack))
                .findFirst();
    }
}
