/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITankTile {

    StandardTank getTank();

    TankManager getTankManager();

    IInventory getInventory();

    String getTitle();

    Slot getInputSlot(IInventory inv, int slotNum, int x, int y);
}
