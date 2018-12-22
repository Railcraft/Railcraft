/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mods.railcraft.client.gui.GuiBlastFurnace;
import mods.railcraft.client.gui.GuiRockCrusher;
import mods.railcraft.client.gui.GuiRollingMachine;
import mods.railcraft.client.gui.GuiRollingMachinePowered;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.equipment.EquipmentVariant;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;
import mods.railcraft.common.core.RailcraftObjects;
import mods.railcraft.common.gui.containers.ContainerBlastFurnace;
import mods.railcraft.common.gui.containers.ContainerRockCrusher;
import mods.railcraft.common.gui.containers.ContainerRollingMachine;
import mods.railcraft.common.gui.containers.ContainerRollingMachinePowered;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.blastfurnace.BlastFurnaceMachineCategory;
import mods.railcraft.common.plugins.jei.blastfurnace.BlastFurnaceRecipeMaker;
import mods.railcraft.common.plugins.jei.cokeoven.CokeOvenCategory;
import mods.railcraft.common.plugins.jei.cokeoven.CokeOvenRecipeMaker;
import mods.railcraft.common.plugins.jei.crafting.FluidRecipeInterpreter;
import mods.railcraft.common.plugins.jei.crafting.ShapedFluidRecipeWrapper;
import mods.railcraft.common.plugins.jei.crafting.ShapelessFluidRecipeWrapper;
import mods.railcraft.common.plugins.jei.rockcrusher.RockCrusherMachineCategory;
import mods.railcraft.common.plugins.jei.rockcrusher.RockCrusherMachineRecipeMaker;
import mods.railcraft.common.plugins.jei.rolling.RollingMachineRecipeCategory;
import mods.railcraft.common.plugins.jei.rolling.RollingMachineRecipeMaker;
import mods.railcraft.common.util.crafting.ShapedFluidRecipe;
import mods.railcraft.common.util.crafting.ShapelessFluidRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 10/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@JEIPlugin
public class RailcraftJEIPlugin implements IModPlugin {
    public static final String ROLLING = "railcraft.rolling";
    public static final String BLAST_FURNACE = "railcraft.blast.furnace";
    public static final String ROCK_CRUSHER = "railcraft.rock.crusher";
    public static final String COKE = "railcraft.coke";

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        FluidRecipeInterpreter.init(registry.getJeiHelpers().getStackHelper(), registry.getIngredientRegistry());
        registry.handleRecipes(ShapedFluidRecipe.class, ShapedFluidRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapelessFluidRecipe.class, ShapelessFluidRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);

        registry.addRecipes(CokeOvenRecipeMaker.getCokeOvenRecipe(registry), COKE);
        registry.addRecipes(RollingMachineRecipeMaker.getRecipes(registry.getJeiHelpers()), ROLLING);
        registry.addRecipes(RockCrusherMachineRecipeMaker.getRecipes(registry.getJeiHelpers()), ROCK_CRUSHER);
        registry.addRecipes(BlastFurnaceRecipeMaker.getRecipes(registry.getJeiHelpers()), BLAST_FURNACE);

        registry.addRecipeCatalyst(RailcraftBlocks.STEAM_OVEN.getStack(), VanillaRecipeCategoryUid.SMELTING);
        registry.addRecipeCatalyst(RailcraftBlocks.COKE_OVEN.getStack(), COKE);
        registry.addRecipeCatalyst(EquipmentVariant.ROLLING_MACHINE_MANUAL.getStack(), ROLLING);
        registry.addRecipeCatalyst(EquipmentVariant.ROLLING_MACHINE_POWERED.getStack(), ROLLING);
        registry.addRecipeCatalyst(RailcraftBlocks.BLAST_FURNACE.getStack(), BLAST_FURNACE);
        registry.addRecipeCatalyst(RailcraftBlocks.ROCK_CRUSHER.getStack(), ROCK_CRUSHER);

        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
        transferRegistry.addRecipeTransferHandler(ContainerRollingMachine.class, ROLLING, 2, 9, 11, 36);
        transferRegistry.addRecipeTransferHandler(ContainerRollingMachinePowered.class, ROLLING, 2, 9, 11, 36);
        transferRegistry.addRecipeTransferHandler(ContainerRockCrusher.class, ROCK_CRUSHER, 0, 9, 17, 36);
        transferRegistry.addRecipeTransferHandler(ContainerBlastFurnace.class, BLAST_FURNACE, 0, 1, 4, 36);

