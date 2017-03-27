/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class SlotFilter extends SlotRailcraft {

    @Nonnull
    public BooleanSupplier isEnabled = () -> true;

    public SlotFilter(IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        setPhantom();
    }

    public SlotFilter(IInventory iinventory, int slotIndex, int posX, int posY, BooleanSupplier isEnabled) {
        this(iinventory, slotIndex, posX, posY);
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return isEnabled.getAsBoolean();
    }

}
