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
import net.minecraft.util.IIcon;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCartVanilla extends ItemCart {

    private final Item original;

    public ItemCartVanilla(ICartType cart, Item original) {
        super(cart);
        this.original = original;
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return original.getIconFromDamage(damage);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        original.registerIcons(iconRegister);
    }

}
