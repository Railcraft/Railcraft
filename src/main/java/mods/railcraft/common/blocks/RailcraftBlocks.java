/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.aesthetics.brick.*;
import mods.railcraft.common.blocks.aesthetics.concrete.BlockReinforcedConcrete;
import mods.railcraft.common.blocks.aesthetics.generic.BlockGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.ItemBlockGeneric;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.materials.ItemMaterial;
import mods.railcraft.common.blocks.aesthetics.metals.BlockMetal;
import mods.railcraft.common.blocks.aesthetics.metals.ItemBlockMetal;
import mods.railcraft.common.blocks.aesthetics.post.*;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.anvil.ItemAnvil;
import mods.railcraft.common.blocks.charge.*;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.ItemDetector;
import mods.railcraft.common.blocks.logbook.BlockLogbook;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.blocks.machine.charge.BlockChargeFeeder;
import mods.railcraft.common.blocks.machine.equipment.BlockMachineEquipment;
import mods.railcraft.common.blocks.machine.manipulator.BlockMachineManipulator;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.BlockMachineActuator;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.BlockMachineSignalBoxRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.signals.BlockMachineSignalDualRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.signals.BlockMachineSignalRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.signals.ItemSignal;
import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspikePoint;
import mods.railcraft.common.blocks.machine.worldspike.ItemWorldspike;
import mods.railcraft.common.blocks.ore.*;
import mods.railcraft.common.blocks.single.*;
import mods.railcraft.common.blocks.structures.*;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.elevator.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlex;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlexAbandoned;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlexElectric;
import mods.railcraft.common.blocks.tracks.flex.ItemTrackStateless;
import mods.railcraft.common.blocks.tracks.force.BlockTrackForce;
import mods.railcraft.common.blocks.tracks.outfitted.BlockTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.firestone.BlockRitual;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftBlockContainer {
    ACTUATOR(BlockDef.build("actuator", BlockMachineActuator.class).item(ItemMachine::new)),
    ANVIL_STEEL(BlockDef.build("anvil", BlockRCAnvil.class).item(ItemAnvil::new)),
    BATTERY_NICKEL_IRON(BlockDef.build("battery_nickel_iron", BlockBatteryNickelIron.class).item(ItemBattery::new)),
    BATTERY_NICKEL_ZINC(BlockDef.build("battery_nickel_zinc", BlockBatteryNickelZinc.class).item(ItemBattery::new)),
    BATTERY_ZINC_CARBON(BlockDef.build("battery_zinc_carbon", BlockBatteryZincCarbon.class).item(ItemBattery::new)),
    BATTERY_ZINC_SILVER(BlockDef.build("battery_zinc_silver", BlockBatteryZincSilver.class).item(ItemBattery::new)),

    // Bricks

    BRICK_ABYSSAL(BlockDef.build("brick_abyssal", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.ABYSSAL)).item(ItemBrick::new)
            .condition(EnumGeneric.STONE_ABYSSAL)
    ),
    BRICK_BLEACHED_BONE(BlockDef.build("brick_bleachedbone", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.BLEACHEDBONE)).item(ItemBrick::new)),
    BRICK_BLOOD_STAINED(BlockDef.build("brick_bloodstained", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.BLOODSTAINED)).item(ItemBrick::new)),
    BRICK_FROST_BOUND(BlockDef.build("brick_frostbound", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.FROSTBOUND)).item(ItemBrick::new)),
    BRICK_INFERNAL(BlockDef.build("brick_infernal", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.INFERNAL)).item(ItemBrick::new)),
    BRICK_PEARLIZED(BlockDef.build("brick_pearlized", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.PEARLIZED)).item(ItemBrick::new)),
    BRICK_QUARRIED(BlockDef.build("brick_quarried", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.QUARRIED)).item(ItemBrick::new)
            .condition(EnumGeneric.STONE_QUARRIED)
    ),
    BRICK_BADLANDS(BlockDef.build("brick_badlands", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.BADLANDS)).item(ItemBrick::new)),
    BRICK_SANDY(BlockDef.build("brick_sandy", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.SANDY)).item(ItemBrick::new)),

    // Vanilla Bricks

    BRICK_ANDESITE(BlockDef.build("brick_andesite", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.ANDESITE)).item(ItemBrick::new)),
    BRICK_DIORITE(BlockDef.build("brick_diorite", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.DIORITE)).item(ItemBrick::new)),
    BRICK_GRANITE(BlockDef.build("brick_granite", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.GRANITE)).item(ItemBrick::new)),
    BRICK_NETHER(BlockDef.build("brick_nether", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.NETHER)).item(ItemBrick::new)),
    BRICK_RED_NETHER(BlockDef.build("brick_red_nether", BlockBrick.class).block(() -> new BlockBrick(BrickTheme.RED_NETHER)).item(ItemBrick::new)),

    // Slabs
    SLAB_ABYSSAL_DOUBLE(BlockDef.build("slab_abyssal_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.ABYSSAL))
            .condition(BRICK_ABYSSAL)
    ),
    SLAB_ABYSSAL(BlockDef.build("slab_abyssal", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.ABYSSAL))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_abyssal_double").block()))
            .condition(BRICK_ABYSSAL)
    ),

    SLAB_BADLANDS_DOUBLE(BlockDef.build("slab_badlands_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.BADLANDS))
            .condition(BRICK_BADLANDS)
    ),
    SLAB_BADLANDS(BlockDef.build("slab_badlands", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.BADLANDS))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_badlands_double").block()))
            .condition(BRICK_BADLANDS)
    ),

    SLAB_BLEACHED_BONE_DOUBLE(BlockDef.build("slab_bleachedbone_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.BLEACHEDBONE))
            .condition(BRICK_BLEACHED_BONE)
    ),
    SLAB_BLEACHED_BONE(BlockDef.build("slab_bleachedbone", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.BLEACHEDBONE))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_bleachedbone_double").block()))
            .condition(BRICK_BLEACHED_BONE)
    ),

    SLAB_BLOOD_STAINED_DOUBLE(BlockDef.build("slab_bloodstained_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.BLOODSTAINED))
            .condition(BRICK_BLOOD_STAINED)
    ),
    SLAB_BLOOD_STAINED(BlockDef.build("slab_bloodstained", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.BLOODSTAINED))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_bloodstained_double").block()))
            .condition(BRICK_BLOOD_STAINED)
    ),

    SLAB_FROST_BOUND_DOUBLE(BlockDef.build("slab_frostbound_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.FROSTBOUND))
            .condition(BRICK_FROST_BOUND)
    ),
    SLAB_FROST_BOUND(BlockDef.build("slab_frostbound", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.FROSTBOUND))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_frostbound_double").block()))
            .condition(BRICK_FROST_BOUND)
    ),

    SLAB_INFERNAL_DOUBLE(BlockDef.build("slab_infernal_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.INFERNAL))
            .condition(BRICK_INFERNAL)
    ),
    SLAB_INFERNAL(BlockDef.build("slab_infernal", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.INFERNAL))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_infernal_double").block()))
            .condition(BRICK_INFERNAL)
    ),

    SLAB_PEARLIZED_DOUBLE(BlockDef.build("slab_pearlized_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.PEARLIZED))
            .condition(BRICK_PEARLIZED)
    ),
    SLAB_PEARLIZED(BlockDef.build("slab_pearlized", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.PEARLIZED))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_pearlized_double").block()))
            .condition(BRICK_PEARLIZED)
    ),

    SLAB_QUARRIED_DOUBLE(BlockDef.build("slab_quarried_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.QUARRIED))
            .condition(BRICK_QUARRIED)
    ),
    SLAB_QUARRIED(BlockDef.build("slab_quarried", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.QUARRIED))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_quarried_double").block()))
            .condition(BRICK_QUARRIED)
    ),

    SLAB_SANDY_DOUBLE(BlockDef.build("slab_sandy_double", BlockBrickSlab.Double.class)
            .block(() -> new BlockBrickSlab.Double(BrickTheme.SANDY))
            .condition(BRICK_SANDY)
    ),
    SLAB_SANDY(BlockDef.build("slab_sandy", BlockBrickSlab.Half.class)
            .block(() -> new BlockBrickSlab.Half(BrickTheme.SANDY))
            .item(block -> new ItemBrickSlab(block, block, (BlockBrickSlab.Double) byTag("slab_sandy_double").block()))
            .condition(BRICK_SANDY)
    ),

    // Stairs

    STAIR_ABYSSAL_BRICK(BlockDef.build("stair_abyssal_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.ABYSSAL, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(EnumGeneric.STONE_ABYSSAL)
            .condition(BRICK_ABYSSAL)
    ),
    STAIR_ABYSSAL_PAVER(BlockDef.build("stair_abyssal_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.ABYSSAL, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(EnumGeneric.STONE_ABYSSAL)
            .condition(BRICK_ABYSSAL)
    ),
    STAIR_BADLANDS_BRICK(BlockDef.build("stair_badlands_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.BADLANDS, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_BADLANDS)
    ),
    STAIR_BADLANDS_PAVER(BlockDef.build("stair_badlands_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.BADLANDS, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_BADLANDS)
    ),
    STAIR_BLEACHED_BONE_BRICK(BlockDef.build("stair_bleachedbone_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.BLEACHEDBONE, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_BLEACHED_BONE)
    ),
    STAIR_BLEACHED_BONE_PAVER(BlockDef.build("stair_bleachedbone_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.BLEACHEDBONE, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_BLEACHED_BONE)
    ),
    STAIR_BLOOD_STAINED_BRICK(BlockDef.build("stair_bloodstained_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.BLOODSTAINED, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_BLOOD_STAINED)
    ),
    STAIR_BLOOD_STAINED_PAVER(BlockDef.build("stair_bloodstained_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.BLOODSTAINED, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_BLOOD_STAINED)
    ),
    STAIR_FROST_BOUND_BRICK(BlockDef.build("stair_frostbound_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.FROSTBOUND, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_FROST_BOUND)
    ),
    STAIR_FROST_BOUND_PAVER(BlockDef.build("stair_frostbound_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.FROSTBOUND, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_FROST_BOUND)
    ),
    STAIR_INFERNAL_BRICK(BlockDef.build("stair_infernal_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.INFERNAL, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_INFERNAL)
    ),
    STAIR_INFERNAL_PAVER(BlockDef.build("stair_infernal_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.INFERNAL, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_INFERNAL)
    ),
    STAIR_PEARLIZED_BRICK(BlockDef.build("stair_pearlized_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.PEARLIZED, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_PEARLIZED)
    ),
    STAIR_PEARLIZED_PAVER(BlockDef.build("stair_pearlized_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.PEARLIZED, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_PEARLIZED)
    ),
    STAIR_QUARRIED_BRICK(BlockDef.build("stair_quarried_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.QUARRIED, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(EnumGeneric.STONE_QUARRIED)
            .condition(BRICK_QUARRIED)
    ),
    STAIR_QUARRIED_PAVER(BlockDef.build("stair_quarried_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.QUARRIED, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(EnumGeneric.STONE_QUARRIED)
            .condition(BRICK_QUARRIED)
    ),
    STAIR_SANDY_BRICK(BlockDef.build("stair_sandy_brick", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.SANDY, BrickVariant.BRICK))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_SANDY)
    ),
    STAIR_SANDY_PAVER(BlockDef.build("stair_sandy_paver", BlockBrickStairs.class)
            .block(() -> new BlockBrickStairs(BrickTheme.SANDY, BrickVariant.PAVER))
            .item(ItemBlockRailcraft::new)
            .condition(BRICK_SANDY)
    ),

    CHARGE_FEEDER(BlockDef.build("charge_feeder", BlockChargeFeeder.class).item(ItemMachine::new)),
    CHARGE_TRAP(BlockDef.build("charge_trap", BlockChargeTrap.class).item(ItemBlockRailcraft::new)),
    DETECTOR(BlockDef.build("detector", BlockDetector.class).item(ItemDetector::new)),
    EQUIPMENT(BlockDef.build("equipment", BlockMachineEquipment.class).item(ItemMachine::new)),
    FRAME(BlockDef.build("frame", BlockFrame.class).item(ItemBlockRailcraft::new)),
    GENERIC(BlockDef.build("generic", BlockGeneric.class).item(ItemBlockGeneric::new)),
    METAL(BlockDef.build("metal", BlockMetal.class).item(ItemBlockMetal::new)),
    GLASS(BlockDef.build("glass", BlockStrengthGlass.class).item(ItemBlockRailcraftColored::new)),
    LANTERN(BlockDef.build("lantern", BlockLantern.class).item(ItemMaterial::new)),
    LOGBOOK(BlockDef.build("logbook", BlockLogbook.class).item(ItemBlockRailcraft::new)),
    MANIPULATOR(BlockDef.build("manipulator", BlockMachineManipulator.class).item(ItemMachine::new)),
    ORE(BlockDef.build("ore", BlockOre.class).item(ItemBlockRailcraftSubtyped::new)),
    ORE_MAGIC(BlockDef.build("ore_magic", BlockOreMagic.class).item(ItemOreMagic::new)),
    ORE_METAL(BlockDef.build("ore_metal", BlockOreMetal.class).item(ItemBlockRailcraftSubtyped::new)),
    ORE_METAL_POOR(BlockDef.build("ore_metal_poor", BlockOreMetalPoor.class).item(ItemBlockRailcraftSubtyped::new)),
    POST(BlockDef.build("post", BlockPost.class).item(ItemPost::new)),
    POST_METAL(BlockDef.build("post_metal", BlockPostMetal.class).item(ItemPostMetal::new)),
    POST_METAL_PLATFORM(BlockDef.build("post_metal_platform", BlockPostMetalPlatform.class).item(ItemPostMetal::new)),
    REINFORCED_CONCRETE(BlockDef.build("reinforced_concrete", BlockReinforcedConcrete.class).item(ItemBlockRailcraftColored::new)),
    RITUAL(BlockDef.build("ritual", BlockRitual.class)),
    SIGNAL(BlockDef.build("signal", BlockMachineSignalRailcraft.class).item(ItemSignal::new)),
    SIGNAL_DUAL(BlockDef.build("signal_dual", BlockMachineSignalDualRailcraft.class).item(ItemSignal::new)),
    SIGNAL_BOX(BlockDef.build("signal_box", BlockMachineSignalBoxRailcraft.class).item(ItemMachine::new)),
    TRACK_ELEVATOR(BlockDef.build("track_elevator", BlockTrackElevator.class).item(ItemTrack::new)),

    // Flex Tracks

    TRACK_FLEX_ABANDONED(BlockDef.build("track_flex_abandoned", BlockTrackFlexAbandoned.class)
            .block(() -> new BlockTrackFlexAbandoned(TrackTypes.ABANDONED.getTrackType()))
            .item(ItemTrackStateless::new)),
    TRACK_FLEX_ELECTRIC(BlockDef.build("track_flex_electric", BlockTrackFlexElectric.class)
            .block(() -> new BlockTrackFlexElectric(TrackTypes.ELECTRIC.getTrackType()))
            .item(ItemTrackStateless::new)),
    TRACK_FLEX_HIGH_SPEED(BlockDef.build("track_flex_high_speed", BlockTrackFlex.class)
            .block(() -> new BlockTrackFlex(TrackTypes.HIGH_SPEED.getTrackType()))
            .item(ItemTrackStateless::new)),
    TRACK_FLEX_HS_ELECTRIC(BlockDef.build("track_flex_hs_electric", BlockTrackFlexElectric.class)
            .block(() -> new BlockTrackFlexElectric(TrackTypes.HIGH_SPEED_ELECTRIC.getTrackType()))
            .item(ItemTrackStateless::new)),
    TRACK_FLEX_REINFORCED(BlockDef.build("track_flex_reinforced", BlockTrackFlex.class)
            .block(() -> new BlockTrackFlex(TrackTypes.REINFORCED.getTrackType()))
            .item(ItemTrackStateless::new)),
    TRACK_FLEX_STRAP_IRON(BlockDef.build("track_flex_strap_iron", BlockTrackFlex.class)
            .block(() -> new BlockTrackFlex(TrackTypes.STRAP_IRON.getTrackType()))
            .item(ItemTrackStateless::new)),

    TRACK_FORCE(BlockDef.build("track_force", BlockTrackForce.class).item(ItemTrack::new)),
    TRACK_OUTFITTED(BlockDef.build("track_outfitted", BlockTrackOutfitted.class).item(ItemTrackOutfitted::new)),
    WALL(BlockDef.build("wall", BlockRailcraftWall.class).item(ItemMaterial::new)),
    WIRE(BlockDef.build("wire", BlockWire.class).item(ItemWire::new)),
    WORLD_LOGIC(BlockDef.build("worldlogic", BlockWorldLogic.class).item(ItemBlockRailcraft::new)),
    WORLDSPIKE(BlockDef.build("worldspike", BlockWorldspike.class).item(ItemWorldspike::new)),
    WORLDSPIKE_POINT(BlockDef.build("worldspike_point", BlockWorldspikePoint.class).item(ItemBlockRailcraft::new)),
    // singles
    TRADE_STATION(BlockDef.build("trade_station", BlockTradeStation.class).item(ItemBlockEntityDelegate::new)),
    FORCE_TRACK_EMITTER(BlockDef.build("force_track_emitter", BlockForceTrackEmitter.class).item(ItemForceTrackEmitter::new)),
    ADMIN_STEAM_PRODUCER(BlockDef.build("admin_steam_producer", BlockAdminSteamProducer.class).item(ItemBlockEntityDelegate::new)),
    CHEST_METALS(BlockDef.build("chest_metals", BlockChestMetals.class).item(ItemBlockEntityDelegate::new)),
    CHEST_VOID(BlockDef.build("chest_void", BlockChestVoid.class).item(ItemBlockEntityDelegate::new)),
    // multiblocks
    COKE_OVEN(BlockDef.build("coke_oven", BlockCokeOvenSandy.class).item(ItemBlockCustomModel::new)),
    COKE_OVEN_RED(BlockDef.build("coke_oven_red", BlockCokeOvenRed.class).item(ItemBlockCustomModel::new)),
    BLAST_FURNACE(BlockDef.build("blast_furnace", BlockBlastFurnace.class).item(ItemBlockCustomModel::new)),
    ROCK_CRUSHER(BlockDef.build("rock_crusher", BlockRockCrusher.class).item(ItemBlockEntityDelegate::new)),
    STEAM_OVEN(BlockDef.build("steam_oven", BlockSteamOven.class).item(ItemBlockCustomModel::new)),
    TANK_IRON_GAUGE(BlockDef.build("tank_iron_gauge", GlassReplacer.class)),
    TANK_IRON_VALVE(BlockDef.build("tank_iron_valve", BlockTankIronValve.class).item(ItemBlockMetalTank::new)),
    TANK_IRON_WALL(BlockDef.build("tank_iron_wall", BlockTankIronWall.class).item(ItemBlockMetalTank::new)),
    TANK_STEEL_GAUGE(BlockDef.build("tank_steel_gauge", GlassReplacer.class)),
    TANK_STEEL_VALVE(BlockDef.build("tank_steel_valve", BlockTankSteelValve.class).item(ItemBlockMetalTank::new)),
    TANK_STEEL_WALL(BlockDef.build("tank_steel_wall", BlockTankSteelWall.class).item(ItemBlockMetalTank::new)),
    TANK_WATER(BlockDef.build("tank_water", BlockTankWater.class).item(ItemBlockEntityDelegate::new)),
    FLUX_TRANSFORMER(BlockDef.build("flux_transformer", BlockFluxTransformer.class).item(ItemBlockEntityDelegate::new)),
    BOILER_FIREBOX_FLUID(BlockDef.build("boiler_firebox_fluid", BlockBoilerFireboxFluid.class).item(ItemBlockEntityDelegate::new)),
    BOILER_FIREBOX_SOLID(BlockDef.build("boiler_firebox_solid", BlockBoilerFireboxSolid.class).item(ItemBlockEntityDelegate::new)),
    BOILER_TANK_PRESSURE_HIGH(BlockDef.build("boiler_tank_pressure_high", BlockBoilerTankHigh.class).item(ItemBlockEntityDelegate::new)),
    BOILER_TANK_PRESSURE_LOW(BlockDef.build("boiler_tank_pressure_low", BlockBoilerTankLow.class).item(ItemBlockEntityDelegate::new)),
    STEAM_TURBINE(BlockDef.build("steam_turbine", BlockSteamTurbine.class).item(ItemBlockCustomModel::new)),
    // others
    ;

    public static final RailcraftBlocks[] VALUES = values();

    public static RailcraftBlocks byTag(String tag) {
        return Arrays.stream(VALUES).filter(bc -> StringUtils.equals(tag, bc.getRegistryName().getPath())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(tag + " not a valid block"));
    }

    private final BlockDef<? extends IRailcraftBlock, ? extends IRailcraftItemBlock> def;
    private final Class<? extends IVariantEnum> variantClass;

    <B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock>
    RailcraftBlocks(BlockDef<B, I> def) {
        this.def = def;
        def.obj = this;
        BlockMeta.Variant annotation = def.blockClass.getAnnotation(BlockMeta.Variant.class);
        this.variantClass = annotation != null ? annotation.value() : null;
        conditions().add(RailcraftConfig::isBlockEnabled, () -> "disabled via config");
    }

    private static class BlockDef<B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock> extends Definition<BlockDef<B, I>> {
        private IRailcraftObjectContainer<?> obj;
        private final Class<B> blockClass;
        private Supplier<B> blockSupplier = defaultSupplier();
        private @Nullable Function<B, I> itemSupplier;
        private Optional<B> block = Optional.empty();
        private Optional<I> item = Optional.empty();

        private BlockDef(String tag, Class<B> blockClass) {
            super(tag);
            this.blockClass = blockClass;
        }

        private static <B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock> BlockDef<B, I>
        build(String tag, Class<B> blockClass) {
            return new BlockDef<>(tag, blockClass);
        }

        private BlockDef<B, I> block(Supplier<B> blockSupplier) {
            this.blockSupplier = blockSupplier;
            return this;
        }

        private BlockDef<B, I> item(Function<B, I> itemSupplier) {
            this.itemSupplier = itemSupplier;
            return this;
        }

        private Supplier<B> defaultSupplier() {
            return () -> {
                try {
                    return blockClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        }

        private void register() {
            if (block.isPresent())
                return;

            if (obj.isEnabled()) {
                block = Optional.of(blockSupplier.get());
                B b = block.get();
                b.setRegistryName(registryName);
                b.setTranslationKey("railcraft." + tag.replace("_", "."));

                I i = null;
                if (itemSupplier != null) {
                    item = Optional.of(itemSupplier.apply(b));
                    i = item.get();
                    i.setRegistryName(registryName);
                }

                RailcraftRegistry.register(b, i);

                b.initializeDefinition();

                if (i != null) {
                    if (i instanceof IRailcraftItemSimple)
                        throw new RuntimeException("Railcraft ItemBlocks must not implement IRailcraftItemSimple");
                    i.initializeDefinition();
                }
            } else {
                conditions.printFailureReason(obj);
            }
        }

        private Optional<B> block() {
            return block;
        }

        private Optional<I> item() {
            return item;
        }
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public void register() {
        def.register();
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void defineRecipes() {
        def.block().ifPresent(o -> o.defineRecipes());
        def.item().ifPresent(o -> o.defineRecipes());
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void finalizeDefinition() {
        def.block().ifPresent(o -> o.finalizeDefinition());
        def.item().ifPresent(o -> o.finalizeDefinition());
    }

    @SuppressWarnings("unused")
    public boolean isEqual(IVariantEnum variant, @Nullable ItemStack stack) {
        return !InvTools.isEmpty(stack) && def.block().isPresent() && InvTools.isItemEqual(stack, getStack(variant));
    }

    @SuppressWarnings("unused")
    public boolean isEqual(@Nullable Block block) {
        return def.block().isPresent() && def.block().get() == block;
    }

    public boolean isEqual(IBlockState state) {
        return def.block().isPresent() && def.block().get() == state.getBlock();
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        return def.block().map(b -> b.getState(variant)).orElseGet(this::getDefaultState);
    }

    @Override
    public @Nullable ItemBlock item() {
        return def.item().orElse(null);
    }

    public @Nullable Class<? extends IVariantEnum> getVariantClass() {
        return variantClass;
    }

    @Override
    public Optional<IRailcraftBlock> getObject() {
        //noinspection unchecked
        return (Optional<IRailcraftBlock>) def.block();
    }

    @Override
    public boolean isLoaded() {
        return def.block().isPresent();
    }

    @Override
    public String toString() {
        return "Block{" + getBaseTag() + "}";
    }

}
