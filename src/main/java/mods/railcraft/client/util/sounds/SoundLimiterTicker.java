/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.util.sounds;

import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundLimiterTicker {

    private int clockClient;
    private int clockServer;

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        clockServer++;
        if (clockServer % 8 == 0)
            SoundHelper.decrementLimiters();
    }

    @SubscribeEvent
    public void tick(ClientTickEvent event) {
        clockClient++;
        if (clockClient % 8 == 0)
            SoundHelper.decrementLimiters();
    }

}
