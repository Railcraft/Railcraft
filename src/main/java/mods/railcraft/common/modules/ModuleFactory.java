/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.machine.equipment.EquipmentVariant;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

@RailcraftModule(value = "railcraft:factory", description = "coke oven, blast furnace, rolling machine, rock crusher, etc...")
public class ModuleFactory extends RailcraftModulePayload {
    private static final int COKE_COOK_TIME = 1800;
    private static final int COKE_COOK_CREOSOTE = 500;

    public ModuleFactory() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.ANVIL_STEEL,
                        RailcraftBlocks.EQUIPMENT,
                        RailcraftItems.COKE,
                        RailcraftBlocks.COKE_OVEN,
                        RailcraftBlocks.BLAST_FURNACE,
                        RailcraftBlocks.ROCK_CRUSHER,
                        RailcraftBlocks.STEAM_OVEN,
                        RailcraftBlocks.TANK_IRON_GAUGE,
                        RailcraftBlocks.TANK_IRON_VALVE,
                        RailcraftBlocks.TANK_IRON_WALL,
                        RailcraftBlocks.TANK_STEEL_GAUGE,
                        RailcraftBlocks.TANK_STEEL_VALVE,
                        RailcraftBlocks.TANK_STEEL_WALL,
                        RailcraftBlocks.TANK_WATER,
                        RailcraftBlocks.CHEST_METALS
                );
            }

            @Override
            public void init() {
                {
                    int burnTime = 1280;
                    Metal steel = Metal.STEEL;
                    ItemDust.EnumDust slag = ItemDust.EnumDust.SLAG;
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_INGOT), burnTime, RailcraftItems.INGOT.getStack(1, steel), RailcraftItems.DUST.getStack(1, slag));

                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_HELMET), burnTime * 5, RailcraftItems.INGOT.getStack(5, steel), RailcraftItems.DUST.getStack(5, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_CHESTPLATE), burnTime * 8, RailcraftItems.INGOT.getStack(8, steel), RailcraftItems.DUST.getStack(8, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_LEGGINGS), burnTime * 7, RailcraftItems.INGOT.getStack(7, steel), RailcraftItems.DUST.getStack(7, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_BOOTS), burnTime * 4, RailcraftItems.INGOT.getStack(4, steel), RailcraftItems.DUST.getStack(4, slag));

                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_HORSE_ARMOR), burnTime * 4, RailcraftItems.INGOT.getStack(4, steel), RailcraftItems.DUST.getStack(4, slag));

                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_SWORD), burnTime * 2, RailcraftItems.INGOT.getStack(2, steel), RailcraftItems.DUST.getStack(2, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_SHOVEL), burnTime, RailcraftItems.INGOT.getStack(1, steel), RailcraftItems.DUST.getStack(1, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_PICKAXE), burnTime * 3, RailcraftItems.INGOT.getStack(3, steel), RailcraftItems.DUST.getStack(3, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_AXE), burnTime * 3, RailcraftItems.INGOT.getStack(3, steel), RailcraftItems.DUST.getStack(3, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_HOE), burnTime * 2, RailcraftItems.INGOT.getStack(2, steel), RailcraftItems.DUST.getStack(2, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.SHEARS), burnTime * 2, RailcraftItems.INGOT.getStack(2, steel), RailcraftItems.DUST.getStack(2, slag));

                    //TODO move to respective classes
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.CROWBAR_IRON.getIngredient(), burnTime * 3, RailcraftItems.INGOT.getStack(3, steel), RailcraftItems.DUST.getStack(3, slag));

                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Items.IRON_DOOR), burnTime * 6, RailcraftItems.INGOT.getStack(6, steel), RailcraftItems.DUST.getStack(6, slag));
                    BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_TRAPDOOR)), burnTime * 6, RailcraftItems.INGOT.getStack(4, steel), RailcraftItems.DUST.getStack(4, slag));

                    int recycleTime = burnTime / 2;
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.ARMOR_HELMET_STEEL.getIngredient(), recycleTime * 4, RailcraftItems.INGOT.getStack(4, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.ARMOR_CHESTPLATE_STEEL.getIngredient(), recycleTime * 6, RailcraftItems.INGOT.getStack(6, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.ARMOR_LEGGINGS_STEEL.getIngredient(), recycleTime * 5, RailcraftItems.INGOT.getStack(5, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.ARMOR_BOOTS_STEEL.getIngredient(), recycleTime * 3, RailcraftItems.INGOT.getStack(3, steel), ItemStack.EMPTY);

                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.SWORD_STEEL.getIngredient(), recycleTime, RailcraftItems.INGOT.getStack(1, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.PICKAXE_STEEL.getIngredient(), recycleTime * 2, RailcraftItems.INGOT.getStack(2, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.HOE_STEEL.getIngredient(), recycleTime, RailcraftItems.INGOT.getStack(1, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.AXE_STEEL.getIngredient(), recycleTime * 2, RailcraftItems.INGOT.getStack(2, steel), ItemStack.EMPTY);
                    BlastFurnaceCraftingManager.getInstance().addRecipe(RailcraftItems.SHEARS_STEEL.getIngredient(), recycleTime, RailcraftItems.INGOT.getStack(1, steel), ItemStack.EMPTY);
                }
                {
                    if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled() || RailcraftItems.DUST.isEnabled()) {
                        ICrusherCraftingManager.ICrusherRecipeBuilder builder = RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                                .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled())
                            builder.addOutput(EnumGeneric.CRUSHED_OBSIDIAN.getStack());
                        if (RailcraftItems.DUST.isEnabled()) {
                            builder.addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                            if (!EnumGeneric.CRUSHED_OBSIDIAN.isEnabled())
                                builder.addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN));
                        }
                        builder.buildAndRegister();
                    }


                    if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled() && RailcraftItems.DUST.isEnabled()) {
                        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                                .input(EnumGeneric.CRUSHED_OBSIDIAN.getIngredient())
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f)
                                .buildAndRegister();
                    }

                    //TODO move to individual classes once we do split up
                    if (WorldspikeVariant.STANDARD.isAvailable()) {
//                        RockCrusherCraftingManager.getInstance().createAndAddRecipe(, true, false);
                        getWorldSpikeBuilder()
                                .input(WorldspikeVariant.STANDARD.getIngredient())
                                .addOutput(new ItemStack(Items.DIAMOND), 0.5f)
                                .buildAndRegister();

                    }

                    if (WorldspikeVariant.PERSONAL.isAvailable()) {
                        getWorldSpikeBuilder()
                                .input(WorldspikeVariant.PERSONAL.getIngredient())
                                .addOutput(new ItemStack(Items.EMERALD), 0.5f)
                                .buildAndRegister();
//                        RockCrusherCraftingManager.getInstance().createAndAddRecipe(WorldspikeVariant.PERSONAL.getStack(), true, false);
//                        addOutput(new ItemStack(Items.EMERALD), 0.5f);
//                        addWorldspikeOutputs(recipe);
                    }

                    if (WorldspikeVariant.PASSIVE.isAvailable()) {
                        getWorldSpikeBuilder()
                                .input(WorldspikeVariant.PASSIVE.getIngredient())
                                .addOutput(new ItemStack(Blocks.PRISMARINE), 0.5f)
                                .buildAndRegister();
//                        RockCrusherCraftingManager.getInstance().createAndAddRecipe(WorldspikeVariant.PASSIVE.getStack(), true, false);
//                addOutput(new ItemStack(Items.EMERALD), 0.5f);
//                        addWorldspikeOutputs(recipe);
                    }

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.COBBLESTONE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.COBBLESTONE), false, false);
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Items.FLINT), 0.10f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.MOSSY_COBBLESTONE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE), false, false);
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Blocks.VINE), 0.80f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.GRAVEL)))
//                            .createAndAddRecipe(new ItemStack(Blocks.GRAVEL), false, false);
                            .addOutput(new ItemStack(Blocks.SAND))
                            .addOutput(new ItemStack(Items.GOLD_NUGGET), 0.001f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.00005f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE), false, false);
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.SANDSTONE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.SANDSTONE), false, false);
                            .addOutput(new ItemStack(Blocks.SAND, 4))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.BRICK_BLOCK)))
