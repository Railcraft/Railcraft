/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.post.BlockPost;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockFactoryLantern;
import mods.railcraft.common.blocks.aesthetics.slab.BlockFactorySlab;
import mods.railcraft.common.blocks.aesthetics.stairs.BlockFactoryStairs;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModuleStructures extends RailcraftModule {
    @Override
    public void preInit() {
        addBlockFactory(new BlockFactoryStairs());
        addBlockFactory(new BlockFactorySlab());
        addBlockFactory(new BlockFactoryLantern());
        for (EnumBrick brick : EnumBrick.VALUES) {
            addBlockFactory(brick.makeFactory());
        }
    }

    @Override
    public void initFirst() {
        RailcraftBlocks.registerBlockSignal();
        BlockPost.registerBlock();
        BlockPostMetal.registerPost();
        BlockPostMetal.registerPlatform();
        BlockRailcraftWall.registerBlocks();
        BlockStrengthGlass.registerBlock();

        EnumCube cubeType = EnumCube.CONCRETE_BLOCK;
        if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
            BlockCube.registerBlock();
            Block cube = BlockCube.getBlock();
            if (cube != null) {
                ItemStack stack = cubeType.getItem();
                if (ModuleManager.isModuleLoaded(Module.FACTORY)
                        && RailcraftBlocks.getBlockMachineAlpha() != null
                        && RailcraftConfig.isSubBlockEnabled(EnumMachineAlpha.ROLLING_MACHINE.getTag())) {
                    stack.stackSize = 8;
                    CraftingPlugin.addShapedRecipe(stack,
                            "SIS",
                            "ISI",
                            "SIS",
                            'I', RailcraftItem.rebar.getRecipeObject(),
                            'S', "stone");
                } else {
                    stack.stackSize = 4;
                    CraftingPlugin.addShapedRecipe(stack,
                            " S ",
                            "SIS",
                            " S ",
                            'I', "ingotIron",
                            'S', "stone");
                }
            }
        }

        cubeType = EnumCube.CREOSOTE_BLOCK;
        if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
            BlockCube.registerBlock();
            Block cube = BlockCube.getBlock();
            if (cube != null) {
                ItemStack stack = cubeType.getItem();
                for (ItemStack container : FluidHelper.getContainersFilledWith(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME))) {
                    CraftingPlugin.addShapelessRecipe(stack, "logWood", container);
                }
            }
        }

        RailcraftBlocks.registerBlockMachineAlpha();
        EnumMachineAlpha alpha = EnumMachineAlpha.SMOKER;
        if (RailcraftConfig.isSubBlockEnabled(alpha.getTag())) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    " N ",
                    "RCR",
                    'N', new ItemStack(Blocks.netherrack),
                    'C', new ItemStack(Items.cauldron),
                    'R', "dustRedstone");
        }

