/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemBlockRailcraft extends ItemBlock {

    public ItemBlockRailcraft(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return EnumColor.fromItemStack(stack).getHexColor();
    }

    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        String tipTag = getUnlocalizedName(stack) + ".tip";
        if (LocalizationPlugin.hasTag(tipTag))
            return ToolTip.buildToolTip(tipTag);
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        ToolTip toolTip = getToolTip(stack, player, adv);
        if (toolTip != null)
            for (ToolTipLine line : toolTip) {
                info.add(line.text);
            }
    }
}
