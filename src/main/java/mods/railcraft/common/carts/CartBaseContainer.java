/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.containers.FactoryContainer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.IInventoryComposite;
import mods.railcraft.common.util.inventory.ItemHandlerFactory;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBaseContainer extends EntityMinecartContainer implements IRailcraftCart, IItemCart, IInventoryComposite {
    private final EnumFacing[] travelDirectionHistory = new EnumFacing[2];
    protected @Nullable EnumFacing travelDirection;
    protected @Nullable EnumFacing verticalTravelDirection;
    @SuppressWarnings("CanBeFinal")
    protected List<InventoryMapper> invMappers = new ArrayList<>();

    protected CartBaseContainer(World world) {
        super(world);
    }

    protected CartBaseContainer(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        cartInit();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        saveToNBT(compound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        loadFromNBT(compound);
    }

    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : LocalizationPlugin.translate(getCartType().getEntityLocalizationTag());
    }

    @Override
    public final boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand))) || doInteract(player, hand);
    }

    @OverridingMethodsMustInvokeSuper
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (Game.isHost(world)) {
            openRailcraftGui(player);
        }
        return true;
    }

    @Override
    public final ItemStack getCartItem() {
        return createCartItem(this);
    }

    @Override
    public void setDead() {
        if (Game.isClient(world))
            for (int slot = 0; slot < getSizeInventory(); slot++) {
                setInventorySlotContents(slot, ItemStack.EMPTY);
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
    @Override
    public EntityMinecart.Type getType() {
        throw new RuntimeException("This method should NEVER be called");
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
        @Nullable EnumFacing facing = determineTravelDirection(shape);
        @Nullable EnumFacing previousEnumFacing = travelDirectionHistory[1];
        if (previousEnumFacing != null && travelDirectionHistory[0] == previousEnumFacing) {
            travelDirection = facing;
            verticalTravelDirection = determineVerticalTravelDirection(shape);
        }
        travelDirectionHistory[0] = previousEnumFacing;
        travelDirectionHistory[1] = facing;
    }

    private @Nullable EnumFacing determineTravelDirection(BlockRailBase.EnumRailDirection shape) {
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

    private @Nullable EnumFacing determineVerticalTravelDirection(BlockRailBase.EnumRailDirection shape) {
        if (shape.isAscending())
            return prevPosY < posY ? EnumFacing.UP : EnumFacing.DOWN;
        return null;
    }

    @Override
    public boolean canPassItemRequests(ItemStack stack) {
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

    @Override
    public String getGuiID() {
        return "railcraft:" + getCartType().getBaseTag();
    }

    protected void openRailcraftGui(EntityPlayer player) {
        GuiHandler.openGui(getGuiType(), player, world, this);
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return FactoryContainer.build(getGuiType(), playerInventory, this, world, (int) posX, (int) posY, (int) posZ);
    }

    /**
     * Gets the GUI type of the minecart.
     *
     * If there is no GUI, the cart must override {@link #openRailcraftGui(EntityPlayer)} to not open a GUI.
     */
    protected abstract EnumGui getGuiType();

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return invMappers.stream().filter(m -> m.containsSlot(index)).allMatch(m -> m.filter().test(stack));
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
    public boolean hasCapability(Capability<?> capability, @javax.annotation.Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @javax.annotation.Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @javax.annotation.Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerFactory.wrap(this, facing));
        }
        return super.getCapability(capability, facing);
    }

}
