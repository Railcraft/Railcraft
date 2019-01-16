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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.manipulator.ManipulatorVariant;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:transport", description = "loaders, cargo cart, tank cart, multiblock tanks")
public class ModuleTransport extends RailcraftModulePayload {
    public ModuleTransport() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftCarts.CARGO,
                        RailcraftCarts.TANK,
                        RailcraftBlocks.MANIPULATOR,
                        RailcraftBlocks.CHEST_VOID,
                        RailcraftCarts.CHEST_VOID
                );
            }

            @Override
            public void init() {
                ManipulatorVariant itemLoader = ManipulatorVariant.ITEM_LOADER;
                if (itemLoader.isAvailable()) {
                    ItemStack stack = itemLoader.getStack();
                    ItemStack detector = EnumDetector.ITEM.getStack();
                    if (InvTools.isEmpty(detector))
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addShapedRecipe(stack,
                            "SSS",
                            "SLS",
                            "SDS",
                            'S', "cobblestone",
                            'D', detector,
                            'L', new ItemStack(Blocks.HOPPER));

                    itemLoader = ManipulatorVariant.ITEM_LOADER_ADVANCED;
                    if (itemLoader.isAvailable())
                        CraftingPlugin.addShapedRecipe(itemLoader.getStack(),
                                "IRI",
                                "RLR",
                                "ISI",
                                'I', "ingotSteel",
                                'R', "dustRedstone",
                                'S', RailcraftItems.SHOVEL_STEEL,
                                'L', stack);
                }

                ManipulatorVariant itemUnloader = ManipulatorVariant.ITEM_UNLOADER;
                if (itemUnloader.isAvailable()) {
                    ItemStack stack = itemUnloader.getStack();
                    ItemStack detector = EnumDetector.ITEM.getStack();
                    if (InvTools.isEmpty(detector))
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addShapedRecipe(stack,
                            "SSS",
                            "SDS",
                            "SLS",
                            'S', "cobblestone",
                            'D', detector,
                            'L', new ItemStack(Blocks.HOPPER));

                    itemUnloader = ManipulatorVariant.ITEM_UNLOADER_ADVANCED;
                    if (itemUnloader.isAvailable())
                        CraftingPlugin.addShapedRecipe(itemUnloader.getStack(),
                                "IRI",
                                "RLR",
                                "ISI",
                                'I', "ingotSteel",
                                'R', "dustRedstone",
                                'S', RailcraftItems.SHOVEL_STEEL,
                                'L', stack);
                }

                ManipulatorVariant liquidLoader = ManipulatorVariant.FLUID_LOADER;

                if (liquidLoader.isAvailable()) {
                    ItemStack detector = EnumDetector.TANK.getStack();
                    if (InvTools.isEmpty(detector))
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addShapedRecipe(liquidLoader.getStack(),
                            "GLG",
                            "G G",
                            "GDG",
                            'D', detector,
                            'G', "blockGlassColorless",
                            'L', Blocks.HOPPER);
                }

                ManipulatorVariant liquidUnloader = ManipulatorVariant.FLUID_UNLOADER;
                if (liquidUnloader.isAvailable()) {
                    ItemStack detector = EnumDetector.TANK.getStack();
                    if (InvTools.isEmpty(detector))
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addShapedRecipe(liquidUnloader.getStack(),
                            "GDG",
                            "G G",
                            "GLG",
                            'D', detector,
                            'G', "blockGlassColorless",
                            'L', Blocks.HOPPER);
                }

//        EnumMachineDelta delta = EnumMachineDelta.CAGE;
//        if (delta.register())
//            CraftingPlugin.addShapedOreRecipe(alpha.getItem(6), 
//                "III",
//                "IWI",
//                "PPP",
//                'I', new ItemStack(Block.fenceIron),
//                'W', new ItemStack(Item.wheat),
//                'P', ItemPlate.getPlate(ItemPlate.EnumPlate.STEEL));
            }
        });
    }
}
