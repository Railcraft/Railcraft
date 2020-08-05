/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.modules.ModuleAutomation;
import mods.railcraft.common.modules.ModuleBuilding;
import mods.railcraft.common.modules.ModuleFactory;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EquipmentVariant implements IEnumMachine<EquipmentVariant> {

    ROLLING_MACHINE_MANUAL(ModuleFactory.class, "rolling_manual", TileRollingMachineManual.class),
    ROLLING_MACHINE_POWERED(ModuleFactory.class, "rolling_powered", TileRollingMachinePowered.class),
    FEED_STATION(ModuleAutomation.class, "feed_station", TileFeedStation.class),
    SMOKER(ModuleBuilding.class, "smoker", TileSmoker.class),
    ;

    private static final List<EquipmentVariant> creativeList = new ArrayList<>();
    public static final EquipmentVariant[] VALUES = values();

    static {
        creativeList.add(ROLLING_MACHINE_MANUAL);
        creativeList.add(ROLLING_MACHINE_POWERED);
        creativeList.add(FEED_STATION);
        creativeList.add(SMOKER);
    }

    private final Definition def;
    private final int w;
    private final int h;

    EquipmentVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(tag, tile, module);
        this.w = 1;
        this.h = 1;
    }

    EquipmentVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, int w, int h) {
        this.def = new Definition(tag, tile, module);
        this.w = w;
        this.h = h;
    }

    public static EquipmentVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<EquipmentVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.equipment_" + getBaseTag();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(w, h);
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.EQUIPMENT;
    }
}
