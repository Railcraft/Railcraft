/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import mods.railcraft.common.core.InterModMessageRegistry;
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
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.util.List;

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
                    int smeltTime = IBlastFurnaceCrafter.SMELT_TIME;
                    Metal steel = Metal.STEEL;
                    BlastFurnaceCrafter bf = BlastFurnaceCrafter.INSTANCE;
                    bf.newRecipe(Items.IRON_INGOT)
                            .name("railcraft:ingot_steel")
                            .output(RailcraftItems.INGOT.getStack(1, steel))
                            .slagOutput(1).time(smeltTime).register();

                    bf.addRecipe("railcraft:smelt_helmet", Ingredient.fromItem(Items.IRON_HELMET), smeltTime * 5,
                            RailcraftItems.INGOT.getStack(5, steel), 5);
                    bf.addRecipe("railcraft:smelt_chestplate", Ingredient.fromItem(Items.IRON_CHESTPLATE), smeltTime * 8,
                            RailcraftItems.INGOT.getStack(8, steel), 8);
                    bf.addRecipe("railcraft:smelt_leggings", Ingredient.fromItem(Items.IRON_LEGGINGS), smeltTime * 7,
                            RailcraftItems.INGOT.getStack(7, steel), 7);
                    bf.addRecipe("railcraft:smelt_boots", Ingredient.fromItem(Items.IRON_BOOTS), smeltTime * 4,
                            RailcraftItems.INGOT.getStack(4, steel), 4);

                    bf.addRecipe("railcraft:smelt_horse_armor", Ingredient.fromItem(Items.IRON_HORSE_ARMOR), smeltTime * 4,
                            RailcraftItems.INGOT.getStack(4, steel), 4);

                    bf.addRecipe("railcraft:smelt_sword", Ingredient.fromItem(Items.IRON_SWORD), smeltTime * 2,
                            RailcraftItems.INGOT.getStack(2, steel), 2);
                    bf.addRecipe("railcraft:smelt_shovel", Ingredient.fromItem(Items.IRON_SHOVEL), smeltTime,
                            RailcraftItems.INGOT.getStack(1, steel), 1);
                    bf.addRecipe("railcraft:smelt_pickaxe", Ingredient.fromItem(Items.IRON_PICKAXE), smeltTime * 3,
                            RailcraftItems.INGOT.getStack(3, steel), 3);
                    bf.addRecipe("railcraft:smelt_axe", Ingredient.fromItem(Items.IRON_AXE), smeltTime * 3,
                            RailcraftItems.INGOT.getStack(3, steel), 3);
                    bf.addRecipe("railcraft:smelt_hoe", Ingredient.fromItem(Items.IRON_HOE), smeltTime * 2,
                            RailcraftItems.INGOT.getStack(2, steel), 2);
                    bf.addRecipe("railcraft:smelt_shears", Ingredient.fromItem(Items.SHEARS), smeltTime * 2,
                            RailcraftItems.INGOT.getStack(2, steel), 2);

                    bf.addRecipe("railcraft:smelt_crowbar", RailcraftItems.CROWBAR_IRON.getIngredient(), smeltTime * 3,
                            RailcraftItems.INGOT.getStack(3, steel), 3);

                    bf.addRecipe("railcraft:smelt_door", Ingredient.fromItem(Items.IRON_DOOR), smeltTime * 6,
                            RailcraftItems.INGOT.getStack(6, steel), 6);
                    bf.addRecipe("railcraft:smelt_trapdoor", Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_TRAPDOOR)), smeltTime * 6,
                            RailcraftItems.INGOT.getStack(4, steel), 4);

                    int recycleTime = smeltTime / 2;
                    bf.addRecipe("railcraft:recycle_helmet", RailcraftItems.ARMOR_HELMET_STEEL.getIngredient(), recycleTime * 4,
                            RailcraftItems.INGOT.getStack(4, steel), 0);
                    bf.addRecipe("railcraft:recycle_chestplate", RailcraftItems.ARMOR_CHESTPLATE_STEEL.getIngredient(), recycleTime * 6,
                            RailcraftItems.INGOT.getStack(6, steel), 0);
                    bf.addRecipe("railcraft:recycle_leggings", RailcraftItems.ARMOR_LEGGINGS_STEEL.getIngredient(), recycleTime * 5,
                            RailcraftItems.INGOT.getStack(5, steel), 0);
                    bf.addRecipe("railcraft:recycle_boots", RailcraftItems.ARMOR_BOOTS_STEEL.getIngredient(), recycleTime * 3,
                            RailcraftItems.INGOT.getStack(3, steel), 0);

                    bf.addRecipe("railcraft:recycle_sword", RailcraftItems.SWORD_STEEL.getIngredient(), recycleTime,
                            RailcraftItems.INGOT.getStack(1, steel), 0);
                    bf.addRecipe("railcraft:recycle_pickaxe", RailcraftItems.PICKAXE_STEEL.getIngredient(), recycleTime * 2,
                            RailcraftItems.INGOT.getStack(2, steel), 0);
                    bf.addRecipe("railcraft:recycle_hoe", RailcraftItems.HOE_STEEL.getIngredient(), recycleTime,
                            RailcraftItems.INGOT.getStack(1, steel), 0);
                    bf.addRecipe("railcraft:recycle_axe", RailcraftItems.AXE_STEEL.getIngredient(), recycleTime * 2,
                            RailcraftItems.INGOT.getStack(2, steel), 0);
                    bf.addRecipe("railcraft:recycle_shears", RailcraftItems.SHEARS_STEEL.getIngredient(), recycleTime,
                            RailcraftItems.INGOT.getStack(1, steel), 0);
                }
                {
                    IRockCrusherCrafter rc = Crafters.rockCrusher();
                    if (EnumGeneric.CRUSHED_OBSIDIAN.isEnabled() || RailcraftItems.DUST.isEnabled()) {
                        IRockCrusherCrafter.IRockCrusherRecipeBuilder builder = rc.makeRecipe(Blocks.OBSIDIAN)
                                .name("railcraft:obsidian");
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
                                .name("railcraft:obsidian_crushed")
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN))
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.OBSIDIAN), 0.25f)
                                .register();
                    }


                    //TODO move to individual classes once we do split up
                    if (WorldspikeVariant.STANDARD.isAvailable()) {
                        getWorldSpikeBuilder("railcraft:recycleWorldspikeStandard", WorldspikeVariant.STANDARD.getIngredient())
                                .addOutput(new ItemStack(Items.DIAMOND), 0.5f)
                                .register();

                    }

                    if (WorldspikeVariant.PERSONAL.isAvailable()) {
                        getWorldSpikeBuilder("railcraft:recycleWorldspikePersonal", WorldspikeVariant.PERSONAL.getIngredient())
                                .addOutput(new ItemStack(Items.EMERALD), 0.5f)
                                .register();
                    }

                    if (WorldspikeVariant.PASSIVE.isAvailable()) {
                        getWorldSpikeBuilder("railcraft:recycleWorldspikePassive", WorldspikeVariant.PASSIVE.getIngredient())
                                .addOutput(new ItemStack(Blocks.PRISMARINE), 0.5f)
                                .register();
                    }

                    rc.makeRecipe(Blocks.COBBLESTONE).name("minecraft:cobblestone")
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Items.FLINT), 0.10f)
                            .register();

                    rc.makeRecipe(Blocks.MOSSY_COBBLESTONE).name("minecraft:cobblestone_mossy")
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .addOutput(new ItemStack(Blocks.VINE), 0.80f)
                            .register();

                    rc.makeRecipe(Blocks.GRAVEL).name("minecraft:gravel")
                            .addOutput(new ItemStack(Blocks.SAND))
                            .addOutput(new ItemStack(Items.GOLD_NUGGET), 0.001f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.00005f)
                            .register();

                    rc.makeRecipe(Blocks.STONE).name("minecraft:stone")
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .register();

                    rc.makeRecipe(Blocks.SANDSTONE).name("minecraft:sandstone")
                            .addOutput(new ItemStack(Blocks.SAND, 4))
                            .register();

                    rc.makeRecipe(Blocks.BRICK_BLOCK).name("minecraft:brick")
                            .addOutput(new ItemStack(Items.BRICK, 3))
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .register();

                    rc.makeRecipe(Blocks.CLAY).name("minecraft:clay")
                            .addOutput(new ItemStack(Items.CLAY_BALL, 4))
                            .register();

                    rc.makeRecipe(Blocks.STONEBRICK).name("minecraft:stonebrick")
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .register();

                    rc.makeRecipe(Blocks.STONE_STAIRS).name("minecraft:stairs_stone")
                            .addOutput(new ItemStack(Blocks.GRAVEL))
                            .register();

                    rc.makeRecipe(Blocks.STONE_BRICK_STAIRS).name("minecraft:stairs_stonebrick")
                            .addOutput(new ItemStack(Blocks.COBBLESTONE))
                            .register();

                    rc.makeRecipe(Blocks.NETHER_BRICK_STAIRS).name("minecraft:stairs_nether")
                            .addOutput(new ItemStack(Blocks.NETHER_BRICK))
                            .register();

                    rc.makeRecipe(Blocks.BRICK_STAIRS).name("minecraft:stairs_brick")
                            .addOutput(new ItemStack(Items.BRICK, 4))
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .addOutput(new ItemStack(Items.BRICK), 0.5f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 0)).name("minecraft:slab_stone")
                            .addOutput(new ItemStack(Blocks.COBBLESTONE), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 1)).name("minecraft:slab_stone")
                            .addOutput(new ItemStack(Blocks.SAND), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 3)).name("minecraft:slab_stone")
                            .addOutput(new ItemStack(Blocks.GRAVEL), 0.45f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 4)).name("minecraft:slab_stone")
                            .addOutput(new ItemStack(Items.BRICK))
                            .addOutput(new ItemStack(Items.BRICK), 0.75f)
                            .register();

                    rc.makeRecipe(Ingredients.from(Blocks.STONE_SLAB, 5)).name("minecraft:slab_stone")
                            .addOutput(new ItemStack(Blocks.COBBLESTONE), 0.45f)
                            .register();

                    rc.makeRecipe(Blocks.ICE).name("minecraft:ice")
                            .addOutput(new ItemStack(Blocks.SNOW), 0.85f)
                            .addOutput(new ItemStack(Items.SNOWBALL), 0.25f)
                            .register();

                    rc.makeRecipe(Blocks.NETHER_BRICK_FENCE).name("minecraft:fence_nether")
                            .addOutput(new ItemStack(Blocks.NETHER_BRICK))
                            .register();

                    rc.makeRecipe(Blocks.GLOWSTONE).name("minecraft:glowstone")
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST, 3))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.75f)
                            .register();

                    rc.makeRecipe(Blocks.REDSTONE_LAMP).name("minecraft:redstone_lamp")
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST, 3))
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.75f)
                            .addOutput(new ItemStack(Items.REDSTONE, 3))
                            .addOutput(new ItemStack(Items.REDSTONE), 0.75f)
                            .register();

                    rc.makeRecipe(Items.BONE).name("minecraft:bone")
                            .addOutput(new ItemStack(Items.DYE, 4, 15))
                            .register();

                    rc.makeRecipe(Items.BLAZE_ROD).name("minecraft:blaze_rod")
                            .addOutput(new ItemStack(Items.BLAZE_POWDER, 2))
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.65f)
                            .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.5f)
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.25f)
                            .register();

                    //todo: Investigate if we should spawn the respective cobblestone variant from crushing ores. This would need to be applied to IC2 ores as well if done
                    rc.makeRecipe(Blocks.REDSTONE_ORE).name("minecraft:ore_redstone")
                            .addOutput(new ItemStack(Items.REDSTONE, 6))
                            .addOutput(new ItemStack(Items.REDSTONE, 2), 0.85f)
                            .addOutput(new ItemStack(Items.REDSTONE, 1), 0.25f)
                            .addOutput(new ItemStack(Items.GLOWSTONE_DUST), 0.1f)
                            .register();

                    rc.makeRecipe(Blocks.DIAMOND_ORE).name("minecraft:ore_diamond")
                            .addOutput(new ItemStack(Items.DIAMOND))
                            .addOutput(new ItemStack(Items.DIAMOND), 0.85f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.25f)
                            .addOutput(new ItemStack(Items.COAL), 0.1f)
                            .register();

                    rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.DARK_DIAMOND)).name("railcraft:ore_diamond_dark")
                            .addOutput(new ItemStack(Items.DIAMOND))
                            .addOutput(new ItemStack(Items.DIAMOND), 0.85f)
                            .addOutput(new ItemStack(Items.DIAMOND), 0.25f)
                            .addOutput(new ItemStack(Items.COAL), 0.1f)
                            .register();

                    rc.makeRecipe(Blocks.EMERALD_ORE).name("minecraft:ore_emerald")
                            .addOutput(new ItemStack(Items.EMERALD))
                            .addOutput(new ItemStack(Items.EMERALD), 0.85f)
                            .addOutput(new ItemStack(Items.EMERALD), 0.25f)
                            .register();

                    rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.DARK_EMERALD)).name("railcraft:ore_emerald_dark")
                            .addOutput(new ItemStack(Items.EMERALD))
                            .addOutput(new ItemStack(Items.EMERALD), 0.85f)
                            .addOutput(new ItemStack(Items.EMERALD), 0.25f)
                            .register();

                    rc.makeRecipe(Blocks.LAPIS_ORE).name("minecraft:ore_lapis")
                            .addOutput(new ItemStack(Items.DYE, 8, 4))
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.85f)
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.35f)
                            .register();

                    rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.DARK_LAPIS)).name("railcraft:ore_lapis_dark")
                            .addOutput(new ItemStack(Items.DYE, 8, 4))
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.85f)
                            .addOutput(new ItemStack(Items.DYE, 1, 4), 0.35f)
                            .register();

                    if (RailcraftItems.DUST.isEnabled()) {
                        rc.makeRecipe(Ingredients.from(Items.COAL, 0)).name("minecraft:coal")
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.COAL))
                                .register();

                        rc.makeRecipe(Blocks.COAL_ORE).name("minecraft:ore_coal")
                                .addOutput(RailcraftItems.DUST.getStack(2, ItemDust.EnumDust.COAL))
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.COAL), 0.65f)
                                .addOutput(new ItemStack(Items.COAL), 0.15f)
                                .addOutput(new ItemStack(Items.DIAMOND), 0.001f)
                                .register();

                        rc.makeRecipe(Ingredients.from(Blocks.COAL_BLOCK, 0)).name("minecraft:block_coal")
                                .addOutput(RailcraftItems.DUST.getStack(9, ItemDust.EnumDust.COAL))
                                .register();

                        rc.makeRecipe(Ingredients.from(Items.COAL, 1)).name("minecraft:charcoal")
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.CHARCOAL))
                                .register();

                        rc.makeRecipe("blockCharcoal").name("minecraft:block_charcoal")
                                .addOutput(RailcraftItems.DUST.getStack(9, ItemDust.EnumDust.CHARCOAL))
                                .register();

                        rc.makeRecipe(Items.ENDER_PEARL).name("minecraft:ender_pearl")
                                .addOutput(RailcraftItems.DUST.getStack(ItemDust.EnumDust.ENDER))
                                .register();

                        rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.SULFUR)).name("railcraft:ore_sulfur")
                                .addOutput(RailcraftItems.DUST.getStack(5, ItemDust.EnumDust.SULFUR))
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.85f)
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SULFUR), 0.35f)
                                .register();

                        rc.makeRecipe(RailcraftBlocks.ORE.getIngredient(EnumOre.SALTPETER)).name("railcraft:ore_saltpeter")
                                .addOutput(RailcraftItems.DUST.getStack(3, ItemDust.EnumDust.SALTPETER))
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SALTPETER), 0.85f)
                                .addOutput(RailcraftItems.DUST.getStack(1, ItemDust.EnumDust.SALTPETER), 0.35f)
                                .register();
                    }
                }

                if (RailcraftModuleManager.isModuleEnabled(ModuleBuilding.class)) {
                    if (RailcraftBlocks.BLAST_FURNACE.isLoaded() && BrickTheme.INFERNAL.isLoaded()) {

                        ItemStack stack = RailcraftBlocks.BLAST_FURNACE.getStack(4);
                        CraftingPlugin.addShapedRecipe(stack,
                                " B ",
                                "BPB",
                                " B ",
                                'B', BrickTheme.INFERNAL.getStack(1, BrickVariant.PAVER),
                                'P', Items.MAGMA_CREAM);
                    }
                }

                if (EnumGeneric.BLOCK_COKE.isEnabled()) {
                    Crafters.cokeOven().newRecipe(Ingredients.from(Blocks.COAL_BLOCK))
                            .name("railcraft:coke_block")
                            .output(EnumGeneric.BLOCK_COKE.getStack())
                            .fluid(Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE * 9))
                            .time(COKE_COOK_TIME * 9)
                            .register();
                    ItemStack stack = EnumGeneric.BLOCK_COKE.getStack();
                    CraftingPlugin.addShapedRecipe(stack,
                            "CCC",
                            "CCC",
                            "CCC",
                            'C', RailcraftItems.COKE);
                    CraftingPlugin.addShapelessRecipe(RailcraftItems.COKE.getStack(9), stack);
                }

                BlastFurnaceCrafter.INSTANCE.initFuel();
                if (!RailcraftBlocks.BLAST_FURNACE.isEnabled() || RailcraftConfig.forceEnableSteelRecipe())
                    registerAltSteelFurnaceRecipe();

                Crafters.cokeOven().newRecipe(Ingredients.from("logWood", "woodRubber"))
                        .name("railcraft:logs")
                        .output(new ItemStack(Items.COAL, 1, 1))
                        .fluid(Fluids.CREOSOTE.get(250))
                        .time(COKE_COOK_TIME)
                        .register();

                if (Mod.FORESTRY.isLoaded()) {
                    Crafters.rockCrusher().makeRecipe(Ingredient.fromStacks(ModItems.APATITE_ORE.getStack()))
                            .name("forestry:apatite")
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
                        registerCrushedOreRecipe(Ingredients.from(Blocks.IRON_ORE), crushedIron);
                        registerCrushedOreRecipe(Ingredients.from(Blocks.GOLD_ORE), crushedGold);

                        registerCrushedOreRecipe(Ingredients.from("oreCopper"), crushedCopper);
                        registerCrushedOreRecipe(Ingredients.from("oreTin"), crushedTin);
                        registerCrushedOreRecipe(Ingredients.from("oreSilver"), crushedSilver);
                        registerCrushedOreRecipe(Ingredients.from("oreLead"), crushedLead);
                        registerCrushedOreRecipe(Ingredients.from("oreUranium"), crushedUranium);
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

                InterModMessageRegistry.getInstance().register("rock-crusher", mess -> {
                    NBTTagCompound tag = mess.getNBTValue();
                    NBTTagCompound inputTag = tag.getCompoundTag("input");
                    ItemStack input = new ItemStack(inputTag);
                    if (InvTools.isEmpty(input)) {
                        Game.log().msg(Level.WARN, "Mod {0} registered a rock crusher recipe with no input. NBT message: {1}", mess.key, tag);
                        return;
                    }
                    boolean matchMeta = tag.getBoolean("matchMeta");
                    boolean matchNbt = tag.getBoolean("matchNBT");

                    if (!matchMeta) {
                        input.setItemDamage(OreDictionary.WILDCARD_VALUE);
                    }

                    // todo ingredient needs much work to support nbt matching

                    Ingredient ingredient = Ingredient.fromStacks(input);

                    IRockCrusherCrafter.IRockCrusherRecipeBuilder builder = RockCrusherCrafter.INSTANCE.makeRecipe(ingredient);

                    ResourceLocation rcName = CraftingPlugin.guessName(input);
                    ResourceLocation name = rcName == null ? null : new ResourceLocation(mess.getSender(), rcName.getPath());
                    builder.name(name);

                    boolean erroneous = false;
                    for (int i = 0; tag.hasKey("output" + i, Constants.NBT.TAG_COMPOUND); i++) {
                        NBTTagCompound each = tag.getCompoundTag("output" + i);
                        ItemStack eachStack = new ItemStack(each);
                        float chance = each.getFloat("chance");
                        if (chance > 0 && InvTools.nonEmpty(eachStack)) {
                            builder.addOutput(eachStack, chance);
                        } else {
                            Game.log().msg(Level.WARN, "Invalid output {1} in rock crusher recipe from {0}", mess.getSender(), i);
                            erroneous = true;
                        }
                    }

                    if (erroneous) {
                        Game.log().msg(Level.WARN, "Message from {0} has erroneous outputs set. Please report to its author. Content: {1}", mess.getSender(), tag);
                        return;
                    }

                    builder.register();

                    Game.log().msg(Level.DEBUG, "Mod {0} registered rock crusher recipe via IMC message named {1}", mess.getSender(), name);
                });

                if (!EquipmentVariant.ROLLING_MACHINE_POWERED.isAvailable() && !EquipmentVariant.ROLLING_MACHINE_MANUAL.isAvailable())
                    RollingMachineCrafter.copyRecipesToWorkbench();
            }

            private IRockCrusherCrafter.IRockCrusherRecipeBuilder getWorldSpikeBuilder(String name, Ingredient ingredient) {
                IRockCrusherCrafter.IRockCrusherRecipeBuilder builder =
                        Crafters.rockCrusher().makeRecipe(ingredient).name(name);
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

            private void registerCrushedOreRecipe(Ingredient ore, ItemStack dust) {
                if (InvTools.isEmpty(dust))
                    return;

                Crafters.rockCrusher().makeRecipe(ore).name("ic2:crushedOre")
                        .addOutput(InvTools.copy(dust, 2))
                        .register();
            }
        });
        setDisabledEventHandler(new ModuleEventHandler() {
            @Override
            public void postInit() {
                RollingMachineCrafter.copyRecipesToWorkbench();
                registerAltSteelFurnaceRecipe();
            }
        });
    }

    void registerAltSteelFurnaceRecipe() {
        CraftingPlugin.addFurnaceRecipe(new ItemStack(Items.IRON_NUGGET, 1), RailcraftItems.NUGGET.getStack(Metal.STEEL), 0);
    }
}