//                            .createAndAddRecipe(new ItemStack(Blocks.BRICK_BLOCK), false, false);
                            .addOutput(new ItemStack(Items.BRICK, 3))
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.CLAY)))
//                            .createAndAddRecipe(new ItemStack(Blocks.CLAY), false, false);
                            .addOutput(new ItemStack(Items.CLAY_BALL, 4))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONEBRICK)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONEBRICK), false, false);
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE_STAIRS)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_STAIRS), false, false);
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE_BRICK_STAIRS)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_BRICK_STAIRS), false, false);
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.NETHER_BRICK_STAIRS)))
//                            .createAndAddRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS), false, false);
                            .addOutput(new ItemStack(Blocks.NETHER_BRICK))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.BIRCH_STAIRS)))
//                            .createAndAddRecipe(new ItemStack(Blocks.BRICK_STAIRS), false, false);
                            .addOutput(new ItemStack(Items.BRICK, 4))
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromStacks(new ItemStack(Blocks.STONE_SLAB, 1, 0)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 0), true, false);
                            .addOutput(new ItemStack(Blocks.COBBLESTONE), 0.45f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromStacks(new ItemStack(Blocks.STONE_SLAB, 1, 1)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 1), true, false);
                            .addOutput(new ItemStack(Blocks.SAND), 0.45f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromStacks(new ItemStack(Blocks.STONE_SLAB, 1, 3)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 3), true, false);
                            .addOutput(new ItemStack(Blocks.GRAVEL), 0.45f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromStacks(new ItemStack(Blocks.STONE_SLAB, 1, 4)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 4), true, false);
                            .addOutput(new ItemStack(Items.BRICK))
                            .addOutput(new ItemStack(Items.BRICK), 0.75f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromStacks(new ItemStack(Blocks.STONE_SLAB, 1, 5)))
