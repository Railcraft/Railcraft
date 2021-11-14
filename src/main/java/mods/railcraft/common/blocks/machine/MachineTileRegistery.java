/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorAdmin;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorPassive;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorPersonal;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.blocks.machine.alpha.TileBlastFurnace;
import mods.railcraft.common.blocks.machine.alpha.TileCokeOven;
import mods.railcraft.common.blocks.machine.alpha.TileFeedStation;
import mods.railcraft.common.blocks.machine.alpha.TileRockCrusher;
import mods.railcraft.common.blocks.machine.alpha.TileRollingMachine;
import mods.railcraft.common.blocks.machine.alpha.TileSmoker;
import mods.railcraft.common.blocks.machine.alpha.TileSteamOven;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTrapAuto;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTrapManual;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import mods.railcraft.common.blocks.machine.alpha.TileTankWater;
import mods.railcraft.common.blocks.machine.alpha.TileTradeStation;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxFluid;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxSolid;
import mods.railcraft.common.blocks.machine.beta.TileBoilerTankHigh;
import mods.railcraft.common.blocks.machine.beta.TileBoilerTankLow;
import mods.railcraft.common.blocks.machine.beta.TileChestMetals;
import mods.railcraft.common.blocks.machine.beta.TileChestVoid;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHigh;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamLow;
import mods.railcraft.common.blocks.machine.beta.TileSawmill;
import mods.railcraft.common.blocks.machine.beta.TileSentinel;
import mods.railcraft.common.blocks.machine.beta.TileTankIronGauge;
import mods.railcraft.common.blocks.machine.beta.TileTankIronValve;
import mods.railcraft.common.blocks.machine.beta.TileTankIronWall;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelGauge;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelValve;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelWall;
import mods.railcraft.common.blocks.machine.delta.TileCage;
import mods.railcraft.common.blocks.machine.delta.TileWire;
import mods.railcraft.common.blocks.machine.epsilon.TileAdminSteamProducer;
import mods.railcraft.common.blocks.machine.epsilon.TileElectricFeeder;
import mods.railcraft.common.blocks.machine.epsilon.TileElectricFeederAdmin;
import mods.railcraft.common.blocks.machine.epsilon.TileEngravingBench;
import mods.railcraft.common.blocks.machine.epsilon.TileFluxTransformer;
import mods.railcraft.common.blocks.machine.epsilon.TileForceTrackEmitter;
import mods.railcraft.common.blocks.machine.gamma.TileDispenserCart;
import mods.railcraft.common.blocks.machine.gamma.TileDispenserTrain;
import mods.railcraft.common.blocks.machine.gamma.TileEnergyLoader;
import mods.railcraft.common.blocks.machine.gamma.TileEnergyUnloader;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileFluidUnloader;
import mods.railcraft.common.blocks.machine.gamma.TileItemLoader;
import mods.railcraft.common.blocks.machine.gamma.TileItemLoaderAdvanced;
import mods.railcraft.common.blocks.machine.gamma.TileItemUnloader;
import mods.railcraft.common.blocks.machine.gamma.TileItemUnloaderAdvanced;
import mods.railcraft.common.blocks.machine.gamma.TileRFLoader;
import mods.railcraft.common.blocks.machine.gamma.TileRFUnloader;
import mods.railcraft.common.blocks.machine.tank.TileGenericMultiTankGauge;
import mods.railcraft.common.blocks.machine.tank.TileGenericMultiTankValve;
import mods.railcraft.common.blocks.machine.tank.TileGenericMultiTankWall;

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
        
        // Advanced Tanks
        GameRegistry.registerTileEntity(TileGenericMultiTankWall.class, "RCAdvTankWallTile");
        GameRegistry.registerTileEntity(TileGenericMultiTankGauge.class, "RCAdvTankGaugeTile");
        GameRegistry.registerTileEntity(TileGenericMultiTankValve.class, "RCAdvTankValveTile");

    }

}
