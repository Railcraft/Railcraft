/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import java.util.List;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IMachineProxy {

    String getTag(int meta);

    IIcon getTexture(int meta, int side);

    TileEntity getTileEntity(int meta);

    List<? extends IEnumMachine> getCreativeList();

    Class getTileClass(int meta);

    ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv);

    void registerIcons(IIconRegister iconRegister);

}
