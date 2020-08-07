/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemTie;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@RailcraftModule(value = "railcraft:building", description = "glass, posts, bricks, stairs, slabs, lanterns, walls")
public class ModuleBuilding extends RailcraftModulePayload {

    public ModuleBuilding() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                for (BrickTheme brick : BrickTheme.VALUES) {
                    add(brick.getContainer());
                }
                add(
//                        RailcraftItems.STONE_CARVER,
                        RailcraftItems.CONCRETE,
                        RailcraftBlocks.GLASS,
                        RailcraftBlocks.GENERIC,
                        RailcraftBlocks.REINFORCED_CONCRETE,
//                        RailcraftBlocks.post,
                        RailcraftBlocks.POST_METAL,
                        RailcraftBlocks.POST_METAL_PLATFORM,

                        RailcraftBlocks.STAIR_ABYSSAL_BRICK,
                        RailcraftBlocks.STAIR_BLEACHED_BONE_BRICK,
                        RailcraftBlocks.STAIR_BLOOD_STAINED_BRICK,
                        RailcraftBlocks.STAIR_FROST_BOUND_BRICK,
                        RailcraftBlocks.STAIR_INFERNAL_BRICK,
                        RailcraftBlocks.STAIR_PEARLIZED_BRICK,
                        RailcraftBlocks.STAIR_QUARRIED_BRICK,
                        RailcraftBlocks.STAIR_BADLANDS_BRICK,
                        RailcraftBlocks.STAIR_SANDY_BRICK,

                        RailcraftBlocks.STAIR_ABYSSAL_PAVER,
                        RailcraftBlocks.STAIR_BLEACHED_BONE_PAVER,
                        RailcraftBlocks.STAIR_BLOOD_STAINED_PAVER,
                        RailcraftBlocks.STAIR_FROST_BOUND_PAVER,
                        RailcraftBlocks.STAIR_INFERNAL_PAVER,
                        RailcraftBlocks.STAIR_PEARLIZED_PAVER,
                        RailcraftBlocks.STAIR_QUARRIED_PAVER,
                        RailcraftBlocks.STAIR_BADLANDS_PAVER,
                        RailcraftBlocks.STAIR_SANDY_PAVER,

                        RailcraftBlocks.SLAB_ABYSSAL_DOUBLE,
                        RailcraftBlocks.SLAB_ABYSSAL,

                        RailcraftBlocks.SLAB_BADLANDS_DOUBLE,
                        RailcraftBlocks.SLAB_BADLANDS,

                        RailcraftBlocks.SLAB_BLEACHED_BONE_DOUBLE,
                        RailcraftBlocks.SLAB_BLEACHED_BONE,

                        RailcraftBlocks.SLAB_BLOOD_STAINED_DOUBLE,
                        RailcraftBlocks.SLAB_BLOOD_STAINED,

                        RailcraftBlocks.SLAB_FROST_BOUND_DOUBLE,
                        RailcraftBlocks.SLAB_FROST_BOUND,

                        RailcraftBlocks.SLAB_INFERNAL_DOUBLE,
                        RailcraftBlocks.SLAB_INFERNAL,

                        RailcraftBlocks.SLAB_PEARLIZED_DOUBLE,
                        RailcraftBlocks.SLAB_PEARLIZED,

                        RailcraftBlocks.SLAB_QUARRIED_DOUBLE,
                        RailcraftBlocks.SLAB_QUARRIED,

                        RailcraftBlocks.SLAB_SANDY_DOUBLE,
                        RailcraftBlocks.SLAB_SANDY

//                        RailcraftBlocks.slab,
//                        RailcraftBlocks.stair,
//                        RailcraftBlocks.lantern,
//                        RailcraftBlocks.wall,
                );
            }

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
                            'T', RailcraftBlocks.SLAB_ABYSSAL, // FIXME
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
                            'T', RailcraftBlocks.SLAB_ABYSSAL, // FIXME
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
                            'T', RailcraftBlocks.SLAB_ABYSSAL, // FIXME
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

                EnumGeneric cubeType = EnumGeneric.BLOCK_CREOSOTE;
                if (cubeType.isEnabled()) {
                    ItemStack stack = cubeType.getStack();
                    FluidStack creosote = Fluids.CREOSOTE.get(FluidTools.BUCKET_VOLUME);
                    CraftingPlugin.addShapelessRecipe("railcraft:block_creosote", stack,
                            "logWood", creosote);
                    ForestryPlugin.instance().addCarpenterRecipe("railcraft:block_creosote", 40,
                            Fluids.CREOSOTE.get(750), ItemStack.EMPTY, stack, "L", 'L', "logWood");
                }
            }
        });
    }
}
