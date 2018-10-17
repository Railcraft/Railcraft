/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.ItemBrick;
import mods.railcraft.common.blocks.aesthetics.concrete.BlockReinforcedConcrete;
import mods.railcraft.common.blocks.aesthetics.concrete.ItemReinforcedConcrete;
import mods.railcraft.common.blocks.aesthetics.generic.BlockGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.ItemBlockGeneric;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftStairs;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.materials.ItemMaterial;
import mods.railcraft.common.blocks.aesthetics.materials.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.materials.slab.ItemSlab;
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
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.blocks.machine.charge.BlockChargeFeeder;
import mods.railcraft.common.blocks.machine.equipment.BlockMachineEquipment;
import mods.railcraft.common.blocks.machine.manipulator.BlockMachineManipulator;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.BlockMachineActuator;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.ItemMachineActuator;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.BlockMachineSignalBoxRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.signals.BlockMachineSignalDualRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.signals.BlockMachineSignalRailcraft;
import mods.railcraft.common.blocks.machine.wayobjects.signals.ItemSignal;
import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspikePoint;
import mods.railcraft.common.blocks.machine.worldspike.ItemWorldspike;
import mods.railcraft.common.blocks.multi.*;
import mods.railcraft.common.blocks.ore.*;
import mods.railcraft.common.blocks.single.*;
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
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.firestone.BlockRitual;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftBlockContainer {
    ACTUATOR("actuator", BlockMachineActuator.class, BlockMachineActuator::new, ItemMachineActuator::new),
    ANVIL_STEEL("anvil", BlockRCAnvil.class, BlockRCAnvil::new, ItemAnvil::new),
    BRICK_ABYSSAL("brick_abyssal", BlockBrick.class, () -> new BlockBrick(BrickTheme.ABYSSAL), ItemBrick::new),
    BRICK_ANDESITE("brick_andesite", BlockBrick.class, () -> new BlockBrick(BrickTheme.ANDESITE), ItemBrick::new),
    BRICK_BLEACHED_BONE("brick_bleachedbone", BlockBrick.class, () -> new BlockBrick(BrickTheme.BLEACHEDBONE), ItemBrick::new),
    BRICK_BLOOD_STAINED("brick_bloodstained", BlockBrick.class, () -> new BlockBrick(BrickTheme.BLOODSTAINED), ItemBrick::new),
    BRICK_DIORITE("brick_diorite", BlockBrick.class, () -> new BlockBrick(BrickTheme.DIORITE), ItemBrick::new),
    BRICK_FROST_BOUND("brick_frostbound", BlockBrick.class, () -> new BlockBrick(BrickTheme.FROSTBOUND), ItemBrick::new),
    BRICK_GRANITE("brick_granite", BlockBrick.class, () -> new BlockBrick(BrickTheme.GRANITE), ItemBrick::new),
    BRICK_INFERNAL("brick_infernal", BlockBrick.class, () -> new BlockBrick(BrickTheme.INFERNAL), ItemBrick::new),
    BRICK_NETHER("brick_nether", BlockBrick.class, () -> new BlockBrick(BrickTheme.NETHER), ItemBrick::new),
    BRICK_PEARLIZED("brick_pearlized", BlockBrick.class, () -> new BlockBrick(BrickTheme.PEARLIZED), ItemBrick::new),
    BRICK_QUARRIED("brick_quarried", BlockBrick.class, () -> new BlockBrick(BrickTheme.QUARRIED), ItemBrick::new),
    BRICK_RED_NETHER("brick_red_nether", BlockBrick.class, () -> new BlockBrick(BrickTheme.REDNETHER), ItemBrick::new),
    BRICK_RED_SANDY("brick_red_sandy", BlockBrick.class, () -> new BlockBrick(BrickTheme.REDSANDY), ItemBrick::new),
    BRICK_SANDY("brick_sandy", BlockBrick.class, () -> new BlockBrick(BrickTheme.SANDY), ItemBrick::new),
    CHARGE_BATTERY("charge_battery", BlockChargeBattery.class, BlockChargeBattery::new, ItemChargeBattery::new),
    CHARGE_FEEDER("charge_feeder", BlockChargeFeeder.class, BlockChargeFeeder::new, ItemMachine::new),
    CHARGE_TRAP("charge_trap", BlockChargeTrap.class, BlockChargeTrap::new, ItemBlockRailcraft::new),
    DETECTOR("detector", BlockDetector.class, BlockDetector::new, ItemDetector::new),
    EQUIPMENT("equipment", BlockMachineEquipment.class, BlockMachineEquipment::new, ItemMachine::new),
    FRAME("frame", BlockFrame.class, BlockFrame::new, ItemBlockRailcraft::new),
    GENERIC("generic", BlockGeneric.class, BlockGeneric::new, ItemBlockGeneric::new),
    METAL("metal", BlockMetal.class, BlockMetal::new, ItemBlockMetal::new),
    GLASS("glass", BlockStrengthGlass.class, BlockStrengthGlass::new, ItemBlockRailcraftColored::new),
    LANTERN("lantern", BlockLantern.class, BlockLantern::new, ItemMaterial::new),
    LOGBOOK("logbook", BlockLogbook.class, BlockLogbook::new, ItemBlockRailcraft::new),
    MANIPULATOR("manipulator", BlockMachineManipulator.class, BlockMachineManipulator::new, ItemMachine::new),
    ORE("ore", BlockOre.class, BlockOre::new, ItemBlockRailcraftSubtyped::new),
    ORE_MAGIC("ore_magic", BlockOreMagic.class, BlockOreMagic::new, ItemOreMagic::new),
    ORE_METAL("ore_metal", BlockOreMetal.class, BlockOreMetal::new, ItemBlockRailcraftSubtyped::new),
    ORE_METAL_POOR("ore_metal_poor", BlockOreMetalPoor.class, BlockOreMetalPoor::new, ItemBlockRailcraftSubtyped::new),
    POST("post", BlockPost.class, BlockPost::new, ItemPost::new),
    POST_METAL("post_metal", BlockPostMetal.class, BlockPostMetal::new, ItemPostMetal::new),
    POST_METAL_PLATFORM("post_metal_platform", BlockPostMetalPlatform.class, BlockPostMetalPlatform::new, ItemPostMetal::new),
    REINFORCED_CONCRETE("reinforced_concrete", BlockReinforcedConcrete.class, BlockReinforcedConcrete::new, ItemReinforcedConcrete::new),
    RITUAL("ritual", BlockRitual.class, BlockRitual::new, null),
    SIGNAL("signal", BlockMachineSignalRailcraft.class, BlockMachineSignalRailcraft::new, ItemSignal::new),
    SIGNAL_DUAL("signal_dual", BlockMachineSignalDualRailcraft.class, BlockMachineSignalDualRailcraft::new, ItemSignal::new),
    SIGNAL_BOX("signal_box", BlockMachineSignalBoxRailcraft.class, BlockMachineSignalBoxRailcraft::new, ItemMachine::new),
    SLAB("slab", BlockRailcraftSlab.class, BlockRailcraftSlab::new, ItemSlab::new),
    STAIR("stair", BlockRailcraftStairs.class, BlockRailcraftStairs::new, ItemMaterial::new),
    TRACK_ELEVATOR("track_elevator", BlockTrackElevator.class, BlockTrackElevator::new, ItemTrack::new),
    TRACK_FLEX_ABANDONED("track_flex_abandoned", BlockTrackFlexAbandoned.class, () -> new BlockTrackFlexAbandoned(TrackTypes.ABANDONED.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_ELECTRIC("track_flex_electric", BlockTrackFlexElectric.class, () -> new BlockTrackFlexElectric(TrackTypes.ELECTRIC.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_HIGH_SPEED("track_flex_high_speed", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.HIGH_SPEED.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_HS_ELECTRIC("track_flex_hs_electric", BlockTrackFlexElectric.class, () -> new BlockTrackFlexElectric(TrackTypes.HIGH_SPEED_ELECTRIC.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_REINFORCED("track_flex_reinforced", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.REINFORCED.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_STRAP_IRON("track_flex_strap_iron", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.STRAP_IRON.getTrackType()), ItemTrackStateless::new),
    TRACK_FORCE("track_force", BlockTrackForce.class, BlockTrackForce::new, ItemTrack::new),
    TRACK_OUTFITTED("track_outfitted", BlockTrackOutfitted.class, BlockTrackOutfitted::new, ItemTrackOutfitted::new),
    WALL("wall", BlockRailcraftWall.class, BlockRailcraftWall::new, ItemMaterial::new),
    WIRE("wire", BlockWire.class, BlockWire::new, ItemBlockRailcraft::new),
    WORLD_LOGIC("worldlogic", BlockWorldLogic.class, BlockWorldLogic::new, ItemBlockRailcraft::new),
    WORLDSPIKE("worldspike", BlockWorldspike.class, BlockWorldspike::new, ItemWorldspike::new),
    WORLDSPIKE_POINT("worldspike_point", BlockWorldspikePoint.class, BlockWorldspikePoint::new, ItemBlockRailcraft::new),
    // singles
    TRADE_STATION("trade_station", BlockTradeStation.class, BlockTradeStation::new, ItemBlockEntityDelegate::new),
    FORCE_TRACK_EMITTER("force_track_emitter", BlockForceTrackEmitter.class, BlockForceTrackEmitter::new, ItemForceTrackEmitter::new),
    ADMIN_STEAM_PRODUCER("admin_steam_producer", BlockAdminSteamProducer.class, BlockAdminSteamProducer::new, ItemBlockEntityDelegate::new),
    CHEST_METALS("chest_metals", BlockChestMetals.class, BlockChestMetals::new, ItemBlockEntityDelegate::new),
    CHEST_VOID("chest_void", BlockChestVoid.class, BlockChestVoid::new, ItemBlockEntityDelegate::new),
    // multiblocks
    COKE_OVEN("coke_oven", BlockCokeOven.class, BlockCokeOvenRegular::new, ItemBlockCustomModel::new),
    BLAST_FURNACE("blast_furnace", BlockBlastFurnace.class, BlockBlastFurnace::new, ItemBlockCustomModel::new),
    ROCK_CRUSHER("rock_crusher", BlockRockCrusher.class, BlockRockCrusher::new, ItemBlockEntityDelegate::new),
    STEAM_OVEN("steam_oven", BlockSteamOven.class, BlockSteamOven::new, ItemBlockCustomModel::new),
    TANK_IRON_GAUGE("tank_iron_gauge", BlockTankIronGauge.class, BlockTankIronGauge::new, ItemBlockRailcraftColored::new),
    TANK_IRON_VALVE("tank_iron_valve", BlockTankIronValve.class, BlockTankIronValve::new, ItemBlockRailcraftColored::new),
    TANK_IRON_WALL("tank_iron_wall", BlockTankIronWall.class, BlockTankIronWall::new, ItemBlockRailcraftColored::new),
    TANK_STEEL_GAUGE("tank_steel_gauge", BlockTankSteelGauge.class, BlockTankSteelGauge::new, ItemBlockRailcraftColored::new),
    TANK_STEEL_VALVE("tank_steel_valve", BlockTankSteelValve.class, BlockTankSteelValve::new, ItemBlockRailcraftColored::new),
    TANK_STEEL_WALL("tank_steel_wall", BlockTankSteelWall.class, BlockTankSteelWall::new, ItemBlockRailcraftColored::new),
    TANK_WATER("tank_water", BlockTankWater.class, BlockTankWater::new, ItemBlockEntityDelegate::new),
    FLUX_TRANSFORMER("flux_transformer", BlockFluxTransformer.class, BlockFluxTransformer::new, ItemBlockEntityDelegate::new),
    BOILER_FIREBOX_FLUID("boiler_firebox_fluid", BlockBoilerFireboxFluid.class, BlockBoilerFireboxFluid::new, ItemBlockEntityDelegate::new),
    BOILER_FIREBOX_SOLID("boiler_firebox_solid", BlockBoilerFireboxSolid.class, BlockBoilerFireboxSolid::new, ItemBlockEntityDelegate::new),
    BOILER_TANK_PRESSURE_HIGH("boiler_tank_pressure_high", BlockBoilerTankHigh.class, BlockBoilerTankHigh::new, ItemBlockEntityDelegate::new),
    BOILER_TANK_PRESSURE_LOW("boiler_tank_pressure_low", BlockBoilerTankLow.class, BlockBoilerTankLow::new, ItemBlockEntityDelegate::new),
    STEAM_TURBINE("steam_turbine", BlockSteamTurbine.class, BlockSteamTurbine::new, ItemBlockCustomModel::new),
    COKE_OVEN_RED("coke_oven_red", BlockCokeOven.class, BlockCokeOvenRed::new, ItemBlockCustomModel::new),
    // others
    ;
    public static final RailcraftBlocks[] VALUES = values();
    private final Supplier<Block> blockSupplier;
    private final Function<Block, ItemBlock> itemSupplier;
    private final Class<? extends IVariantEnum> variantClass;
    private final Definition def;
    private Block block;
    private ItemBlock item;

    RailcraftBlocks(String tag, Class<? extends Block> blockClass, Supplier<Block> blockSupplier, @Nullable Function<Block, ItemBlock> itemSupplier) {
        this(tag, blockClass, blockSupplier, itemSupplier, null);
    }

    RailcraftBlocks(String tag, Class<? extends Block> blockClass, Supplier<Block> blockSupplier,
                    @Nullable Function<Block, ItemBlock> itemSupplier, @Nullable Supplier<?> altRecipeObject) {
        this.def = new Definition(this, tag, altRecipeObject);
        RailcraftBlockMetadata annotation = blockClass.getAnnotation(RailcraftBlockMetadata.class);
        this.variantClass = annotation != null ? annotation.variant() : null;
        this.blockSupplier = blockSupplier;
        this.itemSupplier = itemSupplier;
        conditions().add(RailcraftConfig::isBlockEnabled, () -> "disabled via config");
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public void register() {
        if (block != null)
            return;

        if (isEnabled()) {
            block = blockSupplier.get();
            block.setRegistryName(getRegistryName());
            block.setTranslationKey("railcraft." + getBaseTag().replace("_", "."));

            if (itemSupplier != null) {
                item = itemSupplier.apply(block);
                item.setRegistryName(getRegistryName());
            }

            RailcraftRegistry.register(block, item);

            if (!(block instanceof IRailcraftBlock))
                throw new RuntimeException("Railcraft Blocks must implement IRailcraftBlock");
            IRailcraftBlock blockObject = (IRailcraftBlock) block;
            blockObject.initializeDefinition();

            if (item != null) {
                if (!(item instanceof IRailcraftItemBlock))
                    throw new RuntimeException("Railcraft ItemBlocks must implement IRailcraftItemBlock");
                if (item instanceof IRailcraftItemSimple)
                    throw new RuntimeException("Railcraft ItemBlocks must not implement IRailcraftItemSimple");
                IRailcraftItemBlock itemObject = (IRailcraftItemBlock) item;
                itemObject.initializeDefinition();
            }
        } else {
            conditions().printFailureReason(this);
        }
    }

    @Override
    public void defineRecipes() {
        if (block != null)
            ((IRailcraftObject) block).defineRecipes();
        if (item != null)
            ((IRailcraftObject) item).defineRecipes();
    }

    @Override
    public void finalizeDefinition() {
        if (block != null)
            ((IRailcraftObject) block).finalizeDefinition();
        if (item != null)
            ((IRailcraftObject) item).finalizeDefinition();
    }

    public boolean isEqual(IVariantEnum variant, @Nullable ItemStack stack) {
        return !InvTools.isEmpty(stack) && block != null && InvTools.isItemEqual(stack, getStack(variant));
    }

    public boolean isEqual(@Nullable Block block) {
        return block != null && this.block == block;
    }

    public boolean isEqual(IBlockState state) {
        return block != null && block == state.getBlock();
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        if (block instanceof IRailcraftBlock)
            return ((IRailcraftBlock) block).getState(variant);
        return getDefaultState();
    }

    @Override
    public @Nullable ItemBlock item() {
        return item;
    }

    public @Nullable Class<? extends IVariantEnum> getVariantClass() {
        return variantClass;
    }

    @Override
    public Optional<IRailcraftBlock> getObject() {
        return Optional.ofNullable((IRailcraftBlock) block);
    }

    @Override
    public boolean isLoaded() {
        return block != null;
    }

    @Override
    public String toString() {
        return "Block{" + getBaseTag() + "}";
    }

}
