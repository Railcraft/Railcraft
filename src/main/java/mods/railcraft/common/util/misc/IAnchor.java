/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.inventory.IInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IAnchor extends IInventory {

    long getAnchorFuel();

    ItemMap<Float> getFuelMap();

}
