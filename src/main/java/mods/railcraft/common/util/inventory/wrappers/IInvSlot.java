/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IInvSlot {

    boolean canPutStackInSlot(ItemStack stack);

    boolean canTakeStackFromSlot(ItemStack stack);

    ItemStack decreaseStackInSlot();

    ItemStack getStackInSlot();

    void setStackInSlot(ItemStack stack);
    
    int getIndex();
    
}
