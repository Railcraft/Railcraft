/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IRailcraftCart {
    DataParameter<Byte> SEASON = DataSerializers.BYTE.createKey(225);

    IRailcraftCartContainer getCartType();

    default void initEntityFromItem(ItemStack stack) {
    }

    default ItemStack createCartItem(EntityMinecart cart) {
        ItemStack stack = RailcraftCarts.fromCart(cart).getStack();
        if (!InvTools.isEmpty(stack) && cart.hasCustomName())
            stack.setStackDisplayName(cart.getCustomNameTag());
        return stack;
    }

    default ItemStack[] getComponents(EntityMinecart cart) {
        ItemStack contents = getCartType().getContents();
        if (!InvTools.isEmpty(contents))
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
        if (!cart.world.getGameRules().getBoolean("doEntityDrops"))
            return;
        ItemStack[] drops = getItemsDropped(cart);
        if (!RailcraftConfig.doCartsBreakOnDrop() && cart.hasCustomName() && !ArrayUtils.isEmpty(drops))
            drops[0].setStackDisplayName(cart.getCustomNameTag());
        for (ItemStack item : drops) {
            if (!InvTools.isEmpty(item))
                cart.entityDropItem(item, 0.0F);
        }
    }

    default void cartInit() {
        ((Entity) this).getDataManager().register(SEASON, (byte) 0);
    }

    default SeasonPlugin.Season getSeason() {
        return SeasonPlugin.Season.VALUES[((Entity) this).getDataManager().get(SEASON)];
    }

    default void setSeason(SeasonPlugin.Season season) {
        ((Entity) this).getDataManager().set(SEASON, (byte) season.ordinal());
    }

    default NBTTagCompound saveToNBT(NBTTagCompound nbt) {
        NBTPlugin.writeEnumOrdinal(nbt, "season", getSeason());
        return nbt;
    }

    default void loadFromNBT(NBTTagCompound nbt) {
        setSeason(NBTPlugin.readEnumOrdinal(nbt, "season", SeasonPlugin.Season.VALUES, SeasonPlugin.Season.DEFAULT));
    }

}
