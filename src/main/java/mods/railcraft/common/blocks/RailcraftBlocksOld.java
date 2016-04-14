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
import mods.railcraft.common.blocks.tracks.BlockTrackElevator;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

@Deprecated
public class RailcraftBlocksOld {

    private static Block blockMachineAlpha;
    private static Block blockMachineBeta;
    private static Block blockMachineGamma;
    private static Block blockMachineDelta;
    private static Block blockMachineEpsilon;
    private static Block blockRailElevator;

    public static void registerBlockRailElevator() {
        if (blockRailElevator == null && RailcraftConfig.isBlockEnabled("elevator")) {
            blockRailElevator = new BlockTrackElevator().setUnlocalizedName("railcraft.track.elevator");
            RailcraftRegistry.register(blockRailElevator, ItemBlockRailcraft.class);
            blockRailElevator.setHarvestLevel("crowbar", 0);
            ItemStack stackElevator = new ItemStack(blockRailElevator, 8);
            CraftingPlugin.addRecipe(stackElevator,
                    "IRI",
                    "ISI",
                    "IRI",
                    'I', RailcraftConfig.useOldRecipes() ? "ingotGold" : RailcraftItems.rail.getRecipeObject(EnumRail.ADVANCED),
                    'S', RailcraftConfig.useOldRecipes() ? "ingotIron" : RailcraftItems.rail.getRecipeObject(EnumRail.STANDARD),
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
            blockMachineAlpha = new BlockMachine<EnumMachineAlpha>(new MachineProxyAlpha(), true, lightOpacity).setUnlocalizedName("railcraft.machine.alpha");
            RailcraftRegistry.register(blockMachineAlpha, ItemMachine.class);

            for (EnumMachineAlpha type : EnumMachineAlpha.values()) {
                switch (type) {
                    case FEED_STATION:
                    case TANK_WATER:
                        blockMachineAlpha.setHarvestLevel("axe", 1, type.getState());
//                        blockMachineAlpha.setStateHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    case WORLD_ANCHOR:
                    case PERSONAL_ANCHOR:
                        blockMachineAlpha.setHarvestLevel("pickaxe", 3, type.getState());
//                        blockMachineAlpha.setStateHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    default:
                        blockMachineAlpha.setHarvestLevel("pickaxe", 2, type.getState());
//                        blockMachineAlpha.setStateHarvestLevel("crowbar", 0, type.ordinal());
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
            blockMachineBeta = new BlockMachine<EnumMachineBeta>(new MachineProxyBeta(), false, lightOpacity).setUnlocalizedName("railcraft.machine.beta");
            RailcraftRegistry.register(blockMachineBeta, ItemMachine.class);

            for (EnumMachineBeta type : EnumMachineBeta.values()) {
                switch (type) {
                    case SENTINEL:
                        blockMachineBeta.setHarvestLevel("pickaxe", 3, type.getState());
//                        blockMachineBeta.setStateHarvestLevel("crowbar", 0, type.ordinal());
                        break;
                    default:
                        blockMachineBeta.setHarvestLevel("pickaxe", 2, type.getState());
//                        blockMachineBeta.setStateHarvestLevel("crowbar", 0, type.ordinal());
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
            blockMachineGamma = new BlockMachine<EnumMachineGamma>(new MachineProxyGamma(), false, lightOpacity).setUnlocalizedName("railcraft.machine.gamma");
            blockMachineGamma.setCreativeTab(CreativeTabs.tabTransport);
            RailcraftRegistry.register(blockMachineGamma, ItemMachine.class);

            for (EnumMachineGamma type : EnumMachineGamma.values()) {
                switch (type) {
                    default:
                        blockMachineGamma.setHarvestLevel("pickaxe", 2, type.getState());
//                        blockMachineGamma.setStateHarvestLevel("crowbar", 0, type.ordinal());
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
            int[] lightOpacity = new int[16];
            Arrays.fill(lightOpacity, 255);
            lightOpacity[EnumMachineDelta.WIRE.ordinal()] = 0;
            lightOpacity[EnumMachineDelta.CAGE.ordinal()] = 0;
            blockMachineDelta = new BlockMachine<EnumMachineDelta>(new MachineProxyDelta(), false, lightOpacity).setUnlocalizedName("railcraft.machine.delta");
            blockMachineDelta.setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
            RailcraftRegistry.register(blockMachineDelta, ItemMachine.class);

            for (EnumMachineDelta type : EnumMachineDelta.values()) {
                switch (type) {
                    default:
                        blockMachineDelta.setHarvestLevel("pickaxe", 2, type.getState());
//                        blockMachineDelta.setStateHarvestLevel("crowbar", 0, type.ordinal());
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
            blockMachineEpsilon = new BlockMachine<EnumMachineEpsilon>(new MachineProxyEpsilon(), true, lightOpacity).setUnlocalizedName("railcraft.machine.epsilon");
            RailcraftRegistry.register(blockMachineEpsilon, ItemMachine.class);

            for (EnumMachineEpsilon type : EnumMachineEpsilon.values()) {
                switch (type) {
                    default:
                        blockMachineEpsilon.setHarvestLevel("pickaxe", 2, type.getState());
//                        blockMachineEpsilon.setStateHarvestLevel("crowbar", 0, type.ordinal());
                }
            }
        }
        return blockMachineEpsilon;
    }

    public static Block getBlockMachineEpsilon() {
        return blockMachineEpsilon;
    }

}
