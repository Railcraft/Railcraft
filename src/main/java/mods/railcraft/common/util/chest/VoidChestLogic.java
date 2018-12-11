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
 * The logic behind the void chest.
 */
public class VoidChestLogic extends InventoryLogic {

    public VoidChestLogic(World world, IInventoryComposite inventory) {
        super(world, inventory);
    }

    @Override
    public void update() {
        inventory.removeOneItem();
    }
}
