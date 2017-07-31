/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.bore.IBoreHead;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.item.ItemStack;

public abstract class ItemBoreHead extends ItemRailcraft implements IBoreHead {

    protected ItemBoreHead() {
        maxStackSize = 1;
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 1;
    }
}
