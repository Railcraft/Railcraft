/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.modules.ModuleCharge;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum BatteryVariant implements IEnumMachine<BatteryVariant> {

    NICKEL_IRON(ModuleCharge.class, "nickel_iron", 100_000, 50.0, 0.3, 0.8),;

    private static final List<BatteryVariant> creativeList = new ArrayList<BatteryVariant>();
    public static final BatteryVariant[] VALUES = values();
    public final double capacity, maxDraw, loss, efficiency;
    public final IChargeBlock.ChargeDef chargeDef;

    static {
        creativeList.add(NICKEL_IRON);
    }

    private final Definition def;

    BatteryVariant(Class<? extends IRailcraftModule> module, String tag, final double capacity, final double maxDraw, final double loss, final double efficiency) {
        this.def = new Definition(tag, TileChargeBattery.class, module);
        this.capacity = capacity;
        this.maxDraw = maxDraw;
        this.loss = loss;
        this.efficiency = efficiency;

        this.chargeDef = new IChargeBlock.ChargeDef(IChargeBlock.ConnectType.BLOCK, loss, (world, pos) -> {
            TileEntity tileEntity = WorldPlugin.getBlockTile(world, pos);
            if (tileEntity instanceof TileCharge) {
                return ((TileCharge) tileEntity).getChargeBattery();
            }
            //noinspection ConstantConditions
            return null;
        });
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
