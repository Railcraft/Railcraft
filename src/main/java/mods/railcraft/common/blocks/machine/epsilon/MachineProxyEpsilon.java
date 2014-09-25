/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.IMachineProxy;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyEpsilon implements IMachineProxy {

    @Override
    public String getTag(int meta) {
        return EnumMachineEpsilon.fromId(meta).getTag();
    }

    @Override
    public IIcon getTexture(int meta, int side) {
        return EnumMachineEpsilon.fromId(meta).getTexture(side);
    }

    @Override
    public Class getTileClass(int meta) {
        return EnumMachineEpsilon.fromId(meta).getTileClass();
    }

    @Override
    public TileEntity getTileEntity(int meta) {
        return EnumMachineEpsilon.fromId(meta).getTileEntity();
    }

    @Override
    public List<? extends IEnumMachine> getCreativeList() {
        return EnumMachineEpsilon.getCreativeList();
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        return EnumMachineEpsilon.fromId(stack.getItemDamage()).getToolTip(stack, player, adv);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        EnumMachineEpsilon.registerIcons(iconRegister);
    }

}
