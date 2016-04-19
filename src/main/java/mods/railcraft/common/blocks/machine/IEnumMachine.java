/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.IBlockContainer;
import mods.railcraft.common.blocks.IStateContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends IEnumMachine<M>> extends Comparable<M>, IStringSerializable, IBlockContainer, IStateContainer {
    String getTag();

    boolean isEnabled();

    boolean isAvailable();

    boolean isDepreciated();

    ItemStack getItem();

    ItemStack getItem(int qty);

    RailcraftBlocks getBlockContainer();

    Class<? extends TileMachineBase> getTileClass();

    String getToolClass();

    boolean passesLight();

    TileMachineBase getTileEntity();

    ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv);

    int ordinal();
}
