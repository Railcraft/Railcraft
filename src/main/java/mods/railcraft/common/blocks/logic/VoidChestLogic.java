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
        if (clock(TICK_PER_VOID))
            removeOneItem();
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.CHEST;
    }
}
