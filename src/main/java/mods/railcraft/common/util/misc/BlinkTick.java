/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import mods.railcraft.api.signals.SignalAspect;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlinkTick {

    private int clock;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if(event.side != Side.CLIENT)
            return;
        clock++;
        if (clock % 16 == 0)
            SignalAspect.invertBlinkState();
    }

}
