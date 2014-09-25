/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine
{

    String getTag();

    boolean isAvaliable();

    ItemStack getItem();

    ItemStack getItem(int qty);

    IIcon getTexture(int side);

    Class getTileClass();

    int ordinal();

    Block getBlock();

    boolean isDepreciated();
}
