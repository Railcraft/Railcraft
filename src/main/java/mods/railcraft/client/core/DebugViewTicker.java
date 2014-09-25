/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.Minecraft;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DebugViewTicker {

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().entityRenderer.debugViewDirection != 0) {
            Game.log(Level.ERROR, "Something changed the debugViewDirection var! Please report to CovertJaguar immediately with a list of your mods!");
            Minecraft.getMinecraft().entityRenderer.debugViewDirection = 0;
        }
    }

}
