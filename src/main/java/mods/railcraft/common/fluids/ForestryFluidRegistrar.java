/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fml.common.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ForestryFluidRegistrar extends FluidRegistrar {
    public static ForestryFluidRegistrar INSTANCE = new ForestryFluidRegistrar();

    private ForestryFluidRegistrar() {
    }

    @Override
    @Optional.Method(modid = "forestry")
    public void registerContainer(FluidContainerData container) {
        ItemStack recycle;
        int chance;
        if (InvTools.isItemEqual(container.emptyContainer, ModItems.CELL_EMPTY.get())) {
            recycle = ModItems.INGOT_TIN.get();
            chance = 5;
        } else if (InvTools.isItemEqual(container.emptyContainer, ModItems.CAN_EMPTY.get())) {
            recycle = ModItems.INGOT_TIN.get();
            chance = 5;
        } else if (InvTools.isItemEqual(container.emptyContainer, ModItems.WAX_CAPSULE.get())) {
            recycle = ModItems.BEESWAX.get();
            chance = 10;
        } else if (InvTools.isItemEqual(container.emptyContainer, ModItems.REFRACTORY_EMPTY.get())) {
            recycle = ModItems.REFRACTORY_WAX.get();
            chance = 10;
        } else if (container.emptyContainer.getItem() == Items.GLASS_BOTTLE) {
            recycle = new ItemStack(Blocks.GLASS);
            chance = 10;
        } else
            return;

        if (forestry.api.recipes.RecipeManagers.squeezerManager != null && container.emptyContainer.getItem() != Items.BUCKET)
            if (recycle != null)
                forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{container.filledContainer}, container.fluid, recycle, chance);
            else
                forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{container.filledContainer}, container.fluid);
    }
}
