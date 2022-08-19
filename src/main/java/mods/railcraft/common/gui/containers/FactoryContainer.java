/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.interfaces.ITileAspectResponder;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.blocks.machine.equipment.TileFeedStation;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachine;
import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.blocks.machine.manipulator.*;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspike;
import mods.railcraft.common.blocks.single.TileEngineSteam;
import mods.railcraft.common.blocks.single.TileEngineSteamHobby;
import mods.railcraft.common.blocks.structures.TileBoilerFireboxFluid;
import mods.railcraft.common.blocks.structures.TileBoilerFireboxSolid;
import mods.railcraft.common.blocks.structures.TileSteamTurbine;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GUIParams;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.routing.IRouter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FactoryContainer {

    public static <C extends Container> C build(GUIParams params) {
        return build(params.getGui(), params.getInv(), params.getObj(), params.getWorld(), params.getPos().getX(), params.getPos().getY(), params.getPos().getZ());
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public static <C extends Container> C build(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        if (gui != EnumGui.ANVIL && obj == null)
            return null;

        try {
            switch (gui) {
                case CHEST:
                    return (C) new ContainerRCChest(inv, (IInventoryImplementor) obj);
                case MANIPULATOR_ITEM:
                    return (C) new ContainerManipulatorCartItem(inv, (TileItemManipulator) obj);
                case MANIPULATOR_FLUID:
                    return (C) new ContainerManipulatorCartFluid(inv, (TileFluidManipulator) obj);
                case LOADER_ENERGY:
                    return (C) new ContainerManipulatorCartIC2(inv, (TileIC2Loader) obj);
                case UNLOADER_ENERGY:
                    return (C) new ContainerManipulatorCartIC2(inv, (TileIC2Unloader) obj);
                case MANIPULATOR_RF:
                    return (C) new ContainerManipulatorCartRF((TileRFManipulator) obj);
                case DETECTOR_ITEM:
                    return (C) new ContainerDetectorItem(inv, (TileDetector) obj);
                case DETECTOR_TANK:
                    return (C) new ContainerDetectorTank(inv, (TileDetector) obj);
                case DETECTOR_SHEEP:
                    return (C) new ContainerDetectorSheep(inv, (TileDetector) obj);
                case DETECTOR_ADVANCED:
                    return (C) new ContainerDetectorAdvanced(inv, (TileDetector) obj);
                case DETECTOR_LOCOMOTIVE:
                    return (C) new ContainerDetectorLocomotive(inv, (TileDetector) obj);
                case DETECTOR_ROUTING:
                    return (C) new ContainerRouting(inv, (IRouter) ((TileDetector) obj).getDetector());
                case CART_DISPENSER:
                    return (C) new ContainerDispenserCart(inv, (TileDispenserCart) obj);
                case TRAIN_DISPENSER:
                    return (C) new ContainerDispenserTrain(inv, (TileDispenserTrain) obj);
                case COKE_OVEN:
                    return (C) new ContainerCokeOven(inv, Logic.get(CokeOvenLogic.class, obj));
                case BLAST_FURNACE:
                    return (C) new ContainerBlastFurnace(inv, Logic.get(BlastFurnaceLogic.class, obj));
                case STEAM_OVEN:
                    return (C) new ContainerSteamOven(inv, Logic.get(SteamOvenLogic.class, obj));
                case ROCK_CRUSHER:
                    return (C) new ContainerRockCrusher(inv, Logic.get(RockCrusherLogic.class, obj));
                case TANK:
                    return (C) new ContainerTank(inv, (ILogicContainer) obj);
                case ROLLING_MACHINE_MANUAL:
                    return (C) new ContainerRollingMachine(inv, (TileRollingMachine) obj);
                case ROLLING_MACHINE_POWERED:
                    return (C) new ContainerRollingMachinePowered(inv, (TileRollingMachinePowered) obj);
                case FEED_STATION:
                    return (C) new ContainerFeedStation(inv, (TileFeedStation) obj);
                case TRADE_STATION:
                    return (C) new ContainerTradeStation(inv, Logic.get(TradeStationLogic.class, obj));
                case WORLDSPIKE:
                    return (C) new ContainerWorldspike(inv, (TileWorldspike) obj);
                case ENGINE_STEAM:
                    return (C) new ContainerEngineSteam(inv, (TileEngineSteam) obj);
                case ENGINE_HOBBY:
                    return (C) new ContainerEngineSteamHobby(inv, (TileEngineSteamHobby) obj);
                case BOILER_SOLID:
                    return (C) new ContainerBoilerSolid(inv, (TileBoilerFireboxSolid) obj);
                case BOILER_FLUID:
                    return (C) new ContainerBoilerFluid(inv, (TileBoilerFireboxFluid) obj);
                case TURBINE:
                    return (C) new ContainerTurbine(inv, (TileSteamTurbine) obj);
                case ANVIL:
                    return (C) new ContainerAnvil(inv, world, new BlockPos(x, y, z), inv.player);
                case CART_BORE:
                    return (C) new ContainerBore(inv, (EntityTunnelBore) obj);
                case CART_ENERGY:
                    return (C) new ContainerCartEnergy(inv, (CartBaseEnergy) obj);
                case CART_FE:
                    return (C) new ContainerCartRF((EntityCartRF) obj);
                case CART_TANK:
                    return (C) new ContainerCartTank(inv, (EntityCartTank) obj);
                case CART_CARGO:
                    return (C) new ContainerCartCargo(inv, (EntityCartCargo) obj);
                case CART_WORLDSPIKE:
                    return (C) new ContainerWorldspike(inv, (EntityCartWorldspike) obj);
                case CART_WORK:
                    return (C) new ContainerCartWork(inv, (EntityCartWork) obj);
                case CART_TRACK_LAYER:
                    return (C) new ContainerCartTrackLayer(inv, (EntityCartTrackLayer) obj);
                case CART_TRACK_RELAYER:
                    return (C) new ContainerCartTrackRelayer(inv, (EntityCartTrackRelayer) obj);
                case CART_UNDERCUTTER:
                    return (C) new ContainerCartUndercutter(inv, (EntityCartUndercutter) obj);
                case LOCO_STEAM:
                    return (C) ContainerLocomotiveSteamSolid.make(inv, (EntityLocomotiveSteamSolid) obj);
                case LOCO_DIESEL:
                    return (C) ContainerLocomotiveDiesel.make(inv, (EntityLocomotiveDiesel) obj);
                case LOCO_ELECTRIC:
                    return (C) ContainerLocomotiveElectric.make(inv, (EntityLocomotiveElectric) obj);
                case LOCO_CREATIVE:
                    return (C) ContainerLocomotive.make(inv, (EntityLocomotiveCreative) obj);
                case SWITCH_MOTOR:
                    return (C) new ContainerAspectAction(inv.player, (ITileAspectResponder) obj);
                case BOX_RECEIVER:
                    return (C) new ContainerAspectAction(inv.player, (ITileAspectResponder) obj);
                case BOX_RELAY:
                    return (C) new ContainerAspectAction(inv.player, (ITileAspectResponder) obj);
                case ROUTING:
                    return (C) new ContainerRouting(inv, (IRouter) obj);
                case TRACK_ACTIVATOR:
                    return (C) new ContainerTrackActivator(inv, (TileTrackOutfitted) obj);
                case TRACK_ROUTING:
                    return (C) new ContainerTrackRouting(inv, (TileTrackOutfitted) obj);
                case TRACK_DUMPING:
                    return (C) new ContainerTrackDumping(inv, (TileTrackOutfitted) obj);
                default:
                    return (C) RailcraftModuleManager.getGuiContainer(gui, inv, obj, world, x, y, z);
            }
        } catch (Exception ex) {
            Game.log().msg(Level.WARN, "Error when attempting to build gui container {0}: {1}", gui, ex);
        }
        return null;
    }

    private FactoryContainer() {
    }

}
