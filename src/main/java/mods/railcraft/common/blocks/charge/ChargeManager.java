/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.MapMaker;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeManager {
    private static final Map<World, ChargeNetwork> chargeNetworks = new MapMaker().weakKeys().makeMap();

    public static ChargeNetwork getNetwork(World world) {
        ChargeNetwork chargeNetwork = chargeNetworks.get(world);
        if (chargeNetwork == null) {
            chargeNetwork = new ChargeNetwork(world);
            chargeNetworks.put(world, chargeNetwork);
        }
        return chargeNetwork;
    }

    public static ChargeManager getEventListener() {
        return new ChargeManager();
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER)
            getNetwork(event.world).tick();
    }

}
