/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CovertJaguar on 7/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItem extends IRailcraftObject<Item> {

    default int getHeatValue(ItemStack stack) {
        return 0;
    }

    default String getTooltipTag(ItemStack stack) {
        return stack.getUnlocalizedName() + ".tips";
    }

    @Nullable
    default ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        String tipTag = getTooltipTag(stack);
        if (LocalizationPlugin.hasTag(tipTag))
            return ToolTip.buildToolTip(tipTag);
        return null;
    }

    default void addToolTips(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        ToolTip toolTip = getToolTip(stack, player, adv);
        if (toolTip != null)
            info.addAll(toolTip.convertToStrings());
    }

    @Override
    default Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariant(variant);
        String oreTag = getOreTag(variant);
        if (oreTag != null)
            return oreTag;
        if (variant != null && ((Item) this).getHasSubtypes())
            return getStack(variant);
        return getObject();
    }

    @Nullable
    default String getOreTag(@Nullable IVariantEnum variant) {
        return null;
    }

}
