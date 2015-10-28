/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.ItemIngot;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleSteam extends RailcraftModule {

    @Override
    public void initFirst() {
//        LiquidItems.getSteamBottle(1);
        EnumMachineBeta beta = EnumMachineBeta.ENGINE_STEAM_HOBBY;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "NNN",
                        " C ",
                        "GPG",
                        'P', new ItemStack(Blocks.piston),
                        'N', "nuggetGold",
                        'C', "blockGlassColorless",
                        'G', RailcraftItem.gear.getRecipeObject(EnumGear.GOLD_PLATE));
            }
        }

        beta = EnumMachineBeta.ENGINE_STEAM_LOW;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "III",
                        " C ",
                        "GPG",
                        'P', new ItemStack(Blocks.piston),
                        'I', RailcraftItem.plate.getRecipeObject(EnumPlate.IRON),
                        'C', "blockGlassColorless",
                        'G', "gearIron");

                RailcraftCraftingManager.blastFurnace.addRecipe(stack, true, false, 15360, RailcraftItem.ingot.getStack(12, ItemIngot.EnumIngot.STEEL));
            }
        }

        beta = EnumMachineBeta.ENGINE_STEAM_HIGH;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "III",
                        " C ",
                        "GPG",
                        'P', new ItemStack(Blocks.piston),
                        'I', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL),
                        'C', "blockGlassColorless",
                        'G', RailcraftItem.gear.getRecipeObject(EnumGear.STEEL));
            }
        }

        beta = EnumMachineBeta.BOILER_FIREBOX_SOLID;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "BBB",
                        "BCB",
                        "BFB",
                        'B', "ingotBrick",
                        'C', new ItemStack(Items.fire_charge),
                        'F', new ItemStack(Blocks.furnace));
            }
        }

        beta = EnumMachineBeta.BOILER_FIREBOX_FLUID;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "PBP",
                        "GCG",
                        "PFP",
                        'B', new ItemStack(Items.bucket),
                        'G', new ItemStack(Blocks.iron_bars),
                        'C', new ItemStack(Items.fire_charge),
                        'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL),
                        'F', new ItemStack(Blocks.furnace));
            }
        }

        beta = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "P",
                        "P",
                        'P', RailcraftItem.plate.getRecipeObject(EnumPlate.IRON));

                RailcraftCraftingManager.blastFurnace.addRecipe(stack, true, false, 2560, RailcraftItem.ingot.getStack(2, ItemIngot.EnumIngot.STEEL));
            }
        }

        beta = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            RailcraftBlocks.registerBlockMachineBeta();
            Block block = RailcraftBlocks.getBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                CraftingPlugin.addShapedRecipe(beta.getItem(),
                        "P",
                        "P",
                        'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL));
            }
        }

        EnumMachineAlpha.TURBINE.register();

        EnumMachineAlpha.STEAM_TRAP_MANUAL.register();
        EnumMachineAlpha.STEAM_TRAP_AUTO.register();

        EnumMachineEpsilon.ADMIN_STEAM_PRODUCER.register();
    }

    @Override
    public void initSecond() {
        EnumMachineAlpha alpha = EnumMachineAlpha.STEAM_TRAP_MANUAL;
        if (alpha.isAvaliable()) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    " G ",
                    " T ",
                    " D ",
                    'G', new ItemStack(Blocks.iron_bars),
                    'T', getTankItem(),
                    'D', new ItemStack(Blocks.dispenser));
        }

        alpha = EnumMachineAlpha.STEAM_TRAP_AUTO;
        if (alpha.isAvaliable()) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    " G ",
                    "RTR",
                    " D ",
                    'G', new ItemStack(Blocks.iron_bars),
                    'T', getTankItem(),
                    'R', "dustRedstone",
                    'D', new ItemStack(Blocks.dispenser));
            if (EnumMachineAlpha.STEAM_TRAP_MANUAL.isAvaliable()) {
                CraftingPlugin.addShapedRecipe(stack,
                        "RTR",
                        'T', EnumMachineAlpha.STEAM_TRAP_MANUAL.getItem(),
                        'R', "dustRedstone");
                CraftingPlugin.addShapelessRecipe(EnumMachineAlpha.STEAM_TRAP_MANUAL.getItem(), stack);
            }
        }
    }

    private ItemStack getTankItem() {
        ItemStack tank;
        if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvaliable())
            tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem();
        else if (EnumMachineBeta.TANK_STEEL_WALL.isAvaliable())
            tank = EnumMachineBeta.TANK_STEEL_WALL.getItem();
        else
            tank = RailcraftItem.plate.getStack(1, EnumPlate.STEEL);
        if (tank == null)
            tank = RailcraftItem.ingot.getStack(1, ItemIngot.EnumIngot.STEEL);
        if (tank == null)
            tank = new ItemStack(Blocks.iron_block);
        return tank;
    }

    @Override
    public void postInit() {
        IC2Plugin.nerfSyntheticCoal();
    }

}
