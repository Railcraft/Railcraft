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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.simplemachine.SimpleMachineVariant;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by CovertJaguar on 10/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@JEIPlugin
public class RailcraftJEIPlugin extends BlankModPlugin {
    @Override
    public void register(@Nonnull IModRegistry registry) {
        ISubtypeRegistry subtypeRegistry = registry.getJeiHelpers().getSubtypeRegistry();
        Item trackOutfitted = RailcraftBlocks.TRACK_OUTFITTED.item();
        if (trackOutfitted != null)
            subtypeRegistry.registerNbtInterpreter(trackOutfitted, stack -> ((ItemTrackOutfitted) stack.getItem()).getSuffix(stack));

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registry.addRecipeCategories(new RollingMachineRecipeCategory(guiHelper));

        registry.addRecipeHandlers(new RollingMachineRecipeHandler(jeiHelpers));

        ItemStack rollingMachine = RailcraftBlocks.MACHINE_SIMPLE.getStack(SimpleMachineVariant.ROLLING_MACHINE);
        if (rollingMachine != null) {
            registry.addRecipeClickArea(GuiCrafting.class, 88, 32, 28, 23, RollingMachineRecipeCategory.CATEGORY_UID);

            registry.addRecipeCategoryCraftingItem(rollingMachine, RollingMachineRecipeCategory.CATEGORY_UID);

            registry.addRecipes(RollingMachineRecipeMaker.getRecipes(registry.getJeiHelpers()));
        }
    }
}
