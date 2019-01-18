/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.wrappers;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ChestWrapper extends InvWrapperBase {

    /**
     * Inventory object corresponding to double chest upper part
     */
    private final TileEntityChest firstChest;
    /**
     * Inventory object corresponding to double chest lower part
     */
    private TileEntityChest secondChest;

    public ChestWrapper(TileEntityChest tile) {
        super(tile, false);
        this.firstChest = tile;
        this.secondChest = findSecondChest();
    }

    private void checkChest() {
        this.secondChest = findSecondChest();
    }

    private TileEntityChest findSecondChest() {
        if (secondChest == null || secondChest.isInvalid()) {
            if (firstChest.adjacentChestXNeg != null) {
                secondChest = firstChest.adjacentChestXNeg;
            } else if (firstChest.adjacentChestXPos != null) {
                secondChest = firstChest.adjacentChestXPos;
            } else if (firstChest.adjacentChestZNeg != null) {
                secondChest = firstChest.adjacentChestZNeg;
            } else if (firstChest.adjacentChestZPos != null) {
                secondChest = firstChest.adjacentChestZPos;
            } else {
                secondChest = null;
            }
        }
        return secondChest;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        checkChest();
        int size = firstChest.getSizeInventory();
        if (secondChest != null) {
            size += secondChest.getSizeInventory();
        }
        return size;
    }

    /**
     * Return whether the given inventory is part of this large chest.
     */
    public boolean isPartOfLargeChest(IInventory inv) {
        return firstChest == inv || secondChest == inv;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        checkChest();
        if (slot >= firstChest.getSizeInventory() && secondChest != null) {
            return secondChest.getStackInSlot(slot - firstChest.getSizeInventory());
        }
        return firstChest.getStackInSlot(slot);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        checkChest();
        if (slot >= firstChest.getSizeInventory() && secondChest != null) {
            return secondChest.decrStackSize(slot - firstChest.getSizeInventory(), amount);
        }
        return firstChest.decrStackSize(slot, amount);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int slot, @Nullable ItemStack stack) {
        checkChest();
        if (slot >= firstChest.getSizeInventory() && secondChest != null) {
            secondChest.setInventorySlotContents(slot - firstChest.getSizeInventory(), stack);
        } else {
            firstChest.setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        ItemStack stack = getStackInSlot(slot);
        setInventorySlotContents(slot, InvTools.emptyStack());
        return stack;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return firstChest.getInventoryStackLimit();
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void markDirty() {
        firstChest.markDirty();
        if (secondChest != null) secondChest.markDirty();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return firstChest.isUsableByPlayer(player) && (secondChest == null || secondChest.isUsableByPlayer(player));
    }

    @Override
    public void openInventory(EntityPlayer player) {
        firstChest.openInventory(player);
        if (secondChest != null) secondChest.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        firstChest.closeInventory(player);
        if (secondChest != null) secondChest.closeInventory(player);
    }

}