//        cubeType = EnumCube.BANDED_PLANKS;
//        if(RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
//            RailcraftBlocks.registerBlockCube();
//            Block cube = RailcraftBlocks.getBlockCube();
//            if(cube != null) {
//                ItemStack stack = cubeType.getItem(8);
//                RailcraftLanguage.getInstance().registerItemName(stack, cubeType.getTag());
//                ModLoader.addRecipe(stack, new Object[]{
//                        "WWW",
//                        "III",
//                        "WWW",
//                        'I', new ItemStack(Items.iron_ingot),
//                        'W', Block.planks});
//            }
//        }
        if (BlockStrengthGlass.getBlock() != null)
            for (EnumColor color : EnumColor.VALUES) {
                CraftingPlugin.addShapedRecipe(BlockStrengthGlass.getItem(8, color.inverse().ordinal()),
                        "GGG",
                        "GDG",
                        "GGG",
                        'G', BlockStrengthGlass.getBlock(),
                        'D', color.getDye());
            }
    }

    @Override
    public void initSecond() {
        BlockRailcraftWall.initialize();

        Block blockPost = BlockPost.block;
        if (blockPost != null) {
            CraftingPlugin.addShapelessRecipe(EnumPost.WOOD.getItem(4), RailcraftItem.tie.getRecipeObject(ItemTie.EnumTie.WOOD));
            CraftingPlugin.addShapedRecipe(EnumPost.WOOD_PLATFORM.getItem(),
                    " T ",
                    " I ",
                    'T', BlockRailcraftSlab.getItem(EnumBlockMaterial.CREOSOTE),
                    'I', EnumPost.WOOD.getItem());

            CraftingPlugin.addShapedRecipe(EnumPost.STONE.getItem(8),
                    "SIS",
                    "SIS",
                    "SIS",
                    'I', RailcraftItem.rebar.getRecipeObject(),
                    'S', "stone");
            CraftingPlugin.addShapedRecipe(EnumPost.STONE_PLATFORM.getItem(),
                    " T ",
                    " I ",
                    'T', BlockRailcraftSlab.getItem(EnumBlockMaterial.CONCRETE),
                    'I', EnumPost.STONE.getItem());

            ItemStack stack = EnumPost.METAL_UNPAINTED.getItem(16);

            IRecipe recipe = new ShapedOreRecipe(stack,
                    "III",
                    " I ",
                    "III",
                    'I', "ingotIron");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

            recipe = new ShapedOreRecipe(stack,
                    "I I",
                    "III",
                    "I I",
                    'I', "ingotIron");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

            CraftingPlugin.addShapedRecipe(EnumPost.METAL_PLATFORM_UNPAINTED.getItem(4),
                    " T ",
                    " I ",
                    'T', BlockRailcraftSlab.getItem(EnumBlockMaterial.IRON),
                    'I', EnumPost.METAL_UNPAINTED.getItem());

            stack = EnumPost.METAL_UNPAINTED.getItem(32);
            recipe = new ShapedOreRecipe(stack,
                    "III",
                    " I ",
                    "III",
                    'I', "ingotSteel");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

            recipe = new ShapedOreRecipe(stack,
                    "I I",
                    "III",
                    "I I",
                    'I', "ingotSteel");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

            stack = EnumPost.METAL_UNPAINTED.getItem(12);
            recipe = new ShapedOreRecipe(stack,
                    "III",
                    " I ",
                    "III",
                    'I', "ingotBronze");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);
            recipe = new ShapedOreRecipe(stack,
                    "I I",
                    "III",
                    "I I",
                    'I', "ingotBronze");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);

            stack = EnumPost.METAL_UNPAINTED.getItem(20);
            recipe = new ShapedOreRecipe(stack,
                    "III",
                    " I ",
                    "III",
                    'I', "ingotRefinedIron");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);
            recipe = new ShapedOreRecipe(stack,
                    "I I",
                    "III",
                    "I I",
                    'I', "ingotRefinedIron");
            RollingMachineCraftingManager.getInstance().getRecipeList().add(recipe);
        }

        if (blockPost != null && BlockPostMetal.post != null) {
            ItemStack stackColored = BlockPostMetal.post.getItem(1, OreDictionary.WILDCARD_VALUE);
            ItemStack stackRaw = EnumPost.METAL_UNPAINTED.getItem();

            for (EnumColor color : EnumColor.values()) {
                ItemStack outputStack = new ItemStack(BlockPostMetal.post, 8, color.ordinal());
                CraftingPlugin.addShapedRecipe(outputStack,
                        "III",
                        "IDI",
                        "III",
                        'I', stackRaw,
                        'D', color.getDye());
                CraftingPlugin.addShapedRecipe(outputStack,
                        "III",
                        "IDI",
                        "III",
                        'I', stackColored,
                        'D', color.getDye());
            }
        }

        if (BlockPostMetal.post != null && BlockPostMetal.platform != null) {
            ItemStack stackColored = BlockPostMetal.platform.getItem(1, OreDictionary.WILDCARD_VALUE);
            ItemStack stackRaw = EnumPost.METAL_PLATFORM_UNPAINTED.getItem();

            for (EnumColor color : EnumColor.values()) {
                ItemStack outputStack = new ItemStack(BlockPostMetal.platform, 8, color.ordinal());
                CraftingPlugin.addShapedRecipe(outputStack,
                        "III",
                        "IDI",
                        "III",
                        'I', stackRaw,
                        'D', color.getDye());
                CraftingPlugin.addShapedRecipe(outputStack,
                        "III",
                        "IDI",
                        "III",
                        'I', stackColored,
                        'D', color.getDye());
            }
        }
    }

    @Override
    public void postInit() {
        if (BlockStrengthGlass.getBlock() != null) {
            Object[] frameTypes = new Object[]{"ingotTin", Items.iron_ingot};
            FluidStack water = Fluids.WATER.get(FluidHelper.BUCKET_VOLUME);
            for (ItemStack container : FluidHelper.getContainersFilledWith(water)) {
                for (Object frame : frameTypes) {
                    CraftingPlugin.addShapedRecipe(BlockStrengthGlass.getItem(6, 0),
                            "GFG",
                            "GSG",
                            "GWG",
                            'G', "blockGlassColorless",
                            'F', frame,
                            'S', "dustSaltpeter",
                            'W', container);
                }
            }
        }

        EnumCube cubeType = EnumCube.CREOSOTE_BLOCK;
        if (cubeType.isEnabled()) {
            ItemStack stack = cubeType.getItem();
            for (ItemStack container : FluidHelper.getContainersFilledWith(Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME))) {
                CraftingPlugin.addShapelessRecipe(stack, "logWood", container);
            }
            ForestryPlugin.instance().addCarpenterRecipe("creosote.block", 40, Fluids.CREOSOTE.get(750), null, stack, "L", 'L', "logWood");
        }
    }
}
