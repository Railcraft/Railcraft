/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import mods.railcraft.common.core.RailcraftConfig;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by CovertJaguar on 10/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class VanillaOreDisabler {
    @SubscribeEvent
    public void genOre(OreGenEvent.GenerateMinable event) {
        OreGenEvent.GenerateMinable.EventType type = event.getType();
        if (type == OreGenEvent.GenerateMinable.EventType.IRON || type == OreGenEvent.GenerateMinable.EventType.GOLD) {
            if (event.getRand().nextInt(101) > RailcraftConfig.vanillaOreGenChance())
                event.setResult(Event.Result.DENY);
        }
    }
}
