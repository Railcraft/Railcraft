/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import mods.railcraft.common.util.inventory.IInventoryComposite;
import mods.railcraft.common.util.inventory.InventoryAdaptor;
import mods.railcraft.common.util.inventory.InventoryComposite;
import net.minecraft.inventory.IInventory;

import java.util.Iterator;

/**
 *
 */
public abstract class InventoryLogic extends AbstractLogic implements IInventoryComposite {

    protected final IInventory inventory;
    protected final IInventoryComposite composite;

    InventoryLogic(LogicAdapter adapter, IInventory inventory) {
        super(adapter);
        this.inventory = inventory;
        this.composite = InventoryComposite.of(inventory);
    }

    @Override
    public Iterator<InventoryAdaptor> iterator() {
        return composite.iterator();
    }

    public IInventory getInventory() {
        return inventory;
    }
}
