/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEditableItem {

    boolean validateNBT(NBTTagCompound nbt);

    boolean canPlayerEdit(EntityPlayer player, ItemStack stack);
}
