/******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016                                      *
 * http://railcraft.info                                                      *
 * *
 * This code is the property of CovertJaguar                                  *
 * and may only be used with explicit written                                 *
 * permission unless otherwise specified on the                               *
 * license page at http://railcraft.info/wiki/info:license.                   *
 ******************************************************************************/

package mods.railcraft.common.util.inventory.iterators;

import net.minecraft.item.ItemStack;

/**
 * This interface extends IInvSlot by allowing you modify a slot directly.
 * This is only valid on inventories backed by IInventory.
 * <p/>
 * <p/>
 * Created by CovertJaguar on 3/16/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IExtInvSlot extends IInvSlot {
    /**
     * Sets the current ItemStack in the slot.
     */
    void setStackInSlot(ItemStack stack);
}
