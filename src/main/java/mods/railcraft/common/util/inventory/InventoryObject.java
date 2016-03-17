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

import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by CovertJaguar on 3/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryObject {
    private Object inventory;

    public InventoryObject(IInventory inventory) {
        this.inventory = inventory;
    }

    public InventoryObject(IItemHandler inventory) {
        this.inventory = inventory;
    }

    public Object getObject() {
        return inventory;
    }
}
