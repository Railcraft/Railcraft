/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.items.IFilterItem;
import mods.railcraft.api.items.IPrototypedItem;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by CovertJaguar on 5/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemFilterSimple extends ItemRailcraft implements IFilterItem, IPrototypedItem {

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> info, ITooltipFlag adv) {
        super.addInformation(stack, player, info, adv);
        ItemStack prototype = getPrototype(stack);
        if (!InvTools.isEmpty(prototype)) {
            info.add(LocalizationPlugin.translate("item.railcraft.filter.tips.prototype"));
            info.add("-" + prototype.getDisplayName());
            info.add("--" + prototype.getItem().getRegistryName() + "#" + prototype.getMetadata());
            addAdditionalInfo(stack, prototype, info, adv);
        }
    }

    protected void addAdditionalInfo(ItemStack stack, ItemStack prototype, List<String> info, ITooltipFlag adv) {
    }
}
