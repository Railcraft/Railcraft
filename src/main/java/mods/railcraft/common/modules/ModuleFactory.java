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
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.*;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@RailcraftModule("factory")
public class ModuleFactory extends RailcraftModulePayload {
    private static final int COKE_COOK_TIME = 1800;
    private static final int COKE_COOK_CREOSOTE = 500;

    public ModuleFactory() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.anvil_steel,
                        RailcraftBlocks.cube,
                        RailcraftBlocks.machine_alpha,
                        RailcraftBlocks.machine_beta,
                        RailcraftItems.coke
                );
            }

            @Override
            public void preInit() {
                EnumMachineAlpha alpha = EnumMachineAlpha.COKE_OVEN;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem();
                    CraftingPlugin.addRecipe(stack,
                            "MBM",
                            "BMB",
                            "MBM",
                            'B', "ingotBrick",
                            'M', "sand");

                    ItemStack cokeStack = RailcraftItems.coke.getStack();
                    if (cokeStack != null)
                        RailcraftCraftingManager.cokeOven.addRecipe(new ItemStack(Items.COAL, 1, 0), true, false, cokeStack, Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE), COKE_COOK_TIME);
                }

                alpha = EnumMachineAlpha.STEAM_OVEN;
                if (alpha.isAvailable())
                    CraftingPlugin.addRecipe(alpha.getItem(4),
                            "SSS",
                            "SFS",
                            "SSS",
                            'F', new ItemStack(Blocks.FURNACE),
                            'S', RailcraftItems.plate.getRecipeObject(Metal.STEEL));

                alpha = EnumMachineAlpha.BLAST_FURNACE;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem(4);
                    CraftingPlugin.addRecipe(stack,
                            "MBM",
                            "BPB",
                            "MBM",
                            'B', new ItemStack(Blocks.NETHER_BRICK),
                            'M', new ItemStack(Blocks.SOUL_SAND),
                            'P', Items.MAGMA_CREAM);

                    int burnTime = 1280;
                    Metal steel = Metal.STEEL;
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_INGOT), false, false, burnTime, RailcraftItems.ingot.getStack(1, steel));

                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_HELMET), true, false, burnTime * 5, RailcraftItems.ingot.getStack(5, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_CHESTPLATE), true, false, burnTime * 8, RailcraftItems.ingot.getStack(8, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_LEGGINGS), true, false, burnTime * 7, RailcraftItems.ingot.getStack(7, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_BOOTS), true, false, burnTime * 4, RailcraftItems.ingot.getStack(4, steel));

                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_HORSE_ARMOR), true, false, burnTime * 4, RailcraftItems.ingot.getStack(4, steel));

                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_SWORD), true, false, burnTime * 2, RailcraftItems.ingot.getStack(2, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_SHOVEL), true, false, burnTime, RailcraftItems.ingot.getStack(1, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_PICKAXE), true, false, burnTime * 3, RailcraftItems.ingot.getStack(3, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_AXE), true, false, burnTime * 3, RailcraftItems.ingot.getStack(3, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_HOE), true, false, burnTime * 2, RailcraftItems.ingot.getStack(2, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.SHEARS), true, false, burnTime * 2, RailcraftItems.ingot.getStack(2, steel));

                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftItems.crowbarIron.getStack(), true, false, burnTime * 3, RailcraftItems.ingot.getStack(3, steel));

                    RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Items.IRON_DOOR), false, false, burnTime * 6, RailcraftItems.ingot.getStack(6, steel));

                    int recycleTime = burnTime / 2;
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelHelm(), false, false, recycleTime * 4, RailcraftItems.ingot.getStack(4, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelArmor(), false, false, recycleTime * 6, RailcraftItems.ingot.getStack(6, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelLegs(), false, false, recycleTime * 5, RailcraftItems.ingot.getStack(5, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelBoots(), false, false, recycleTime * 3, RailcraftItems.ingot.getStack(3, steel));

                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelSword(), false, false, recycleTime, RailcraftItems.ingot.getStack(1, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelPickaxe(), false, false, recycleTime * 2, RailcraftItems.ingot.getStack(2, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelHoe(), false, false, recycleTime, RailcraftItems.ingot.getStack(1, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelAxe(), false, false, recycleTime * 2, RailcraftItems.ingot.getStack(2, steel));
                    RailcraftCraftingManager.blastFurnace.addRecipe(RailcraftToolItems.getSteelShears(), false, false, recycleTime, RailcraftItems.ingot.getStack(1, steel));
                }

                alpha = EnumMachineAlpha.ROCK_CRUSHER;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem(4);
                    CraftingPlugin.addRecipe(stack,
                            "PDP",
                            "DSD",
                            "PDP",
                            'D', "gemDiamond",
                            'P', new ItemStack(Blocks.PISTON),
                            'S', "blockSteel");

                    ICrusherCraftingManager.ICrusherRecipe recipe;

                    if (EnumCube.CRUSHED_OBSIDIAN.isEnabled() || RailcraftItems.dust.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.OBSIDIAN), false, false);
                        if (EnumCube.CRUSHED_OBSIDIAN.isEnabled())
                            addOutput(recipe, EnumCube.CRUSHED_OBSIDIAN.getStack(), 1.0f);
                        if (RailcraftItems.dust.isEnabled()) {
                            addOutput(recipe, RailcraftItems.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                            if (!EnumCube.CRUSHED_OBSIDIAN.isEnabled())
                                addOutput(recipe, RailcraftItems.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 1.0f);
                        }
                    }


                    if (EnumCube.CRUSHED_OBSIDIAN.isEnabled() && RailcraftItems.dust.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumCube.CRUSHED_OBSIDIAN.getStack(), true, false);
                        addOutput(recipe, RailcraftItems.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 1.0f);
                        addOutput(recipe, RailcraftItems.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                    }

                    if (EnumMachineAlpha.COKE_OVEN.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumMachineAlpha.COKE_OVEN.getItem(), true, false);
                        addOutput(recipe, new ItemStack(Items.BRICK, 3), 1.0f);
                        addOutput(recipe, new ItemStack(Items.BRICK), 0.5f);
                        addOutput(recipe, new ItemStack(Blocks.SAND), 0.25f);
                        addOutput(recipe, new ItemStack(Blocks.SAND), 0.25f);
                        addOutput(recipe, new ItemStack(Blocks.SAND), 0.25f);
                        addOutput(recipe, new ItemStack(Blocks.SAND), 0.25f);
                        addOutput(recipe, new ItemStack(Blocks.SAND), 0.25f);
                    }

                    if (EnumMachineAlpha.BLAST_FURNACE.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumMachineAlpha.BLAST_FURNACE.getItem(), true, false);
                        addOutput(recipe, new ItemStack(Blocks.NETHER_BRICK), 0.75f);
                        addOutput(recipe, new ItemStack(Blocks.SOUL_SAND), 0.75f);
                        addOutput(recipe, new ItemStack(Items.BLAZE_POWDER), 0.05f);
                    }

                    if (EnumMachineAlpha.ANCHOR_WORLD.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumMachineAlpha.ANCHOR_WORLD.getItem(), true, false);
                        addOutput(recipe, new ItemStack(Items.DIAMOND), 0.5f);
                        addAnchorOutputs(recipe);
                    }

                    if (EnumMachineAlpha.ANCHOR_PERSONAL.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumMachineAlpha.ANCHOR_PERSONAL.getItem(), true, false);
                        addOutput(recipe, new ItemStack(Items.EMERALD), 0.5f);
                        addAnchorOutputs(recipe);
                    }

                    if (EnumMachineAlpha.ANCHOR_PASSIVE.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumMachineAlpha.ANCHOR_PASSIVE.getItem(), true, false);
