/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.util.inventory;

import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;

import java.util.Comparator;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum InventorySorter implements Comparator<IInventoryObject> {

    SIZE_DESCENDING {
        @Override
        public int compare(IInventoryObject inv1, IInventoryObject inv2) {
            return inv2.getNumSlots() - inv1.getNumSlots();
        }

    }
}