        registry.addRecipeClickArea(GuiRollingMachine.class, 90, 45, 23, 9, ROLLING);
        registry.addRecipeClickArea(GuiRollingMachinePowered.class, 90, 36, 23, 9, ROLLING);
        registry.addRecipeClickArea(GuiRockCrusher.class, 73, 20, 30, 38, ROCK_CRUSHER);
        registry.addRecipeClickArea(GuiBlastFurnace.class, 80, 36, 22, 15, BLAST_FURNACE);

        if (RailcraftItems.CROWBAR_SEASONS.isLoaded())
            jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(RailcraftItems.CROWBAR_SEASONS.getStack());
        if (RailcraftItems.BLEACHED_CLAY.isLoaded())
            jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(RailcraftItems.BLEACHED_CLAY.getStack());

        RailcraftObjects.processBlockVariants((block, variant) -> addDescription(registry, block.getStack(variant)));
        RailcraftObjects.processItemVariants((item, variant) -> addDescription(registry, item.getStack(variant)));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new RollingMachineRecipeCategory(guiHelper));
        registry.addRecipeCategories(new CokeOvenCategory(guiHelper));
        registry.addRecipeCategories(new RockCrusherMachineCategory(guiHelper));
        registry.addRecipeCategories(new BlastFurnaceMachineCategory(guiHelper));
    }

//    @Override
//    public void register(IModRegistry registry) {
//        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
//        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
//        FluidRecipeInterpreter.init(jeiHelpers.getStackHelper(), registry.getIngredientRegistry());
//        registry.handleRecipes();
//        addRecipeHandlers(new RollingMachineRecipeHandler(jeiHelpers));
//        registry.addRecipeHandlers(new ShapedFluidRecipeHandler());
//        registry.addRecipeHandlers(new ShapelessFluidRecipeHandler(jeiHelpers));
//
//        registry.addRecipeClickArea(GuiRollingMachine.class, 90, 45, 23, 9, ROLLING);
//        registry.addRecipeClickArea(GuiRollingMachinePowered.class, 90, 36, 23, 9, ROLLING);
//
//        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
//        recipeTransferRegistry.addRecipeTransferHandler(ContainerRollingMachine.class, ROLLING, 2, 9, 11, 36);
//        recipeTransferRegistry.addRecipeTransferHandler(ContainerRollingMachinePowered.class, ROLLING, 2, 9, 11, 36);
//
//        boolean rolling = false;
//        ItemStack stack = RailcraftBlocks.EQUIPMENT.getStack(EquipmentVariant.ROLLING_MACHINE_MANUAL);
//        if (!InvTools.isEmpty(stack)) {
//            registry.addRecipeCatalyst(stack, ROLLING);
//            rolling = true;
//        }
//        stack = RailcraftBlocks.EQUIPMENT.getStack(EquipmentVariant.ROLLING_MACHINE_POWERED);
//        if (!InvTools.isEmpty(stack)) {
//            registry.addRecipeCatalyst(stack, ROLLING);
//            rolling = true;
//        }
//
//        if (rolling)
//            registry.addRecipes(RollingMachineRecipeMaker.recipes(registry.getJeiHelpers()));
//
//    }

    private void addDescription(IModRegistry registry, ItemStack stack) {
        if (!InvTools.isEmpty(stack)) {
            String locTag = stack.getTranslationKey() + ".desc";
            if (LocalizationPlugin.hasTag(locTag))
                registry.addIngredientInfo(stack, VanillaTypes.ITEM, locTag);
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        Item trackOutfitted = RailcraftBlocks.TRACK_OUTFITTED.item();
        if (trackOutfitted != null)
            subtypeRegistry.registerSubtypeInterpreter(trackOutfitted, stack -> ((ItemTrackOutfitted) stack.getItem()).getSuffix(stack));
    }
}