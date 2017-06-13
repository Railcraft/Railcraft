/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.*;
import mods.railcraft.common.fluids.tanks.FilteredTank;
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
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;

public class EntityCartTank extends CartBaseFiltered implements ISidedInventory, IFluidCart {
    private static final DataParameter<OptionalFluidStack> FLUID_STACK = EntityDataManager.createKey(EntityCartTank.class, DataManagerPlugin.OPTIONAL_FLUID_STACK);
    private static final DataParameter<Boolean> FILLING = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.BOOLEAN);
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank = new FilteredTank(RailcraftConfig.getTankCartCapacity());
    private final InventoryMapper invLiquids = new InventoryMapper(this, false);
    private final InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 1, false);
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1, false);
    private int update = MiscTools.RANDOM.nextInt();

    public EntityCartTank(World world) {
        super(world);
        tank.setFilter(this::getFilterFluid);
        tankManager.add(tank);
    }

    public EntityCartTank(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.TANK;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FLUID_STACK, OptionalFluidStack.empty());
        dataManager.register(FILLING, false);
    }

    @Nullable
    private FluidStack getFluidStack() {
        return dataManager.get(FLUID_STACK).orElse(null);
    }

    private void setFluidStack(@Nullable FluidStack fluidStack) {
        dataManager.set(FLUID_STACK, OptionalFluidStack.of(Fluids.copy(fluidStack)));
//        dataManager.setDirty(FLUID_STACK);
//        Game.log(Level.INFO, "sync tank");
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (Game.isHost(worldObj))
            return;
        if (key == FLUID_STACK)
            tank.setFluid(Fluids.copy(getFluidStack()));
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) getTankManager();
        return super.getCapability(capability, facing);
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
            return;
        }

        FluidStack fluidStack = tank.getFluid();
        if (!Fluids.areIdentical(fluidStack, getFluidStack()))
            setFluidStack(fluidStack);


        update++;

        ItemStack topSlot = invLiquids.getStackInSlot(SLOT_INPUT);
        if (!InvTools.isEmpty(topSlot) && !FluidItemHelper.isContainer(topSlot)) {
            invLiquids.setInventorySlotContents(SLOT_INPUT, null);
            entityDropItem(topSlot, 1);
        }

        ItemStack bottomSlot = invLiquids.getStackInSlot(SLOT_OUTPUT);
        if (!InvTools.isEmpty(bottomSlot) && !FluidItemHelper.isContainer(bottomSlot)) {
            invLiquids.setInventorySlotContents(SLOT_OUTPUT, null);
            entityDropItem(bottomSlot, 1);
        }

        //FIXME
//        if (update % FluidTools.BUCKET_FILL_TIME == 0)
//            FluidTools.processContainers(tank, invLiquids, SLOT_INPUT, SLOT_OUTPUT);
    }

    @Override
    public boolean doInteract(EntityPlayer player, @Nullable ItemStack stack, @Nullable EnumHand hand) {
        if (FluidTools.interactWithFluidHandler(stack, getTankManager(), player))
            return true;
        if (Game.isHost(worldObj)) {
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
        if (InvTools.isEmpty(filter))
            return null;
        return FluidItemHelper.getFluidInContainer(filter);
    }

    public IInventory getInvLiquids() {
        return invLiquids;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nullable ItemStack stack) {
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

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        return EnumGui.TANK;
    }
}
