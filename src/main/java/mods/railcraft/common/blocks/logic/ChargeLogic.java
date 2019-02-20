/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBatteryBlock;

import java.util.Optional;

/**
 * Created by CovertJaguar on 2/20/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeLogic extends Logic {
    private final Charge network;

    public ChargeLogic(Adapter adapter, Charge network) {
        super(adapter);
        this.network = network;
    }

    public IBatteryBlock getBattery() {
        Optional<? extends IBatteryBlock> battery = access().getBattery();
        assert battery.isPresent();
        return battery.get();
    }

    public Charge.IAccess access() {
        return network.network(theWorldAsserted()).access(getPos());
    }
}
