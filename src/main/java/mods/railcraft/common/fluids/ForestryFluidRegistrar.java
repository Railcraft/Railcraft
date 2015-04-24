/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import cpw.mods.fml.common.Optional;
import mods.railcraft.common.items.ModItems;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ForestryFluidRegistrar extends FluidRegistrar {
    public static ForestryFluidRegistrar INSTANCE = new ForestryFluidRegistrar();

    private ForestryFluidRegistrar() {
    }

    @Override
    @Optional.Method(modid = "Forestry")
    public void registerContainer(FluidContainerData container) {
        ItemStack recycle;
        int chance;
        if (InvTools.isItemEqual(container.emptyContainer, ModItems.cellEmpty.get())) {
            recycle = ModItems.ingotTin.get();
            chance = 5;
        } else if (InvTools.isItemEqual(container.emptyContainer, ModItems.canEmpty.get())) {
            recycle = ModItems.ingotTin.get();
            chance = 5;
        } else if (InvTools.isItemEqual(container.emptyContainer, ModItems.waxCapsule.get())) {
            recycle = ModItems.beeswax.get();
            chance = 10;
        } else if (InvTools.isItemEqual(container.emptyContainer, ModItems.refractoryEmpty.get())) {
            recycle = ModItems.refractoryWax.get();
            chance = 10;
        } else if (container.emptyContainer.getItem() == Items.glass_bottle) {
            recycle = new ItemStack(Blocks.glass);
            chance = 10;
        } else
            return;

        if (forestry.api.recipes.RecipeManagers.squeezerManager != null && container.emptyContainer.getItem() != Items.bucket)
            if (recycle != null)
                forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{container.filledContainer}, container.fluid, recycle, chance);
            else
                forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{container.filledContainer}, container.fluid);
    }
}
