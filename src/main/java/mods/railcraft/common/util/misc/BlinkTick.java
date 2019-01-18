/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import mods.railcraft.api.signals.SignalAspect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

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
