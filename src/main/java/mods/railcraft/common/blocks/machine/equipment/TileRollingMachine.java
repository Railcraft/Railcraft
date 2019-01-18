/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IRollingMachineCrafter;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.InventoryConcatenator;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Optional;

import static mods.railcraft.common.util.inventory.InvTools.*;

public abstract class TileRollingMachine extends TileMachineBase {

    public static final int SLOT_RESULT = 0;
    public static final int[] SLOTS = InvTools.buildSlotArray(0, 10);
    private final RollingContainer matrixListener = new RollingContainer();
    protected final InventoryCrafting craftMatrix = new InventoryCrafting(matrixListener, 3, 3);

    protected final InventoryAdvanced invResult = new InventoryAdvanced(1).callbackTile(this);
    protected final IInventory inv = InventoryConcatenator.make().add(invResult).add(craftMatrix);
    public boolean useLast;
    protected boolean isWorking, paused;
    private Optional<IRollingMachineCrafter.IRollingRecipe> currentRecipe = Optional.empty();
    private int progress;
    private int processTime = IRollingMachineCrafter.DEFAULT_PROCESS_TIME;

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
        InvTools.spewInventory(inv, world, getPos());
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressScaled(int i) {
        return (progress * i) / processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = Math.max(processTime, 1);
    }

    public int getProcessTime() {
        return processTime;
    }

    public InventoryCrafting getCraftMatrix(Container listener) {
        matrixListener.listener = listener;
        return craftMatrix;
    }

    public InventoryAdvanced getInvResult() {
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

        if (Game.isClient(world))
            return;

        balanceSlots();

        if (paused)
            return;

        if (clock % 8 == 0) {
            currentRecipe = Crafters.rollingMachine().getRecipe(craftMatrix, world);
            if (currentRecipe.isPresent()) {
                processTime = currentRecipe.get().getTickTime();
                findMoreStuff();
            } else {
                processTime = IRollingMachineCrafter.DEFAULT_PROCESS_TIME;
            }
        }

        if (currentRecipe.isPresent() && canMakeMore()) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            IRollingMachineCrafter.IRollingRecipe r = currentRecipe.get();
            if (progress >= r.getTickTime()) {
                isWorking = false;
                ItemStack result = r.getCraftingResult(craftMatrix);
                if (invResult.canFit(result)) {
                    InventoryIterator.get(craftMatrix).stream().forEach(IInvSlot::decreaseStack);
                    invResult.addStack(result);
                    useLast = false;
                    progress = 0;
                    currentRecipe = Optional.empty();
                }
            } else {
                isWorking = true;
                progress();
            }
        } else {
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
        for (IInvSlot slotA : InventoryIterator.get(craftMatrix)) {
            ItemStack stackA = slotA.getStack();
            if (InvTools.isEmpty(stackA))
                continue;
            for (IInvSlot slotB : InventoryIterator.get(craftMatrix)) {
                if (slotA.getIndex() == slotB.getIndex())
                    continue;
                ItemStack stackB = slotB.getStack();
                if (InvTools.isEmpty(stackB))
                    continue;
                if (InvTools.isItemEqual(stackA, stackB))
                    if (sizeOf(stackA) > sizeOf(stackB) + 1) {
                        dec(stackA);
                        inc(stackB);
                        return;
                    }
            }
        }
    }

    protected void findMoreStuff() {
    }

    public void setPaused(boolean p) {
        paused = p;
    }

    public boolean canMakeMore() {
        if (!Crafters.rollingMachine().getRecipe(craftMatrix, world).isPresent())
            return false;
        if (useLast)
            return true;
        return InventoryIterator.get(craftMatrix).streamStacks()
                .filter(InvTools::nonEmpty)
                .anyMatch(s -> sizeOf(s) > 1);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    private static class RollingContainer extends Container {

        Container listener;

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
