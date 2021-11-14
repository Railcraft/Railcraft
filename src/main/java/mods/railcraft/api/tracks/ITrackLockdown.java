/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.tracks;

import net.minecraft.entity.item.EntityMinecart;

/**
 * Any rail tile entity that can completely halt
 * all cart movement should implement this interface.
 * (Used in collision handling)
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITrackLockdown extends ITrackInstance
{

    public boolean isCartLockedDown(EntityMinecart cart);

    public void releaseCart();
}
