/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.common.util.misc.Game;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartContainerBase extends EntityMinecartContainer implements IRailcraftCart, IItemCart {
    private final EnumFacing[] travelDirectionHistory = new EnumFacing[2];
    protected EnumFacing travelDirection = null;
    protected EnumFacing verticalTravelDirection = null;

    public CartContainerBase(World world) {
        super(world);
        renderDistanceWeight = CartConstants.RENDER_DIST_MULTIPLIER;
    }

    public CartContainerBase(World world, double x, double y, double z) {
        super(world, x, y, z);
        renderDistanceWeight = CartConstants.RENDER_DIST_MULTIPLIER;
    }

    public abstract ICartType getCartType();

    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : getCartType().getTag();
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
    }

    @Override
    public final boolean interactFirst(EntityPlayer player) {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player)))
            return true;
        return doInteract(player);
    }

    public boolean doInteract(EntityPlayer player) {
        return true;
    }

    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack stack = EnumCart.fromCart(this).getCartItem();
        if (hasCustomName())
            stack.setStackDisplayName(getCustomNameTag());
        return stack;
    }

    public abstract List<ItemStack> getItemsDropped();

    @Override
    public void setDead() {
        if (Game.isNotHost(worldObj))
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                setInventorySlotContents(slot, null);
            }
        super.setDead();
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        setDead();
        List<ItemStack> drops = getItemsDropped();
        if (hasCustomName())
            drops.get(0).setStackDisplayName(getCustomNameTag());
        for (ItemStack item : drops) {
            entityDropItem(item, 0.0F);
        }
    }

    @Override
    public EntityMinecart.EnumMinecartType getMinecartType() {
        return null;
    }

    protected void updateTravelDirection(BlockPos pos, IBlockState state) {
        EnumRailDirection dir = getDirectionFromBlock(pos, state);
        if (dir != null) {
            EnumFacing EnumFacing = determineTravelDirection(dir);
            EnumFacing previousEnumFacing = travelDirectionHistory[1];
            if (previousEnumFacing != null && travelDirectionHistory[0] == previousEnumFacing) {
                travelDirection = EnumFacing;
                verticalTravelDirection = determineVerticalTravelDirection(dir);
            }
            travelDirectionHistory[0] = previousEnumFacing;
            travelDirectionHistory[1] = EnumFacing;
        }
    }

    private EnumRailDirection getDirectionFromBlock(BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockRailBase) {
            BlockRailBase rail = (BlockRailBase) block;
            return rail.getRailDirection(worldObj, pos, state, this);
        }
        return null;
    }

    private EnumFacing determineTravelDirection(EnumRailDirection direction) {
        if (direction == EnumRailDirection.NORTH_SOUTH || direction == EnumRailDirection.EAST_WEST) {
            if (posX - prevPosX > 0)
                return EnumFacing.EAST;
            if (posX - prevPosX < 0)
                return EnumFacing.WEST;
            if (posZ - prevPosZ > 0)
                return EnumFacing.SOUTH;
            if (posZ - prevPosZ < 0)
                return EnumFacing.NORTH;
        } else {
            switch (direction) {
                case SOUTH_EAST:
                    if (prevPosZ > posZ)
                        return EnumFacing.EAST;
                    else
                        return EnumFacing.SOUTH;
                case SOUTH_WEST:
                    if (prevPosZ > posZ)
                        return EnumFacing.WEST;
                    else
                        return EnumFacing.SOUTH;
                case NORTH_WEST:
                    if (prevPosZ > posZ)
                        return EnumFacing.NORTH;
                    else
                        return EnumFacing.WEST;
                case NORTH_EAST:
                    if (prevPosZ > posZ)
                        return EnumFacing.NORTH;
                    else
                        return EnumFacing.EAST;
            }
        }
        return null;
    }

    private EnumFacing determineVerticalTravelDirection(EnumRailDirection direction) {
        if (direction.isAscending())
            return prevPosY < posY ? EnumFacing.UP : EnumFacing.DOWN;
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
