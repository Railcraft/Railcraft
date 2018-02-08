/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestVoid extends TileChestRailcraft {

    private static final int TICK_PER_VOID = 8;

    @Nullable
    @Override
    public EnumGui getGui() {
        return null;
    }

    @Override
    public void update() {
        super.update();

        if (clock % TICK_PER_VOID == 0 && Game.isHost(world))
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                ItemStack stack = getStackInSlot(slot);
                if (!InvTools.isEmpty(stack)) {
                    decrStackSize(slot, 1);
                    break;
                }
            }
    }
}
