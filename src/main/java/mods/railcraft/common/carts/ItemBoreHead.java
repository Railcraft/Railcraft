/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import mods.railcraft.api.carts.bore.IBoreHead;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.misc.MiscTools;

public abstract class ItemBoreHead extends Item implements IBoreHead {

    protected ItemBoreHead() {
        maxStackSize = 1;
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:" + MiscTools.cleanTag(getUnlocalizedName()));
    }
}
