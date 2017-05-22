/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.generic.BlockGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.machine.equipment.EquipmentVariant;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@RailcraftModule(value = "railcraft:structures", description = "glass, posts, stairs, slabs, lanterns, walls")
public class ModuleStructures extends RailcraftModulePayload {

    public ModuleStructures() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                for (BrickTheme brick : BrickTheme.VALUES) {
                    add(brick.getContainer());
                }
                add(
                        RailcraftItems.STONE_CARVER,
                        RailcraftBlocks.GLASS,
                        RailcraftBlocks.GENERIC
//                        RailcraftBlocks.post,
//                        RailcraftBlocks.postMetal,
//                        RailcraftBlocks.postMetalPlatform,
//                        RailcraftBlocks.signal, why??
//                        RailcraftBlocks.slab,
//                        RailcraftBlocks.stair,
//                        RailcraftBlocks.lantern,
//                        RailcraftBlocks.wall,
//                        RailcraftBlocks.machine_alpha
                );
            }

            @Override
            public void preInit() {

                EnumGeneric cubeType = EnumGeneric.BLOCK_CONCRETE;
                if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
                    Block cube = BlockGeneric.getBlock();
                    if (cube != null) {
                        ItemStack stack = cubeType.getStack();
                        if (EquipmentVariant.ROLLING_MACHINE_POWERED.isAvailable() && RailcraftItems.REBAR.isEnabled()) {
                            stack.stackSize = 8;
                            CraftingPlugin.addRecipe(stack,
                                    "SIS",
                                    "IWI",
                                    "SIS",
                                    'W', Items.WATER_BUCKET,
                                    'I', RailcraftItems.REBAR,
                                    'S', RailcraftItems.CONCRETE_BAG);
                        } else {
                            stack.stackSize = 4;
                            CraftingPlugin.addRecipe(stack,
                                    " S ",
                                    "SIS",
                                    " S ",
                                    'I', "ingotIron",
                                    'S', RailcraftItems.CONCRETE_BAG);
                        }
                    }
                }

                cubeType = EnumGeneric.BLOCK_CREOSOTE;
                if (RailcraftConfig.isSubBlockEnabled(cubeType.getTag())) {
                    Block cube = BlockGeneric.getBlock();
                    if (cube != null) {
                        ItemStack stack = cubeType.getStack();
                        for (ItemStack container : FluidTools.getContainersFilledWith(Fluids.CREOSOTE.get(FluidTools.BUCKET_VOLUME))) {
                            CraftingPlugin.addShapelessRecipe(stack, "logWood", container);
                        }
                    }
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

            }

            @Override
            public void init() {
                Block blockPost = RailcraftBlocks.POST.block();
                if (blockPost != null) {
                    CraftingPlugin.addShapelessRecipe(EnumPost.WOOD.getStack(4), RailcraftItems.TIE.getRecipeObject(ItemTie.EnumTie.WOOD));
                    CraftingPlugin.addRecipe(EnumPost.WOOD_PLATFORM.getStack(),
                            " T ",
                            " I ",
                            'T', RailcraftBlocks.SLAB, Materials.CREOSOTE,
                            'I', EnumPost.WOOD.getStack());

                    CraftingPlugin.addRecipe(EnumPost.STONE.getStack(8),
                            "SIS",
                            "SIS",
                            "SIS",
                            'I', RailcraftItems.REBAR.getRecipeObject(),
                            'S', "stone");
                    CraftingPlugin.addRecipe(EnumPost.STONE_PLATFORM.getStack(),
                            " T ",
                            " I ",
                            'T', RailcraftBlocks.SLAB, Materials.CONCRETE,
                            'I', EnumPost.STONE.getStack());

                    ItemStack stack = EnumPost.METAL_UNPAINTED.getStack(16);

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

                    CraftingPlugin.addRecipe(EnumPost.METAL_PLATFORM_UNPAINTED.getStack(4),
                            " T ",
                            " I ",
                            'T', RailcraftBlocks.SLAB, Materials.IRON,
                            'I', EnumPost.METAL_UNPAINTED.getStack());

                    stack = EnumPost.METAL_UNPAINTED.getStack(32);
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

                    stack = EnumPost.METAL_UNPAINTED.getStack(12);
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

                    stack = EnumPost.METAL_UNPAINTED.getStack(20);
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

//                if (blockPost != null && BlockPostMetal.post != null) {
//                    ItemStack stackColored = BlockPostMetal.post.getStack(1, OreDictionary.WILDCARD_VALUE);
//                    ItemStack stackRaw = EnumPost.METAL_UNPAINTED.getStack();
//
//                    for (EnumColor color : EnumColor.values()) {
//                        ItemStack outputStack = new ItemStack(BlockPostMetal.post, 8, color.ordinal());
//                        CraftingPlugin.addRecipe(outputStack,
//                                "III",
//                                "IDI",
//                                "III",
//                                'I', stackRaw,
//                                'D', color.getDyeOreDictTag());
//                        CraftingPlugin.addRecipe(outputStack,
//                                "III",
//                                "IDI",
//                                "III",
//                                'I', stackColored,
//                                'D', color.getDyeOreDictTag());
//                    }
//                }
//
//                if (BlockPostMetal.post != null && BlockPostMetal.platform != null) {
//                    ItemStack stackColored = BlockPostMetal.platform.getStack(1, OreDictionary.WILDCARD_VALUE);
//                    ItemStack stackRaw = EnumPost.METAL_PLATFORM_UNPAINTED.getStack();
//
//                    for (EnumColor color : EnumColor.values()) {
//                        ItemStack outputStack = new ItemStack(BlockPostMetal.platform, 8, color.ordinal());
//                        CraftingPlugin.addRecipe(outputStack,
//                                "III",
//                                "IDI",
//                                "III",
//                                'I', stackRaw,
//                                'D', color.getDyeOreDictTag());
//                        CraftingPlugin.addRecipe(outputStack,
//                                "III",
//                                "IDI",
//                                "III",
//                                'I', stackColored,
//                                'D', color.getDyeOreDictTag());
//                    }
//                }
            }

            @Override
            public void postInit() {
                EnumGeneric cubeType = EnumGeneric.BLOCK_CREOSOTE;
                if (cubeType.isEnabled()) {
                    ItemStack stack = cubeType.getStack();
                    for (ItemStack container : FluidTools.getContainersFilledWith(Fluids.CREOSOTE.get(FluidTools.BUCKET_VOLUME))) {
                        CraftingPlugin.addShapelessRecipe(stack, "logWood", container);
                    }
                    ForestryPlugin.instance().addCarpenterRecipe("creosote.block", 40, Fluids.CREOSOTE.get(750), null, stack, "L", 'L', "logWood");
                }
            }
        });
    }
}
