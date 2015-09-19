package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 9/19/2015.
 */
public interface ICartType {
    byte getId();

    String getTag();

    Class<? extends EntityMinecart> getCartClass();

    ItemStack getContents();

    EntityMinecart makeCart(ItemStack stack, World world, double i, double j, double k);

    ItemStack getCartItem();

    boolean isEnabled();
}
