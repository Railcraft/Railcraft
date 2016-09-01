/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    }

    protected CartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public abstract IRailcraftCartContainer getCartType();

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : getCartType().getEntityLocalizationTag();
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

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        ItemStack stack = RailcraftCarts.fromCart(this).getStack();
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

    public World theWorld() {
        return worldObj;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }
}
