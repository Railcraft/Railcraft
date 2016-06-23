/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

public class EntityCartTank extends CartBaseFiltered implements IFluidHandler, ISidedInventory, IFluidCart {
    private static final DataParameter<Optional<FluidStack>> FLUID_STACK = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataManagerPlugin.OPTIONAL_FLUID_STACK);
    private static final DataParameter<Boolean> FILLING = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.BOOLEAN);
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private final TankManager tankManager = new TankManager();
    private final StandardTank tank = new StandardTank(RailcraftConfig.getTankCartCapacity());
    private final InventoryMapper invLiquids = new InventoryMapper(this, false);
    private final InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 1, false);
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1, false);
    private int update = MiscTools.RANDOM.nextInt();

    public EntityCartTank(World world) {
        super(world);
        tankManager.add(tank);
    }

    public EntityCartTank(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + getYOffset(), d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.TANK;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FLUID_STACK, Optional.empty());
        dataManager.register(FILLING, false);
    }

    @Nullable
    private FluidStack getFluidStack() {
        return dataManager.get(FLUID_STACK).orElse(null);
    }

    private void setFluidStack(@Nullable FluidStack fluidStack) {
        dataManager.set(FLUID_STACK, Optional.ofNullable(fluidStack));
        dataManager.setDirty(FLUID_STACK);
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public void setDead() {
        super.setDead();
        InvTools.dropInventory(invLiquids, worldObj, getPosition());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isClient(worldObj)) {
            FluidStack fluidStack = getFluidStack();
            if (fluidStack != null) {
                tank.renderData.fluid = fluidStack.getFluid();
                tank.renderData.amount = fluidStack.amount;
                tank.renderData.color = tank.renderData.fluid.getColor(fluidStack);
            } else {
                tank.renderData.fluid = null;
                tank.renderData.amount = 0;
                tank.renderData.color = StandardTank.DEFAULT_COLOR;
            }
            return;
        }

        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null) {
            FluidStack syncedStack = getFluidStack();
            if (!fluidStack.isFluidStackIdentical(syncedStack))
                setFluidStack(fluidStack);
        } else {
            setFluidStack(null);
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
            if (FluidHelper.handleRightClick(this, null, player, true, true))
                return true;
            GuiHandler.openGui(EnumGui.CART_TANK, player, worldObj, this);
        }
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (resource == null)
            return 0;
        Fluid filterFluid = getFilterFluid();
        if (filterFluid == null || resource.getFluid() == filterFluid)
            return tank.fill(resource, doFill);
        return 0;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        if (tank.getFluidType() == resource.getFluid())
            return tank.drain(resource.amount, doDrain);
        return null;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        Fluid filterFluid = getFilterFluid();
        return filterFluid == null || fluid == filterFluid;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    /**
     * @return Array of {@link StandardTank}s contained in this ITankContainer
     */
    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing side) {
        return tankManager.getTankInfo();
    }

    public boolean isFilling() {
        return dataManager.get(FILLING);
    }

    @Override
    public void setFilling(boolean fill) {
        dataManager.set(FILLING, fill);
    }

    @Nullable
    public Fluid getFilterFluid() {
        ItemStack filter = getFilterItem();
        if (filter == null)
            return null;
        return FluidItemHelper.getFluidInContainer(filter);
    }

    public IInventory getInvLiquids() {
        return invLiquids;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == SLOT_INPUT && FluidItemHelper.isContainer(stack);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public boolean canPassFluidRequests(Fluid fluid) {
        if (hasFilter())
            return getFilterFluid() == fluid;
        return !(!tank.isEmpty() && tank.getFluidType() != fluid);
    }

    @Override
    public boolean canAcceptPushedFluid(EntityMinecart requester, Fluid fluid) {
        return canPassFluidRequests(fluid);
    }

    @Override
    public boolean canProvidePulledFluid(EntityMinecart requester, Fluid fluid) {
        return canPassFluidRequests(fluid);
    }

}