//                addOutput(recipe, new ItemStack(Items.EMERALD), 0.5f);
                        addAnchorOutputs(recipe);
                    }

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.COBBLESTONE), false, false);
                    addOutput(recipe, new ItemStack(Blocks.GRAVEL), 1.0f);
                    addOutput(recipe, new ItemStack(Items.FLINT), 0.10f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE), false, false);
                    addOutput(recipe, new ItemStack(Blocks.GRAVEL), 1.0f);
                    addOutput(recipe, new ItemStack(Blocks.VINE), 0.10f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.GRAVEL), false, false);
                    addOutput(recipe, new ItemStack(Blocks.SAND), 1.0f);
                    addOutput(recipe, new ItemStack(Items.GOLD_NUGGET), 0.001f);
                    addOutput(recipe, new ItemStack(Items.DIAMOND), 0.00005f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE), false, false);
                    addOutput(recipe, new ItemStack(Blocks.COBBLESTONE), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.SANDSTONE), false, false);
                    addOutput(recipe, new ItemStack(Blocks.SAND, 4), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.BRICK_BLOCK), false, false);
                    addOutput(recipe, new ItemStack(Items.BRICK, 3), 1.0f);
                    addOutput(recipe, new ItemStack(Items.BRICK), 0.5f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.CLAY), false, false);
                    addOutput(recipe, new ItemStack(Items.CLAY_BALL, 4), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONEBRICK), false, false);
                    addOutput(recipe, new ItemStack(Blocks.COBBLESTONE), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_STAIRS), false, false);
                    addOutput(recipe, new ItemStack(Blocks.GRAVEL), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_BRICK_STAIRS), false, false);
                    addOutput(recipe, new ItemStack(Blocks.COBBLESTONE), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS), false, false);
                    addOutput(recipe, new ItemStack(Blocks.NETHER_BRICK), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.BRICK_STAIRS), false, false);
                    addOutput(recipe, new ItemStack(Items.BRICK, 4), 1.0f);
                    addOutput(recipe, new ItemStack(Items.BRICK), 0.5f);
                    addOutput(recipe, new ItemStack(Items.BRICK), 0.5f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 0), true, false);
                    addOutput(recipe, new ItemStack(Blocks.COBBLESTONE), 0.45f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 1), true, false);
                    addOutput(recipe, new ItemStack(Blocks.SAND), 0.45f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 3), true, false);
                    addOutput(recipe, new ItemStack(Blocks.GRAVEL), 0.45f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 4), true, false);
                    addOutput(recipe, new ItemStack(Items.BRICK), 1.0f);
                    addOutput(recipe, new ItemStack(Items.BRICK), 0.75f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 5), true, false);
                    addOutput(recipe, new ItemStack(Blocks.COBBLESTONE), 0.45f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.ICE), false, false);
                    addOutput(recipe, new ItemStack(Blocks.SNOW), 0.85f);
                    addOutput(recipe, new ItemStack(Items.SNOWBALL), 0.25f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.NETHER_BRICK_FENCE), false, false);
                    addOutput(recipe, new ItemStack(Blocks.NETHER_BRICK), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.GLOWSTONE), false, false);
                    addOutput(recipe, new ItemStack(Items.GLOWSTONE_DUST, 3), 1.0f);
                    addOutput(recipe, new ItemStack(Items.GLOWSTONE_DUST), 0.75f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Blocks.REDSTONE_LAMP), false, false);
                    addOutput(recipe, new ItemStack(Items.GLOWSTONE_DUST, 3), 1.0f);
                    addOutput(recipe, new ItemStack(Items.GLOWSTONE_DUST), 0.75f);
                    addOutput(recipe, new ItemStack(Items.REDSTONE, 3), 1.0f);
                    addOutput(recipe, new ItemStack(Items.REDSTONE), 0.75f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Items.BONE), false, false);
                    addOutput(recipe, new ItemStack(Items.DYE, 4, 15), 1.0f);

                    recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Items.BLAZE_ROD), false, false);
                    addOutput(recipe, new ItemStack(Items.BLAZE_POWDER, 2), 1.0f);
                    addOutput(recipe, new ItemStack(Items.BLAZE_POWDER), 0.25f);
                    addOutput(recipe, new ItemStack(Items.BLAZE_POWDER), 0.25f);
                    addOutput(recipe, new ItemStack(Items.BLAZE_POWDER), 0.25f);

                    if (RailcraftItems.dust.isEnabled()) {
                        recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(Items.COAL, 1, 1), true, false);
                        addOutput(recipe, RailcraftItems.dust.getStack(ItemDust.EnumDust.CHARCOAL), 1.0f);
                    }
                }

                alpha = EnumMachineAlpha.ROLLING_MACHINE;
                if (alpha.isAvailable()) {
                    ItemStack stack = alpha.getItem();
                    CraftingPlugin.addRecipe(stack,
                            "IPI",
                            "PCP",
                            "IPI",
                            'I', "ingotIron",
                            'P', Blocks.PISTON,
                            'C', "craftingTableWood");
                } else
                    RollingMachineCraftingManager.copyRecipesToWorkbench();

                EnumMachineBeta metalsChest = EnumMachineBeta.METALS_CHEST;
                if (metalsChest.isAvailable())
                    CraftingPlugin.addRecipe(metalsChest.getItem(),
                            "GPG",
                            "PAP",
                            "GPG",
                            'A', new ItemStack(Blocks.ANVIL),
                            'P', new ItemStack(Blocks.PISTON),
                            'G', RailcraftItems.gear.getRecipeObject(ItemGear.EnumGear.STEEL));

                if (BlockCube.getBlock() != null) {
                    EnumCube type = EnumCube.STEEL_BLOCK;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag())) {
                        initMetalBlock(Metal.STEEL);

                        LootPlugin.addLoot(type.getStack(), 1, 1, LootPlugin.Type.TOOL, "steel.block");

                        if (EnumMachineAlpha.BLAST_FURNACE.isAvailable())
                            RailcraftCraftingManager.blastFurnace.addRecipe(new ItemStack(Blocks.IRON_BLOCK), false, false, 11520, EnumCube.STEEL_BLOCK.getStack());
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
                        ItemStack stack = type.getStack();

                        BallastRegistry.registerBallast(BlockCube.getBlock(), type.ordinal());

                        if (IC2Plugin.isModInstalled() && RailcraftConfig.addObsidianRecipesToMacerator() && RailcraftItems.dust.isEnabled()) {
                            IC2Plugin.addMaceratorRecipe(new ItemStack(Blocks.OBSIDIAN), stack);
                            IC2Plugin.addMaceratorRecipe(stack, RailcraftItems.dust.getStack(ItemDust.EnumDust.OBSIDIAN));
                        }
                    }

                    type = EnumCube.COKE_BLOCK;
                    if (RailcraftConfig.isSubBlockEnabled(type.getTag())) {
                        Block cube = BlockCube.getBlock();
                        if (cube != null) {
                            ItemStack stack = type.getStack();
                            CraftingPlugin.addRecipe(stack,
                                    "CCC",
                                    "CCC",
                                    "CCC",
                                    'C', RailcraftItems.coke);
                            CraftingPlugin.addShapelessRecipe(RailcraftItems.coke.getStack(9), stack);
                        }
                    }
                }
            }

            private void initMetalBlock(Metal m) {
                String blockTag = m.getOreTag(Metal.Form.BLOCK);
                OreDictionary.registerOre(blockTag, m.getStack(Metal.Form.BLOCK));
                CraftingPlugin.addRecipe(m.getStack(Metal.Form.BLOCK),
                        "III",
                        "III",
                        "III",
                        'I', m.getOreTag(Metal.Form.INGOT));
                CraftingPlugin.addShapelessRecipe(m.getStack(Metal.Form.INGOT, 9), blockTag);
            }

            private void addAnchorOutputs(ICrusherCraftingManager.ICrusherRecipe recipe) {
                if (EnumCube.CRUSHED_OBSIDIAN.isEnabled()) {
                    addOutput(recipe, EnumCube.CRUSHED_OBSIDIAN.getStack(), 1.0f);
                    addOutput(recipe, EnumCube.CRUSHED_OBSIDIAN.getStack(), 0.5f);
                } else {
                    addOutput(recipe, new ItemStack(Blocks.OBSIDIAN), 1.0f);
                    addOutput(recipe, new ItemStack(Blocks.OBSIDIAN), 0.5f);
                }
                addOutput(recipe, new ItemStack(Blocks.OBSIDIAN), 0.25f);
                if (RailcraftItems.dust.isEnabled())
                    addOutput(recipe, RailcraftItems.dust.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                addOutput(recipe, new ItemStack(Items.GOLD_NUGGET, 16), 1.0f);
                addOutput(recipe, new ItemStack(Items.GOLD_NUGGET, 8), 0.5f);
                addOutput(recipe, new ItemStack(Items.GOLD_NUGGET, 8), 0.5f);
                addOutput(recipe, new ItemStack(Items.GOLD_NUGGET, 4), 0.5f);
            }

            @Override
            public void init() {
                if (RailcraftModuleManager.isModuleEnabled(ModuleStructures.class)) {
                    if (EnumMachineAlpha.BLAST_FURNACE.isAvailable() && BrickTheme.INFERNAL.getBlock() != null) {

                        ItemStack stack = EnumMachineAlpha.BLAST_FURNACE.getItem(4);
                        CraftingPlugin.addRecipe(stack,
                                " B ",
                                "BPB",
                                " B ",
                                'B', BrickTheme.INFERNAL.get(BrickVariant.BRICK, 1),
                                'P', Items.MAGMA_CREAM);
                    }
                    if (EnumMachineAlpha.COKE_OVEN.isAvailable() && BrickTheme.SANDY.getBlock() != null) {
                        ItemStack stack = EnumMachineAlpha.COKE_OVEN.getItem();
                        CraftingPlugin.addRecipe(stack,
                                " B ",
                                " S ",
                                " B ",
                                'B', BrickTheme.SANDY.get(BrickVariant.BRICK, 1),
                                'S', "sand");
                    }
                }

                if (EnumCube.COKE_BLOCK.isEnabled())
                    RailcraftCraftingManager.cokeOven.addRecipe(new ItemStack(Blocks.COAL_BLOCK), false, false, EnumCube.COKE_BLOCK.getStack(), Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE * 9), COKE_COOK_TIME * 9);

                if (Fluids.CREOSOTE.get() != null && RailcraftConfig.creosoteTorchOutput() > 0) {
                    FluidStack creosote = Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME);
                    for (ItemStack container : FluidHelper.getContainersFilledWith(creosote)) {
                        CraftingPlugin.addRecipe(new ItemStack(Blocks.TORCH, RailcraftConfig.creosoteTorchOutput()),
                                "C",
                                "W",
                                "S",
                                'C', container,
                                'W', Blocks.WOOL,
                                'S', "stickWood");
                    }
                    ForestryPlugin.instance().addCarpenterRecipe("torches", 10, Fluids.CREOSOTE.get(FluidHelper.BUCKET_VOLUME), null, new ItemStack(Blocks.TORCH, RailcraftConfig.creosoteTorchOutput()),
                            "#",
                            "|",
                            '#', Blocks.WOOL,
                            '|', Items.STICK);
                }
            }

            private void registerCrushedOreRecipe(ItemStack ore, ItemStack dust) {
                if (dust == null)
                    return;
                dust = dust.copy();
                dust.stackSize = 2;

                ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(ore, true, false);
                addOutput(recipe, dust, 1.0f);
            }

            @Override
            public void postInit() {
                if (OreDictionary.getOres("blockSteel").isEmpty())
                    OreDictionary.registerOre("blockSteel", Blocks.IRON_BLOCK);

                if (!EnumMachineAlpha.BLAST_FURNACE.isAvailable())
                    registerAltSteelFurnaceRecipe();

                List<ItemStack> logs = new ArrayList<ItemStack>(25);
                logs.addAll(OreDictionary.getOres("logWood"));
                logs.addAll(OreDictionary.getOres("woodRubber"));
                for (ItemStack log : logs) {
                    RailcraftCraftingManager.cokeOven.addRecipe(log, true, false, new ItemStack(Items.COAL, 1, 1), Fluids.CREOSOTE.get(250), COKE_COOK_TIME);
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
                        registerCrushedOreRecipe(new ItemStack(Blocks.IRON_ORE), crushedIron);
                        registerCrushedOreRecipe(new ItemStack(Blocks.GOLD_ORE), crushedGold);

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
                        IC2Plugin.removeMaceratorRecipes(new ItemStack(Items.DYE, 1, 15));

                    if (!RailcraftConfig.getRecipeConfig("ic2.macerator.blaze"))
                        IC2Plugin.removeMaceratorRecipes(new ItemStack(Items.BLAZE_POWDER));

                    if (!RailcraftConfig.getRecipeConfig("ic2.macerator.cobble"))
                        IC2Plugin.removeMaceratorRecipes(new ItemStack(Blocks.COBBLESTONE));

                    if (!RailcraftConfig.getRecipeConfig("ic2.macerator.dirt"))
                        IC2Plugin.removeMaceratorRecipes(new ItemStack(Blocks.DIRT));
                }

                ForestryPlugin.instance().addCarpenterRecipe("ties", 40, Fluids.CREOSOTE.get(750), null, RailcraftItems.tie.getStack(1, EnumTie.WOOD),
                        "###",
                        '#', "slabWood");
            }

            private void addOutput(ICrusherCraftingManager.ICrusherRecipe recipe, @Nullable ItemStack output, float chance) {
                if (output == null)
                    return;
                recipe.addOutput(output, RailcraftCraftingManager.rockCrusher.createGenRule(chance));
            }
        });
        setDisabledEventHandler(new ModuleEventHandler() {
            @Override
            public void postInit() {
                RollingMachineCraftingManager.copyRecipesToWorkbench();
                registerAltSteelFurnaceRecipe();
            }
        });
    }

    private void registerAltSteelFurnaceRecipe() {
        List<ItemStack> iron = OreDictionary.getOres("nuggetIron");
        for (ItemStack nugget : iron) {
            CraftingPlugin.addFurnaceRecipe(nugget, RailcraftItems.nugget.getStack(Metal.STEEL), 0);
        }
    }
}
