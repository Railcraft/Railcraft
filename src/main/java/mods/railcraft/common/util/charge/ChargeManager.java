/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.charge;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.util.misc.Code;
import mods.railcraft.common.util.misc.Game;
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
    DISTRIBUTION(Charge.distribution);

    private final Charge type;

    ChargeManager(Charge type) {
        this.type = type;
        Code.setValue(Charge.class, type, this, "manager");
    }

    private final Map<World, ChargeNetwork> networks = new MapMaker().weakKeys().makeMap();

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END) {
            ((ChargeNetwork) network(event.world)).tick();
        }
    }

    @Override
    public Charge.INetwork network(World world) {
        return networks.computeIfAbsent(Game.requireHost(world), (w) -> new ChargeNetwork(type, w));
    }
}
