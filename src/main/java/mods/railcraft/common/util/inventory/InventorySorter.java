/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import java.util.Comparator;
import net.minecraft.inventory.IInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum InventorySorter implements Comparator<IInventory> {

    SIZE_DECENDING {
        @Override
        public int compare(IInventory inv1, IInventory inv2) {
            return inv2.getSizeInventory() - inv1.getSizeInventory();
        }

    };
}
