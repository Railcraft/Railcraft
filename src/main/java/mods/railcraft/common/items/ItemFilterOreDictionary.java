/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by CovertJaguar on 5/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemFilterOreDictionary extends ItemFilterSimple {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(getStack(), RailcraftItems.FILTER_BLANK, RailcraftItems.INGOT, Metal.COPPER);
    }

    @Override
    public boolean matches(ItemStack matcher, ItemStack target) {
        ItemStack prototype = getPrototype(matcher);
        return !InvTools.isEmpty(prototype) && OreDictPlugin.matches(prototype, target);
    }

    @Override
    public boolean isValidPrototype(ItemStack stack) {
        return !OreDictPlugin.getOreTags(stack).isEmpty();
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void addAdditionalInfo(ItemStack stack, ItemStack prototype, List<String> info, ITooltipFlag adv) {
        info.add(LocalizationPlugin.translate("item.railcraft.filter.ore.dict.tips.tags"));
        OreDictPlugin.getOreTags(prototype).forEach(t -> info.add("-" + t));
    }
}
