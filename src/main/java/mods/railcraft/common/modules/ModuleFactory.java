/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.*;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.misc.BallastRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ModuleFactory extends RailcraftModule {
    private static final int COKE_COOK_TIME = 1800;
    private static final int COKE_COOK_CREOSOTE = 500;

    private static void registerAltSteelFurnaceRecipe() {
        List<ItemStack> iron = OreDictionary.getOres("nuggetIron");
        for (ItemStack nugget : iron) {
            CraftingPlugin.addFurnaceRecipe(nugget, RailcraftItem.nugget.getStack(ItemNugget.EnumNugget.STEEL), 0);
        }
    }

    @Override
    public void initFirst() {
        BlockCube.registerBlock();
        RailcraftToolItems.registerCoalCoke();
        BlockRCAnvil.registerBlock();

        if (BlockRCAnvil.getBlock() != null)
            CraftingPlugin.addShapedRecipe(new ItemStack(BlockRCAnvil.getBlock(), 1, 0),
                    "BBB",
                    " I ",
                    "III",
                    'B', "blockSteel",
                    'I', "ingotSteel");

        EnumMachineAlpha alpha = EnumMachineAlpha.COKE_OVEN;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    "MBM",
                    "BMB",
                    "MBM",
                    'B', "ingotBrick",
                    'M', "sand");

            if (RailcraftToolItems.getCoalCoke() != null)
                RailcraftCraftingManager.cokeOven.addRecipe(new ItemStack(Items.coal, 1, 0), true, false, RailcraftToolItems.getCoalCoke(), Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE), COKE_COOK_TIME);
        }

        alpha = EnumMachineAlpha.STEAM_OVEN;
        if (alpha.register())
            CraftingPlugin.addShapedRecipe(alpha.getItem(4),
                    "SSS",
                    "SFS",
                    "SSS",
                    'F', new ItemStack(Blocks.furnace),
                    'S', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL));

        alpha = EnumMachineAlpha.BLAST_FURNACE;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem(4);
            CraftingPlugin.addShapedRecipe(stack,
                    "MBM",
                    "BPB",
                    "MBM",
                    'B', new ItemStack(Blocks.nether_brick),
                    'M', new ItemStack(Blocks.soul_sand),
                    'P', Items.magma_cream);

            int burnTime = 1280;
            ItemIngot.EnumIngot steel = ItemIngot.EnumIngot.STEEL;
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_ingot), false, false, burnTime, RailcraftItem.ingot.getStack(1, steel));

            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_helmet), true, false, burnTime * 5, RailcraftItem.ingot.getStack(5, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_chestplate), true, false, burnTime * 8, RailcraftItem.ingot.getStack(8, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_leggings), true, false, burnTime * 7, RailcraftItem.ingot.getStack(7, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_boots), true, false, burnTime * 4, RailcraftItem.ingot.getStack(4, steel));

            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_horse_armor), true, false, burnTime * 4, RailcraftItem.ingot.getStack(4, steel));

            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_sword), true, false, burnTime * 2, RailcraftItem.ingot.getStack(2, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_shovel), true, false, burnTime, RailcraftItem.ingot.getStack(1, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_pickaxe), true, false, burnTime * 3, RailcraftItem.ingot.getStack(3, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_axe), true, false, burnTime * 3, RailcraftItem.ingot.getStack(3, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_hoe), true, false, burnTime * 2, RailcraftItem.ingot.getStack(2, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.shears), true, false, burnTime * 2, RailcraftItem.ingot.getStack(2, steel));

            RailcraftCraftingManager.blastFurnace.addRecipe(ItemCrowbar.getItem(), true, false, burnTime * 3, RailcraftItem.ingot.getStack(3, steel));

            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.iron_door), false, false, burnTime * 6, RailcraftItem.ingot.getStack(6, steel));

            int recycleTime = burnTime / 2;
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelHelm(), false, false, recycleTime * 4, RailcraftItem.ingot.getStack(4, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelArmor(), false, false, recycleTime * 6, RailcraftItem.ingot.getStack(6, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelLegs(), false, false, recycleTime * 5, RailcraftItem.ingot.getStack(5, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelBoots(), false, false, recycleTime * 3, RailcraftItem.ingot.getStack(3, steel));

            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelSword(), false, false, recycleTime * 1, RailcraftItem.ingot.getStack(1, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelPickaxe(), false, false, recycleTime * 2, RailcraftItem.ingot.getStack(2, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelHoe(), false, false, recycleTime * 1, RailcraftItem.ingot.getStack(1, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelAxe(), false, false, recycleTime * 2, RailcraftItem.ingot.getStack(2, steel));
            RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelShears(), false, false, recycleTime * 1, RailcraftItem.ingot.getStack(1, steel));
        }

        alpha = EnumMachineAlpha.ROCK_CRUSHER;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem(4);
            CraftingPlugin.addShapedRecipe(stack,
                    "PDP",
                    "DSD",
                    "PDP",
                    'D', "gemDiamond",
                    'P', new ItemStack(Blocks.piston),
                    'S', "blockSteel");

            IRockCrusherRecipe recipe;

            if (EnumCube.CRUSHED_OBSIDIAN.isEnabled() || RailcraftItem.dust.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.obsidian), false, false);
                if (EnumCube.CRUSHED_OBSIDIAN.isEnabled())
                    recipe.addOutput(EnumCube.CRUSHED_OBSIDIAN.getItem(), 1.0f);
                if (RailcraftItem.dust.isEnabled()) {
                    recipe.addOutput(RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                    if (!EnumCube.CRUSHED_OBSIDIAN.isEnabled())
                        recipe.addOutput(RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 1.0f);
                }
            }


            if (EnumCube.CRUSHED_OBSIDIAN.isEnabled() && RailcraftItem.dust.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumCube.CRUSHED_OBSIDIAN.getItem(), true, false);
                recipe.addOutput(RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 1.0f);
                recipe.addOutput(RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
            }

            if (EnumMachineAlpha.COKE_OVEN.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumMachineAlpha.COKE_OVEN.getItem(), true, false);
                recipe.addOutput(new ItemStack(Items.brick, 3), 1.0f);
                recipe.addOutput(new ItemStack(Items.brick), 0.5f);
                recipe.addOutput(new ItemStack(Blocks.sand), 0.25f);
                recipe.addOutput(new ItemStack(Blocks.sand), 0.25f);
                recipe.addOutput(new ItemStack(Blocks.sand), 0.25f);
                recipe.addOutput(new ItemStack(Blocks.sand), 0.25f);
                recipe.addOutput(new ItemStack(Blocks.sand), 0.25f);
            }

            if (EnumMachineAlpha.BLAST_FURNACE.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumMachineAlpha.BLAST_FURNACE.getItem(), true, false);
                recipe.addOutput(new ItemStack(Blocks.nether_brick), 0.75f);
                recipe.addOutput(new ItemStack(Blocks.soul_sand), 0.75f);
                recipe.addOutput(new ItemStack(Items.blaze_powder), 0.05f);
            }

            if (EnumMachineAlpha.WORLD_ANCHOR.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumMachineAlpha.WORLD_ANCHOR.getItem(), true, false);
                recipe.addOutput(new ItemStack(Items.diamond), 0.5f);
                addAnchorOutputs(recipe);
            }

            if (EnumMachineAlpha.PERSONAL_ANCHOR.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumMachineAlpha.PERSONAL_ANCHOR.getItem(), true, false);
                recipe.addOutput(new ItemStack(Items.emerald), 0.5f);
                addAnchorOutputs(recipe);
            }

            if (EnumMachineAlpha.PASSIVE_ANCHOR.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumMachineAlpha.PASSIVE_ANCHOR.getItem(), true, false);
