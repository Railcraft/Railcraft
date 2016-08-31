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
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseContainer extends EntityMinecartContainer implements IRailcraftCart, IItemCart, IInventoryObject {
    private final EnumFacing[] travelDirectionHistory = new EnumFacing[2];
    protected EnumFacing travelDirection;
    protected EnumFacing verticalTravelDirection;

    protected CartBaseContainer(World world) {
        super(world);
    }

    protected CartBaseContainer(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public abstract IRailcraftCartContainer getCartType();

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : getCartType().getTag();
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nullable ItemStack stack, @Nonnull EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, stack, hand)) || doInteract(player, stack, hand);
    }

    public boolean doInteract(EntityPlayer player, @Nullable ItemStack stack, @Nullable EnumHand hand) {
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

    public abstract List<ItemStack> getItemsDropped();

    @Override
    public void setDead() {
        if (Game.isClient(worldObj))
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

    @Nonnull
    @Override
    public EntityMinecart.Type getType() {
        return null;
    }

    protected void updateTravelDirection(BlockPos pos, IBlockState state) {
        BlockRailBase.EnumRailDirection shape = TrackTools.getTrackDirection(getEntityWorld(), pos, state);
        if (shape != null) {
            EnumFacing EnumFacing = determineTravelDirection(shape);
            EnumFacing previousEnumFacing = travelDirectionHistory[1];
            if (previousEnumFacing != null && travelDirectionHistory[0] == previousEnumFacing) {
                travelDirection = EnumFacing;
                verticalTravelDirection = determineVerticalTravelDirection(shape);
            }
            travelDirectionHistory[0] = previousEnumFacing;
            travelDirectionHistory[1] = EnumFacing;
        }
    }

    private EnumFacing determineTravelDirection(BlockRailBase.EnumRailDirection shape) {
        if (TrackShapeHelper.isStraight(shape)) {
            if (posX - prevPosX > 0)
                return EnumFacing.EAST;
            if (posX - prevPosX < 0)
                return EnumFacing.WEST;
            if (posZ - prevPosZ > 0)
                return EnumFacing.SOUTH;
            if (posZ - prevPosZ < 0)
                return EnumFacing.NORTH;
        } else {
            switch (shape) {
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

    private EnumFacing determineVerticalTravelDirection(BlockRailBase.EnumRailDirection shape) {
        if (shape.isAscending())
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

    @Nonnull
    @Override
    public String getGuiID() {
        return "railcraft:" + getCartType().getBaseTag();
    }

    //TODO: Will this explode?
    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return null;
    }

    @Override
    public int getNumSlots() {
        return getSizeInventory();
    }

    @Override
    public Object getInventoryObject() {
        return this;
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
