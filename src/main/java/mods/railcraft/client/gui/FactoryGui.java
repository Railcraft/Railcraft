/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.interfaces.ITileAspectResponder;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.blocks.machine.ITankTile;
import mods.railcraft.common.blocks.machine.equipment.TileFeedStation;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachine;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.blocks.machine.manipulator.*;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.TileActuatorMotor;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxAnalog;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxCapacitor;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxController;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspike;
import mods.railcraft.common.blocks.single.TileEngineSteam;
import mods.railcraft.common.blocks.single.TileEngineSteamHobby;
import mods.railcraft.common.blocks.structures.*;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.kits.*;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.routing.IRouter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FactoryGui {

    @SuppressWarnings("ConstantConditions")
    public static @Nullable GuiScreen build(EnumGui gui, InventoryPlayer inv, @Nullable Object obj, World world, int x, int y, int z) {
        if (gui != EnumGui.ANVIL && obj == null)
            return null;

        if (obj instanceof TileMultiBlock && !((TileMultiBlock) obj).isStructureValid())
            return null;

        try {
            switch (gui) {
                case CHEST:
                    return new GuiRCChest(inv, ((IInventory) obj));
                case MANIPULATOR_ITEM:
                    return new GuiManipulatorCartItem(inv, (TileItemManipulator) obj);
                case MANIPULATOR_FLUID:
                    return new GuiManipulatorCartFluid(inv, (TileFluidManipulator) obj);
                case LOADER_ENERGY:
                    return new GuiManipulatorCartIC2Loader(inv, (TileIC2Loader) obj);
                case UNLOADER_ENERGY:
                    return new GuiManipulatorCartIC2Unloader(inv, (TileIC2Unloader) obj);
                case MANIPULATOR_RF:
                    return new GuiManipulatorCartRF((TileRFManipulator) obj);
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
                    return new GuiRouting(inv, (TileRailcraft) obj, (IRouter) ((TileDetector) obj).getDetector());
                case CART_DISPENSER:
                    return new GuiDispenserCart(inv, (TileDispenserCart) obj);
                case TRAIN_DISPENSER:
                    return new GuiDispenserTrain(inv, (TileDispenserTrain) obj);
                case COKE_OVEN:
                    return new GuiCokeOven(inv, Logic.get(CokeOvenLogic.class, obj));
                case BLAST_FURNACE:
                    return new GuiBlastFurnace(inv, Logic.get(BlastFurnaceLogic.class, obj));
                case STEAM_OVEN:
                    return new GuiSteamOven(inv, Logic.get(SteamOvenLogic.class, obj));
                case TANK:
                    return new GuiTank(inv, (ITankTile) obj);
                case TANK_WATER:
                    return new GuiTankWater(inv, (TileTankWater) obj);
                case ROCK_CRUSHER:
                    return new GuiRockCrusher(inv, Logic.get(RockCrusherLogic.class, obj));
                case ROLLING_MACHINE_MANUAL:
                    return new GuiRollingMachine(inv, (TileRollingMachine) obj);
                case ROLLING_MACHINE_POWERED:
                    return new GuiRollingMachinePowered(inv, (TileRollingMachinePowered) obj);
                case FEED_STATION:
                    return new GuiFeedStation(inv, (TileFeedStation) obj);
                case TRADE_STATION:
                    return new GuiTradeStation(inv, Logic.get(TradeStationLogic.class, obj), (IWorldNameable) obj);
                case WORLDSPIKE:
                    return new GuiWorldspike(inv, (TileWorldspike) obj);
                case ENGINE_STEAM:
                    return new GuiEngineSteam(inv, (TileEngineSteam) obj);
                case ENGINE_HOBBY:
                    return new GuiEngineSteamHobby(inv, (TileEngineSteamHobby) obj);
                case BOILER_SOLID:
                    return new GuiBoilerSolid(inv, (TileBoilerFireboxSolid) obj);
                case BOILER_LIQUID:
                    return new GuiBoilerFluid(inv, (TileBoilerFireboxFluid) obj);
                case TURBINE:
                    return new GuiSteamTurbine(inv, (TileSteamTurbine) obj);
                case ANVIL:
                    return new GuiAnvil(inv, world, new BlockPos(x, y, z));
                case ROUTING:
                    return new GuiRouting(inv, (TileRailcraft) obj, (IRouter) obj);
                case TRACK_ROUTING:
                    return new GuiTrackRouting(inv, (TrackKitRouting) ((TileTrackOutfitted) obj).getTrackKitInstance());
                case SWITCH_MOTOR:
                    return new GuiActuatorMotor(inv.player, (TileActuatorMotor) obj, LocalizationPlugin.translate("gui.railcraft.actuator.motor.action"));
                case BOX_RECEIVER:
                    return new GuiAspectAction(inv.player, (ITileAspectResponder) obj, LocalizationPlugin.translate("gui.railcraft.box.aspect.action"));
                case BOX_RELAY:
                    return new GuiAspectAction(inv.player, (ITileAspectResponder) obj, LocalizationPlugin.translate("gui.railcraft.box.aspect.action"));
                case BOX_CONTROLLER:
                    return new GuiBoxController((TileBoxController) obj);
                case BOX_ANALOG_CONTROLLER:
                    return new GuiBoxAnalogController((TileBoxAnalog) obj);
                case BOX_CAPACITOR:
                    return new GuiBoxCapacitor((TileBoxCapacitor) obj);
                case TRACK_LAUNCHER:
                    return new GuiTrackLauncher((TrackKitLauncher) ((TileTrackOutfitted) obj).getTrackKitInstance());
                case TRACK_PRIMING:
                    return new GuiTrackPriming((TrackKitPriming) ((TileTrackOutfitted) obj).getTrackKitInstance());
                case TRACK_EMBARKING:
                    return new GuiTrackEmbarking((TrackKitEmbarking) ((TileTrackOutfitted) obj).getTrackKitInstance());
                case TRACK_DELAYED:
                    return new GuiTrackDelayedLocking((TrackKitDelayedLocking) ((TileTrackOutfitted) obj).getTrackKitInstance());
                case CART_BORE:
                    return new GuiCartBore(inv, (EntityTunnelBore) obj);
                case CART_ENERGY:
                    return new GuiCartEnergy(inv, (CartBaseEnergy) obj);
                case CART_FE:
                    return new GuiCartForgeEnergy((EntityCartRF) obj);
                case CART_TANK:
                    return new GuiCartTank(inv, (EntityCartTank) obj);
                case CART_CARGO:
                    return new GuiCartCargo(inv, (EntityCartCargo) obj);
                case CART_WORLDSPIKE:
                    return new GuiCartWorldspike(inv, (EntityCartWorldspike) obj);
                case CART_TNT_FUSE:
                    return new GuiCartTNTFuse((CartBaseExplosive) obj);
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
                    return RailcraftModuleManager.getGuiScreen(gui, inv, obj, world, x, y, z);
            }
        } catch (Exception ex) {
            Game.log().msg(Level.WARN, "Error when attempting to build gui {0}: {1}", gui, ex);
        }
        return null;
    }

}
