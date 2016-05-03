/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.machine.epsilon.TileAdminSteamProducer;
import mods.railcraft.common.blocks.machine.epsilon.TileElectricFeederAdmin;
import mods.railcraft.common.blocks.machine.epsilon.TileElectricFeeder;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.common.blocks.machine.alpha.*;
import mods.railcraft.common.blocks.machine.beta.*;
import mods.railcraft.common.blocks.machine.delta.TileCage;
import mods.railcraft.common.blocks.machine.delta.TileWire;
import mods.railcraft.common.blocks.machine.epsilon.*;
import mods.railcraft.common.blocks.machine.gamma.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineTileRegistery {

    public static void registerTileEntities() {
        // Alpha
        GameRegistry.registerTileEntity(TileAnchorWorld.class, "RCWorldAnchorTile");
        GameRegistry.registerTileEntity(TileAnchorPersonal.class, "RCPersonalAnchorTile");
        GameRegistry.registerTileEntity(TileAnchorAdmin.class, "RCAdminAnchorTile");
        GameRegistry.registerTileEntity(TileFeedStation.class, "RCFeedStationTile");
        GameRegistry.registerTileEntity(TileTradeStation.class, "RCTradeStationTile");
        GameRegistry.registerTileEntity(TileSteamTurbine.class, "RCSteamTurbineTile");
        GameRegistry.registerTileEntity(TileBlastFurnace.class, "RCBlastFurnaceTile");
        GameRegistry.registerTileEntity(TileCokeOven.class, "RCCokeOvenTile");
        GameRegistry.registerTileEntity(TileRockCrusher.class, "RCRockCrusherTile");
        GameRegistry.registerTileEntity(TileRollingMachine.class, "RCRollingMachineTile");
        GameRegistry.registerTileEntity(TileTankWater.class, "RCWaterTankTile");
        GameRegistry.registerTileEntity(TileSteamOven.class, "RCSteamOvenTile");
        GameRegistry.registerTileEntity(TileSmoker.class, "RCSmokerTile");
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

        // Gamma
        GameRegistry.registerTileEntity(TileDispenserCart.class, "RCMinecartDispenserTile");
        GameRegistry.registerTileEntity(TileEnergyLoader.class, "RCLoaderTileEnergy");
        GameRegistry.registerTileEntity(TileEnergyUnloader.class, "RCUnloaderTileEnergy");
        GameRegistry.registerTileEntity(TileDispenserTrain.class, "RCTrainDispenserTile");
        GameRegistry.registerTileEntity(TileItemLoader.class, "RCLoaderTile");
        GameRegistry.registerTileEntity(TileItemLoaderAdvanced.class, "RCLoaderAdvancedTile");
        GameRegistry.registerTileEntity(TileItemUnloader.class, "RCUnloaderTile");
        GameRegistry.registerTileEntity(TileItemUnloaderAdvanced.class, "RCUnloaderAdvancedTile");
        GameRegistry.registerTileEntity(TileFluidLoader.class, "RCLoaderTileLiquid");
        GameRegistry.registerTileEntity(TileFluidUnloader.class, "RCUnloaderTileLiquid");
        GameRegistry.registerTileEntity(TileRFLoader.class, "RCLoaderTileRF");
        GameRegistry.registerTileEntity(TileRFUnloader.class, "RCUnloaderTileRF");

        // Delta
        GameRegistry.registerTileEntity(TileCage.class, "RCCageTile");
        GameRegistry.registerTileEntity(TileWire.class, "RCWireTile");

        // Epsilon
        GameRegistry.registerTileEntity(TileElectricFeeder.class, "RCElectricFeederTile");
        GameRegistry.registerTileEntity(TileElectricFeederAdmin.class, "RCElectricFeederAdminTile");
        GameRegistry.registerTileEntity(TileAdminSteamProducer.class, "RCAdminSteamProducerTile");
        GameRegistry.registerTileEntity(TileFluxTransformer.class, "RCFluxTransformerTile");
        GameRegistry.registerTileEntity(TileForceTrackEmitter.class, "RCForceTrackEmitterTile");
        GameRegistry.registerTileEntity(TileEngravingBench.class, "RCEngravingBenchTile");

    }

}
