/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by CovertJaguar on 3/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryObject implements IInventoryObject {
    private final Object inventory;

    private InventoryObject(Object inventory) {
        this.inventory = inventory;
    }

    public static InventoryObject get(final IInventory inventory) {
        return new InventoryObject(inventory) {

            @Override
            public int getNumSlots() {
                return inventory.getSizeInventory();
            }
        };
    }

    public static InventoryObject get(final IItemHandler inventory) {
        return new InventoryObject(inventory) {

            @Override
            public int getNumSlots() {
                return inventory.getSlots();
            }
        };
    }

    @Override
    public Object getInventoryObject() {
        return inventory;
    }

}
