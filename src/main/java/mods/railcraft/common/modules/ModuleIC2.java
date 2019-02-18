/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import ic2.api.recipe.Recipes;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.manipulator.ManipulatorVariant;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

//import mods.railcraft.common.plugins.ic2.crops.*;

@RailcraftModule(value = "railcraft:ic2", description = "industrial craft integration")
public class ModuleIC2 extends RailcraftModulePayload {

    @Override
    public void checkPrerequisites() throws MissingPrerequisiteException {
        if (!Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC))
            throw new MissingPrerequisiteException("IC2 not detected");
    }

    public ModuleIC2() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftCarts.ENERGY_BATBOX,
                        RailcraftCarts.ENERGY_MFE,
                        RailcraftCarts.ENERGY_CESU,
                        RailcraftBlocks.MANIPULATOR
                );
                if (Mod.IC2_CLASSIC.isLoaded()) add(RailcraftCarts.ENERGY_MFSU);
            }

            @Override
            public void preInit(){
            //    IC2Plugin.addCanningRecipe(IC2Plugin.getItem("fluid_cell"), RailcraftItems.TARBERRY.getStack(), IC2Plugin.getItem("fluid_cell#railcraftcreosote")); ##Saved for later
            }

            @Override
            public void init() {
            //    Crops.instance.registerCrop(new CropCreosote()); ##Save this for when a proper crafting recipe is possible
//                Block blockDetector = RailcraftBlocks.DETECTOR.block();
//
//                if (blockDetector != null) {
//                    ItemStack stack = EnumDetector.ENERGY.getItem();
//                    Object tin = RailcraftItems.PLATE.getRecipeObject(Metal.TIN);
//                    if (tin == null)
//                        tin = "ingotTin";
//                    CraftingPlugin.addRecipe(stack, false,
//                            "XXX",
//                            "XPX",
//                            "XXX",
//                            'X', tin,
//                            'P', Blocks.STONE_PRESSURE_PLATE);
//                }

                ItemStack batbox = ModItems.BAT_BOX.getStack();
                if (!InvTools.isEmpty(batbox)) {
                    RailcraftCarts cart = RailcraftCarts.ENERGY_BATBOX;
                    ItemStack stack = cart.getStack();
                    if (!InvTools.isEmpty(stack)) {
                        CraftingPlugin.addShapedRecipe(stack,
                                "E",
                                "M",
                                'E', batbox,
                                'M', Items.MINECART
                        );
                    }
                }

                if (!Mod.IC2_CLASSIC.isLoaded()) {
                    ItemStack cesu = ModItems.CESU.getStack();
                    if (!InvTools.isEmpty(cesu)) {
                        RailcraftCarts cart = RailcraftCarts.ENERGY_CESU;
                        ItemStack stack = cart.getStack();
                        if (!InvTools.isEmpty(stack)) {
                            CraftingPlugin.addShapedRecipe(stack,
                                    "E",
                                    "M",
                                    'E', cesu,
                                    'M', Items.MINECART
                            );
                        }
                    }
                } else {
                    ItemStack mfsu = ModItems.MFSU.getStack();
                    if (!InvTools.isEmpty(mfsu)) {
                        RailcraftCarts cart = RailcraftCarts.ENERGY_MFSU;
                        ItemStack stack = cart.getStack();
                        if (!InvTools.isEmpty(stack)) {
                            CraftingPlugin.addShapedRecipe(stack,
                                    "E",
                                    "M",
                                    'E', mfsu,
                                    'M', Items.MINECART
                            );
                        }
                    }
                }

                ItemStack mfe = ModItems.MFE.getStack();
                if (!InvTools.isEmpty(mfe)) {
                    RailcraftCarts cart = RailcraftCarts.ENERGY_MFE;
                    ItemStack stack = cart.getStack();
                    if (!InvTools.isEmpty(stack)) {

                        CraftingPlugin.addShapedRecipe(stack,
                                "E",
                                "M",
                                'E', mfe,
                                'M', Items.MINECART
                        );
                    }
                }

                ItemStack battery = ModItems.BATTERY.getStack();
                ItemStack machine = ModItems.IC2_MACHINE.getStack();

                ItemStack detector;
                if (RailcraftBlocks.DETECTOR.isLoaded())
                    detector = EnumDetector.ADVANCED.getStack();
                else
                    detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);

                if (!InvTools.isEmpty(battery) && !InvTools.isEmpty(machine)) {
                    if (ManipulatorVariant.ENERGY_LOADER.isAvailable())
                        Recipes.advRecipes.addRecipe(ManipulatorVariant.ENERGY_LOADER.getStack(),
                                "BLB",
                                "BIB",
                                "BDB",
                                'D', detector,
                                'B', battery,
                                'I', machine,
                                'L', new ItemStack(Blocks.HOPPER));

                    if (ManipulatorVariant.ENERGY_UNLOADER.isAvailable())
                        Recipes.advRecipes.addRecipe(ManipulatorVariant.ENERGY_UNLOADER.getStack(),
                                "BDB",
                                "BIB",
                                "BLB",
                                'D', detector,
                                'B', battery,
                                'I', machine,
                                'L', new ItemStack(Blocks.HOPPER));
                }

                if (!RailcraftConfig.getRecipeConfig("ic2.macerator.bones"))
                    IC2Plugin.removeMaceratorRecipes(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 1, 15));

                if (!RailcraftConfig.getRecipeConfig("ic2.macerator.blaze"))
                    IC2Plugin.removeMaceratorRecipes(new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER));

                if (!RailcraftConfig.getRecipeConfig("ic2.macerator.cobble")) {
                    IC2Plugin.removeMaceratorRecipes(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND));
                    IC2Plugin.removeMaceratorRecipes(new ItemStack(Blocks.STONE), new ItemStack(Blocks.COBBLESTONE));
                }

                if (!RailcraftConfig.getRecipeConfig("ic2.macerator.dirt"))
                    IC2Plugin.removeMaceratorRecipes(recipe -> recipe.getOutput().stream().anyMatch(item -> item.getItem() == Item.getItemFromBlock(Blocks.DIRT)));
            }
        });
    }
}
