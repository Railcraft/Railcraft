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
import java.util.*;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryConcatenator;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.inventory.filters.ArrayStackFilter;
import mods.railcraft.common.util.inventory.wrappers.IInvSlot;
import mods.railcraft.common.util.inventory.wrappers.InventoryIterator;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.ISidedInventory;

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
    private final AdjacentInventoryCache cache = new AdjacentInventoryCache(this, tileCache, null, InventorySorter.SIZE_DECENDING);
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
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(side);
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
        if (player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) > 64D)
            return false;
        GuiHandler.openGui(EnumGui.ROLLING_MACHINE, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void markDirty() {
        craftMatrix.markDirty();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        InvTools.dropInventory(inv, worldObj, xCoord, yCoord, zCoord);
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
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(worldObj))
            return;

        balanceSlots();

        if (clock % 16 == 0)
            processActions();

        if (paused)
            return;

        if (clock % 8 == 0) {
            currentReceipe = RollingMachineCraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
            if (currentReceipe != null)
                findMoreStuff();
        }

        if (currentReceipe != null && canMakeMore())
            if (progress >= PROCESS_TIME) {
                isWorking = false;
                if (InvTools.isRoomForStack(currentReceipe, invResult)) {
                    currentReceipe = RollingMachineCraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
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
        if (RollingMachineCraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj) == null)
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
    public int[] getAccessibleSlotsFromSide(int var1) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_RESULT;
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
        if (getStackInSlot(slot) == null)
            return false;
        return true;
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
    public ItemStack getStackInSlotOnClosing(int slot) {
        return inv.getStackInSlotOnClosing(slot);
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUseableByPlayerHelper(this, player);
    }

    @Override
    public String getInventoryName() {
        return getName();
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection side) {
        return energyStorage != null;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (energyStorage == null)
            return 0;
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (energyStorage == null)
            return 0;
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (energyStorage == null)
            return 0;
        return energyStorage.getMaxEnergyStored();
    }

}
