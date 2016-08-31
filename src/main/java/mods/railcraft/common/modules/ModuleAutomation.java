/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.ai.TamingInteractHandler;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.orehandlers.BoreOreHandler;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@RailcraftModule("railcraft:automation")
public class ModuleAutomation extends RailcraftModulePayload {
    public ModuleAutomation() {
        setEnabledEventHandler(new ModuleEventHandler() {

            @Override
            public void construction() {
                MinecraftForge.EVENT_BUS.register(new BoreOreHandler());
                add(
                        RailcraftBlocks.DETECTOR,
                        RailcraftBlocks.GENERIC,
//                        RailcraftBlocks.machine_alpha,
//                        RailcraftBlocks.machine_gamma,

                        RailcraftCarts.BORE,
                        RailcraftItems.BORE_HEAD_IRON,
                        RailcraftItems.BORE_HEAD_STEEL,
                        RailcraftItems.BORE_HEAD_DIAMOND,
                        RailcraftCarts.MOW_TRACK_LAYER,
                        RailcraftCarts.MOW_TRACK_RELAYER,
                        RailcraftCarts.MOW_TRACK_REMOVER,
                        RailcraftCarts.MOW_TRACK_LAYER
                );
            }

            @Override
            public void preInit() {
                EnumMachineGamma gamma = EnumMachineGamma.DISPENSER_CART;
                if (gamma.isAvailable())
                    CraftingPlugin.addRecipe(gamma.getItem(),
                            "ML",
                            'M', Items.MINECART,
                            'L', Blocks.DISPENSER);

                EnumMachineAlpha alpha = EnumMachineAlpha.FEED_STATION;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem();
                    CraftingPlugin.addRecipe(stack,
                            "PCP",
                            "CSC",
                            "PCP",
                            'P', "plankWood",
                            'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) ? RailcraftItems.PLATE.getRecipeObject(Metal.STEEL) : "blockIron",
                            'C', new ItemStack(Items.GOLDEN_CARROT));

                    MinecraftForge.EVENT_BUS.register(new TamingInteractHandler());
                }

                alpha = EnumMachineAlpha.TRADE_STATION;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem();
                    CraftingPlugin.addRecipe(stack,
                            "SGS",
                            "EDE",
                            "SGS",
                            'D', new ItemStack(Blocks.DISPENSER),
                            'G', "paneGlass",
                            'E', "gemEmerald",
                            'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) ? RailcraftItems.PLATE.getRecipeObject(Metal.STEEL) : "blockIron");
                }
            }
        });
    }
}
