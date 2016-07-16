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
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.ai.TamingInteractHandler;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.ItemBoreHeadDiamond;
import mods.railcraft.common.carts.ItemBoreHeadIron;
import mods.railcraft.common.carts.ItemBoreHeadSteel;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.orehandlers.BoreOreHandler;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
                        RailcraftBlocks.detector,
                        RailcraftBlocks.cube,
                        RailcraftBlocks.machine_alpha,
                        RailcraftBlocks.machine_gamma
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
                            'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) ? RailcraftItems.plate.getRecipeObject(Metal.STEEL) : "blockIron",
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
                            'S', RailcraftModuleManager.isModuleEnabled(ModuleFactory.class) ? RailcraftItems.plate.getRecipeObject(Metal.STEEL) : "blockIron");
                }

                // Define Bore
                EnumCart cart = EnumCart.BORE;
                if (cart.setup()) {
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "ICI",
                            "FCF",
                            " S ",
                            'I', "blockSteel",
                            'S', Items.CHEST_MINECART,
                            'F', Blocks.FURNACE,
                            'C', Items.MINECART);

                    String tag = "tool.bore.head.diamond";
                    if (RailcraftConfig.isItemEnabled(tag)) {
                        Item item = new ItemBoreHeadDiamond();
                        RailcraftRegistry.register(item);
                        CraftingPlugin.addRecipe(new ItemStack(item),
                                "III",
                                "IDI",
                                "III",
                                'I', "ingotSteel",
                                'D', "blockDiamond");
                    }

                    tag = "tool.bore.head.steel";
                    if (RailcraftConfig.isItemEnabled(tag)) {
                        Item item = new ItemBoreHeadSteel();
                        RailcraftRegistry.register(item);
                        CraftingPlugin.addRecipe(new ItemStack(item),
                                "III",
                                "IDI",
                                "III",
                                'I', "ingotSteel",
                                'D', "blockSteel");
                    }

                    tag = "tool.bore.head.iron";
                    if (RailcraftConfig.isItemEnabled(tag)) {
                        Item item = new ItemBoreHeadIron();
                        RailcraftRegistry.register(item);
                        CraftingPlugin.addRecipe(new ItemStack(item),
                                "III",
                                "IDI",
                                "III",
                                'I', "ingotSteel",
                                'D', "blockIron");
                    }
                }

                // Define Track Relayer Cart
                cart = EnumCart.TRACK_RELAYER;
                if (cart.setup())
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "YLY",
                            "RSR",
                            "DMD",
                            'L', new ItemStack(Blocks.REDSTONE_LAMP),
                            'Y', "dyeYellow",
                            'R', new ItemStack(Items.BLAZE_ROD),
                            'D', new ItemStack(Items.DIAMOND_PICKAXE),
                            'S', "blockSteel",
                            'M', new ItemStack(Items.MINECART));

                // Define Undercutter Cart
                cart = EnumCart.UNDERCUTTER;
                if (cart.setup())
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "YLY",
                            "RSR",
                            "DMD",
                            'L', new ItemStack(Blocks.REDSTONE_LAMP),
                            'Y', "dyeYellow",
                            'R', new ItemStack(Blocks.PISTON),
                            'D', new ItemStack(Items.DIAMOND_SHOVEL),
                            'S', "blockSteel",
                            'M', new ItemStack(Items.MINECART));

                cart = EnumCart.TRACK_LAYER;
                if (cart.setup())
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "YLY",
                            "ESE",
                            "DMD",
                            'Y', "dyeYellow",
                            'L', new ItemStack(Blocks.REDSTONE_LAMP),
                            'E', new ItemStack(Blocks.ANVIL),
                            'S', "blockSteel",
                            'D', new ItemStack(Blocks.DISPENSER),
                            'M', new ItemStack(Items.MINECART));

                cart = EnumCart.TRACK_REMOVER;
                if (cart.setup())
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "YLY",
                            "PSP",
                            "CMC",
                            'Y', "dyeYellow",
                            'L', new ItemStack(Blocks.REDSTONE_LAMP),
                            'P', new ItemStack(Blocks.STICKY_PISTON),
                            'S', "blockSteel",
                            'C', IToolCrowbar.ORE_TAG,
                            'M', new ItemStack(Items.MINECART));
            }
        });
    }
}
