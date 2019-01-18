/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 6/25/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemBattery<B extends BlockBattery> extends ItemBlockRailcraft<B> {
    public ItemBattery(B block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ToolTip getToolTip(ItemStack stack, @Nullable World world, ITooltipFlag adv) {
        ToolTip tips = super.getToolTip(stack, world, adv);
        IChargeBlock.ChargeSpec chargeSpec = getBlock().getChargeSpec(InvTools.getBlockStateFromStack(stack));
        IBatteryBlock.Spec batterySpec = chargeSpec.getBatterySpec();
        assert batterySpec != null;

        tips.add(LocalizationPlugin.translate("tile.railcraft.battery.tips.type", batterySpec.getInitialState()), TextFormatting.BLUE);

        tips.add(LocalizationPlugin.translate("tile.railcraft.battery.tips.capacity",
                HumanReadableNumberFormatter.format(batterySpec.getCapacity())));

        tips.add(LocalizationPlugin.translate("tile.railcraft.battery.tips.maxdraw",
                HumanReadableNumberFormatter.format(batterySpec.getMaxDraw())));

        tips.add(LocalizationPlugin.translate("tile.railcraft.battery.tips.loss",
                HumanReadableNumberFormatter.format(chargeSpec.getLosses() * RailcraftConfig.chargeLossMultiplier())));

        tips.add(LocalizationPlugin.translate("tile.railcraft.battery.tips.efficiency",
                HumanReadableNumberFormatter.format(batterySpec.getEfficiency() * 100.0)));
        return tips;
    }

}
