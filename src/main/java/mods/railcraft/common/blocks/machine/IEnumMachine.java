/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends IEnumMachine<M>> extends Comparable<M>, IStringSerializable {
    String getTag();

    boolean isAvailable();

    ItemStack getItem();

    ItemStack getItem(int qty);

    Class<? extends TileMachineBase> getTileClass();

    TileMachineBase getTileEntity();

    ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv);

    int ordinal();

    Block getBlock();

    IBlockState getState();

    boolean isDepreciated();
}
