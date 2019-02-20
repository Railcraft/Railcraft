/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.BlockGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.metals.BlockMetal;
import mods.railcraft.common.blocks.aesthetics.metals.EnumMetal;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

@RailcraftModule(value = "railcraft:resources", description = "metals, fluids, raw materials")
public class ModuleResources extends RailcraftModulePayload {

    private static ModuleResources instance;
    boolean bottleFree;

    public ModuleResources() {
        instance = this;
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.METAL,
                        RailcraftItems.BOTTLE_CREOSOTE,
                        RailcraftItems.BOTTLE_STEAM,
                        RailcraftItems.NUGGET,
                        RailcraftItems.INGOT,
                        RailcraftItems.GEAR,
                        RailcraftItems.PLATE,
                        RailcraftItems.DUST
                );
            }

            @Override
            public void init() {
                if (Fluids.CREOSOTE.isPresent() && RailcraftConfig.creosoteTorchOutput() > 0) {
                    FluidStack creosote = Fluids.CREOSOTE.get(FluidTools.BUCKET_VOLUME);
                    CraftingPlugin.addShapedRecipe(new ItemStack(Blocks.TORCH, RailcraftConfig.creosoteTorchOutput()),
                            "C",
                            "W",
                            "S",
                            'C', creosote,
                            'W', Blocks.WOOL,
                            'S', "stickWood");
                }

                if (RailcraftBlocks.METAL.isLoaded()) {
                    EnumMetal type = EnumMetal.BLOCK_STEEL;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag())) {
                        initMetalBlock(Metal.STEEL);
                        Crafters.blastFurnace().newRecipe(Blocks.IRON_BLOCK)
                                .name("railcraft:smelt_block")
                                .time(IBlastFurnaceCrafter.SMELT_TIME * 9)
                                .output(EnumMetal.BLOCK_STEEL.getStack())
                                .slagOutput(9)
                                .register();
                    }

                    type = EnumMetal.BLOCK_COPPER;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.COPPER);

                    type = EnumMetal.BLOCK_TIN;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.TIN);

                    type = EnumMetal.BLOCK_LEAD;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.LEAD);

                    type = EnumMetal.BLOCK_SILVER;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.SILVER);

                    type = EnumMetal.BLOCK_BRONZE;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.BRONZE);
                    if ((RailcraftConfig.forceEnableBronzeRecipe() || !OreDictPlugin.oreExists("dustBronze")) && RailcraftItems.INGOT.isEnabled()) {
                        CraftingPlugin.addShapelessRecipe(Metal.BRONZE.getStack(Metal.Form.INGOT, RailcraftConfig.enableHarderBronze() ? 3 : 4), "ingotTin", "ingotCopper", "ingotCopper", "ingotCopper");
                    }

                    type = EnumMetal.BLOCK_NICKEL;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.NICKEL);

                    type = EnumMetal.BLOCK_INVAR;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.INVAR);
                    if ((RailcraftConfig.forceEnableInvarRecipe() || !OreDictPlugin.oreExists("dustInvar")) && RailcraftItems.INGOT.isEnabled()) {
                        CraftingPlugin.addShapelessRecipe(Metal.INVAR.getStack(Metal.Form.INGOT, 3), Items.IRON_INGOT, Items.IRON_INGOT, "ingotNickel");
                    }

                    type = EnumMetal.BLOCK_ZINC;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.ZINC);

                    type = EnumMetal.BLOCK_BRASS;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                        initMetalBlock(Metal.BRASS);
                    if ((RailcraftConfig.forceEnableBrassRecipe() || !OreDictPlugin.oreExists("dustBrass")) && RailcraftItems.INGOT.isEnabled()) {
                        CraftingPlugin.addShapelessRecipe(Metal.BRASS.getStack(Metal.Form.INGOT, RailcraftConfig.enableHarderBrass() ? 3 : 4), "ingotZinc", "ingotCopper", "ingotCopper", "ingotCopper");
                    }
                }

                bottleFree = RailcraftConfig.useCreosoteFurnaceRecipes() || !RailcraftBlocks.COKE_OVEN.isLoaded();
                if (bottleFree) {
                    CraftingPlugin.addFurnaceRecipe(new ItemStack(Items.COAL, 1, 0), RailcraftItems.BOTTLE_CREOSOTE.getStack(2), 0.0F);
                    CraftingPlugin.addFurnaceRecipe(new ItemStack(Items.COAL, 1, 1), RailcraftItems.BOTTLE_CREOSOTE.getStack(1), 0.0F);
                }

                if (RailcraftBlocks.GENERIC.isLoaded() && RailcraftConfig.isSubBlockEnabled(EnumGeneric.CRUSHED_OBSIDIAN.getTag())) {
                    ItemStack stack = EnumGeneric.CRUSHED_OBSIDIAN.getStack();

                    BallastRegistry.registerBallast(BlockGeneric.getBlock(), EnumGeneric.CRUSHED_OBSIDIAN.ordinal());

                    if (Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC) && RailcraftItems.DUST.isEnabled()) {
                        ItemStack obsidian = new ItemStack(Blocks.OBSIDIAN);
                        final boolean crushedObsidian = RailcraftConfig.getRecipeConfig("ic2.macerator.crushed.obsidian");
                        if (crushedObsidian || !RailcraftConfig.getRecipeConfig("ic2.macerator.obsidian")) {
                            IC2Plugin.removeMaceratorRecipes(recipe -> recipe.getInput().matches(obsidian) && OreDictPlugin.hasOreType("dustObsidian", recipe.getOutput()));
                        }
                        if (crushedObsidian) {
                            IC2Plugin.addMaceratorRecipe(obsidian, stack);
                            IC2Plugin.addMaceratorRecipe(stack, RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN));
                        }
                    }
                }
                checkSteelBlock();
            }

            private void initMetalBlock(Metal m) {
                String blockTag = m.getOreTag(Metal.Form.BLOCK);
                OreDictionary.registerOre(blockTag, m.getStack(Metal.Form.BLOCK));
                CraftingPlugin.addShapedRecipe(m.getStack(Metal.Form.BLOCK),
                        "III",
                        "III",
                        "III",
                        'I', m.getOreTag(Metal.Form.INGOT));
                CraftingPlugin.addShapelessRecipe(m.getStack(Metal.Form.INGOT, 9), blockTag);
            }
        });
        setDisabledEventHandler(new ModuleEventHandler() {
            @Override
            public void postInit() {
                checkSteelBlock();
            }
        });
    }

    private void checkSteelBlock() {
        if (OreDictionary.getOres("blockSteel").isEmpty())
            OreDictionary.registerOre("blockSteel", Blocks.IRON_BLOCK);
    }

    public static ModuleResources getInstance() {
        return instance;
    }

    public boolean isBottleFree() {
        return bottleFree;
    }
}
