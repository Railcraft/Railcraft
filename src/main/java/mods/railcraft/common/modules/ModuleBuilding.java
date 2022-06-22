/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.item.ItemStack;

@RailcraftModule(value = "railcraft:building",
        softDependencyClasses = ModuleWorld.class,
        description = "glass, posts, bricks, stairs, slabs, lanterns, walls")
public class ModuleBuilding extends RailcraftModulePayload {

    public ModuleBuilding() {
        add(BrickTheme.VALUES);
        add(
//                        RailcraftItems.STONE_CARVER,
                RailcraftItems.CONCRETE,
                RailcraftBlocks.GLASS,
                RailcraftBlocks.GENERIC,

                RailcraftBlocks.CREOSOTE_BLOCK,
                RailcraftBlocks.CREOSOTE_STAIRS,
                RailcraftBlocks.CREOSOTE_DOUBLE_SLAB,
                RailcraftBlocks.CREOSOTE_SLAB,

                RailcraftBlocks.REINFORCED_CONCRETE,
                RailcraftBlocks.POST_METAL,
                RailcraftBlocks.POST_METAL_PLATFORM,

                RailcraftBlocks.ABYSSAL_BRICK_STAIRS,
                RailcraftBlocks.BLEACHED_BONE_BRICK_STAIRS,
                RailcraftBlocks.BLOOD_STAINED_BRICK_STAIRS,
                RailcraftBlocks.FROST_BOUND_BRICK_STAIRS,
                RailcraftBlocks.INFERNAL_BRICK_STAIRS,
                RailcraftBlocks.JADED_BRICK_STAIRS,
                RailcraftBlocks.PEARLIZED_BRICK_STAIRS,
                RailcraftBlocks.QUARRIED_BRICK_STAIRS,
                RailcraftBlocks.BADLANDS_BRICK_STAIRS,
                RailcraftBlocks.SANDY_BRICK_STAIRS,

                RailcraftBlocks.ABYSSAL_PAVER_STAIRS,
                RailcraftBlocks.BLEACHED_BONE_PAVER_STAIRS,
                RailcraftBlocks.BLOOD_STAINED_PAVER_STAIRS,
                RailcraftBlocks.FROST_BOUND_PAVER_STAIRS,
                RailcraftBlocks.INFERNAL_PAVER_STAIRS,
                RailcraftBlocks.JADED_PAVER_STAIRS,
                RailcraftBlocks.PEARLIZED_PAVER_STAIRS,
                RailcraftBlocks.QUARRIED_PAVER_STAIRS,
                RailcraftBlocks.BADLANDS_PAVER_STAIRS,
                RailcraftBlocks.SANDY_PAVER_STAIRS,

                RailcraftBlocks.ABYSSAL_DOUBLE_SLAB,
                RailcraftBlocks.ABYSSAL_SLAB,

                RailcraftBlocks.BADLANDS_DOUBLE_SLAB,
                RailcraftBlocks.BADLANDS_SLAB,

                RailcraftBlocks.BLEACHED_BONE_DOUBLE_SLAB,
                RailcraftBlocks.BLEACHED_BONE_SLAB,

                RailcraftBlocks.BLOOD_STAINED_DOUBLE_SLAB,
                RailcraftBlocks.BLOOD_STAINED_SLAB,

                RailcraftBlocks.FROST_BOUND_DOUBLE_SLAB,
                RailcraftBlocks.FROST_BOUND_SLAB,

                RailcraftBlocks.INFERNAL_DOUBLE_SLAB,
                RailcraftBlocks.INFERNAL_SLAB,

                RailcraftBlocks.JADED_DOUBLE_SLAB,
                RailcraftBlocks.JADED_SLAB,

                RailcraftBlocks.PEARLIZED_DOUBLE_SLAB,
                RailcraftBlocks.PEARLIZED_SLAB,

                RailcraftBlocks.QUARRIED_DOUBLE_SLAB,
                RailcraftBlocks.QUARRIED_SLAB,

                RailcraftBlocks.SANDY_DOUBLE_SLAB,
                RailcraftBlocks.SANDY_SLAB
        );

        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void init() {

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

                if (RailcraftBlocks.POST.isLoaded()) {
                    CraftingPlugin.addShapelessRecipe(EnumPost.WOOD.getStack(4), RailcraftItems.TIE.getIngredient(ItemTie.EnumTie.WOOD));
                    CraftingPlugin.addShapedRecipe(EnumPost.WOOD_PLATFORM.getStack(),
                            " T ",
                            " I ",
                            'T', RailcraftBlocks.ABYSSAL_SLAB, // FIXME
                            'I', EnumPost.WOOD.getStack());

                    CraftingPlugin.addShapedRecipe(EnumPost.STONE.getStack(8),
                            "SIS",
                            "SIS",
                            "SIS",
                            'I', RailcraftItems.REBAR,
                            'S', "stone");
                    CraftingPlugin.addShapedRecipe(EnumPost.STONE_PLATFORM.getStack(),
                            " T ",
                            " I ",
                            'T', RailcraftBlocks.ABYSSAL_SLAB, // FIXME
                            'I', EnumPost.STONE.getStack());

                    ItemStack stack = EnumPost.METAL_UNPAINTED.getStack(16);

                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "III",
                            " I ",
                            "III",
                            'I', "ingotIron");

                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotIron");

                    CraftingPlugin.addShapedRecipe(EnumPost.METAL_PLATFORM_UNPAINTED.getStack(4),
                            " T ",
                            " I ",
                            'T', RailcraftBlocks.ABYSSAL_SLAB, // FIXME
                            'I', EnumPost.METAL_UNPAINTED.getStack());

                    stack = EnumPost.METAL_UNPAINTED.getStack(32);
                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "III",
                            " I ",
                            "III",
                            'I', "ingotSteel");

                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotSteel");

                    stack = EnumPost.METAL_UNPAINTED.getStack(12);
                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "III",
                            " I ",
                            "III",
                            'I', "ingotBronze");
                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotBronze");

                    stack = EnumPost.METAL_UNPAINTED.getStack(20);
                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "III",
                            " I ",
                            "III",
                            'I', "ingotRefinedIron");
                    Crafters.rollingMachine().newRecipe(stack).shaped(
                            "I I",
                            "III",
                            "I I",
                            'I', "ingotRefinedIron");
                }
            }
        });
    }
}
