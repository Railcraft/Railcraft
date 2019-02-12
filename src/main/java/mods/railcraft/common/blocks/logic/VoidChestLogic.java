/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvOp;
import mods.railcraft.common.util.inventory.InvTools;
import org.jetbrains.annotations.Nullable;

/**
 * The logic behind the void chest.
 */
public class VoidChestLogic extends InventoryLogic {
    private static final int TICK_PER_VOID = 8;

    public VoidChestLogic(Adapter adapter) {
        super(adapter, 27);
    }

    @Override
    public void updateServer() {
        if (clock(TICK_PER_VOID)) {
            final double fullness = InvTools.calculateFullness(this);
            streamSlots().forEach(slot -> {
                int remove = (int) Math.round(slot.getMaxStackSize() * fullness);
                slot.removeFromSlot(remove < 1 ? 1 : remove, InvOp.EXECUTE);
            });
        }
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.CHEST;
    }
}
