/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mods.railcraft.client.gui.GuiRollingMachine;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.simplemachine.SimpleMachineVariant;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;
import mods.railcraft.common.core.RailcraftObjects;
import mods.railcraft.common.gui.containers.ContainerRollingMachine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.rolling.RollingMachineRecipeCategory;
import mods.railcraft.common.plugins.jei.rolling.RollingMachineRecipeHandler;
import mods.railcraft.common.plugins.jei.rolling.RollingMachineRecipeMaker;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 10/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@JEIPlugin
public class RailcraftJEIPlugin extends BlankModPlugin {
    public static final String ROLLING = "railcraft.rolling";

    @Override
    public void register(IModRegistry registry) {
        ISubtypeRegistry subtypeRegistry = registry.getJeiHelpers().getSubtypeRegistry();
        Item trackOutfitted = RailcraftBlocks.TRACK_OUTFITTED.item();
        if (trackOutfitted != null)
            subtypeRegistry.registerNbtInterpreter(trackOutfitted, stack -> ((ItemTrackOutfitted) stack.getItem()).getSuffix(stack));

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registry.addRecipeCategories(new RollingMachineRecipeCategory(guiHelper));

        registry.addRecipeHandlers(new RollingMachineRecipeHandler(jeiHelpers));

        registry.addRecipeClickArea(GuiRollingMachine.class, 90, 45, 23, 9, ROLLING);

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(ContainerRollingMachine.class, ROLLING, 2, 9, 11, 36);

        ItemStack rollingMachine = RailcraftBlocks.MACHINE_SIMPLE.getStack(SimpleMachineVariant.ROLLING_MACHINE);
        if (rollingMachine != null) {
            registry.addRecipeCategoryCraftingItem(rollingMachine, ROLLING);
            registry.addRecipes(RollingMachineRecipeMaker.getRecipes(registry.getJeiHelpers()));
        }

        RailcraftObjects.processBlockVariants((block, variant) -> addDescription(registry, block.getStack(variant)));
        RailcraftObjects.processItemVariants((item, variant) -> addDescription(registry, item.getStack(variant)));
    }

    private void addDescription(IModRegistry registry, @Nullable ItemStack stack) {
        if (stack != null) {
            String locTag = stack.getUnlocalizedName() + ".desc";
            if (LocalizationPlugin.hasTag(locTag))
                registry.addDescription(stack, locTag);
        }
    }
}
