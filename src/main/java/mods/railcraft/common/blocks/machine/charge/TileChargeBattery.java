/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.IEnumMachine;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChargeBattery extends TileCharge {

    private final BatteryVariant variant;
    private IChargeBlock.ChargeBattery chargeBattery;

    public TileChargeBattery(BatteryVariant variant) {
        this.variant = variant;
        this.chargeBattery = new IChargeBlock.ChargeBattery(variant.capacity, variant.maxDraw, variant.efficiency);
    }

    @Override
    public IEnumMachine<?> getMachineType() {
        return variant;
    }

    @Override
    public IChargeBlock.ChargeBattery getChargeBattery() {
        return chargeBattery;
    }
}
