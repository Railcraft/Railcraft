/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.machine.charge.TileChargeFeederAdmin;
import mods.railcraft.common.blocks.machine.charge.TileChargeFeederIC2;
import mods.railcraft.common.blocks.machine.equipment.*;
import mods.railcraft.common.blocks.machine.manipulator.*;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.TileActuatorLever;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.TileActuatorMotor;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.TileActuatorRouting;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.*;
import mods.railcraft.common.blocks.machine.wayobjects.signals.*;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspikeAdmin;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspikePassive;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspikePersonal;
import mods.railcraft.common.blocks.single.*;
import mods.railcraft.common.blocks.structures.*;
import mods.railcraft.common.blocks.tracks.force.TileTrackForce;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class MachineTileRegistry {

    public static void registerTileEntities() {
        // Alpha
        RailcraftRegistry.register(TileTradeStation.class, "trade_station");
        RailcraftRegistry.register(TileSteamTurbine.class, "steam_turbine");
        RailcraftRegistry.register(TileBlastFurnace.class, "blast_furnace");
        RailcraftRegistry.register(TileCokeOven.class, "coke_oven");
        RailcraftRegistry.register(TileRockCrusher.class, "rock_crusher");
        RailcraftRegistry.register(TileTankWater.class, "water_tank");
        RailcraftRegistry.register(TileSteamOven.class, "steam_oven");
        RailcraftRegistry.register(TileSteamTrapManual.class, "steam_trap_manual");
        RailcraftRegistry.register(TileSteamTrapAuto.class, "steam_trap_auto");

        // Beta
        RailcraftRegistry.register(TileEngineSteamHobby.class, "engine_steam_hobby");
        RailcraftRegistry.register(TileEngineSteamLow.class, "engine_steam_commercial");
        RailcraftRegistry.register(TileEngineSteamHigh.class, "engine_steam_industrial");
        RailcraftRegistry.register(TileBoilerFireboxSolid.class, "boiler_firebox_solid");
        RailcraftRegistry.register(TileBoilerFireboxFluid.class, "boiler_firebox_fluid");
        RailcraftRegistry.register(TileBoilerTankLow.class, "boiler_tank_pressure_low");
        RailcraftRegistry.register(TileBoilerTankHigh.class, "boiler_tank_pressure_high");
        RailcraftRegistry.register(TileTankIron.class, "tank_iron_wall");
        RailcraftRegistry.register(TileTankValveIron.class, "tank_iron_valve");
        RailcraftRegistry.register(TileTankSteel.class, "tank_steel_wall");
        RailcraftRegistry.register(TileTankValveSteel.class, "tank_steel_valve");
        RailcraftRegistry.register(TileChestVoid.class, "chest_void");
        RailcraftRegistry.register(TileChestMetals.class, "chest_metals");

        // Epsilon
        RailcraftRegistry.register(TileAdminSteamProducer.class, "admin_steam_producer");
        RailcraftRegistry.register(TileFluxTransformer.class, "flux_transformer");
        RailcraftRegistry.register(TileForceTrackEmitter.class, "force_track_emitter");
        RailcraftRegistry.register(TileEngravingBench.class, "engraving_bench");

        // Actuator
        RailcraftRegistry.register(TileActuatorLever.class, "actuator_lever");
        RailcraftRegistry.register(TileActuatorMotor.class, "actuator_motor");
        RailcraftRegistry.register(TileActuatorRouting.class, "actuator_router");

        // Charge Batteries
//        RailcraftRegistry.register(TileChargeBattery.class, "charge_battery");

        // Charge Feeders
        RailcraftRegistry.register(TileChargeFeederAdmin.class, "charge_feeder_admin");
        RailcraftRegistry.register(TileChargeFeederIC2.class, "charge_feeder_ic2");

        // Equipment
        RailcraftRegistry.register(TileRollingMachineManual.class, "equipment_rolling_manual");
        RailcraftRegistry.register(TileRollingMachinePowered.class, "equipment_rolling_powered");
        RailcraftRegistry.register(TileFeedStation.class, "equipment_feed_station");
        RailcraftRegistry.register(TileSmoker.class, "equipment_smoker");

        // Manipulator
        RailcraftRegistry.register(TileDispenserCart.class, "manipulator_dispenser_cart", "RCMinecartDispenserTile");
        RailcraftRegistry.register(TileDispenserTrain.class, "manipulator_dispenser_train", "RCTrainDispenserTile");
        RailcraftRegistry.register(TileIC2Loader.class, "manipulator_ic2_loader", "RCLoaderTileEnergy");
        RailcraftRegistry.register(TileIC2Unloader.class, "manipulator_ic2_unloader", "RCUnloaderTileEnergy");
        RailcraftRegistry.register(TileItemLoader.class, "manipulator_item_loader", "RCLoaderTile");
        RailcraftRegistry.register(TileItemLoaderAdvanced.class, "manipulator_item_loader_adv", "RCLoaderAdvancedTile");
        RailcraftRegistry.register(TileItemUnloader.class, "manipulator_item_unloader", "RCUnloaderTile");
        RailcraftRegistry.register(TileItemUnloaderAdvanced.class, "manipulator_item_unloader_adv", "RCUnloaderAdvancedTile");
        RailcraftRegistry.register(TileFluidLoader.class, "manipulator_fluid_loader", "RCLoaderTileLiquid");
        RailcraftRegistry.register(TileFluidUnloader.class, "manipulator_fluid_unloader", "RCUnloaderTileLiquid");
        RailcraftRegistry.register(TileRFLoader.class, "manipulator_rf_loader", "RCLoaderTileRF");
        RailcraftRegistry.register(TileRFUnloader.class, "manipulator_rf_unloader", "RCUnloaderTileRF");

        // Signals
        RailcraftRegistry.register(TileSignalDistant.class, "signal_block");
        RailcraftRegistry.register(TileSignalBlock.class, "signal_distant");
        RailcraftRegistry.register(TileSignalToken.class, "signal_token");
        RailcraftRegistry.register(TileSignalDistantDual.class, "signal_distant_dual");
        RailcraftRegistry.register(TileSignalBlockDual.class, "signal_block_dual");
        RailcraftRegistry.register(TileSignalTokenDual.class, "signal_token_dual");

        // Signal Boxes
        RailcraftRegistry.register(TileBoxAnalog.class, "signal_box_analog");
        RailcraftRegistry.register(TileBoxRelay.class, "signal_box_relay");
        RailcraftRegistry.register(TileBoxCapacitor.class, "signal_box_capacitor");
        RailcraftRegistry.register(TileBoxController.class, "signal_box_controller");
        RailcraftRegistry.register(TileBoxInterlock.class, "signal_box_interlock");
        RailcraftRegistry.register(TileBoxReceiver.class, "signal_box_receiver");
        RailcraftRegistry.register(TileBoxSequencer.class, "signal_box_sequencer");

        // Worldspike
        RailcraftRegistry.register(TileWorldspike.class, "worldspike_standard");
        RailcraftRegistry.register(TileWorldspikePersonal.class, "worldspike_personal");
        RailcraftRegistry.register(TileWorldspikeAdmin.class, "worldspike_admin");
        RailcraftRegistry.register(TileWorldspikePassive.class, "worldspike_passive");

        RailcraftRegistry.register(TileTrackForce.class, "track_force");
    }

    private MachineTileRegistry() {
    }
}
