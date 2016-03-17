/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.MachineProxyAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.beta.MachineProxyBeta;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.delta.MachineProxyDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.epsilon.MachineProxyEpsilon;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.machine.gamma.MachineProxyGamma;
import mods.railcraft.common.blocks.signals.BlockSignalRailcraft;
import mods.railcraft.common.blocks.signals.ItemSignal;
import mods.railcraft.common.blocks.tracks.BlockTrack;
import mods.railcraft.common.blocks.tracks.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class RailcraftBlocks {

    private static Block blockMachineAlpha;
    private static Block blockMachineBeta;
    private static Block blockMachineGamma;
    private static Block blockMachineDelta;
    private static Block blockMachineEpsilon;
    private static Block blockTrack;
    private static Block blockRailElevator;
    private static Block blockSignal;

    public static void registerBlockTrack() {
        if (blockTrack == null && RailcraftConfig.isBlockEnabled("track")) {
            int renderId = Railcraft.getProxy().getRenderId();
            blockTrack = new BlockTrack(renderId).setBlockName("railcraft.track");
            RailcraftRegistry.register(blockTrack, ItemTrack.class);
        }
    }

    public static Block getBlockTrack() {
        return blockTrack;
    }

    public static void registerBlockRailElevator() {
        if (blockRailElevator == null && RailcraftConfig.isBlockEnabled("elevator")) {
            int renderId = Railcraft.getProxy().getRenderId();
            blockRailElevator = new BlockTrackElevator(renderId).setBlockName("railcraft.track.elevator");
            RailcraftRegistry.register(blockRailElevator, ItemBlockRailcraft.class);
            blockRailElevator.setHarvestLevel("crowbar", 0);
            ItemStack stackElevator = new ItemStack(blockRailElevator, 8);
            CraftingPlugin.addShapedRecipe(stackElevator,
                    "IRI",
                    "ISI",
                    "IRI",
                    'I', RailcraftConfig.useOldRecipes() ? "ingotGold" : RailcraftItem.rail.getRecipeObject(EnumRail.ADVANCED),
                    'S', RailcraftConfig.useOldRecipes() ? "ingotIron" : RailcraftItem.rail.getRecipeObject(EnumRail.STANDARD),
                    'R', "dustRedstone");
        }
    }

    public static Block getBlockElevator() {
        return blockRailElevator;
    }

    public static Block registerBlockMachineAlpha() {
        if (blockMachineAlpha == null && RailcraftConfig.isBlockEnabled("machine.alpha")) {
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            blockMachineAlpha = new BlockMachine(0, new MachineProxyAlpha(), true, lightOpacity).setBlockName("railcraft.machine.alpha");
            RailcraftRegistry.register(blockMachineAlpha, ItemMachine.class);

            for (EnumMachineAlpha type : EnumMachineAlpha.values()) {
                switch (type) {
                    case FEED_STATION:
                    case TANK_WATER:
                        blockMachineAlpha.setHarvestLevel("axe", 1, type.ordinal());
//                        blockMachineAlpha.setHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    case WORLD_ANCHOR:
                    case PERSONAL_ANCHOR:
                        blockMachineAlpha.setHarvestLevel("pickaxe", 3, type.ordinal());
//                        blockMachineAlpha.setHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    default:
                        blockMachineAlpha.setHarvestLevel("pickaxe", 2, type.ordinal());
//                        blockMachineAlpha.setHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineAlpha;
    }

    public static Block getBlockMachineAlpha() {
        return blockMachineAlpha;
    }

    public static Block registerBlockMachineBeta() {
        if (blockMachineBeta == null && RailcraftConfig.isBlockEnabled("machine.beta")) {

            int renderId = Railcraft.getProxy().getRenderId();
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            lightOpacity[EnumMachineBeta.ENGINE_STEAM_HOBBY.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.ENGINE_STEAM_LOW.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.ENGINE_STEAM_HIGH.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.TANK_IRON_WALL.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.TANK_IRON_VALVE.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.TANK_IRON_GAUGE.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.TANK_STEEL_WALL.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.TANK_STEEL_VALVE.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.TANK_STEEL_GAUGE.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.SENTINEL.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.VOID_CHEST.ordinal()] = 0;
            lightOpacity[EnumMachineBeta.METALS_CHEST.ordinal()] = 0;
            blockMachineBeta = new BlockMachine(renderId, new MachineProxyBeta(), false, lightOpacity).setBlockName("railcraft.machine.beta");
            RailcraftRegistry.register(blockMachineBeta, ItemMachine.class);

            for (EnumMachineBeta type : EnumMachineBeta.values()) {
                switch (type) {
                    case SENTINEL:
                        blockMachineBeta.setHarvestLevel("pickaxe", 3, type.ordinal());
//                        blockMachineBeta.setHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    default:
                        blockMachineBeta.setHarvestLevel("pickaxe", 2, type.ordinal());
//                        blockMachineBeta.setHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineBeta;
    }

    public static Block getBlockMachineBeta() {
        return blockMachineBeta;
    }

    public static Block registerBlockMachineGamma() {
        if (blockMachineGamma == null && RailcraftConfig.isBlockEnabled("machine.gamma")) {

            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            lightOpacity[EnumMachineGamma.FLUID_LOADER.ordinal()] = 0;
            lightOpacity[EnumMachineGamma.FLUID_UNLOADER.ordinal()] = 0;
            blockMachineGamma = new BlockMachine(0, new MachineProxyGamma(), false, lightOpacity).setBlockName("railcraft.machine.gamma");
            blockMachineGamma.setCreativeTab(CreativeTabs.tabTransport);
            RailcraftRegistry.register(blockMachineGamma, ItemMachine.class);

            for (EnumMachineGamma type : EnumMachineGamma.values()) {
                switch (type) {
                    default:
                        blockMachineGamma.setHarvestLevel("pickaxe", 2, type.ordinal());
//                        blockMachineGamma.setHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineGamma;
    }

    public static Block getBlockMachineGamma() {
        return blockMachineGamma;
    }

    public static Block registerBlockMachineDelta() {
        if (blockMachineDelta == null && RailcraftConfig.isBlockEnabled("machine.delta")) {
            int renderId = Railcraft.getProxy().getRenderId();
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            lightOpacity[EnumMachineDelta.WIRE.ordinal()] = 0;
            lightOpacity[EnumMachineDelta.CAGE.ordinal()] = 0;
            blockMachineDelta = new BlockMachine(renderId, new MachineProxyDelta(), false, lightOpacity).setBlockName("railcraft.machine.delta");
            blockMachineDelta.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
            RailcraftRegistry.register(blockMachineDelta, ItemMachine.class);

            for (EnumMachineDelta type : EnumMachineDelta.values()) {
                switch (type) {
                    default:
                        blockMachineDelta.setHarvestLevel("pickaxe", 2, type.ordinal());
//                        blockMachineDelta.setHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineDelta;
    }

    public static Block getBlockMachineDelta() {
        return blockMachineDelta;
    }

    public static Block registerBlockMachineEpsilon() {
        if (blockMachineEpsilon == null && RailcraftConfig.isBlockEnabled("machine.epsilon")) {
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            blockMachineEpsilon = new BlockMachine(0, new MachineProxyEpsilon(), true, lightOpacity).setBlockName("railcraft.machine.epsilon");
            RailcraftRegistry.register(blockMachineEpsilon, ItemMachine.class);

            for (EnumMachineEpsilon type : EnumMachineEpsilon.values()) {
                switch (type) {
                    default:
                        blockMachineEpsilon.setHarvestLevel("pickaxe", 2, type.ordinal());
//                        blockMachineEpsilon.setHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineEpsilon;
    }

    public static Block getBlockMachineEpsilon() {
        return blockMachineEpsilon;
    }

    public static void registerBlockSignal() {
        if (blockSignal == null && RailcraftConfig.isBlockEnabled("signal")) {
            int renderId = Railcraft.getProxy().getRenderId();
            blockSignal = new BlockSignalRailcraft(renderId);
            RailcraftRegistry.register(blockSignal, ItemSignal.class);
        }
    }

    public static Block getBlockSignal() {
        return blockSignal;
    }

}
