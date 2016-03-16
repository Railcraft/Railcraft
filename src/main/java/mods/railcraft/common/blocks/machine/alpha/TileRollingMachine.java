/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import buildcraft.api.statements.IActionExternal;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.inventory.filters.ArrayStackFilter;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TileRollingMachine extends TileMachineBase implements IEnergyHandler, ISidedInventory, IHasWork {

    private final static int PROCESS_TIME = 100;
    private final static int ACTIVATION_POWER = 50;
    private final static int MAX_RECEIVE = 1000;
    private final static int MAX_ENERGY = ACTIVATION_POWER * PROCESS_TIME;
    private final static int SLOT_RESULT = 0;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 10);
    private final InventoryCrafting craftMatrix = new InventoryCrafting(new RollingContainer(), 3, 3);
    private final StandaloneInventory invResult = new StandaloneInventory(1, "invResult", (IInventory) this);
    private final IInventory inv = InventoryConcatenator.make().add(invResult).add(craftMatrix);
    private EnergyStorage energyStorage;
    public boolean useLast;
    private boolean isWorking, paused;
    private ItemStack currentReceipe;
    private int progress;
    private final AdjacentInventoryCache cache = new AdjacentInventoryCache(this, tileCache, null, InventorySorter.SIZE_DESCENDING);
    private final Set<IActionExternal> actions = new HashSet<IActionExternal>();

    private static class RollingContainer extends Container {

        @Override
        public boolean canInteractWith(EntityPlayer entityplayer) {
            return true;
        }

    }

    public TileRollingMachine() {
        if (RailcraftConfig.machinesRequirePower())
            energyStorage = new EnergyStorage(MAX_ENERGY, MAX_RECEIVE);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.ROLLING_MACHINE;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("progress", progress);

        if (energyStorage != null)
            energyStorage.writeToNBT(data);

        invResult.writeToNBT("invResult", data);
        InvTools.writeInvToNBT(craftMatrix, "Crafting", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        progress = data.getInteger("progress");

        if (energyStorage != null)
            energyStorage.readFromNBT(data);

        invResult.readFromNBT("invResult", data);
        InvTools.readInvFromNBT(craftMatrix, "Crafting", data);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) > 64D)
            return false;
        GuiHandler.openGui(EnumGui.ROLLING_MACHINE, player, worldObj, getPos());
        return true;
    }

    @Override
    public void markDirty() {
        craftMatrix.markDirty();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        InvTools.dropInventory(inv, worldObj, getPos());
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public int getProgressScaled(int i) {
        return (progress * i) / PROCESS_TIME;
    }

    public InventoryCrafting getCraftMatrix() {
        return craftMatrix;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(worldObj))
            return;

        balanceSlots();

        if (clock % 16 == 0)
            processActions();

        if (paused)
            return;

        if (clock % 8 == 0) {
            currentReceipe = RollingMachineCraftingManager.instance().findMatchingRecipe(craftMatrix, worldObj);
            if (currentReceipe != null)
                findMoreStuff();
        }

        if (currentReceipe != null && canMakeMore())
            if (progress >= PROCESS_TIME) {
                isWorking = false;
                if (InvTools.isRoomForStack(currentReceipe, invResult)) {
                    currentReceipe = RollingMachineCraftingManager.instance().findMatchingRecipe(craftMatrix, worldObj);
                    if (currentReceipe != null) {
                        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                            craftMatrix.decrStackSize(i, 1);
                        }
                        InvTools.moveItemStack(currentReceipe, invResult);
                    }
                    useLast = false;
                    progress = 0;
                }
            } else {
                isWorking = true;
                if (energyStorage != null) {
                    int energy = energyStorage.extractEnergy(ACTIVATION_POWER, true);
                    if (energy >= ACTIVATION_POWER) {
                        progress++;
                        energyStorage.extractEnergy(ACTIVATION_POWER, false);
                    }
                } else
                    progress++;
            }
        else {
            progress = 0;
            isWorking = false;
        }
    }

    /**
     * Evenly redistributes items between all the slots.
     */
    private void balanceSlots() {
        for (IInvSlot slotA : InventoryIterator.getIterable(craftMatrix)) {
            ItemStack stackA = slotA.getStackInSlot();
            if (stackA == null)
                continue;
            for (IInvSlot slotB : InventoryIterator.getIterable(craftMatrix)) {
                if (slotA.getIndex() == slotB.getIndex())
                    continue;
                ItemStack stackB = slotB.getStackInSlot();
                if (stackB == null)
                    continue;
                if (InvTools.isItemEqual(stackA, stackB))
                    if (stackA.stackSize > stackB.stackSize + 1) {
                        stackA.stackSize--;
                        stackB.stackSize++;
                        return;
                    }
            }
        }
    }

    private void findMoreStuff() {
        Collection<IInventory> chests = cache.getAdjacentInventories();
        for (IInvSlot slot : InventoryIterator.getIterable(craftMatrix)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null && stack.isStackable() && stack.stackSize == 1) {
                ItemStack request = InvTools.removeOneItem(chests, new ArrayStackFilter(stack));
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
    public boolean hasWork() {
        return isWorking;
    }

    public void setPaused(boolean p) {
        paused = p;
    }

    private void processActions() {
        paused = false;
        for (IActionExternal action : actions) {
            if (action == Actions.PAUSE)
                paused = true;
        }
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        actions.add(action);
    }

    public boolean canMakeMore() {
        if (RollingMachineCraftingManager.instance().findMatchingRecipe(craftMatrix, worldObj) == null)
            return false;
        if (useLast)
            return true;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack slot = craftMatrix.getStackInSlot(i);
            if (slot != null && slot.stackSize <= 1)
                return false;
        }
        return true;
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
        return index == SLOT_RESULT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
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
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUsableByPlayerHelper(this, player);
    }

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
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
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
}
