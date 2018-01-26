/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.collect.Iterators;
import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.containers.FactoryContainer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.wrappers.IInventoryComposite;
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
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseContainer extends EntityMinecartContainer implements IRailcraftCart, IItemCart, IInventoryObject, IInventoryComposite {
    private final EnumFacing[] travelDirectionHistory = new EnumFacing[2];
    protected EnumFacing travelDirection;
    protected EnumFacing verticalTravelDirection;

    protected CartBaseContainer(World world) {
        super(world);
    }

    protected CartBaseContainer(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : LocalizationPlugin.translate(getCartType().getEntityLocalizationTag());
    }

    @Override
    public final boolean processInitialInteract(@Nonnull EntityPlayer player, @Nullable ItemStack stack, @Nonnull EnumHand hand) {
        return (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, stack, hand))) || doInteract(player, stack, hand);
    }

    public boolean doInteract(EntityPlayer player, @Nullable ItemStack stack, @Nullable EnumHand hand) {
        return true;
    }

    @Nonnull
    @Override
    public final ItemStack getCartItem() {
        return createCartItem(this);
    }

    @Override
    public void setDead() {
        if (Game.isClient(world))
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                setInventorySlotContents(slot, null);
            }
        super.setDead();
    }

    @Override
    public final void killMinecart(DamageSource par1DamageSource) {
        killAndDrop(this);
    }

    /**
     * {@link net.minecraft.entity.item.EntityArmorStand#IS_RIDEABLE_MINECART}
     */
    @Nullable
    @Override
    public EntityMinecart.Type getType() {
        FMLLog.bigWarning("This method should NEVER be called");
        return null;
    }

    @Override
    public boolean isPoweredCart() {
        return false;
    }

    @Override
    public boolean canBeRidden() {
        return false;
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

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return FactoryContainer.build(getGuiType(), playerInventory, this, world, (int) posX, (int) posY, (int) posZ);
    }

    @Nonnull
    protected abstract EnumGui getGuiType();

    @Override
    public int getNumSlots() {
        return getSizeInventory();
    }

    @Override
    public Object getBackingObject() {
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

    @Override
    public Iterator<IInventoryObject> iterator() {
        return Iterators.singletonIterator(this);
    }
}
