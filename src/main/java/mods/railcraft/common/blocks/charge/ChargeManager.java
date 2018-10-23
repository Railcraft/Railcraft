/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
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
public enum ChargeManager implements Charge.IManager {
    DISTRIBUTION, TRANSMISSION;

    static {
        Charge.distribution = DISTRIBUTION;
    }

    private final Map<World, ChargeNetwork> networks = new MapMaker().weakKeys().makeMap();

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END) {
            ((ChargeNetwork) network(event.world)).tick();
        }
    }

    @Override
    public IChargeNetwork network(World world) {
        return networks.computeIfAbsent(world, ChargeNetwork::new);
    }
}
