/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.worldspike;

import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.carts.ItemCartWorldspike;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 5/22/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemWorldspike extends ItemMachine {
    public ItemWorldspike(BlockMachine<?> block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ToolTip getToolTip(ItemStack stack, @Nullable World world, ITooltipFlag adv) {
        ToolTip tips = super.getToolTip(stack, world, adv);
        WorldspikeVariant variant = (WorldspikeVariant) getVariant(stack);
        if (variant != null && !variant.getFuelList().isEmpty()) {
            tips = addFuelInfo(tips, stack);
        }
        return tips;
    }

    private ToolTip addFuelInfo(ToolTip toolTip, ItemStack stack) {
        long fuel = ItemCartWorldspike.getFuel(stack);
        double hours = ((double) fuel) / RailcraftConstants.TICKS_PER_HOUR;
        String format = LocalizationPlugin.translate("gui.railcraft.worldspike.fuel.remaining", hours);
        toolTip.add(format);
        return toolTip;
    }
}
