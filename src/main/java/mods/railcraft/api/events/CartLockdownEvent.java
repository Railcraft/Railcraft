/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.item.EntityMinecart;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartLockdownEvent extends Event {

    public final EntityMinecart cart;
    public final int x;
    public final int y;
    public final int z;

    private CartLockdownEvent(EntityMinecart cart, int x, int y, int z) {
        this.cart = cart;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * This event is posted every tick that a LockType Track (Lockdown, Holding,
     * Boarding) is holding onto a minecart.
     */
    public static class Lock extends CartLockdownEvent {

        public Lock(EntityMinecart cart, int x, int y, int z) {
            super(cart, x, y, z);
        }
    }

    /**
     * This event is posted every tick that a LockType Track (Lockdown, Holding,
     * Boarding) is releasing a minecart.
     */
    public static class Release extends CartLockdownEvent {

        public Release(EntityMinecart cart, int x, int y, int z) {
            super(cart, x, y, z);
        }
    }
}
