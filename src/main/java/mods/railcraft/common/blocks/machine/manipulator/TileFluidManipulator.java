/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.blocks.interfaces.ITileTank;
import mods.railcraft.common.fluids.AdvancedFluidHandler;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class TileFluidManipulator extends TileManipulatorCart implements ISidedInventory, ITileTank {

    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_PROCESSING = 1;
    protected static final int SLOT_OUTPUT = 2;
    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 3);
    protected static final int CAPACITY = FluidTools.BUCKET_VOLUME * 32;
    protected final InventoryAdvanced invFilter = new InventoryAdvanced(1).callbackInv(this).phantom();
    //        protected final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1);
//        protected final IInventory invInput = new InventoryMapper(this, SLOT_PROCESSING, 1);
//        protected final IInventory invInput = new InventoryMapper(this, SLOT_OUTPUT, 1);
    protected final TankManager tankManager = new TankManager();
    protected final StandardTank tank = new FilteredTank(CAPACITY, this).setFilterFluidStack(this::getFilterFluid);
    private FluidTools.ProcessState processState = FluidTools.ProcessState.RESET;

    protected TileFluidManipulator() {
        setInventorySize(3);
        tankManager.add(tank);
    }

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    public InventoryAdvanced getFluidFilter() {
        return invFilter;
    }

    public @Nullable FluidStack getFilterFluid() {
        if (!invFilter.getStackInSlot(0).isEmpty()) {
            return FluidItemHelper.getFluidStackInContainer(invFilter.getStackInSlot(0));
        }
        return null;
    }

    public @Nullable FluidStack getFluidHandled() {
        FluidStack fluid = getFilterFluid();
        if (fluid != null)
            return fluid;
        return tank.getFluid();
    }

    protected @Nullable AdvancedFluidHandler getFluidHandler(EntityMinecart cart, EnumFacing facing) {
        IFluidHandler fluidHandler = cart.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
        if (fluidHandler == null)
            return null;
        return new AdvancedFluidHandler(fluidHandler);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean bucket = FluidTools.interactWithFluidHandler(player, hand, tank);
        if (bucket && Game.isHost(world))
            sendUpdateToClient();
        return bucket || super.blockActivated(player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return FluidTools.getFluidHandler(getFacing().getOpposite(), cart) != null && super.canHandleCart(cart);
    }

    @Override
    protected void upkeep() {
        super.upkeep();
        if (clock % FluidTools.NETWORK_UPDATE_INTERVAL == 0)
            sendUpdateToClient();


        if (clock % FluidTools.BUCKET_FILL_TIME == 0) {
            processState = FluidTools.processContainer(this, tank, this instanceof TileFluidUnloader ? ProcessType.FILL_ONLY : ProcessType.DRAIN_ONLY, processState);
        }
    }

    @Override
    public @Nullable EnumGui getGui() {
        return EnumGui.MANIPULATOR_FLUID;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == SLOT_INPUT) {
            if (!FluidItemHelper.isContainer(stack))
                return false;
            if (FluidItemHelper.isEmptyContainer(stack))
                return true;
            FluidStack filter = getFilterFluid();
            return filter == null || FluidItemHelper.containsFluid(stack, filter);
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_OUTPUT;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTPlugin.writeEnumOrdinal(data, "processState", processState);

        tankManager.writeTanksToNBT(data);
        getFluidFilter().writeToNBT("invFilter", data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        processState = NBTPlugin.readEnumOrdinal(data, "processState", FluidTools.ProcessState.values(), FluidTools.ProcessState.RESET);

        if (data.getTag("tanks") instanceof NBTTagCompound)
            data.setTag("tanks", new NBTTagList());
        tankManager.readTanksFromNBT(data);

        if (data.hasKey("filter")) {
            NBTTagCompound filter = data.getCompoundTag("filter");
            getFluidFilter().readFromNBT("Items", filter);
        } else
            getFluidFilter().readFromNBT("invFilter", data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        tankManager.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        tankManager.readPacketData(data);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean canRotate(EnumFacing axis) {
        return false;
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        return false;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return new EnumFacing[]{getDefaultFacing()};
    }

    @Override
    public abstract EnumFacing getDefaultFacing();

    @Override
    public final EnumFacing getFacing() {
        return getDefaultFacing();
    }
}
