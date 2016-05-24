/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBase extends EntityMinecart implements IRailcraftCart, IItemCart {
    protected CartBase(World world) {
        super(world);
        //TODO: Is this the best way?
        setRenderDistanceWeight(CartConstants.RENDER_DIST_MULTIPLIER);
    }

    protected CartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
        setRenderDistanceWeight(CartConstants.RENDER_DIST_MULTIPLIER);
    }

    public abstract ICartType getCartType();

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : getCartType().getTag();
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, stack, hand)) || doInteract(player);
    }

    public boolean doInteract(EntityPlayer player) {
        return true;
    }

    public abstract double getDrag();

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        ItemStack stack = EnumCart.fromCart(this).getCartItem();
        if (hasCustomName())
            stack.setStackDisplayName(getCustomNameTag());
        return stack;
    }

    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        setDead();
        List<ItemStack> drops = getItemsDropped();
        if (hasCustomName())
            drops.get(0).setStackDisplayName(this.getCustomNameTag());
        for (ItemStack item : drops) {
            entityDropItem(item, 0.0F);
        }
    }

    @Override
    public EntityMinecart.Type getType() {
        return null;
    }

    @Override
    public boolean canPassItemRequests() {
        return false;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }
}
