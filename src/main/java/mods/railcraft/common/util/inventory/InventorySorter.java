/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import java.util.Comparator;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum InventorySorter implements Comparator<InventoryAdaptor> {

    SIZE_DESCENDING {
        @Override
        public int compare(InventoryAdaptor inv1, InventoryAdaptor inv2) {
            return inv2.slotCount() - inv1.slotCount();
        }

    }
}