//                recipe.addOutput(new ItemStack(Items.emerald), 0.5f);
                addAnchorOutputs(recipe);
            }

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.cobblestone), false, false);
            recipe.addOutput(new ItemStack(Blocks.gravel), 1.0f);
            recipe.addOutput(new ItemStack(Items.flint), 0.10f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.mossy_cobblestone), false, false);
            recipe.addOutput(new ItemStack(Blocks.gravel), 1.0f);
            recipe.addOutput(new ItemStack(Blocks.vine), 0.10f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.gravel), false, false);
            recipe.addOutput(new ItemStack(Blocks.sand), 1.0f);
            recipe.addOutput(new ItemStack(Items.gold_nugget), 0.001f);
            recipe.addOutput(new ItemStack(Items.diamond), 0.00005f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone), false, false);
            recipe.addOutput(new ItemStack(Blocks.cobblestone), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.sandstone), false, false);
            recipe.addOutput(new ItemStack(Blocks.sand, 4), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.brick_block), false, false);
            recipe.addOutput(new ItemStack(Items.brick, 3), 1.0f);
            recipe.addOutput(new ItemStack(Items.brick), 0.5f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.clay), false, false);
            recipe.addOutput(new ItemStack(Items.clay_ball, 4), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stonebrick), false, false);
            recipe.addOutput(new ItemStack(Blocks.cobblestone), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_stairs), false, false);
            recipe.addOutput(new ItemStack(Blocks.gravel), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_brick_stairs), false, false);
            recipe.addOutput(new ItemStack(Blocks.cobblestone), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.nether_brick_stairs), false, false);
            recipe.addOutput(new ItemStack(Blocks.nether_brick), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.brick_stairs), false, false);
            recipe.addOutput(new ItemStack(Items.brick, 4), 1.0f);
            recipe.addOutput(new ItemStack(Items.brick), 0.5f);
            recipe.addOutput(new ItemStack(Items.brick), 0.5f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_slab, 1, 0), true, false);
            recipe.addOutput(new ItemStack(Blocks.cobblestone), 0.45f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_slab, 1, 1), true, false);
            recipe.addOutput(new ItemStack(Blocks.sand), 0.45f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_slab, 1, 3), true, false);
            recipe.addOutput(new ItemStack(Blocks.gravel), 0.45f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_slab, 1, 4), true, false);
            recipe.addOutput(new ItemStack(Items.brick), 1.0f);
            recipe.addOutput(new ItemStack(Items.brick), 0.75f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.stone_slab, 1, 5), true, false);
            recipe.addOutput(new ItemStack(Blocks.cobblestone), 0.45f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.ice), false, false);
            recipe.addOutput(new ItemStack(Blocks.snow), 0.85f);
            recipe.addOutput(new ItemStack(Items.snowball), 0.25f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.nether_brick_fence), false, false);
            recipe.addOutput(new ItemStack(Blocks.nether_brick), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.glowstone), false, false);
            recipe.addOutput(new ItemStack(Items.glowstone_dust, 3), 1.0f);
            recipe.addOutput(new ItemStack(Items.glowstone_dust), 0.75f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Blocks.redstone_lamp), false, false);
            recipe.addOutput(new ItemStack(Items.glowstone_dust, 3), 1.0f);
            recipe.addOutput(new ItemStack(Items.glowstone_dust), 0.75f);
            recipe.addOutput(new ItemStack(Items.redstone, 3), 1.0f);
            recipe.addOutput(new ItemStack(Items.redstone), 0.75f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Items.bone), false, false);
            recipe.addOutput(new ItemStack(Items.dye, 4, 15), 1.0f);

            recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Items.blaze_rod), false, false);
            recipe.addOutput(new ItemStack(Items.blaze_powder, 2), 1.0f);
            recipe.addOutput(new ItemStack(Items.blaze_powder), 0.25f);
            recipe.addOutput(new ItemStack(Items.blaze_powder), 0.25f);
            recipe.addOutput(new ItemStack(Items.blaze_powder), 0.25f);

            if (RailcraftItem.dust.isEnabled()) {
                recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(Items.coal, 1, 1), true, false);
                recipe.addOutput(RailcraftItem.dust.getStack(ItemDust.EnumDust.CHARCOAL), 1.0f);
            }
        }

        alpha = EnumMachineAlpha.ROLLING_MACHINE;
        if (alpha.register()) {
            ItemStack stack = alpha.getItem();
            CraftingPlugin.addShapedRecipe(stack,
                    "IPI",
                    "PCP",
                    "IPI",
                    'I', "ingotIron",
                    'P', Blocks.piston,
                    'C', "craftingTableWood");
        } else
            RollingMachineCraftingManager.copyRecipesToWorkbench();

        EnumMachineBeta metalsChest = EnumMachineBeta.METALS_CHEST;
        if (metalsChest.register())
            CraftingPlugin.addShapedRecipe(metalsChest.getItem(),
                    "GPG",
                    "PAP",
                    "GPG",
                    'A', new ItemStack(Blocks.anvil),
                    'P', new ItemStack(Blocks.piston),
                    'G', RailcraftItem.gear.getRecipeObject(ItemGear.EnumGear.STEEL));

        if (BlockCube.getBlock() != null) {
            EnumCube type = EnumCube.STEEL_BLOCK;
            if (RailcraftConfig.isSubBlockEnabled(type.getTag())) {
                initMetalBlock(Metal.STEEL);

                LootPlugin.addLootTool(type.getItem(), 1, 1, "steel.block");

                if (EnumMachineAlpha.BLAST_FURNACE.isAvaliable())
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Blocks.iron_block), false, false, 11520, EnumCube.STEEL_BLOCK.getItem());
            }

            type = EnumCube.COPPER_BLOCK;
            if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                initMetalBlock(Metal.COPPER);

            type = EnumCube.TIN_BLOCK;
            if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                initMetalBlock(Metal.TIN);

            type = EnumCube.LEAD_BLOCK;
            if (RailcraftConfig.isSubBlockEnabled(type.getTag()))
                initMetalBlock(Metal.LEAD);

            type = EnumCube.CRUSHED_OBSIDIAN;
            if (RailcraftConfig.isSubBlockEnabled(type.getTag())) {
                ItemStack stack = type.getItem();

                BallastRegistry.registerBallast(BlockCube.getBlock(), type.ordinal());

                if (IC2Plugin.isModInstalled() && RailcraftConfig.addObsidianRecipesToMacerator() && RailcraftItem.dust.isEnabled()) {
                    IC2Plugin.addMaceratorRecipe(new ItemStack(Blocks.obsidian), stack);
                    IC2Plugin.addMaceratorRecipe(stack, RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN));
                }
            }

            type = EnumCube.COKE_BLOCK;
            if (RailcraftConfig.isSubBlockEnabled(type.getTag())) {
                BlockCube.registerBlock();
                Block cube = BlockCube.getBlock();
                if (cube != null) {
                    ItemStack stack = type.getItem();
                    CraftingPlugin.addShapedRecipe(stack,
                            "CCC",
                            "CCC",
                            "CCC",
                            'C', RailcraftToolItems.getCoalCoke());
                    CraftingPlugin.addShapelessRecipe(RailcraftToolItems.getCoalCoke(9), stack);
                }
            }
        }
    }

    private void addAnchorOutputs(IRockCrusherRecipe recipe) {
        if (EnumCube.CRUSHED_OBSIDIAN.isEnabled()) {
            recipe.addOutput(EnumCube.CRUSHED_OBSIDIAN.getItem(), 1.0f);
            recipe.addOutput(EnumCube.CRUSHED_OBSIDIAN.getItem(), 0.5f);
        } else {
            recipe.addOutput(new ItemStack(Blocks.obsidian), 1.0f);
            recipe.addOutput(new ItemStack(Blocks.obsidian), 0.5f);
        }
        recipe.addOutput(new ItemStack(Blocks.obsidian), 0.25f);
        if (RailcraftItem.dust.isEnabled())
            recipe.addOutput(RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
        recipe.addOutput(new ItemStack(Items.gold_nugget, 16), 1.0f);
        recipe.addOutput(new ItemStack(Items.gold_nugget, 8), 0.5f);
        recipe.addOutput(new ItemStack(Items.gold_nugget, 8), 0.5f);
        recipe.addOutput(new ItemStack(Items.gold_nugget, 4), 0.5f);
    }

    @Override
    public void initSecond() {
        if (ModuleManager.isModuleLoaded(ModuleManager.Module.STRUCTURES)) {
            if (EnumMachineAlpha.BLAST_FURNACE.isAvaliable() && EnumBrick.INFERNAL.getBlock() != null) {

                ItemStack stack = EnumMachineAlpha.BLAST_FURNACE.getItem(4);
                CraftingPlugin.addShapedRecipe(stack,
                        " B ",
                        "BPB",
                        " B ",
                        'B', EnumBrick.INFERNAL.get(BrickVariant.BRICK, 1),
                        'P', Items.magma_cream);
            }
            if (EnumMachineAlpha.COKE_OVEN.isAvaliable() && EnumBrick.SANDY.getBlock() != null) {
                ItemStack stack = EnumMachineAlpha.COKE_OVEN.getItem();
                CraftingPlugin.addShapedRecipe(stack,
                        " B ",
                        " S ",
                        " B ",
                        'B', EnumBrick.SANDY.get(BrickVariant.BRICK, 1),
                        'S', "sand");
            }
        }

        if (EnumCube.COKE_BLOCK.isEnabled())
            RailcraftCraftingManager.cokeOven.addRecipe(new ItemStack(Blocks.coal_block), false, false, EnumCube.COKE_BLOCK.getItem(), Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE * 9), COKE_COOK_TIME * 9);

        if (Fluids.CREOSOTE.get() != null && RailcraftConfig.creosoteTorchOutput() > 0) {
            FluidStack creosote = Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME);
            for (ItemStack container : FluidHelper.getContainersFilledWith(creosote)) {
                CraftingPlugin.addShapedRecipe(new ItemStack(Blocks.torch, RailcraftConfig.creosoteTorchOutput()),
                        "C",
                        "W",
                        "S",
                        'C', container,
                        'W', Blocks.wool,
                        'S', "stickWood");
            }
            ForestryPlugin.instance().addCarpenterRecipe("torches", 10, Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), null, new ItemStack(Blocks.torch, RailcraftConfig.creosoteTorchOutput()),
                    "#",
                    "|",
                    '#', Blocks.wool,
                    '|', Items.stick);
        }
    }

    private void registerCrushedOreRecipe(ItemStack ore, ItemStack dust) {
        if (dust == null)
            return;
        dust = dust.copy();
        dust.stackSize = 2;

        IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(ore, true, false);
        recipe.addOutput(dust, 1.0f);
    }

    @Override
    public void postInit() {
        if (OreDictionary.getOres("blockSteel").isEmpty())
            OreDictionary.registerOre("blockSteel", Blocks.iron_block);

        if (!EnumMachineAlpha.BLAST_FURNACE.isAvaliable())
            registerAltSteelFurnaceRecipe();

        List<ItemStack> logs = new ArrayList<ItemStack>(25);
        logs.addAll(OreDictionary.getOres("logWood"));
        logs.addAll(OreDictionary.getOres("woodRubber"));
        for (ItemStack log : logs) {
            RailcraftCraftingManager.cokeOven.addRecipe(log, true, false, new ItemStack(Items.coal, 1, 1), Fluids.CREOSOTE.get(250), COKE_COOK_TIME);
        }

        if (IC2Plugin.isModInstalled()) {
            boolean classic = IC2Plugin.isClassic();
            ItemStack crushedIron = IC2Plugin.getItem(classic ? "ironDust" : "crushedIronOre");
            ItemStack crushedGold = IC2Plugin.getItem(classic ? "goldDust" : "crushedGoldOre");
            ItemStack crushedCopper = IC2Plugin.getItem(classic ? "copperDust" : "crushedCopperOre");
            ItemStack crushedTin = IC2Plugin.getItem(classic ? "tinDust" : "crushedTinOre");
            ItemStack crushedSilver = IC2Plugin.getItem(classic ? "silverDust" : "crushedSilverOre");
            ItemStack crushedLead = IC2Plugin.getItem("crushedLeadOre");
            ItemStack crushedUranium = IC2Plugin.getItem(classic ? "uraniumDrop" : "crushedUraniumOre");

            if (RailcraftConfig.canCrushOres()) {
                registerCrushedOreRecipe(new ItemStack(Blocks.iron_ore), crushedIron);
                registerCrushedOreRecipe(new ItemStack(Blocks.gold_ore), crushedGold);

                List<ItemStack> ores = OreDictionary.getOres("oreCopper");
                for (ItemStack ore : ores) {
                    registerCrushedOreRecipe(ore, crushedCopper);
                }

                ores = OreDictionary.getOres("oreTin");
                for (ItemStack ore : ores) {
                    registerCrushedOreRecipe(ore, crushedTin);
                }

                ores = OreDictionary.getOres("oreSilver");
                for (ItemStack ore : ores) {
                    registerCrushedOreRecipe(ore, crushedSilver);
                }

                ores = OreDictionary.getOres("oreLead");
                for (ItemStack ore : ores) {
                    registerCrushedOreRecipe(ore, crushedLead);
                }

                ores = OreDictionary.getOres("oreUranium");
                for (ItemStack ore : ores) {
                    registerCrushedOreRecipe(ore, crushedUranium);
                }
            }

            if (!RailcraftConfig.getRecipeConfig("ic2.macerator.ores"))
                IC2Plugin.removeMaceratorDustRecipes(crushedIron, crushedGold, crushedCopper, crushedTin, crushedSilver, crushedLead, crushedUranium);

            if (!RailcraftConfig.getRecipeConfig("ic2.macerator.bones"))
                IC2Plugin.removeMaceratorRecipes(new ItemStack(Items.dye, 1, 15));

            if (!RailcraftConfig.getRecipeConfig("ic2.macerator.blaze"))
                IC2Plugin.removeMaceratorRecipes(new ItemStack(Items.blaze_powder));

            if (!RailcraftConfig.getRecipeConfig("ic2.macerator.cobble"))
                IC2Plugin.removeMaceratorRecipes(new ItemStack(Blocks.cobblestone));

            if (!RailcraftConfig.getRecipeConfig("ic2.macerator.dirt"))
                IC2Plugin.removeMaceratorRecipes(new ItemStack(Blocks.dirt));
        }

        ForestryPlugin.instance().addCarpenterRecipe("ties", 40, Fluids.CREOSOTE.get(750), null, RailcraftItem.tie.getStack(1, EnumTie.WOOD),
                "###",
                '#', "slabWood");
    }

    @Override
    public void postInitNotLoaded() {
        RollingMachineCraftingManager.copyRecipesToWorkbench();
        registerAltSteelFurnaceRecipe();
    }

    private void initMetalBlock(Metal m) {
        OreDictionary.registerOre(m.getBlockTag(), m.getBlock());
        CraftingPlugin.addShapedRecipe(m.getBlock(),
                "III",
                "III",
                "III",
                'I', m.getIngotTag());
        CraftingPlugin.addShapelessRecipe(m.getIngot(9), m.getBlockTag());
    }
}
