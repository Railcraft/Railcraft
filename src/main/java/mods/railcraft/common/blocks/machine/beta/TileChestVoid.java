/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestVoid extends TileChestRailcraft {

    private static final int TICK_PER_VOID = 8;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.VOID_CHEST;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (clock % TICK_PER_VOID == 0 && Game.isHost(worldObj))
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                ItemStack stack = getStackInSlot(slot);
                if (stack != null) {
                    decrStackSize(slot, 1);
                    break;
                }
            }
    }

}
