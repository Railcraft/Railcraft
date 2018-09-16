/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.ChargeNodeDefinition;
import mods.railcraft.api.charge.ConnectType;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.modules.ModuleCharge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum BatteryVariant implements IVariantEnumBlock<BatteryVariant> {

    NICKEL_IRON(ModuleCharge.class, "nickel_iron", 100_000, 32.0, 0.3, 0.8),
    NICKEL_ZINC(ModuleCharge.class, "nickel_zinc", 150_000, 16.0, 0.2, 0.7),;

    private static final List<BatteryVariant> creativeList = new ArrayList<>();
    public static final BatteryVariant[] VALUES = values();
    public final double capacity, maxDraw, loss, efficiency;
    public final ChargeNodeDefinition chargeDef;

    static {
        creativeList.add(NICKEL_IRON);
    }

    private final IVariantEnumBlock.Definition def;

    BatteryVariant(Class<? extends IRailcraftModule> module, String tag, final double capacity, final double maxDraw, final double loss, final double efficiency) {
        this.def = new Definition(tag, module);
        this.capacity = capacity;
        this.maxDraw = maxDraw;
        this.loss = loss;
        this.efficiency = efficiency;

        this.chargeDef = new ChargeNodeDefinition(ConnectType.BLOCK, loss,
                (world, pos) -> new ChargeBattery(capacity, maxDraw, efficiency));
    }

    public static BatteryVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<BatteryVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.charge_battery_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.CHARGE_BATTERY;
    }
}