//                            .createAndAddRecipe(new ItemStack(Blocks.STONE_SLAB, 1, 5), true, false);
                            .addOutput(new ItemStack(Blocks.COBBLESTONE), 0.45f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.ICE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.ICE), false, false);
                            .addOutput(new ItemStack(Blocks.SNOW), 0.85f)
                            .addOutput(new ItemStack(Items.SNOWBALL), 0.25f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.NETHER_BRICK_FENCE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.NETHER_BRICK_FENCE), false, false);
                            .addOutput(new ItemStack(Blocks.NETHER_BRICK))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLOWSTONE)))
//                            .createAndAddRecipe(new ItemStack(Blocks.GLOWSTONE), false, false);
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST, 3))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.75f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Item.getItemFromBlock(Blocks.REDSTONE_LAMP)))
//                            createAndAddRecipe(new ItemStack(Blocks.REDSTONE_LAMP), false, false);
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST, 3))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.75f)
                            .addOutput(new ItemStack(Items.REDSTONE, 3))
                            .addOutput(new ItemStack(Items.REDSTONE), 0.75f)
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Items.BONE))
                            .addOutput(new ItemStack(Items.DYE, 4, 15))
                            .buildAndRegister();

                    RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                            .input(Ingredient.fromItem(Items.BLAZE_ROD))
                            .addOutput(new ItemStack(Items.BLAZE_POWDER, 2))
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .buildAndRegister();
//                    RockCrusherCraftingManager.getInstance().createAndAddRecipe(new ItemStack(Items.BLAZE_ROD), false, false);


                    if (RailcraftItems.DUST.isEnabled()) {
                        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                                .input(Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 0)))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.COAL))
                                .buildAndRegister();
//                        RockCrusherCraftingManager.getInstance().createAndAddRecipe(new ItemStack(Items.COAL, 1, 0), true, false);
//                        addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.COAL));

                        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                                .input(Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 1)))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.CHARCOAL))
                                .buildAndRegister();
//                        RockCrusherCraftingManager.getInstance().createAndAddRecipe(new ItemStack(Items.COAL, 1, 1), true, false);
//                        addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.CHARCOAL));

                        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                                .input(Ingredient.fromItem(Items.ENDER_PEARL))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.ENDER))
                                .buildAndRegister();
