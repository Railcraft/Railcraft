/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.blocks.charge.Charge;

import java.util.List;

/**
 * Created by CovertJaguar on 10/31/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileMultiBlockCharge extends TileMultiBlock {
    protected TileMultiBlockCharge(List<? extends MultiBlockPattern> patterns) {
        super(patterns);
    }

    protected IBatteryBlock getBattery() {
        return Charge.distribution.network(world).access(pos).getBattery();
    }

    @Override
    protected void onPatternLock(MultiBlockPattern pattern) {
        super.onPatternLock(pattern);
        if (isMaster) {
            getBattery().setState(IBatteryBlock.State.RECHARGEABLE);
        } else {
            getBattery().setState(IBatteryBlock.State.DISABLED);
        }
    }
}
