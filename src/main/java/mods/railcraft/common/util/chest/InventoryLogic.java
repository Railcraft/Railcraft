/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.chest;

import mods.railcraft.common.util.inventory.IInventoryComposite;
import net.minecraft.world.World;

/**
 *
 */
public abstract class InventoryLogic extends AbstractLogic {

    protected IInventoryComposite inventory;

    InventoryLogic(World world, IInventoryComposite inventory) {
        super(world);
        this.inventory = inventory;
    }

    public IInventory getInventory() {
        return inventory;
    }

    public interface IContainer extends IEntityLogic.IContainer {
        @Override
        InventoryLogic getLogic();
    }
}
