/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;

import java.util.Objects;

/**
 * Created by CovertJaguar on 3/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryAdaptor implements IInventoryObject {
    private final Object inventory;

    private InventoryAdaptor(Object inventory) {
        this.inventory = inventory;
    }

    public static InventoryAdaptor get(final IInventory inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryAdaptor(inventory) {

            @Override
            public int getNumSlots() {
                return inventory.getSizeInventory();
            }
        };
    }

    public static InventoryAdaptor get(final IItemHandler inventory) {
        Objects.requireNonNull(inventory);
        return new InventoryAdaptor(inventory) {

            @Override
            public int getNumSlots() {
                return inventory.getSlots();
            }
        };
    }

    @Override
    public Object getBackingObject() {
        return inventory;
    }

}
