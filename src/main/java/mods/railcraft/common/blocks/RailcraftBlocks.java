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
    ACTUATOR("actuator", BlockMachineActuator.class, ItemMachineActuator::new),
    ANVIL_STEEL("anvil", BlockRCAnvil.class, ItemAnvil::new),
    BATTERY_NICKEL_IRON("battery_nickel_iron", BlockBatteryNickelIron.class, ItemBattery::new),
    BATTERY_NICKEL_ZINC("battery_nickel_zinc", BlockBatteryNickelZinc.class, ItemBattery::new),
    BATTERY_ZINC_CARBON("battery_zinc_carbon", BlockBatteryZincCarbon.class, ItemBattery::new),
    BATTERY_ZINC_SILVER("battery_zinc_silver", BlockBatteryZincSilver.class, ItemBattery::new),
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
    CHARGE_FEEDER("charge_feeder", BlockChargeFeeder.class, ItemMachine::new),
    CHARGE_TRAP("charge_trap", BlockChargeTrap.class, ItemBlockRailcraft::new),
    DETECTOR("detector", BlockDetector.class, ItemDetector::new),
    EQUIPMENT("equipment", BlockMachineEquipment.class, ItemMachine::new),
    FRAME("frame", BlockFrame.class, ItemBlockRailcraft::new),
    GENERIC("generic", BlockGeneric.class, ItemBlockGeneric::new),
    METAL("metal", BlockMetal.class, ItemBlockMetal::new),
    GLASS("glass", BlockStrengthGlass.class, ItemBlockRailcraftColored::new),
    LANTERN("lantern", BlockLantern.class, ItemMaterial::new),
    LOGBOOK("logbook", BlockLogbook.class, ItemBlockRailcraft::new),
    MANIPULATOR("manipulator", BlockMachineManipulator.class, ItemMachine::new),
    ORE("ore", BlockOre.class, ItemBlockRailcraftSubtyped::new),
    ORE_MAGIC("ore_magic", BlockOreMagic.class, ItemOreMagic::new),
    ORE_METAL("ore_metal", BlockOreMetal.class, ItemBlockRailcraftSubtyped::new),
    ORE_METAL_POOR("ore_metal_poor", BlockOreMetalPoor.class, ItemBlockRailcraftSubtyped::new),
    POST("post", BlockPost.class, ItemPost::new),
    POST_METAL("post_metal", BlockPostMetal.class, ItemPostMetal::new),
    POST_METAL_PLATFORM("post_metal_platform", BlockPostMetalPlatform.class, ItemPostMetal::new),
    REINFORCED_CONCRETE("reinforced_concrete", BlockReinforcedConcrete.class, ItemBlockRailcraftColored::new),
    RITUAL("ritual", BlockRitual.class, null),
    SIGNAL("signal", BlockMachineSignalRailcraft.class, ItemSignal::new),
    SIGNAL_DUAL("signal_dual", BlockMachineSignalDualRailcraft.class, ItemSignal::new),
    SIGNAL_BOX("signal_box", BlockMachineSignalBoxRailcraft.class, ItemMachine::new),
    SLAB("slab", BlockRailcraftSlab.class, ItemSlab::new),
    STAIR("stair", BlockRailcraftStairs.class, ItemMaterial::new),
    TRACK_ELEVATOR("track_elevator", BlockTrackElevator.class, ItemTrack::new),
    TRACK_FLEX_ABANDONED("track_flex_abandoned", BlockTrackFlexAbandoned.class, () -> new BlockTrackFlexAbandoned(TrackTypes.ABANDONED.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_ELECTRIC("track_flex_electric", BlockTrackFlexElectric.class, () -> new BlockTrackFlexElectric(TrackTypes.ELECTRIC.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_HIGH_SPEED("track_flex_high_speed", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.HIGH_SPEED.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_HS_ELECTRIC("track_flex_hs_electric", BlockTrackFlexElectric.class, () -> new BlockTrackFlexElectric(TrackTypes.HIGH_SPEED_ELECTRIC.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_REINFORCED("track_flex_reinforced", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.REINFORCED.getTrackType()), ItemTrackStateless::new),
    TRACK_FLEX_STRAP_IRON("track_flex_strap_iron", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.STRAP_IRON.getTrackType()), ItemTrackStateless::new),
    TRACK_FORCE("track_force", BlockTrackForce.class, ItemTrack::new),
    TRACK_OUTFITTED("track_outfitted", BlockTrackOutfitted.class, ItemTrackOutfitted::new),
    WALL("wall", BlockRailcraftWall.class, ItemMaterial::new),
    WIRE("wire", BlockWire.class, ItemWire::new),
    WORLD_LOGIC("worldlogic", BlockWorldLogic.class, ItemBlockRailcraft::new),
    WORLDSPIKE("worldspike", BlockWorldspike.class, ItemWorldspike::new),
    WORLDSPIKE_POINT("worldspike_point", BlockWorldspikePoint.class, ItemBlockRailcraft::new),
    // singles
    TRADE_STATION("trade_station", BlockTradeStation.class, ItemBlockEntityDelegate::new),
    FORCE_TRACK_EMITTER("force_track_emitter", BlockForceTrackEmitter.class, ItemForceTrackEmitter::new),
    ADMIN_STEAM_PRODUCER("admin_steam_producer", BlockAdminSteamProducer.class, ItemBlockEntityDelegate::new),
    CHEST_METALS("chest_metals", BlockChestMetals.class, ItemBlockEntityDelegate::new),
    CHEST_VOID("chest_void", BlockChestVoid.class, ItemBlockEntityDelegate::new),
    // multiblocks
    COKE_OVEN("coke_oven", BlockCokeOvenSandy.class, ItemBlockCustomModel::new),
    COKE_OVEN_RED("coke_oven_red", BlockCokeOvenRed.class, ItemBlockCustomModel::new),
    BLAST_FURNACE("blast_furnace", BlockBlastFurnace.class, ItemBlockCustomModel::new),
    ROCK_CRUSHER("rock_crusher", BlockRockCrusher.class, ItemBlockEntityDelegate::new),
    STEAM_OVEN("steam_oven", BlockSteamOven.class, ItemBlockCustomModel::new),
    TANK_IRON_GAUGE("tank_iron_gauge", BlockTankIronGauge.class, ItemBlockRailcraftColored::new),
    TANK_IRON_VALVE("tank_iron_valve", BlockTankIronValve.class, ItemBlockRailcraftColored::new),
    TANK_IRON_WALL("tank_iron_wall", BlockTankIronWall.class, ItemBlockRailcraftColored::new),
    TANK_STEEL_GAUGE("tank_steel_gauge", BlockTankSteelGauge.class, ItemBlockRailcraftColored::new),
    TANK_STEEL_VALVE("tank_steel_valve", BlockTankSteelValve.class, ItemBlockRailcraftColored::new),
    TANK_STEEL_WALL("tank_steel_wall", BlockTankSteelWall.class, ItemBlockRailcraftColored::new),
    TANK_WATER("tank_water", BlockTankWater.class, ItemBlockEntityDelegate::new),
    FLUX_TRANSFORMER("flux_transformer", BlockFluxTransformer.class, ItemBlockEntityDelegate::new),
    BOILER_FIREBOX_FLUID("boiler_firebox_fluid", BlockBoilerFireboxFluid.class, ItemBlockEntityDelegate::new),
    BOILER_FIREBOX_SOLID("boiler_firebox_solid", BlockBoilerFireboxSolid.class, ItemBlockEntityDelegate::new),
    BOILER_TANK_PRESSURE_HIGH("boiler_tank_pressure_high", BlockBoilerTankHigh.class, ItemBlockEntityDelegate::new),
    BOILER_TANK_PRESSURE_LOW("boiler_tank_pressure_low", BlockBoilerTankLow.class, ItemBlockEntityDelegate::new),
    STEAM_TURBINE("steam_turbine", BlockSteamTurbine.class, ItemBlockCustomModel::new),
    // others
    ;

    public static final RailcraftBlocks[] VALUES = values();
    private final BlockDef<? extends IRailcraftBlock, ? extends IRailcraftItemBlock> def;
    private final Class<? extends IVariantEnum> variantClass;

    <B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock>
    RailcraftBlocks(String tag, Class<B> blockClass, @Nullable Function<B, I> itemSupplier) {
        this(tag, blockClass, null, itemSupplier, null);
    }

    <B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock>
    RailcraftBlocks(String tag, Class<B> blockClass, @Nullable Supplier<B> blockSupplier, @Nullable Function<B, I> itemSupplier) {
        this(tag, blockClass, blockSupplier, itemSupplier, null);
    }

    @SuppressWarnings("SameParameterValue")
    <B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock>
    RailcraftBlocks(String tag, Class<B> blockClass, @Nullable Supplier<B> blockSupplier,
                    @Nullable Function<B, I> itemSupplier, @Nullable Supplier<?> altRecipeObject) {
        this.def = new BlockDef<>(this, tag, blockClass, blockSupplier, itemSupplier, altRecipeObject);
        BlockMeta.Variant annotation = blockClass.getAnnotation(BlockMeta.Variant.class);
        this.variantClass = annotation != null ? annotation.value() : null;
        conditions().add(RailcraftConfig::isBlockEnabled, () -> "disabled via config");
    }

    private class BlockDef<B extends Block & IRailcraftBlock, I extends ItemBlock & IRailcraftItemBlock> extends Definition {
        private final Class<B> blockClass;
        private final Supplier<B> blockSupplier;
        private final @Nullable Function<B, I> itemSupplier;
        private Optional<B> block = Optional.empty();
        private Optional<I> item = Optional.empty();

        public BlockDef(IRailcraftObjectContainer<?> obj,
                        String tag,
                        Class<B> blockClass,
                        @Nullable Supplier<B> blockSupplier,
                        @Nullable Function<B, I> itemSupplier,
                        @Nullable Supplier<?> altRecipeObject) {
            super(obj, tag, altRecipeObject);
            this.blockClass = blockClass;
            this.blockSupplier = blockSupplier == null ? defaultSupplier() : blockSupplier;
            this.itemSupplier = itemSupplier;
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

            if (isEnabled()) {
                block = Optional.of(blockSupplier.get());
                B b = block.get();
                b.setRegistryName(getRegistryName());
                b.setTranslationKey("railcraft." + getBaseTag().replace("_", "."));

                I i = null;
                if (itemSupplier != null) {
                    item = Optional.of(itemSupplier.apply(b));
                    i = item.get();
                    i.setRegistryName(getRegistryName());
                }

                RailcraftRegistry.register(b, i);

                b.initializeDefinition();

                if (i != null) {
                    if (i instanceof IRailcraftItemSimple)
                        throw new RuntimeException("Railcraft ItemBlocks must not implement IRailcraftItemSimple");
                    i.initializeDefinition();
                }
            } else {
                conditions().printFailureReason(RailcraftBlocks.this);
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