//                        RockCrusherCraftingManager.getInstance().createAndAddRecipe(new ItemStack(Items.ENDER_PEARL), false, false);
//                        addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.ENDER));
                    }
                }

                //TODO class declaration
//                EnumMachineBeta metalsChest = EnumMachineBeta.METALS_CHEST;
//                if (metalsChest.isAvailable())
//                    CraftingPlugin.addRecipe(metalsChest.getStack(),
//                            "GPG",
//                            "PAP",
//                            "GPG",
//                            'A', new ItemStack(Blocks.ANVIL),
//                            'P', new ItemStack(Blocks.PISTON),
//                            'G', "gearSteel");
                if (RailcraftModuleManager.isModuleEnabled(ModuleStructures.class)) {
                    if (RailcraftBlocks.BLAST_FURNACE.isLoaded() && BrickTheme.INFERNAL.getBlock() != null) {

                        ItemStack stack = RailcraftBlocks.BLAST_FURNACE.getStack(4);
                        CraftingPlugin.addRecipe(stack,
                                " B ",
                                "BPB",
                                " B ",
                                'B', BrickTheme.INFERNAL.getStack(1, BrickVariant.BRICK),
                                'P', Items.MAGMA_CREAM);
                    }
                    if (RailcraftBlocks.COKE_OVEN.isLoaded() && BrickTheme.SANDY.getBlock() != null) {
                        ItemStack stack = RailcraftBlocks.COKE_OVEN.getStack();
                        CraftingPlugin.addRecipe(stack,
                                " B ",
                                " S ",
                                " B ",
                                'B', BrickTheme.SANDY.getStack(1, BrickVariant.BRICK),
                                'S', "sand");
                    }
                }

                if (EnumGeneric.BLOCK_COKE.isEnabled()) {
                    RailcraftCraftingManager.getCokeOvenCraftings().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.COAL_BLOCK)), EnumGeneric.BLOCK_COKE.getStack(), Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE * 9), COKE_COOK_TIME * 9);
                    ItemStack stack = EnumGeneric.BLOCK_COKE.getStack();
                    CraftingPlugin.addRecipe(stack,
                            "CCC",
                            "CCC",
                            "CCC",
                            'C', RailcraftItems.COKE);
                    CraftingPlugin.addShapelessRecipe(RailcraftItems.COKE.getStack(9), stack);
                }
            }

            private ICrusherCraftingManager.ICrusherRecipeBuilder getWorldSpikeBuilder() {
                ICrusherCraftingManager.ICrusherRecipeBuilder builder = RockCrusherCraftingManager.getInstance().createRecipeBuilder();
                if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled()) {
                    builder.addOutput(EnumGeneric.CRUSHED_OBSIDIAN.getStack());
                    builder.addOutput(EnumGeneric.CRUSHED_OBSIDIAN.getStack(), 0.5f);
                } else {
                    builder.addOutput(new ItemStack(Blocks.OBSIDIAN));
                    builder.addOutput(new ItemStack(Blocks.OBSIDIAN), 0.5f);
                }
                builder.addOutput(new ItemStack(Blocks.OBSIDIAN), 0.25f);
                if (RailcraftItems.DUST.isEnabled()) {
                    builder.addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                }
                builder.addOutput(new ItemStack(Items.GOLD_NUGGET, 16));
                builder.addOutput(new ItemStack(Items.GOLD_NUGGET, 8), 0.5f);
                builder.addOutput(new ItemStack(Items.GOLD_NUGGET, 8), 0.5f);
                builder.addOutput(new ItemStack(Items.GOLD_NUGGET, 4), 0.5f);
                return builder;
            }

            private void registerCrushedOreRecipe(ItemStack ore, ItemStack dust) {
                if (InvTools.isEmpty(dust))
                    return;
                dust = dust.copy();
                setSize(dust, 2);

                RockCrusherCraftingManager.getInstance().
                        createRecipeBuilder()
                        .input(Ingredient.fromStacks(ore))
                        .addOutput(dust)
                        .buildAndRegister();
            }

            @Override
            public void postInit() {
                //TODO this is not right
                if (!EquipmentVariant.ROLLING_MACHINE_POWERED.isAvailable())
                    RollingMachineCraftingManager.copyRecipesToWorkbench();
                if (!RailcraftBlocks.BLAST_FURNACE.isEnabled() || RailcraftConfig.forceEnableSteelRecipe())
                    registerAltSteelFurnaceRecipe();

                List<ItemStack> logs = new ArrayList<>(25);
                logs.addAll(OreDictionary.getOres("logWood"));
                logs.addAll(OreDictionary.getOres("woodRubber"));
                RailcraftCraftingManager.getCokeOvenCraftings().addRecipe(Ingredient.fromStacks(logs.toArray(new ItemStack[0])), new ItemStack(Items.COAL, 1, 1), Fluids.CREOSOTE.get(250), COKE_COOK_TIME);

                if (Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC)) {
                    boolean classic = Mod.IC2_CLASSIC.isLoaded();
                    ItemStack crushedIron = classic ? ModItems.DUST_IRON.get() : ModItems.CRUSHED_IRON.get();
                    ItemStack crushedGold = classic ? ModItems.DUST_GOLD.get() : ModItems.CRUSHED_GOLD.get();
                    ItemStack crushedCopper = classic ? ModItems.DUST_COPPER.get() : ModItems.CRUSHED_COPPER.get();
                    ItemStack crushedTin = classic ? ModItems.DUST_TIN.get() : ModItems.CRUSHED_TIN.get();
                    ItemStack crushedSilver = classic ? ModItems.DUST_SILVER.get() : ModItems.CRUSHED_SILVER.get();
                    ItemStack crushedLead = ModItems.CRUSHED_LEAD.get();
                    ItemStack crushedUranium = classic ? ModItems.URANIUM_DROP.get() : ModItems.CRUSHED_URANIUM.get();

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

                    if (RailcraftConfig.getRecipeConfig("ic2.macerator.ores")) {

                        ItemStack firestoneore = EnumOreMagic.FIRESTONE.getStack();
                        ItemStack rawfirestone = RailcraftItems.FIRESTONE_RAW.getStack();
                        IC2Plugin.addMaceratorRecipe(firestoneore, rawfirestone);

                        List<ItemStack> ores = OreDictionary.getOres("orePoorCopper");
                        for (ItemStack ore : ores) {
                            IC2Plugin.addMaceratorRecipe(ore, 3, crushedCopper, 2);
                        }

                        ores = OreDictionary.getOres("orePoorTin");
                        for (ItemStack ore : ores) {
                            IC2Plugin.addMaceratorRecipe(ore, 3, crushedTin, 2);
                        }

                        ores = OreDictionary.getOres("orePoorIron");
                        for (ItemStack ore : ores) {
                            IC2Plugin.addMaceratorRecipe(ore, 3, crushedIron, 2);
                        }

                        ores = OreDictionary.getOres("orePoorGold");
                        for (ItemStack ore : ores) {
                            IC2Plugin.addMaceratorRecipe(ore, 3, crushedGold, 2);
                        }

                        ores = OreDictionary.getOres("orePoorSilver");
                        for (ItemStack ore : ores) {
                            IC2Plugin.addMaceratorRecipe(ore, 3, crushedSilver, 2);
                        }

                        ores = OreDictionary.getOres("orePoorLead");
                        for (ItemStack ore : ores) {
                            IC2Plugin.addMaceratorRecipe(ore, 3, crushedLead, 2);
                        }
                    } else {
                        IC2Plugin.removeMaceratorDustRecipes(crushedIron, crushedGold, crushedCopper, crushedTin, crushedSilver, crushedLead, crushedUranium);
                    }
                }
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

    void registerAltSteelFurnaceRecipe() {
        CraftingPlugin.addFurnaceRecipe(new ItemStack(Items.IRON_NUGGET, 1), RailcraftItems.NUGGET.getStack(Metal.STEEL), 0);
    }
}
