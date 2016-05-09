/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import org.apache.logging.log4j.Level;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.detector.TileDetector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.blocks.machine.ITankTile;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.alpha.*;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxFluid;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxSolid;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteam;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.blocks.machine.gamma.*;
import mods.railcraft.common.blocks.signals.IAspectActionManager;
import mods.railcraft.common.blocks.signals.IRouter;
import mods.railcraft.common.blocks.signals.TileBoxAnalogController;
import mods.railcraft.common.blocks.signals.TileBoxCapacitor;
import mods.railcraft.common.blocks.signals.TileBoxController;
import mods.railcraft.common.blocks.signals.TileSwitchMotor;
import mods.railcraft.common.blocks.tracks.*;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FactoryGui {

    public static GuiScreen build(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        if (gui != EnumGui.ANVIL && obj == null)
            return null;

        if (obj instanceof TileMultiBlock && !((TileMultiBlock) obj).isStructureValid())
            return null;

        try {
            switch (gui) {
                case LOADER_ITEM:
                    return new GuiLoaderItem(inv, (TileLoaderItemBase) obj);
                case LOADER_FLUID:
                    return new GuiLoaderFluid(inv, (TileFluidLoader) obj);
                case UNLOADER_FLUID:
                    return new GuiUnloaderFluid(inv, (TileFluidUnloader) obj);
                case LOADER_ENERGY:
                    return new GuiLoaderEnergy(inv, (TileEnergyLoader) obj);
                case UNLOADER_ENERGY:
                    return new GuiUnloaderEnergy(inv, (TileEnergyUnloader) obj);
                case LOADER_RF:
                    return new GuiLoaderRF((TileRFLoader) obj);
                case UNLOADER_RF:
                    return new GuiUnloaderRF((TileRFUnloader) obj);
                case DETECTOR_ITEM:
                    return new GuiDetectorItem(inv, (TileDetector) obj);
                case DETECTOR_TANK:
                    return new GuiDetectorTank(inv, (TileDetector) obj);
                case DETECTOR_SHEEP:
                    return new GuiDetectorSheep(inv, (TileDetector) obj);
                case DETECTOR_ANIMAL:
                    return new GuiDetectorAnimal((TileDetector) obj);
                case DETECTOR_ADVANCED:
                    return new GuiDetectorAdvanced(inv, (TileDetector) obj);
                case DETECTOR_TRAIN:
                    return new GuiDetectorTrain((TileDetector) obj);
                case DETECTOR_VILLAGER:
                    return new GuiDetectorVillager((TileDetector) obj);
                case DETECTOR_LOCOMOTIVE:
                    return new GuiDetectorLocomotive(inv, (TileDetector) obj);
                case DETECTOR_ROUTING:
                    return new GuiRouting(inv, (RailcraftTileEntity) obj, (IRouter) ((TileDetector) obj).getDetector());
                case CART_DISPENSER:
                    return new GuiDispenserCart(inv, (TileDispenserCart) obj);
                case TRAIN_DISPENSER:
                    return new GuiDispenserTrain(inv, (TileDispenserTrain) obj);
                case COKE_OVEN:
                    return new GuiCokeOven(inv, (TileCokeOven) obj);
                case BLAST_FURNACE:
                    return new GuiBlastFurnace(inv, (TileBlastFurnace) obj);
                case STEAN_OVEN:
                    return new GuiSteamOven(inv, (TileSteamOven) obj);
                case TANK:
                    return new GuiTank(inv, (ITankTile) obj);
                case ROCK_CRUSHER:
                    return new GuiRockCrusher(inv, (TileRockCrusher) obj);
                case ROLLING_MACHINE:
                    return new GuiRollingMachine(inv, (TileRollingMachine) obj);
                case FEED_STATION:
                    return new GuiFeedStation(inv, (TileFeedStation) obj);
                case TRADE_STATION:
                    return new GuiTradeStation(inv, (TileTradeStation) obj);
                case WORLD_ANCHOR:
                    return new GuiWorldAnchor(inv, (TileAnchorWorld) obj);
                case ENGINE_STEAM:
                    return new GuiEngineSteam(inv, (TileEngineSteam) obj);
                case ENGINE_HOBBY:
                    return new GuiEngineSteamHobby(inv, (TileEngineSteamHobby) obj);
                case BOILER_SOLID:
                    return new GuiBoilerSolid(inv, (TileBoilerFireboxSolid) obj);
                case BOILER_LIQUID:
                    return new GuiBoilerFluid(inv, (TileBoilerFireboxFluid) obj);
                case TURBINE:
                    return new GuiTurbine(inv, (TileSteamTurbine) obj);
                case ANVIL:
                    return new GuiAnvil(inv, world, x, y, z);
                case ROUTING:
                    return new GuiRouting(inv, (RailcraftTileEntity) obj, (IRouter) obj);
                case TRACK_ROUTING:
                    return new GuiTrackRouting(inv, (TrackRouting) ((TileTrack) obj).getTrackInstance());
                case SWITCH_MOTOR:
                    return new GuiSwitchMotor(inv.player, (TileSwitchMotor) obj, LocalizationPlugin.translate("railcraft.gui.switch.motor.action"));
                case BOX_RECEIVER:
                    return new GuiAspectAction(inv.player, (IAspectActionManager) obj, LocalizationPlugin.translate("railcraft.gui.box.aspect.action"));
                case BOX_RELAY:
                    return new GuiAspectAction(inv.player, (IAspectActionManager) obj, LocalizationPlugin.translate("railcraft.gui.box.aspect.action"));
                case BOX_CONTROLLER:
                    return new GuiBoxController((TileBoxController) obj);
                case BOX_ANALOG_CONTROLLER:
                    return new GuiBoxAnalogController((TileBoxAnalogController) obj);
                case BOX_CAPACITOR:
                    return new GuiBoxCapacitor((TileBoxCapacitor) obj);
                case TRACK_LAUNCHER:
                    return new GuiTrackLauncher((TrackLauncher) ((TileTrack) obj).getTrackInstance());
                case TRACK_PRIMING:
                    return new GuiTrackPriming((TrackPriming) ((TileTrack) obj).getTrackInstance());
                case TRACK_EMBARKING:
                    return new GuiTrackEmbarking((TrackEmbarking) ((TileTrack) obj).getTrackInstance());
                case CART_BORE:
                    return new GuiCartBore(inv, (EntityTunnelBore) obj);
                case CART_ENERGY:
                    return new GuiCartEnergy(inv, (IIC2EnergyCart) obj);
                case CART_RF:
                    return new GuiCartRF((EntityCartRF) obj);
                case CART_TANK:
                    return new GuiCartTank(inv, (EntityCartTank) obj);
                case CART_CARGO:
                    return new GuiCartCargo(inv, (EntityCartCargo) obj);
                case CART_ANCHOR:
                    return new GuiCartAnchor(inv, (EntityCartAnchor) obj);
                case CART_TNT_FUSE:
                    return new GuiCartTNTFuse((CartExplosiveBase) obj);
                case CART_WORK:
                    return new GuiCartWork(inv, (EntityCartWork) obj);
                case CART_TRACK_LAYER:
                    return new GuiCartTrackLayer(inv, (EntityCartTrackLayer) obj);
                case CART_TRACK_RELAYER:
                    return new GuiCartTrackRelayer(inv, (EntityCartTrackRelayer) obj);
                case CART_UNDERCUTTER:
                    return new GuiCartUndercutter(inv, (EntityCartUndercutter) obj);
                case LOCO_STEAM:
                    return new GuiLocomotiveSteamSolid(inv, (EntityLocomotiveSteamSolid) obj);
                case LOCO_ELECTRIC:
                    return new GuiLocomotiveElectric(inv, (EntityLocomotiveElectric) obj);
                case LOCO_CREATIVE:
                    return new GuiLocomotiveCreative(inv, (EntityLocomotiveCreative) obj);
                default:
                    return ModuleManager.getGuiScreen(gui, inv, obj, world, x, y, z);
            }
        } catch (ClassCastException ex) {
            Game.log(Level.WARN, "Error when attempting to build gui {0}: {1}", gui, ex);
        }
        return null;
    }

}
