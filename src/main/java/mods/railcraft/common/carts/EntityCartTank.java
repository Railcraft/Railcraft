/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mods.railcraft.common.fluids.FluidItemHelper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.carts.ILiquidTransfer;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.DataTools;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.fluids.*;

public class EntityCartTank extends CartContainerBase implements IFluidHandler, ILiquidTransfer, IEntityAdditionalSpawnData, ISidedInventory, IMinecart {
    private static final byte FLUID_ID_DATA_ID = 25;
    private static final byte FLUID_QTY_DATA_ID = 26;
    private static final byte FLUID_COLOR_DATA_ID = 27;
    private static final byte FILLING_DATA_ID = 28;
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private final TankManager tankManager = new TankManager();
    private final StandardTank tank = new StandardTank(RailcraftConfig.getTankCartCapacity());
    private final PhantomInventory invFilter = new PhantomInventory(1, this);
    private final IInventory invLiquids = new InventoryMapper(this, false);
    private final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1, false);
    private final IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1, false);
    private int update = MiscTools.getRand().nextInt();

    public EntityCartTank(World world) {
        super(world);
        tankManager.add(tank);
    }

    public EntityCartTank(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    public static ItemStack getFilterFromCartItem(ItemStack cart) {
        ItemStack filter = null;
        NBTTagCompound nbt = cart.getTagCompound();
        if (nbt != null) {
            NBTTagCompound filterNBT = nbt.getCompoundTag("filterStack");
            filter = ItemStack.loadItemStackFromNBT(filterNBT);
        }
        return filter;
    }

    public static ItemStack getCartItemForFilter(ItemStack filter) {
        ItemStack stack = EnumCart.TANK.getCartItem();
        return getCartItemForFilter(stack, filter);
    }

    public static ItemStack getCartItemForFilter(ItemStack cart, ItemStack filter) {
        if (filter != null) {
            NBTTagCompound nbt = InvTools.getItemData(cart);
            NBTTagCompound filterNBT = new NBTTagCompound();
            filter.writeToNBT(filterNBT);
            nbt.setTag("filterStack", filterNBT);
        }
        return cart;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(FLUID_ID_DATA_ID, new Integer(-1));
        dataWatcher.addObject(FLUID_QTY_DATA_ID, new Integer(0));
        dataWatcher.addObject(FLUID_COLOR_DATA_ID, new Integer(StandardTank.DEFAULT_COLOR));
        dataWatcher.addObject(FILLING_DATA_ID, Byte.valueOf((byte) 0));
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
        super.initEntityFromItem(stack);
        ItemStack filter = EntityCartTank.getFilterFromCartItem(stack);
        setFilter(filter);
    }

    private int getFluidQty() {
        return dataWatcher.getWatchableObjectInt(FLUID_QTY_DATA_ID);
    }

    private void setFluidQty(int qty) {
        dataWatcher.updateObject(FLUID_QTY_DATA_ID, qty);
    }

    private int getFluidId() {
        return dataWatcher.getWatchableObjectInt(FLUID_ID_DATA_ID);
    }

    private void setFluidId(int fluidId) {
        dataWatcher.updateObject(FLUID_ID_DATA_ID, fluidId);
    }

    private int getFluidColor() {
        return dataWatcher.getWatchableObjectInt(FLUID_COLOR_DATA_ID);
    }

    private void setFluidColor(int color) {
        dataWatcher.updateObject(FLUID_COLOR_DATA_ID, color);
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public void setDead() {
        super.setDead();
        InvTools.dropInventory(invLiquids, worldObj, (int) posX, (int) posY, (int) posZ);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isNotHost(worldObj)) {
            if (getFluidId() != -1) {
                tank.renderData.fluid = FluidRegistry.getFluid(getFluidId());
                tank.renderData.amount = getFluidQty();
                tank.renderData.color = getFluidColor();
            } else {
                tank.renderData.fluid = null;
                tank.renderData.amount = 0;
                tank.renderData.color = StandardTank.DEFAULT_COLOR;
            }
            return;
        }

        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null) {
            if (fluidStack.getFluidID() != getFluidId())
                setFluidId(fluidStack.getFluidID());
            if (fluidStack.amount != getFluidQty())
                setFluidQty(fluidStack.amount);
            if (tank.getColor() != getFluidColor())
                setFluidQty(tank.getColor());
        } else {
            if (getFluidId() != -1)
                setFluidId(-1);
            if (getFluidQty() != 0)
                setFluidQty(0);
            if (getFluidColor() != StandardTank.DEFAULT_COLOR)
                setFluidColor(StandardTank.DEFAULT_COLOR);
        }

        update++;

        ItemStack topSlot = invLiquids.getStackInSlot(SLOT_INPUT);
        if (topSlot != null && !FluidItemHelper.isContainer(topSlot)) {
            invLiquids.setInventorySlotContents(SLOT_INPUT, null);
            entityDropItem(topSlot, 1);
        }

        ItemStack bottomSlot = invLiquids.getStackInSlot(SLOT_OUTPUT);
        if (bottomSlot != null && !FluidItemHelper.isContainer(bottomSlot)) {
            invLiquids.setInventorySlotContents(SLOT_OUTPUT, null);
            entityDropItem(bottomSlot, 1);
        }

        if (update % FluidHelper.BUCKET_FILL_TIME == 0)
            FluidHelper.processContainers(tank, invLiquids, SLOT_INPUT, SLOT_OUTPUT);
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj)) {
            if (FluidHelper.handleRightClick(this, ForgeDirection.UNKNOWN, player, true, true))
                return true;
            GuiHandler.openGui(EnumGui.CART_TANK, player, worldObj, this);
        }
        return true;
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack stack = getCartItemForFilter(getFilterItem());
        if (hasCustomInventoryName())
            stack.setStackDisplayName(getCommandSenderName());
        return stack;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public String getInventoryName() {
        return LocalizationPlugin.translate(EnumCart.TANK.getTag());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        invFilter.readFromNBT("invFilter", data);

        tankManager.readTanksFromNBT(data);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        invFilter.writeToNBT("invFilter", data);

        tankManager.writeTanksToNBT(data);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null)
            return 0;
        Fluid filterFluid = getFilterFluid();
        if (filterFluid == null || resource.getFluid() == filterFluid)
            return tank.fill(resource, doFill);
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        if (tank.getFluidType() == resource.getFluid())
            return tank.drain(resource.amount, doDrain);
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        Fluid filterFluid = getFilterFluid();
        return filterFluid == null || fluid == filterFluid;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    /**
     * @return Array of {@link StandardTank}s contained in this ITankContainer
     */
    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        return tankManager.getTankInfo();
    }

    @Override
    public boolean isFilling() {
        return dataWatcher.getWatchableObjectByte(FILLING_DATA_ID) != 0;
    }

    @Override
    public void setFilling(boolean fill) {
        dataWatcher.updateObject(FILLING_DATA_ID, Byte.valueOf(fill ? 1 : (byte) 0));
    }

    public boolean hasFilter() {
        return getFilterItem() != null;
    }

    public Fluid getFilterFluid() {
        ItemStack filter = getFilterItem();
        if (filter == null)
            return null;
        return FluidItemHelper.getFluidInContainer(filter);
    }

    public ItemStack getFilterItem() {
        return getFilter().getStackInSlot(0);
    }

    public PhantomInventory getFilter() {
        return invFilter;
    }

    public void setFilter(ItemStack filter) {
        getFilter().setInventorySlotContents(0, filter);
    }

    public IInventory getInvLiquids() {
        return invLiquids;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        try {
            DataOutputStream byteStream = new DataOutputStream(new ByteBufOutputStream(data));
            DataTools.writeItemStack(getFilterItem(), byteStream);
        } catch (IOException ex) {
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        try {
            DataInputStream byteSteam = new DataInputStream(new ByteBufInputStream(data));
            setFilter(DataTools.readItemStack(byteSteam));
        } catch (IOException ex) {
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == SLOT_INPUT && FluidItemHelper.isContainer(stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == EnumCart.TANK;
    }

    @Override
    public int offerLiquid(Object source, FluidStack offer) {
        int qty = offer.amount;
        int used = fill(ForgeDirection.UNKNOWN, offer, true);

        offer.amount = qty - used;
        if (offer.amount <= 0)
            return used;

        LinkageManager lm = LinkageManager.instance();

        EntityMinecart linkedCart = lm.getLinkedCartA(this);
        if (linkedCart != source && linkedCart instanceof ILiquidTransfer)
            used += ((ILiquidTransfer) linkedCart).offerLiquid(this, offer);

        offer.amount = qty - used;
        if (offer.amount <= 0)
            return used;

        linkedCart = lm.getLinkedCartB(this);
        if (linkedCart != source && linkedCart instanceof ILiquidTransfer)
            used += ((ILiquidTransfer) linkedCart).offerLiquid(this, offer);

        return used;
    }

    @Override
    public int requestLiquid(Object source, FluidStack request) {
        FluidStack acquired = drain(ForgeDirection.UNKNOWN, request.amount, false);
        if (acquired == null || !request.isFluidEqual(acquired))
            return 0;

        drain(ForgeDirection.UNKNOWN, request.amount, true);

        if (acquired.amount >= request.amount)
            return acquired.amount;

        FluidStack newRequest = request.copy();
        newRequest.amount = request.amount - acquired.amount;

        LinkageManager lm = LinkageManager.instance();

        EntityMinecart linkedCart = lm.getLinkedCartA(this);
        if (linkedCart != source && linkedCart instanceof ILiquidTransfer)
            acquired.amount += ((ILiquidTransfer) linkedCart).requestLiquid(this, newRequest);

        if (acquired.amount >= request.amount)
            return acquired.amount;

        newRequest.amount = request.amount - acquired.amount;

        linkedCart = lm.getLinkedCartB(this);
        if (linkedCart != source && linkedCart instanceof ILiquidTransfer)
            acquired.amount += ((ILiquidTransfer) linkedCart).requestLiquid(this, newRequest);

        return acquired.amount;
    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }
}
