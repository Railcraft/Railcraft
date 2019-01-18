/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by CovertJaguar on 7/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItem extends IRailcraftObject<Item> {

    default String getTooltipTag(ItemStack stack) {
        return stack.getTranslationKey() + ".tips";
    }

    @SideOnly(Side.CLIENT)
    default ToolTip getToolTip(ItemStack stack, @Nullable World world, ITooltipFlag flag) {
        String tipTag = getTooltipTag(stack);
        if (LocalizationPlugin.hasTag(tipTag))
            return ToolTip.buildToolTip(tipTag);
        return new ToolTip();
    }

    @SideOnly(Side.CLIENT)
    default void addToolTips(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag flag) {
        ToolTip toolTip = getToolTip(stack, world, flag);
        info.addAll(toolTip.convertToStrings());
    }

}
