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
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 5/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemFilterType extends ItemFilterSimple {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(getStack(), RailcraftItems.FILTER_BLANK, new ItemStack(Items.DYE, 1, 4));
    }

    @Override
    public boolean matches(ItemStack matcher, ItemStack target) {
        ItemStack prototype = getPrototype(matcher);
        return !InvTools.isEmpty(prototype) && InvTools.isItemEqual(prototype, target, false, false);
    }
}
