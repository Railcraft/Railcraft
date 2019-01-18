/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.modules.ModuleCharge;
import mods.railcraft.common.modules.ModuleIC2;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum FeederVariant implements IEnumMachine<FeederVariant> {

    IC2(ModuleIC2.class, "ic2", TileChargeFeederIC2.class, new IBatteryBlock.Spec(IBatteryBlock.State.SOURCE, 1024.0, 512.0, 1.0)),
    ADMIN(ModuleCharge.class, "admin", TileChargeFeederAdmin.class, new IBatteryBlock.Spec(IBatteryBlock.State.INFINITE, 4000.0, 4000.0, 1.0)),
    ;

    private static final List<FeederVariant> creativeList = new ArrayList<>();
    public static final FeederVariant[] VALUES = values();
    private final IChargeBlock.ChargeSpec chargeSpec;

    static {
        creativeList.add(IC2);
        creativeList.add(ADMIN);
    }

    private final Definition def;

    FeederVariant(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, IBatteryBlock.Spec batterySpec) {
        this.def = new Definition(tag, tile, module);
        this.chargeSpec = new IChargeBlock.ChargeSpec(IChargeBlock.ConnectType.BLOCK, 0.5, batterySpec);
    }

    public IChargeBlock.ChargeSpec getChargeSpec() {
        return chargeSpec;
    }

    public static FeederVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<FeederVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.charge_feeder_" + getBaseTag();
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.CHARGE_FEEDER;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }
}
