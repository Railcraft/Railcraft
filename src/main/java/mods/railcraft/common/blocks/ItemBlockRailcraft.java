/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemBlockRailcraft extends ItemBlock implements ColorPlugin.IColoredItem, IRailcraftObject {

    public ItemBlockRailcraft(Block block) {
        super(block);
    }

    @Override
    public void finalizeDefinition() {
        if (block instanceof ColorPlugin.IColoredBlock)
            ColorPlugin.instance.register(this, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor colorHandler() {
        return (stack, tintIndex) -> EnumColor.fromItemStack(stack).getHexColor();
    }

    @Nullable
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
