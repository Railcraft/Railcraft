/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;

public abstract class TileRollingMachine extends TileMachineBase {

    public static final int PROCESS_TIME = 100;
    public static final int SLOT_RESULT = 0;
    public static final int[] SLOTS = InvTools.buildSlotArray(0, 10);
    private final RollingContainer matrixListener = new RollingContainer();
    protected final InventoryCrafting craftMatrix = new InventoryCrafting(matrixListener, 3, 3);

    protected final StandaloneInventory invResult = new StandaloneInventory(1, "invResult", this);
    protected final IInventory inv = InventoryConcatenator.make().add(invResult).add(craftMatrix);
    private final AdjacentInventoryCache cache = new AdjacentInventoryCache(tileCache, null, InventorySorter.SIZE_DESCENDING);
    public boolean useLast;
    protected boolean isWorking, paused;
    private ItemStack currentRecipe;
    private int progress;

    protected TileRollingMachine() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("progress", progress);

        invResult.writeToNBT("invResult", data);
        InvTools.writeInvToNBT(craftMatrix, "Crafting", data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        progress = data.getInteger("progress");

        invResult.readFromNBT("invResult", data);
        InvTools.readInvFromNBT(craftMatrix, "Crafting", data);
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressScaled(int i) {
        return (progress * i) / PROCESS_TIME;
    }

    public InventoryCrafting getCraftMatrix(Container listener) {
        matrixListener.listener = listener;
        return craftMatrix;
    }

    public StandaloneInventory getInvResult() {
        return invResult;
    }

    public IInventory getInventory() {
        return inv;
    }

    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(worldObj))
            return;

        balanceSlots();

        if (paused)
            return;

        if (clock % 8 == 0) {
            currentRecipe = RollingMachineCraftingManager.instance().findMatchingRecipe(craftMatrix, worldObj);
            if (currentRecipe != null)
                findMoreStuff();
        }

        if (currentRecipe != null && canMakeMore())
            if (progress >= PROCESS_TIME) {
                isWorking = false;
                if (InvTools.isRoomForStack(currentRecipe, invResult)) {
                    currentRecipe = RollingMachineCraftingManager.instance().findMatchingRecipe(craftMatrix, worldObj);
                    if (currentRecipe != null) {
                        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                            craftMatrix.decrStackSize(i, 1);
                        }
                        InvTools.moveItemStack(currentRecipe, invResult);
                    }
                    useLast = false;
                    progress = 0;
                }
            } else {
                isWorking = true;
                progress();
            }
        else {
            progress = 0;
            isWorking = false;
        }
    }

    protected void progress() {
        progress++;
    }

    /**
     * Evenly redistributes items between all the slots.
     */
    private void balanceSlots() {
        for (IInvSlot slotA : InventoryIterator.getVanilla(craftMatrix)) {
            ItemStack stackA = slotA.getStack();
            if (stackA == null)
                continue;
            for (IInvSlot slotB : InventoryIterator.getVanilla(craftMatrix)) {
                if (slotA.getIndex() == slotB.getIndex())
                    continue;
                ItemStack stackB = slotB.getStack();
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

    public void setPaused(boolean p) {
        paused = p;
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
    public boolean hasCustomName() {
        return false;
    }

    private static class RollingContainer extends Container {

        public Container listener;

        @Override
        public boolean canInteractWith(EntityPlayer entityplayer) {
            return true;
        }

        @Override
        public void onCraftMatrixChanged(IInventory inventoryIn) {
            if (listener != null)
                listener.onCraftMatrixChanged(inventoryIn);
        }
    }
}
