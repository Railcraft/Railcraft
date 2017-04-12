/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import buildcraft.api.statements.IActionExternal;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CovertJaguar on 3/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@net.minecraftforge.fml.common.Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public class TileRollingMachinePowered extends TileRollingMachine implements IEnergyReceiver, ISidedInventory, IHasWork {
    private static final int ACTIVATION_POWER = 50;
    private static final int MAX_RECEIVE = 1000;
    private static final int MAX_ENERGY = TileRollingMachinePowered.ACTIVATION_POWER * PROCESS_TIME;
    private final AdjacentInventoryCache cache = new AdjacentInventoryCache(tileCache, null, InventorySorter.SIZE_DESCENDING);
    private final Set<Object> actions = new HashSet<Object>();
    private EnergyStorage energyStorage;

    public TileRollingMachinePowered() {
        if (RailcraftConfig.machinesRequirePower())
            energyStorage = new EnergyStorage(TileRollingMachinePowered.MAX_ENERGY, TileRollingMachinePowered.MAX_RECEIVE);
    }

    @Override
    public EquipmentVariant getMachineType() {
        return EquipmentVariant.ROLLING_MACHINE_POWERED;
    }

    @Override
    public void update() {
        if (Game.isHost(worldObj)) {
            if (clock % 16 == 0)
                processActions();
        }
        super.update();
    }

    @Override
    protected void progress() {
        if (energyStorage != null) {
            int energy = energyStorage.extractEnergy(TileRollingMachinePowered.ACTIVATION_POWER, true);
            if (energy >= TileRollingMachinePowered.ACTIVATION_POWER) {
                super.progress();
                energyStorage.extractEnergy(TileRollingMachinePowered.ACTIVATION_POWER, false);
            }
        } else
            super.progress();
    }

    @Override
    protected void findMoreStuff() {
        Collection<IInventoryObject> chests = cache.getAdjacentInventories();
        for (IInvSlot slot : InventoryIterator.getVanilla(craftMatrix)) {
            ItemStack stack = slot.getStack();
            if (stack != null && stack.isStackable() && stack.stackSize == 1) {
                ItemStack request = InvTools.removeOneItem(chests, StackFilters.of(stack));
                if (request != null) {
                    stack.stackSize++;
                    break;
                }
                if (stack.stackSize > 1)
                    break;
            }
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) > 64D)
            return false;
        GuiHandler.openGui(EnumGui.ROLLING_MACHINE_POWERED, player, worldObj, getPos());
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (energyStorage != null)
            energyStorage.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (energyStorage != null)
            energyStorage.readFromNBT(data);
    }

    @Nullable
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing side) {
        return energyStorage != null;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (energyStorage == null)
            return 0;
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        if (energyStorage == null)
            return 0;
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        if (energyStorage == null)
            return 0;
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, @Nullable ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nullable ItemStack stack, EnumFacing direction) {
        return index == SLOT_RESULT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, @Nullable ItemStack stack) {
        if (slot == SLOT_RESULT)
            return false;
        if (stack == null)
            return false;
        if (!stack.isStackable())
            return false;
        if (stack.getItem().hasContainerItem(stack))
            return false;
        return getStackInSlot(slot) != null;
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return inv.decrStackSize(slot, count);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inv.removeStackFromSlot(index);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUsableByPlayerHelper(this, player);
    }

    @Override
    public boolean hasWork() {
        return isWorking;
    }

    private void processActions() {
        setPaused(actions.stream().anyMatch(a -> a == Actions.PAUSE));
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        actions.add(action);
    }
}
