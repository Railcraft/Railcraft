/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.RailcraftTileEntity;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SignalBlockSimple extends SignalBlock {

    private SignalAspect aspect = SignalAspect.BLINK_RED;

    protected SignalBlockSimple(RailcraftTileEntity tile) {
        super(tile, 1);
    }

    @Override
    public void updateSignalAspect() {
        aspect = determineAspect(pairings.peek());
    }

    @Override
    public SignalAspect getSignalAspect() {
        return aspect;
    }

    @Override
    protected SignalAspect getSignalAspectForPair(WorldCoordinate otherCoord) {
        return SignalAspect.GREEN;
    }
}
