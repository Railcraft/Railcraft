/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.glass;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemStrengthGlass extends ItemBlock {

    public ItemStrengthGlass(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return BlockStrengthGlass.getBlock().getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (BlockStrengthGlass.renderingHighlight)
            return super.getColorFromItemStack(stack, pass);
        return EnumColor.fromId(15 - stack.getItemDamage()).getHexColor();
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

//    @Override
//    public String getUnlocalizedName(ItemStack stack) {
//        return getUnlocalizedName() + "." + EnumColor.fromId(15 - stack.getItemDamage()).getBasicTag();
//    }

}
