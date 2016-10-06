/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IRailcraftCart {

    IRailcraftCartContainer getCartType();

    default void initEntityFromItem(ItemStack stack) {
    }

    @Nullable
    default ItemStack createCartItem(EntityMinecart cart) {
        ItemStack stack = RailcraftCarts.fromCart(cart).getStack();
        if (stack != null && cart.hasCustomName())
            stack.setStackDisplayName(cart.getCustomNameTag());
        return stack;
    }

    default ItemStack[] getComponents(EntityMinecart cart) {
        ItemStack contents = getCartType().getContents();
        if (contents != null)
            return new ItemStack[]{new ItemStack(Items.MINECART), contents};
        return new ItemStack[]{createCartItem(cart)};
    }

    default ItemStack[] getItemsDropped(EntityMinecart cart) {
        if (RailcraftConfig.doCartsBreakOnDrop())
            return getComponents(cart);
        else
            return new ItemStack[]{createCartItem(cart)};
    }

    default void killAndDrop(EntityMinecart cart) {
        cart.setDead();
        ItemStack[] drops = getItemsDropped(cart);
        if (!RailcraftConfig.doCartsBreakOnDrop() && cart.hasCustomName() && !ArrayUtils.isEmpty(drops))
            drops[0].setStackDisplayName(cart.getCustomNameTag());
        for (ItemStack item : drops) {
            if (item != null)
                cart.entityDropItem(item, 0.0F);
        }
    }

}
