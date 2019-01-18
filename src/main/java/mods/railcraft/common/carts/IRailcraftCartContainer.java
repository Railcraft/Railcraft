/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.items.IRailcraftItemSimple;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;

/**
 * Defines a cart.
 * Created by CovertJaguar on 9/19/2015.
 */
public interface IRailcraftCartContainer extends IRailcraftObjectContainer<IRailcraftItemSimple> {
    String getEntityLocalizationTag();

    Class<? extends EntityMinecart> getCartClass();

    EntityEntry getRegistration();

    ItemStack getContents();

    EntityMinecart makeCart(ItemStack stack, World world, double i, double j, double k);

}
