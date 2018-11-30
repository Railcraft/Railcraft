/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

/**
 * Wrapper object for the various inventory API objects.
 *
 * Created by CovertJaguar on 5/7/2016.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IInventoryAdapter {
    Object getBackingObject();

    int getNumSlots();
}
