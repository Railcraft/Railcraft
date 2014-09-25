/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChestWrapper implements IInventory {

    /**
     * Inventory object corresponding to double chest upper part
     */
    private final TileEntityChest upperChest;
    /**
     * Inventory object corresponding to double chest lower part
     */
    private TileEntityChest lowerChest;

    public ChestWrapper(TileEntityChest tile) {
        this.upperChest = tile;
        checkChest();
    }

    private void checkChest() {
        if (lowerChest == null || lowerChest.isInvalid()) {
            if (upperChest.adjacentChestXNeg != null) {
                lowerChest = upperChest.adjacentChestXNeg;
            } else if (upperChest.adjacentChestXPos != null) {
                lowerChest = upperChest.adjacentChestXPos;
            } else if (upperChest.adjacentChestZNeg != null) {
                lowerChest = upperChest.adjacentChestZNeg;
            } else if (upperChest.adjacentChestZPos != null) {
                lowerChest = upperChest.adjacentChestZPos;
            } else {
                lowerChest = null;
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        checkChest();
        int size = upperChest.getSizeInventory();
        if (lowerChest != null) {
            size += lowerChest.getSizeInventory();
        }
        return size;
    }

    /**
     * Return whether the given inventory is part of this large chest.
     */
    public boolean isPartOfLargeChest(IInventory inv) {
        return this.upperChest == inv || this.lowerChest == inv;
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInventoryName() {
        return "";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized
     * name, and translated into the player's language. Otherwise it will be
     * used directly.
     */
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        checkChest();
        if (slot >= upperChest.getSizeInventory() && lowerChest != null) {
            return lowerChest.getStackInSlot(slot - upperChest.getSizeInventory());
        }
        return upperChest.getStackInSlot(slot);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int amout) {
        checkChest();
        if (slot >= upperChest.getSizeInventory() && lowerChest != null) {
            return lowerChest.decrStackSize(slot - upperChest.getSizeInventory(), amout);
        }
        return upperChest.decrStackSize(slot, amout);
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        checkChest();
        if (slot >= upperChest.getSizeInventory() && lowerChest != null) {
            return lowerChest.getStackInSlotOnClosing(slot - upperChest.getSizeInventory());
        }
        return upperChest.getStackInSlotOnClosing(slot);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        checkChest();
        if (slot >= this.upperChest.getSizeInventory() && lowerChest != null) {
            lowerChest.setInventorySlotContents(slot - upperChest.getSizeInventory(), stack);
        } else {
            upperChest.setInventorySlotContents(slot, stack);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return this.upperChest.getInventoryStackLimit();
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void markDirty() {
        this.upperChest.markDirty();
        if (lowerChest != null) lowerChest.markDirty();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.upperChest.isUseableByPlayer(player) && (lowerChest == null || lowerChest.isUseableByPlayer(player));
    }

    @Override
    public void openInventory() {
        this.upperChest.openInventory();
        if (lowerChest != null) lowerChest.openInventory();
    }

    @Override
    public void closeInventory() {
        this.upperChest.closeInventory();
        if (lowerChest != null) lowerChest.closeInventory();
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

}
