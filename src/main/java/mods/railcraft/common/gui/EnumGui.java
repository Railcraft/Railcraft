/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui;

/**
 * @author CovertJaguar
 */
public enum EnumGui {

    ANVIL(true),
    BLAST_FURNACE(true),
    BOILER_LIQUID(true),
    BOILER_SOLID(true),
    BOX_ANALOG_CONTROLLER(false),
    BOX_CAPACITOR(false),
    BOX_CONTROLLER(false),
    BOX_RECEIVER(true),
    BOX_RELAY(true),
    CART_BORE(true),
    CART_CARGO(true),
    CART_DISPENSER(true),
    CART_ENERGY(true),
    CART_FE(true),
    CART_TANK(true),
    CART_TNT_BLAST(false),
    CART_TNT_FUSE(false),
    CART_TRACK_LAYER(true),
    CART_TRACK_RELAYER(true),
    CART_UNDERCUTTER(true),
    CART_WORK(true),
    CART_WORLDSPIKE(true),
    CHEST(true),
    COKE_OVEN(true),
    DETECTOR_ADVANCED(true),
    DETECTOR_ANIMAL(false),
    DETECTOR_CART(true),
    DETECTOR_ITEM(true),
    DETECTOR_LOCOMOTIVE(true),
    DETECTOR_ROUTING(true),
    DETECTOR_SHEEP(true),
    DETECTOR_TANK(true),
    DETECTOR_TRAIN(false),
    DETECTOR_VILLAGER(false),
    ENGINE_HOBBY(true),
    ENGINE_STEAM(true),
    ENGRAVING_BENCH(true),
    ENGRAVING_BENCH_UNLOCK(true),
    FEED_STATION(true),
    LOADER_ENERGY(true),
    LOCO_CREATIVE(true),
    LOCO_ELECTRIC(true),
    LOCO_STEAM(true),
    MANIPULATOR_FLUID(true),
    MANIPULATOR_ITEM(true),
    MANIPULATOR_RF(true),
    ROCK_CRUSHER(true),
    ROLLING_MACHINE_MANUAL(true),
    ROLLING_MACHINE_POWERED(true),
    ROUTING(true),
    STEAM_OVEN(true),
    SWITCH_MOTOR(true),
    TANK(true),
    TANK_WATER(true),
    TRACK_DELAYED(false),
    TRACK_EMBARKING(false),
    TRACK_LAUNCHER(false),
    TRACK_PRIMING(false),
    TRACK_ROUTING(true),
    TRADE_STATION(true),
    TRAIN_DISPENSER(true),
    TURBINE(true),
    UNLOADER_ENERGY(true),
    WORLDSPIKE(true),
    ;
    private static final EnumGui[] VALUES = values();
    private final boolean hasContainer;

    EnumGui(boolean hasContainer) {
        this.hasContainer = hasContainer;
    }

    public boolean hasContainer() {
        return hasContainer;
    }

    public static EnumGui fromOrdinal(int i) {
//        if (i < 0 || i >= VALUES.length)
//            return null;
        return VALUES[i];
    }

}
