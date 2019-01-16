/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.util.misc.Game;

import java.util.List;
import java.util.Optional;

/**
 * Created by CovertJaguar on 10/31/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileMultiBlockCharge extends TileMultiBlock {
    protected TileMultiBlockCharge(List<? extends MultiBlockPattern> patterns) {
        super(patterns);
    }

    public IBatteryBlock getBattery() {
        Optional<? extends IBatteryBlock> battery = Charge.distribution.network(world).access(pos).getBattery();
        assert battery.isPresent();
        return battery.get();
    }

    private int prevComparatorOutput;

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world) && clock % 16 == 0) {
            int newComparatorOutput = Charge.distribution.network(world).access(pos).getComparatorOutput();
            if (prevComparatorOutput != newComparatorOutput)
                world.updateComparatorOutputLevel(pos, getBlockType());
            prevComparatorOutput = newComparatorOutput;
        }
    }

    @Override
    protected void onPatternLock(MultiBlockPattern pattern) {
        super.onPatternLock(pattern);
        if (isMaster) {
            getBattery().setState(IBatteryBlock.State.SOURCE);
        } else {
            getBattery().setState(IBatteryBlock.State.DISABLED);
        }
    }
}
