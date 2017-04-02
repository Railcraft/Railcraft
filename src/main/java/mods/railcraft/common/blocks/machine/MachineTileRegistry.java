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
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineTileRegistry {

    public static void registerTileEntities() {
        // Alpha
        GameRegistry.registerTileEntity(TileAnchorWorld.class, "RCWorldAnchorTile");
        GameRegistry.registerTileEntity(TileAnchorPersonal.class, "RCPersonalAnchorTile");
        GameRegistry.registerTileEntity(TileAnchorAdmin.class, "RCAdminAnchorTile");
        GameRegistry.registerTileEntity(TileTradeStation.class, "RCTradeStationTile");
        GameRegistry.registerTileEntity(TileSteamTurbine.class, "RCSteamTurbineTile");
        GameRegistry.registerTileEntity(TileBlastFurnace.class, "RCBlastFurnaceTile");
        GameRegistry.registerTileEntity(TileCokeOven.class, "RCCokeOvenTile");
        GameRegistry.registerTileEntity(TileRockCrusher.class, "RCRockCrusherTile");
        GameRegistry.registerTileEntity(TileTankWater.class, "RCWaterTankTile");
        GameRegistry.registerTileEntity(TileSteamOven.class, "RCSteamOvenTile");
        GameRegistry.registerTileEntity(TileSteamTrapManual.class, "RCSteamTrapManualTile");
        GameRegistry.registerTileEntity(TileSteamTrapAuto.class, "RCSteamTrapAutoTile");
        GameRegistry.registerTileEntity(TileAnchorPassive.class, "RCPassiveAnchorTile");

        // Beta
        GameRegistry.registerTileEntity(TileSentinel.class, "RCAnchorSentinelTile");
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
        GameRegistry.registerTileEntity(TileActuatorLever.class, "railcraft:actuator_lever");
        GameRegistry.registerTileEntity(TileActuatorMotor.class, "railcraft:actuator_motor");
        GameRegistry.registerTileEntity(TileActuatorRouting.class, "railcraft:actuator_router");

        // Equipment
        GameRegistry.registerTileEntity(TileRollingMachineManual.class, "railcraft:equipment_rolling_manual");
        GameRegistry.registerTileEntity(TileRollingMachinePowered.class, "railcraft:equipment_rolling_powered");
        GameRegistry.registerTileEntity(TileFeedStation.class, "railcraft:equipment_feed_station");
        GameRegistry.registerTileEntity(TileSmoker.class, "railcraft:equipment_smoker");

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

        // Signal Boxes
        GameRegistry.registerTileEntity(TileBoxAnalog.class, "railcraft:signal_box_analog");
        GameRegistry.registerTileEntity(TileBoxRelay.class, "railcraft:signal_box_relay");
        GameRegistry.registerTileEntity(TileBoxCapacitor.class, "railcraft:signal_box_capacitor");
        GameRegistry.registerTileEntity(TileBoxController.class, "railcraft:signal_box_controller");
        GameRegistry.registerTileEntity(TileBoxInterlock.class, "railcraft:signal_box_interlock");
        GameRegistry.registerTileEntity(TileBoxReceiver.class, "railcraft:signal_box_receiver");
        GameRegistry.registerTileEntity(TileBoxSequencer.class, "railcraft:signal_box_sequencer");
    }
}
