/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import mods.railcraft.api.crafting.IRockCrusherCrafter;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.machine.equipment.EquipmentVariant;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.crafting.*;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Code;
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
                        RailcraftBlocks.COKE_OVEN_RED,
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
                        RailcraftBlocks.CHEST_METALS,
                        RailcraftCarts.CHEST_METALS
                );
                Code.setValue(Crafters.class, null, BlastFurnaceCrafter.INSTANCE, "blastFurnace");
                Code.setValue(Crafters.class, null, CokeOvenCrafter.INSTANCE, "cokeOven");
                Code.setValue(Crafters.class, null, RockCrusherCrafter.INSTANCE, "rockCrusher");
            }

            @Override
            public void init() {
                {
                    int burnTime = 1280;
                    Metal steel = Metal.STEEL;
                    ItemDust.EnumDust slag = ItemDust.EnumDust.SLAG;
                    IBlastFurnaceCrafter bf = Crafters.blastFurnace();
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_INGOT), burnTime, RailcraftItems.INGOT.getStack(1, steel), RailcraftItems.DUST.getStack(1, slag));

                    bf.addRecipe(Ingredient.fromItem(Items.IRON_HELMET), burnTime * 5, RailcraftItems.INGOT.getStack(5, steel), RailcraftItems.DUST.getStack(5, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_CHESTPLATE), burnTime * 8, RailcraftItems.INGOT.getStack(8, steel), RailcraftItems.DUST.getStack(8, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_LEGGINGS), burnTime * 7, RailcraftItems.INGOT.getStack(7, steel), RailcraftItems.DUST.getStack(7, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_BOOTS), burnTime * 4, RailcraftItems.INGOT.getStack(4, steel), RailcraftItems.DUST.getStack(4, slag));

                    bf.addRecipe(Ingredient.fromItem(Items.IRON_HORSE_ARMOR), burnTime * 4, RailcraftItems.INGOT.getStack(4, steel), RailcraftItems.DUST.getStack(4, slag));

                    bf.addRecipe(Ingredient.fromItem(Items.IRON_SWORD), burnTime * 2, RailcraftItems.INGOT.getStack(2, steel), RailcraftItems.DUST.getStack(2, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_SHOVEL), burnTime, RailcraftItems.INGOT.getStack(1, steel), RailcraftItems.DUST.getStack(1, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_PICKAXE), burnTime * 3, RailcraftItems.INGOT.getStack(3, steel), RailcraftItems.DUST.getStack(3, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_AXE), burnTime * 3, RailcraftItems.INGOT.getStack(3, steel), RailcraftItems.DUST.getStack(3, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.IRON_HOE), burnTime * 2, RailcraftItems.INGOT.getStack(2, steel), RailcraftItems.DUST.getStack(2, slag));
                    bf.addRecipe(Ingredient.fromItem(Items.SHEARS), burnTime * 2, RailcraftItems.INGOT.getStack(2, steel), RailcraftItems.DUST.getStack(2, slag));

                    //TODO move to respective classes
                    bf.addRecipe(RailcraftItems.CROWBAR_IRON.getIngredient(), burnTime * 3, RailcraftItems.INGOT.getStack(3, steel), RailcraftItems.DUST.getStack(3, slag));

                    bf.addRecipe(Ingredient.fromItem(Items.IRON_DOOR), burnTime * 6, RailcraftItems.INGOT.getStack(6, steel), RailcraftItems.DUST.getStack(6, slag));
                    bf.addRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_TRAPDOOR)), burnTime * 6, RailcraftItems.INGOT.getStack(4, steel), RailcraftItems.DUST.getStack(4, slag));

                    int recycleTime = burnTime / 2;
                    bf.addRecipe(RailcraftItems.ARMOR_HELMET_STEEL.getIngredient(), recycleTime * 4, RailcraftItems.INGOT.getStack(4, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.ARMOR_CHESTPLATE_STEEL.getIngredient(), recycleTime * 6, RailcraftItems.INGOT.getStack(6, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.ARMOR_LEGGINGS_STEEL.getIngredient(), recycleTime * 5, RailcraftItems.INGOT.getStack(5, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.ARMOR_BOOTS_STEEL.getIngredient(), recycleTime * 3, RailcraftItems.INGOT.getStack(3, steel), ItemStack.EMPTY);

                    bf.addRecipe(RailcraftItems.SWORD_STEEL.getIngredient(), recycleTime, RailcraftItems.INGOT.getStack(1, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.PICKAXE_STEEL.getIngredient(), recycleTime * 2, RailcraftItems.INGOT.getStack(2, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.HOE_STEEL.getIngredient(), recycleTime, RailcraftItems.INGOT.getStack(1, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.AXE_STEEL.getIngredient(), recycleTime * 2, RailcraftItems.INGOT.getStack(2, steel), ItemStack.EMPTY);
                    bf.addRecipe(RailcraftItems.SHEARS_STEEL.getIngredient(), recycleTime, RailcraftItems.INGOT.getStack(1, steel), ItemStack.EMPTY);
                }
                {
                    IRockCrusherCrafter rc = Crafters.rockCrusher();
                    if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled() || RailcraftItems.DUST.isEnabled()) {
                        IRockCrusherCrafter.IRecipeBuilder builder = rc.makeRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled())
                            builder.addOutput(EnumGeneric.CRUSHED_OBSIDIAN.getStack());
                        if (RailcraftItems.DUST.isEnabled()) {
                            builder.addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f);
                            if (!EnumGeneric.CRUSHED_OBSIDIAN.isEnabled())
                                builder.addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN));
                        }
                        builder.register();
                    }


                    if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled() && RailcraftItems.DUST.isEnabled()) {
                        rc.makeRecipe(EnumGeneric.CRUSHED_OBSIDIAN)
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f)
                                .register();
                    }


                    //TODO move to individual classes once we do split up
                    if (WorldspikeVariant.STANDARD.isAvailable()) {
                        getWorldSpikeBuilder(WorldspikeVariant.STANDARD.getIngredient())
                                .addOutput(new ItemStack(Items.DIAMOND), 0.5f)
                                .register();

                    }

                    if (WorldspikeVariant.PERSONAL.isAvailable()) {
                        getWorldSpikeBuilder(WorldspikeVariant.PERSONAL.getIngredient())
                                .addOutput(new ItemStack(Items.EMERALD), 0.5f)
                                .register();
                    }

                    if (WorldspikeVariant.PASSIVE.isAvailable()) {
                        getWorldSpikeBuilder(WorldspikeVariant.PASSIVE.getIngredient())
                                .addOutput(new ItemStack(Blocks.PRISMARINE), 0.5f)
                                .register();
                    }

                    rc.makeRecipe(Ingredients.from(Blocks.COBBLESTONE))
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Items.FLINT), 0.10f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.MOSSY_COBBLESTONE))
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Blocks.VINE), 0.80f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Blocks.SAND))
                            .addOutput(new ItemStack(Items.GOLD_NUGGET), 0.001f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.00005f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE))
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.SANDSTONE))
                            .addOutput(new ItemStack(Blocks.SAND, 4))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.BRICK_BLOCK))
                            .addOutput(new ItemStack(Items.BRICK, 3))
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.CLAY))
                            .addOutput(new ItemStack(Items.CLAY_BALL, 4))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONEBRICK))
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_STAIRS))
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_BRICK_STAIRS))
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.NETHER_BRICK_STAIRS))
                            .addOutput(new ItemStack(Blocks.NETHER_BRICK))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.BRICK_STAIRS))
                            .addOutput(new ItemStack(Items.BRICK, 4))
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 0))
                            .addOutput(new ItemStack(Blocks.COBBLESTONE), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 1))
                            .addOutput(new ItemStack(Blocks.SAND), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 3))
                            .addOutput(new ItemStack(Blocks.GRAVEL), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 4))
                            .addOutput(new ItemStack(Items.BRICK))
                            .addOutput(new ItemStack(Items.BRICK), 0.75f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 5))
                            .addOutput(new ItemStack(Blocks.COBBLESTONE), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.ICE))
                            .addOutput(new ItemStack(Blocks.SNOW), 0.85f)
                            .addOutput(new ItemStack(Items.SNOWBALL), 0.25f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.NETHER_BRICK_FENCE))
                            .addOutput(new ItemStack(Blocks.NETHER_BRICK))
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.GLOWSTONE))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST, 3))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.75f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.REDSTONE_LAMP))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST, 3))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.75f)
                            .addOutput(new ItemStack(Items.REDSTONE, 3))
                            .addOutput(new ItemStack(Items.REDSTONE), 0.75f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Items.BONE))
                            .addOutput(new ItemStack(Items.DYE, 4, 15))
                            .register();

                    rc.makeRecipe(Ingredients.from(Items.BLAZE_ROD))
                            .addOutput(new ItemStack(Items.BLAZE_POWDER, 2))
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.65f)
                            .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.5f)
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .register();

                    //todo: Investigate if we should spawn the respective cobblestone variant from crushing ores. This would need to be applied to IC2 ores as well if done
                    rc.makeRecipe(Ingredients.from(Blocks.REDSTONE_ORE))
                            .addOutput(new ItemStack(Items.REDSTONE, 6))
                            .addOutput(new ItemStack(Items.REDSTONE, 2), 0.85f)
                            .addOutput(new ItemStack(Items.REDSTONE, 1), 0.25f)
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.1f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.DIAMOND_ORE))
                            .addOutput(new ItemStack(Items.DIAMOND))
                            .addOutput(new ItemStack(Items.DIAMOND), 0.85f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.25f)
                            .addOutput(new ItemStack(Items.COAL), 0.1f)
                            .register();

                    rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.DARK_DIAMOND))
                            .addOutput(new ItemStack(Items.DIAMOND))
                            .addOutput(new ItemStack(Items.DIAMOND), 0.85f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.25f)
                            .addOutput(new ItemStack(Items.COAL), 0.1f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.EMERALD_ORE))
                            .addOutput(new ItemStack(Items.EMERALD))
                            .addOutput(new ItemStack(Items.EMERALD), 0.85f)
                            .addOutput(new ItemStack(Items.EMERALD), 0.25f)
                            .register();

                    rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.DARK_EMERALD))
                            .addOutput(new ItemStack(Items.EMERALD))
                            .addOutput(new ItemStack(Items.EMERALD), 0.85f)
                            .addOutput(new ItemStack(Items.EMERALD), 0.25f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.LAPIS_ORE))
                            .addOutput(new ItemStack(Items.DYE, 8, 4))
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.85f)
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.35f)
                            .register();

                    rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.DARK_LAPIS))
                            .addOutput(new ItemStack(Items.DYE, 8, 4))
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.85f)
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.35f)
                            .register();

                    if (RailcraftItems.DUST.isEnabled()) {
                        rc.makeRecipe(Ingredients.from(Items.COAL, 0))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.COAL))
                                .register();

                        rc.makeRecipe(Ingredients.from(Blocks.COAL_ORE))
                                .addOutput(RailcraftItems.DUST.getStack(2, ItemDust.EnumDust.COAL))
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.COAL), 0.65f)
                                .addOutput(new ItemStack(Items.COAL), 0.15f)
                                .addOutput(new ItemStack(Items.DIAMOND), 0.001f)
                                .register();

                        rc.makeRecipe(Ingredients.from(Blocks.COAL_BLOCK, 0))
                                .addOutput(RailcraftItems.DUST.getStack(9, ItemDust.EnumDust.COAL))
                                .register();

                        rc.makeRecipe(Ingredients.from(Items.COAL, 1))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.CHARCOAL))
                                .register();

                        rc.makeRecipe(Ingredients.from("blockCharcoal"))
                                .addOutput(RailcraftItems.DUST.getStack(9, ItemDust.EnumDust.CHARCOAL))
                                .register();

                        rc.makeRecipe(Ingredient.fromItem(Items.ENDER_PEARL))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.ENDER))
                                .register();

                        rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.SULFUR))
                                .addOutput(RailcraftItems.DUST.getStack(5, ItemDust.EnumDust.SULFUR))
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.85f)
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.35f)
                                .register();

                        rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.SALTPETER))
                                .addOutput(RailcraftItems.DUST.getStack(3, ItemDust.EnumDust.SALTPETER))
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SALTPETER), 0.85f)
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SALTPETER), 0.35f)
                                .register();
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
                    Crafters.cokeOven().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.COAL_BLOCK)), EnumGeneric.BLOCK_COKE.getStack(), Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE * 9), COKE_COOK_TIME * 9);
                    ItemStack stack = EnumGeneric.BLOCK_COKE.getStack();
                    CraftingPlugin.addRecipe(stack,
                            "CCC",
                            "CCC",
                            "CCC",
                            'C', RailcraftItems.COKE);
                    CraftingPlugin.addShapelessRecipe(RailcraftItems.COKE.getStack(9), stack);
                }
            }

            private IRockCrusherCrafter.IRecipeBuilder getWorldSpikeBuilder(Ingredient ingredient) {
                IRockCrusherCrafter.IRecipeBuilder builder = Crafters.rockCrusher().makeRecipe(ingredient);
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

                Crafters.rockCrusher().makeRecipe(Ingredient.fromStacks(ore))
                        .addOutput(dust)
                        .register();
            }

            @Override
            public void postInit() {
                BlastFurnaceCrafter.INSTANCE.postInit();
                //TODO this is not right
                if (!EquipmentVariant.ROLLING_MACHINE_POWERED.isAvailable())
                    RollingMachineCraftingManager.copyRecipesToWorkbench();
                if (!RailcraftBlocks.BLAST_FURNACE.isEnabled() || RailcraftConfig.forceEnableSteelRecipe())
                    registerAltSteelFurnaceRecipe();

                List<ItemStack> logs = new ArrayList<>(25);
                logs.addAll(OreDictionary.getOres("logWood"));
                logs.addAll(OreDictionary.getOres("woodRubber"));
                Crafters.cokeOven().addRecipe(Ingredient.fromStacks(logs.toArray(new ItemStack[0])), new ItemStack(Items.COAL, 1, 1), Fluids.CREOSOTE.get(250), COKE_COOK_TIME);

                if (Mod.FORESTRY.isLoaded()) {
                    Crafters.rockCrusher().makeRecipe(Ingredient.fromStacks(ModItems.APATITE_ORE.getStack()))
                            .addOutput(ModItems.APATITE.getStack(4))
                            .addOutput(ModItems.APATITE.getStack(), 0.85f)
                            .addOutput(ModItems.APATITE.getStack(), 0.25f)
                            .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.2f)
                            .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SALTPETER), 0.05f)
                            .register();
                }

                if (Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC)) {
                    boolean classic = Mod.IC2_CLASSIC.isLoaded();
                    ItemStack crushedIron = classic ? ModItems.DUST_IRON.getStack() : ModItems.CRUSHED_IRON.getStack();
                    ItemStack crushedGold = classic ? ModItems.DUST_GOLD.getStack() : ModItems.CRUSHED_GOLD.getStack();
                    ItemStack crushedCopper = classic ? ModItems.DUST_COPPER.getStack() : ModItems.CRUSHED_COPPER.getStack();
                    ItemStack crushedTin = classic ? ModItems.DUST_TIN.getStack() : ModItems.CRUSHED_TIN.getStack();
                    ItemStack crushedSilver = classic ? ModItems.DUST_SILVER.getStack() : ModItems.CRUSHED_SILVER.getStack();
                    ItemStack crushedLead = ModItems.CRUSHED_LEAD.getStack();
                    ItemStack crushedUranium = classic ? ModItems.URANIUM_DROP.getStack() : ModItems.CRUSHED_URANIUM.getStack();

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

                        ItemStack firestoneOre = EnumOreMagic.FIRESTONE.getStack();
                        ItemStack firestoneRaw = RailcraftItems.FIRESTONE_RAW.getStack();
                        IC2Plugin.addMaceratorRecipe(firestoneOre, firestoneRaw);

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
