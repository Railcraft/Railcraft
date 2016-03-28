/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

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
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;

public class RailcraftBlocks {

    private static BlockMachine<EnumMachineAlpha> blockMachineAlpha;
    private static BlockMachine<EnumMachineBeta> blockMachineBeta;
    private static BlockMachine<EnumMachineGamma> blockMachineGamma;
    private static BlockMachine<EnumMachineDelta> blockMachineDelta;
    private static BlockMachine<EnumMachineEpsilon> blockMachineEpsilon;
    private static Block blockTrack;
    private static Block blockRailElevator;
    private static Block blockSignal;

    public static void registerBlockTrack() {
        if (blockTrack == null && RailcraftConfig.isBlockEnabled("track")) {
            blockTrack = new BlockTrack().setRegistryName("railcraft.track");
            RailcraftRegistry.register(blockTrack, ItemTrack.class);
        }
    }

    public static Block getBlockTrack() {
        return blockTrack;
    }

    public static void registerBlockRailElevator() {
        if (blockRailElevator == null && RailcraftConfig.isBlockEnabled("elevator")) {
            blockRailElevator = new BlockTrackElevator().setRegistryName("railcraft.track.elevator");
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

    public static BlockMachine<EnumMachineAlpha> registerBlockMachineAlpha() {
        if (blockMachineAlpha == null && RailcraftConfig.isBlockEnabled("machine.alpha")) {
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            MachineProxyAlpha proxy = new MachineProxyAlpha();
            blockMachineAlpha = new BlockMachine<EnumMachineAlpha>(proxy, true, lightOpacity);
            blockMachineAlpha.setRegistryName("railcraft.machine.alpha");
            RailcraftRegistry.register(blockMachineAlpha, ItemMachine.class);

            for (EnumMachineAlpha type : EnumMachineAlpha.values()) {
                IBlockState state = blockMachineAlpha.getDefaultState().withProperty(proxy.getVariantProperty(), type);
                switch (type) {
                    case FEED_STATION:
                    case TANK_WATER:
                        blockMachineAlpha.setHarvestLevel("axe", 1, state);
//                        blockMachineAlpha.setStateHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    case WORLD_ANCHOR:
                    case PERSONAL_ANCHOR:
                        blockMachineAlpha.setHarvestLevel("pickaxe", 3, state);
//                        blockMachineAlpha.setStateHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    default:
                        blockMachineAlpha.setHarvestLevel("pickaxe", 2, state);
//                        blockMachineAlpha.setStateHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineAlpha;
    }

    public static BlockMachine<EnumMachineAlpha> getBlockMachineAlpha() {
        return blockMachineAlpha;
    }

    public static BlockMachine<EnumMachineBeta> registerBlockMachineBeta() {
        if (blockMachineBeta == null && RailcraftConfig.isBlockEnabled("machine.beta")) {

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
            MachineProxyBeta proxy = new MachineProxyBeta();
            blockMachineBeta = new BlockMachine<EnumMachineBeta>(proxy, false, lightOpacity);
            blockMachineBeta.setRegistryName("railcraft.machine.beta");
            RailcraftRegistry.register(blockMachineBeta, ItemMachine.class);

            for (EnumMachineBeta type : EnumMachineBeta.values()) {
                IBlockState state = blockMachineBeta.getDefaultState().withProperty(proxy.getVariantProperty(), type);
                switch (type) {
                    case SENTINEL:
                        blockMachineBeta.setHarvestLevel("pickaxe", 3, state);
//                        blockMachineBeta.setStateHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    default:
                        blockMachineBeta.setHarvestLevel("pickaxe", 2, state);
//                        blockMachineBeta.setStateHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineBeta;
    }

    public static BlockMachine<EnumMachineBeta> getBlockMachineBeta() {
        return blockMachineBeta;
    }

    public static BlockMachine<EnumMachineGamma> registerBlockMachineGamma() {
        if (blockMachineGamma == null && RailcraftConfig.isBlockEnabled("machine.gamma")) {

            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            lightOpacity[EnumMachineGamma.FLUID_LOADER.ordinal()] = 0;
            lightOpacity[EnumMachineGamma.FLUID_UNLOADER.ordinal()] = 0;
            MachineProxyGamma proxy = new MachineProxyGamma();
            blockMachineGamma = new BlockMachine<EnumMachineGamma>(proxy, false, lightOpacity);
            blockMachineGamma.setRegistryName("railcraft.machine.gamma");
            blockMachineGamma.setCreativeTab(CreativeTabs.tabTransport);
            RailcraftRegistry.register(blockMachineGamma, ItemMachine.class);

            for (EnumMachineGamma type : EnumMachineGamma.values()) {
                IBlockState state = blockMachineBeta.getDefaultState().withProperty(proxy.getVariantProperty(), type);
                switch (type) {
                    default:
                        blockMachineGamma.setHarvestLevel("pickaxe", 2, state);
//                        blockMachineGamma.setStateHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineGamma;
    }

    public static BlockMachine<EnumMachineGamma> getBlockMachineGamma() {
        return blockMachineGamma;
    }

    public static BlockMachine<EnumMachineDelta> registerBlockMachineDelta() {
        if (blockMachineDelta == null && RailcraftConfig.isBlockEnabled("machine.delta")) {
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            lightOpacity[EnumMachineDelta.WIRE.ordinal()] = 0;
            lightOpacity[EnumMachineDelta.CAGE.ordinal()] = 0;
            MachineProxyDelta proxy = new MachineProxyDelta();
            blockMachineDelta = new BlockMachine<EnumMachineDelta>(proxy, false, lightOpacity);
            blockMachineDelta.setRegistryName("railcraft.machine.delta");
            blockMachineDelta.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
            RailcraftRegistry.register(blockMachineDelta, ItemMachine.class);

            for (EnumMachineDelta type : EnumMachineDelta.values()) {
                IBlockState state = blockMachineBeta.getDefaultState().withProperty(proxy.getVariantProperty(), type);
                switch (type) {
                    default:
                        blockMachineDelta.setHarvestLevel("pickaxe", 2, state);
//                        blockMachineDelta.setStateHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineDelta;
    }

    public static BlockMachine<EnumMachineDelta> getBlockMachineDelta() {
        return blockMachineDelta;
    }

    public static BlockMachine<EnumMachineEpsilon> registerBlockMachineEpsilon() {
        if (blockMachineEpsilon == null && RailcraftConfig.isBlockEnabled("machine.epsilon")) {
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            MachineProxyEpsilon proxy = new MachineProxyEpsilon();
            blockMachineEpsilon = new BlockMachine<EnumMachineEpsilon>(proxy, true, lightOpacity);
            blockMachineEpsilon.setRegistryName("railcraft.machine.epsilon");
            RailcraftRegistry.register(blockMachineEpsilon, ItemMachine.class);

            for (EnumMachineEpsilon type : EnumMachineEpsilon.values()) {
                IBlockState state = blockMachineBeta.getDefaultState().withProperty(proxy.getVariantProperty(), type);
                switch (type) {
                    default:
                        blockMachineEpsilon.setHarvestLevel("pickaxe", 2, state);
//                        blockMachineEpsilon.setStateHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineEpsilon;
    }

    public static BlockMachine<EnumMachineEpsilon> getBlockMachineEpsilon() {
        return blockMachineEpsilon;
    }

    public static void registerBlockSignal() {
        if (blockSignal == null && RailcraftConfig.isBlockEnabled("signal")) {
            blockSignal = new BlockSignalRailcraft();
            RailcraftRegistry.register(blockSignal, ItemSignal.class);
        }
    }

    public static Block getBlockSignal() {
        return blockSignal;
    }
}
