/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.anchor;

import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 5/22/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemAnchor extends ItemMachine {
    public ItemAnchor(Block block) {
        super(block);
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        ToolTip tips = super.getToolTip(stack, player, adv);
        AnchorVariant variant = (AnchorVariant) getVariant(stack);
        if (variant != null && !variant.getFuelList().isEmpty()) {
            tips = addAnchorInfo(tips, stack);
        }
        return tips;
    }

    private ToolTip addAnchorInfo(@Nullable ToolTip toolTip, ItemStack stack) {
        if (toolTip == null)
            toolTip = new ToolTip();
        long fuel = ItemCartAnchor.getFuel(stack);
        double hours = (double) fuel / RailcraftConstants.TICKS_PER_HOUR;
        String format = LocalizationPlugin.translate("gui.railcraft.anchor.fuel.remaining");
        toolTip.add(String.format(format, hours));
        return toolTip;
    }
}
