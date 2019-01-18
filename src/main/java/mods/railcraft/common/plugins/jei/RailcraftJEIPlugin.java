/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mods.railcraft.api.crafting.IRollingMachineCrafter;
import mods.railcraft.client.gui.*;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.equipment.EquipmentVariant;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;
import mods.railcraft.common.core.RailcraftObjects;
import mods.railcraft.common.gui.containers.*;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.jei.blastfurnace.BlastFurnaceMachineCategory;
import mods.railcraft.common.plugins.jei.blastfurnace.BlastFurnaceRecipeProvider;
import mods.railcraft.common.plugins.jei.cokeoven.CokeOvenCategory;
import mods.railcraft.common.plugins.jei.cokeoven.CokeOvenRecipeProvider;
import mods.railcraft.common.plugins.jei.crafting.FluidRecipeInterpreter;
import mods.railcraft.common.plugins.jei.rockcrusher.RockCrusherMachineCategory;
import mods.railcraft.common.plugins.jei.rockcrusher.RockCrusherRecipeProvider;
import mods.railcraft.common.plugins.jei.rolling.RollingMachineRecipeCategory;
import mods.railcraft.common.util.crafting.*;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    private final List<IRecipeCategory<?>> categories = new ArrayList<>();

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        FluidRecipeInterpreter.init(registry.getJeiHelpers().getStackHelper(), registry.getIngredientRegistry());

        registry.handleRecipes(CartDisassemblyRecipe.class, recipe ->
                        new DefaultRecipeWrapper(registry, recipe, "gui.railcraft.jei.recipe.split") {
                            @Override
                            public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
                                super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
                                IDrawable drawable = registry.getJeiHelpers().getGuiHelper().createDrawableIngredient(new ItemStack(Items.MINECART));
                                drawable.draw(minecraft, 65, 35);
                            }
                        },
                VanillaRecipeCategoryUid.CRAFTING);

        registry.handleRecipes(NBTCopyRecipe.class, recipe ->
                        new DefaultRecipeWrapper(registry, recipe, "gui.railcraft.jei.recipe.copy"),
                VanillaRecipeCategoryUid.CRAFTING);

        registry.handleRecipes(LocomotivePaintingRecipe.class, recipe ->
                        new DefaultRecipeWrapper(registry, recipe, "gui.railcraft.jei.recipe.paint"),
                VanillaRecipeCategoryUid.CRAFTING);

        registry.handleRecipes(FilterBeesGenomeRecipe.class, recipe ->
                        new DefaultRecipeWrapper(registry, recipe, "gui.railcraft.jei.recipe.setup"),
                VanillaRecipeCategoryUid.CRAFTING);

        registry.handleRecipes(RotorRepairRecipe.class, recipe ->
                        new DefaultRecipeWrapper(registry, recipe, "gui.railcraft.jei.recipe.repair")
                                .modifyInputs(stack -> {
                                    if (RailcraftItems.TURBINE_ROTOR.isEqual(stack))
                                        stack.setItemDamage(RotorRepairRecipe.REPAIR_PER_BLADE);
                                }),
                VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(IRollingMachineCrafter.IRollingRecipe.class,
                recipe -> new DefaultRecipeWrapper(registry, recipe),
                VanillaRecipeCategoryUid.CRAFTING); // Rolling machine recipe copying

        registry.handleRecipes(IRecipe.class, recipe ->
                new DefaultRecipeWrapper(registry, recipe), ROLLING);

        registry.addRecipes(CokeOvenRecipeProvider.get(registry).getRecipes(), COKE);
        registry.addRecipes(RollingMachineCrafter.INSTANCE.getValidRecipes(), ROLLING);
        registry.addRecipes(RockCrusherRecipeProvider.get(registry).getRecipes(), ROCK_CRUSHER);
        registry.addRecipes(BlastFurnaceRecipeProvider.get(registry).getRecipes(), BLAST_FURNACE);

        if (RailcraftBlocks.STEAM_OVEN.isLoaded())
            registry.addRecipeCatalyst(RailcraftBlocks.STEAM_OVEN.getStack(), VanillaRecipeCategoryUid.SMELTING);
        if (RailcraftBlocks.COKE_OVEN.isLoaded())
            registry.addRecipeCatalyst(RailcraftBlocks.COKE_OVEN.getStack(), COKE);
        if (RailcraftBlocks.COKE_OVEN_RED.isLoaded())
            registry.addRecipeCatalyst(RailcraftBlocks.COKE_OVEN_RED.getStack(), COKE);
        if (EquipmentVariant.ROLLING_MACHINE_MANUAL.isAvailable())
            registry.addRecipeCatalyst(EquipmentVariant.ROLLING_MACHINE_MANUAL.getStack(), ROLLING);
        if (EquipmentVariant.ROLLING_MACHINE_POWERED.isAvailable())
            registry.addRecipeCatalyst(EquipmentVariant.ROLLING_MACHINE_POWERED.getStack(), ROLLING);
        if (RailcraftBlocks.BLAST_FURNACE.isLoaded())
            registry.addRecipeCatalyst(RailcraftBlocks.BLAST_FURNACE.getStack(), BLAST_FURNACE);
        if (RailcraftBlocks.ROCK_CRUSHER.isLoaded())
            registry.addRecipeCatalyst(RailcraftBlocks.ROCK_CRUSHER.getStack(), ROCK_CRUSHER);

        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
        transferRegistry.addRecipeTransferHandler(ContainerSteamOven.class, VanillaRecipeCategoryUid.SMELTING, 0, 9, 9, 36);
        transferRegistry.addRecipeTransferHandler(ContainerRollingMachine.class, ROLLING, 2, 9, 11, 36);
        transferRegistry.addRecipeTransferHandler(ContainerRollingMachinePowered.class, ROLLING, 2, 9, 11, 36);
        transferRegistry.addRecipeTransferHandler(ContainerCokeOven.class, COKE, 0, 1, 4, 36);
        transferRegistry.addRecipeTransferHandler(ContainerRockCrusher.class, ROCK_CRUSHER, 0, 9, 17, 36);
        transferRegistry.addRecipeTransferHandler(ContainerBlastFurnace.class, BLAST_FURNACE, 0, 1, 4, 36);

        registry.addRecipeClickArea(GuiSteamOven.class, 65, 18, 23, 50, VanillaRecipeCategoryUid.SMELTING);
        registry.addRecipeClickArea(GuiRollingMachine.class, 90, 45, 23, 9, ROLLING);
        registry.addRecipeClickArea(GuiRollingMachinePowered.class, 90, 36, 23, 9, ROLLING);
        registry.addRecipeClickArea(GuiCokeOven.class, 34, 43, 20, 16, COKE);
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

        categories.add(new RollingMachineRecipeCategory(guiHelper));
        categories.add(new CokeOvenCategory(guiHelper));
        categories.add(new RockCrusherMachineCategory(guiHelper));
        categories.add(new BlastFurnaceMachineCategory(guiHelper));

        registry.addRecipeCategories(categories.toArray(new IRecipeCategory[0]));
    }

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

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeRegistry recipeRegistry = jeiRuntime.getRecipeRegistry();

        for (IRecipeCategory<?> category : categories) {
            if (recipeRegistry.getRecipeCatalysts(category).isEmpty()) {
                recipeRegistry.hideRecipeCategory(category.getUid());
            }
        }
    }

    private class DefaultRecipeWrapper implements IRecipeWrapper {
        private final IModRegistry registry;
        private final IRecipe recipe;
        private final @Nullable String locTag;
        private Consumer<ItemStack> stackModifier = stack -> {
        };

        public DefaultRecipeWrapper(IModRegistry registry, IRecipe recipe) {
            this(registry, recipe, null);
        }

        public DefaultRecipeWrapper(IModRegistry registry, IRecipe recipe, @Nullable String locTag) {
            this.registry = registry;
            this.recipe = recipe;
            this.locTag = locTag;
        }

        DefaultRecipeWrapper modifyInputs(Consumer<ItemStack> stackModifier) {
            this.stackModifier = stackModifier;
            return this;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputLists = registry.getJeiHelpers().getStackHelper()
                    .expandRecipeItemStackInputs(recipe.getIngredients());
            inputLists.forEach(l -> l.forEach(stackModifier));
            ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            if (locTag != null)
                GuiTools.drawStringCenteredAtPos(minecraft.fontRenderer, LocalizationPlugin.translate(locTag), 82, 0);
        }
    }
}