/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import net.minecraft.inventory.IInventory;

/**
 * The logic behind the void chest.
 */
public class VoidChestLogic extends InventoryLogic {
    private static final int TICK_PER_VOID = 8;

    public VoidChestLogic(LogicAdapter adapter, IInventory inventory) {
        super(adapter, inventory);
    }

    @Override
    public void updateServer() {
        if (clock(TICK_PER_VOID))
            removeOneItem();
    }
}
