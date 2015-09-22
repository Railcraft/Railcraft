/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.TamingInteractHandler;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemCrowbarReinforced;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.modules.orehandlers.BoreOreHandler;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ModuleAutomation extends RailcraftModule {
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new BoreOreHandler());
    }

    @Override
    public void initFirst() {
        BlockDetector.registerBlock();
        BlockCube.registerBlock();

        Block blockDetector = BlockDetector.getBlock();

        if (blockDetector != null) {
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.ITEM.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', "plankWood",
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.ANY.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', "stone",
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.EMPTY.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', new ItemStack(Blocks.stonebrick, 1, 0),
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.MOB.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', new ItemStack(Blocks.stonebrick, 1, 1),
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.MOB.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', Blocks.mossy_cobblestone,
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.POWERED.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', "cobblestone",
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.PLAYER.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', new ItemStack(Blocks.stone_slab, 1, 0),
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.EXPLOSIVE.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', "slabWood",
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.ANIMAL.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', new ItemStack(Blocks.log, 1, 0),
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.AGE.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', new ItemStack(Blocks.log, 1, 1),
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.ADVANCED.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', "ingotSteel",
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.TANK.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', "ingotBrick",
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.SHEEP.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', Blocks.wool,
                    'P', Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.VILLAGER.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', Items.leather,
                    'P', Blocks.stone_pressure_plate);
        }

        EnumMachineGamma gamma = EnumMachineGamma.DISPENSER_CART;
        if (gamma.register())
            CraftingPlugin.addShapedRecipe(gamma.getItem(),
                    "ML",
                    'M', Items.minecart,
                    'L', Blocks.dispenser);

        EnumMachineAlpha alpha = EnumMachineAlpha.FEED_STATION;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    "PCP",
                    "CSC",
                    "PCP",
                    'P', "plankWood",
                    'S', ModuleManager.isModuleLoaded(ModuleManager.Module.FACTORY) ? RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL) : "blockIron",
                    'C', new ItemStack(Items.golden_carrot));

            MinecraftForge.EVENT_BUS.register(new TamingInteractHandler());
        }

        alpha = EnumMachineAlpha.TRADE_STATION;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    "SGS",
                    "EDE",
                    "SGS",
                    'D', new ItemStack(Blocks.dispenser),
                    'G', "paneGlass",
                    'E', "gemEmerald",
                    'S', ModuleManager.isModuleLoaded(ModuleManager.Module.FACTORY) ? RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL) : "blockIron");
        }

        // Define Bore
        EnumCart cart = EnumCart.BORE;
        if (cart.setup()) {
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "ICI",
                    "FCF",
                    " S ",
                    'I', "blockSteel",
                    'S', Items.chest_minecart,
                    'F', Blocks.furnace,
                    'C', Items.minecart);

            String tag = "tool.bore.head.diamond";
            if (RailcraftConfig.isItemEnabled(tag)) {
                Item item = new ItemBoreHeadDiamond();
                RailcraftRegistry.register(item);
                CraftingPlugin.addShapedRecipe(new ItemStack(item),
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
                CraftingPlugin.addShapedRecipe(new ItemStack(item),
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
                CraftingPlugin.addShapedRecipe(new ItemStack(item),
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
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "YLY",
                    "RSR",
                    "DMD",
                    'L', new ItemStack(Blocks.redstone_lamp),
                    'Y', "dyeYellow",
                    'R', new ItemStack(Items.blaze_rod),
                    'D', new ItemStack(Items.diamond_pickaxe),
                    'S', "blockSteel",
                    'M', new ItemStack(Items.minecart));

        // Define Undercutter Cart
        cart = EnumCart.UNDERCUTTER;
        if (cart.setup())
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "YLY",
                    "RSR",
                    "DMD",
                    'L', new ItemStack(Blocks.redstone_lamp),
                    'Y', "dyeYellow",
                    'R', new ItemStack(Blocks.piston),
                    'D', new ItemStack(Items.diamond_shovel),
                    'S', "blockSteel",
                    'M', new ItemStack(Items.minecart));

        cart = EnumCart.TRACK_LAYER;
        if (cart.setup())
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "YLY",
                    "ESE",
                    "DMD",
                    'Y', "dyeYellow",
                    'L', new ItemStack(Blocks.redstone_lamp),
                    'E', new ItemStack(Blocks.anvil),
                    'S', "blockSteel",
                    'D', new ItemStack(Blocks.dispenser),
                    'M', new ItemStack(Items.minecart));

        cart = EnumCart.TRACK_REMOVER;
        if (cart.setup())
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "YLY",
                    "PSP",
                    "CMC",
                    'Y', "dyeYellow",
                    'L', new ItemStack(Blocks.redstone_lamp),
                    'P', new ItemStack(Blocks.sticky_piston),
                    'S', "blockSteel",
                    'C', ItemCrowbarReinforced.getItem(),
                    'M', new ItemStack(Items.minecart));
    }

    @Override
    public void initSecond() {
        Block blockDetector = BlockDetector.getBlock();

        if (blockDetector != null)
            if (EnumBrick.INFERNAL.getBlock() != null)
                CraftingPlugin.addShapedRecipe(new ItemStack(blockDetector, 1, EnumDetector.LOCOMOTIVE.ordinal()),
                        "XXX",
                        "XPX",
                        "XXX",
                        'X', EnumBrick.INFERNAL.get(BrickVariant.BRICK, 1),
                        'P', Blocks.stone_pressure_plate);
    }
}
