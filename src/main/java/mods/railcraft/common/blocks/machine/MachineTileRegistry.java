/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.machine.alpha.*;
import mods.railcraft.common.blocks.machine.beta.*;
import mods.railcraft.common.blocks.machine.charge.TileChargeFeederAdmin;
import mods.railcraft.common.blocks.machine.charge.TileChargeFeederIC2;
import mods.railcraft.common.blocks.machine.epsilon.TileAdminSteamProducer;
import mods.railcraft.common.blocks.machine.epsilon.TileEngravingBench;
import mods.railcraft.common.blocks.machine.epsilon.TileFluxTransformer;
import mods.railcraft.common.blocks.machine.epsilon.TileForceTrackEmitter;
import mods.railcraft.common.blocks.machine.equipment.TileFeedStation;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachineManual;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.blocks.machine.equipment.TileSmoker;
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
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineTileRegistry {

    public static void registerTileEntities() {
        // Alpha
        GameRegistry.registerTileEntity(TileTradeStation.class, "RCTradeStationTile");
        GameRegistry.registerTileEntity(TileSteamTurbine.class, "RCSteamTurbineTile");
        GameRegistry.registerTileEntity(TileBlastFurnace.class, "RCBlastFurnaceTile");
        GameRegistry.registerTileEntity(TileCokeOven.class, "RCCokeOvenTile");
        GameRegistry.registerTileEntity(TileRockCrusher.class, "RCRockCrusherTile");
        GameRegistry.registerTileEntity(TileTankWater.class, "RCWaterTankTile");
        GameRegistry.registerTileEntity(TileSteamOven.class, "RCSteamOvenTile");
        GameRegistry.registerTileEntity(TileSteamTrapManual.class, "RCSteamTrapManualTile");
        GameRegistry.registerTileEntity(TileSteamTrapAuto.class, "RCSteamTrapAutoTile");

        // Beta
        GameRegistry.registerTileEntity(TileEngineSteamHobby.class, "RCEngineSteamHobby");
        GameRegistry.registerTileEntity(TileEngineSteamLow.class, "RCEngineSteamLow");
        GameRegistry.registerTileEntity(TileEngineSteamHigh.class, "RCEngineSteamHigh");
        GameRegistry.registerTileEntity(TileBoilerFireboxSolid.class, "RCBoilerFireboxSoildTile");
        GameRegistry.registerTileEntity(TileBoilerFireboxFluid.class, "RCBoilerFireboxLiquidTile");
        GameRegistry.registerTileEntity(TileBoilerTankLow.class, "RCBoilerTankLowTile");
        GameRegistry.registerTileEntity(TileBoilerTankHigh.class, "RCBoilerTankHighTile");
        GameRegistry.registerTileEntity(TileTankIronWall.class, "RCIronTankWallTile");
        GameRegistry.registerTileEntity(TileTankIronGauge.class, "RCIronTankGaugeTile");
        GameRegistry.registerTileEntity(TileTankIronValve.class, "RCIronTankValveTile");
        GameRegistry.registerTileEntity(TileTankSteelWall.class, "RCSteelTankWallTile");
        GameRegistry.registerTileEntity(TileTankSteelGauge.class, "RCSteelTankGaugeTile");
        GameRegistry.registerTileEntity(TileTankSteelValve.class, "RCSteelTankValveTile");
        GameRegistry.registerTileEntity(TileChestVoid.class, "RCVoidChestTile");
        GameRegistry.registerTileEntity(TileChestMetals.class, "RCMetalsChestTile");
        GameRegistry.registerTileEntity(TileSawmill.class, "RCSawmillTile");

        // Epsilon
        GameRegistry.registerTileEntity(TileAdminSteamProducer.class, "RCAdminSteamProducerTile");
        GameRegistry.registerTileEntity(TileFluxTransformer.class, "RCFluxTransformerTile");
        GameRegistry.registerTileEntity(TileForceTrackEmitter.class, "RCForceTrackEmitterTile");
        GameRegistry.registerTileEntity(TileEngravingBench.class, "RCEngravingBenchTile");

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
        GameRegistry.registerTileEntity(TileDispenserCart.class, "RCMinecartDispenserTile");
        GameRegistry.registerTileEntity(TileIC2Loader.class, "RCLoaderTileEnergy");
        GameRegistry.registerTileEntity(TileIC2Unloader.class, "RCUnloaderTileEnergy");
        GameRegistry.registerTileEntity(TileDispenserTrain.class, "RCTrainDispenserTile");
        GameRegistry.registerTileEntity(TileItemLoader.class, "RCLoaderTile");
        GameRegistry.registerTileEntity(TileItemLoaderAdvanced.class, "RCLoaderAdvancedTile");
        GameRegistry.registerTileEntity(TileItemUnloader.class, "RCUnloaderTile");
        GameRegistry.registerTileEntity(TileItemUnloaderAdvanced.class, "RCUnloaderAdvancedTile");
        GameRegistry.registerTileEntity(TileFluidLoader.class, "RCLoaderTileLiquid");
        GameRegistry.registerTileEntity(TileFluidUnloader.class, "RCUnloaderTileLiquid");
        GameRegistry.registerTileEntity(TileRFLoader.class, "RCLoaderTileRF");
        GameRegistry.registerTileEntity(TileRFUnloader.class, "RCUnloaderTileRF");

        // Signals
        RailcraftRegistry.register(TileSignalDistant.class, "signal_block");
        RailcraftRegistry.register(TileSignalBlock.class, "signal_distant");
        RailcraftRegistry.register(TileSignalToken.class, "signal_token");
        RailcraftRegistry.register(TileSignalDistantDual.class, "signal_distant_dual");
        RailcraftRegistry.register(TileSignalBlockDual.class, "signal_block_dual");

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
    }
}
