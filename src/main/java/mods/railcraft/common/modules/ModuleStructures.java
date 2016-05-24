/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockFactoryLantern;
import mods.railcraft.common.blocks.aesthetics.post.BlockPost;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.aesthetics.slab.BlockFactorySlab;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.stairs.BlockFactoryStairs;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

@RailcraftModule("structures")
public class ModuleStructures extends RailcraftModulePayload {

    public ModuleStructures() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                addBlockFactory(new BlockFactoryStairs());
                addBlockFactory(new BlockFactorySlab());
                addBlockFactory(new BlockFactoryLantern());
                for (BrickTheme brick : BrickTheme.VALUES) {
                    addBlockFactory(brick.makeFactory());
                }
                add(
                        RailcraftBlocks.signal,
                        RailcraftBlocks.machine_alpha
                );
            }

            @Override
            public void preInit() {
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
                        if (EnumMachineAlpha.ROLLING_MACHINE.isAvailable() && RailcraftItems.rebar.isEnabled()) {
                            stack.stackSize = 8;
                            CraftingPlugin.addRecipe(stack,
                                    "SIS",
                                    "ISI",
                                    "SIS",
                                    'I', RailcraftItems.rebar.getRecipeObject(),
                                    'S', "stone");
                        } else {
                            stack.stackSize = 4;
                            CraftingPlugin.addRecipe(stack,
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

                EnumMachineAlpha alpha = EnumMachineAlpha.SMOKER;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem();
                    CraftingPlugin.addRecipe(stack,
                            " N ",
                            "RCR",
                            'N', new ItemStack(Blocks.netherrack),
                            'C', new ItemStack(Items.CAULDRON),
                            'R', "dustRedstone");
                }

//        cubeType = EnumCube.BANDED_PLANKS;
//        if(RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
//            RailcraftBlocksOld.registerBlockCube();
//            Block cube = RailcraftBlocksOld.getBlockCube();
//            if(cube != null) {
//                ItemStack stack = cubeType.getItem(8);
//                RailcraftLanguage.instance().registerItemName(stack, cubeType.getTag());
//                ModLoader.addRecipe(stack, new Object[]{
//                        "WWW",
//                        "III",
//                        "WWW",
//                        'I', new ItemStack(Items.IRON_INGOT),
//                        'W', Block.planks});
//            }
//        }
                if (BlockStrengthGlass.getBlock() != null)
                    for (EnumColor color : EnumColor.VALUES) {
                        CraftingPlugin.addRecipe(BlockStrengthGlass.getItem(8, color.inverse().ordinal()),
                                "GGG",
                                "GDG",
                                "GGG",
                                'G', BlockStrengthGlass.getBlock(),
                                'D', color.getDyeOreDictTag());
                    }
            }

            @Override
            public void init() {
                BlockRailcraftWall.initialize();

                Block blockPost = BlockPost.getBlock();
                if (blockPost != null) {
                    CraftingPlugin.addShapelessRecipe(EnumPost.WOOD.getItem(4), RailcraftItems.tie.getRecipeObject(ItemTie.EnumTie.WOOD));
                    CraftingPlugin.addRecipe(EnumPost.WOOD_PLATFORM.getItem(),
                            " T ",
                            " I ",
                            'T', BlockRailcraftSlab.getItem(BlockMaterial.CREOSOTE),
                            'I', EnumPost.WOOD.getItem());

                    CraftingPlugin.addRecipe(EnumPost.STONE.getItem(8),
                            "SIS",
                            "SIS",
                            "SIS",
                            'I', RailcraftItems.rebar.getRecipeObject(),
                            'S', "stone");
                    CraftingPlugin.addRecipe(EnumPost.STONE_PLATFORM.getItem(),
                            " T ",
                            " I ",
                            'T', BlockRailcraftSlab.getItem(BlockMaterial.CONCRETE),
                            'I', EnumPost.STONE.getItem());

                    ItemStack stack = EnumPost.METAL_UNPAINTED.getItem(16);

                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "III",
                            " I ",
                            "III",
                            'I', "ingotIron");

                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotIron");

                    CraftingPlugin.addRecipe(EnumPost.METAL_PLATFORM_UNPAINTED.getItem(4),
                            " T ",
                            " I ",
                            'T', BlockRailcraftSlab.getItem(BlockMaterial.IRON),
                            'I', EnumPost.METAL_UNPAINTED.getItem());

                    stack = EnumPost.METAL_UNPAINTED.getItem(32);
                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "III",
                            " I ",
                            "III",
                            'I', "ingotSteel");

                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotSteel");

                    stack = EnumPost.METAL_UNPAINTED.getItem(12);
                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "III",
                            " I ",
                            "III",
                            'I', "ingotBronze");
                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotBronze");

                    stack = EnumPost.METAL_UNPAINTED.getItem(20);
                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "III",
                            " I ",
                            "III",
                            'I', "ingotRefinedIron");
                    RailcraftCraftingManager.rollingMachine.addRecipe(stack,
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotRefinedIron");
                }

                if (blockPost != null && BlockPostMetal.post != null) {
                    ItemStack stackColored = BlockPostMetal.post.getItem(1, OreDictionary.WILDCARD_VALUE);
                    ItemStack stackRaw = EnumPost.METAL_UNPAINTED.getItem();

                    for (EnumColor color : EnumColor.values()) {
                        ItemStack outputStack = new ItemStack(BlockPostMetal.post, 8, color.ordinal());
                        CraftingPlugin.addRecipe(outputStack,
                                "III",
                                "IDI",
                                "III",
                                'I', stackRaw,
                                'D', color.getDyeOreDictTag());
                        CraftingPlugin.addRecipe(outputStack,
                                "III",
                                "IDI",
                                "III",
                                'I', stackColored,
                                'D', color.getDyeOreDictTag());
                    }
                }

                if (BlockPostMetal.post != null && BlockPostMetal.platform != null) {
                    ItemStack stackColored = BlockPostMetal.platform.getItem(1, OreDictionary.WILDCARD_VALUE);
                    ItemStack stackRaw = EnumPost.METAL_PLATFORM_UNPAINTED.getItem();

                    for (EnumColor color : EnumColor.values()) {
                        ItemStack outputStack = new ItemStack(BlockPostMetal.platform, 8, color.ordinal());
                        CraftingPlugin.addRecipe(outputStack,
                                "III",
                                "IDI",
                                "III",
                                'I', stackRaw,
                                'D', color.getDyeOreDictTag());
                        CraftingPlugin.addRecipe(outputStack,
                                "III",
                                "IDI",
                                "III",
                                'I', stackColored,
                                'D', color.getDyeOreDictTag());
                    }
                }
            }

            @Override
            public void postInit() {
                if (BlockStrengthGlass.getBlock() != null) {
                    Object[] frameTypes = {"ingotTin", Items.IRON_INGOT};
                    FluidStack water = Fluids.WATER.get(FluidHelper.BUCKET_VOLUME);
                    for (ItemStack container : FluidHelper.getContainersFilledWith(water)) {
                        for (Object frame : frameTypes) {
                            CraftingPlugin.addRecipe(BlockStrengthGlass.getItem(6, 0),
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
        });
    }
}
