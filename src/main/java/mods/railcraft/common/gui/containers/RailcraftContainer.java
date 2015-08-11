/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import mods.railcraft.common.gui.widgets.Widget;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailcraftContainer extends Container {

    private final IInventory callback;
    private final List<Widget> widgets = new ArrayList<Widget>();

    public RailcraftContainer(IInventory inv) {
        this.callback = inv;
    }

    public RailcraftContainer() {
        this.callback = null;
    }

    public List<Widget> getElements() {
        return widgets;
    }

    public void addSlot(Slot slot) {
        addSlotToContainer(slot);
    }

    public void addWidget(Widget widget) {
        widget.addToContainer(this);
        widgets.add(widget);
    }

    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);
        for (Widget widget : widgets) {
            widget.initWidget(player);
        }
    }

    @Override
    public final void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            sendUpdateToClient();
            for (Widget widget : widgets) {
                for (ICrafting player : (List<ICrafting>) crafters) {
                    widget.updateWidget(player);
                }
            }
        }
    }


    public void sendUpdateToClient() {
    }

    public void sendWidgetDataToClient(Widget widget, ICrafting player, byte[] data) {
        PacketBuilder.instance().sendGuiWidgetPacket((EntityPlayerMP) player, windowId, widgets.indexOf(widget), data);
    }

    public void handleWidgetClientData(int widgetId, DataInputStream data) throws IOException {
        widgets.get(widgetId).handleClientPacketData(data);
    }

    @SideOnly(Side.CLIENT)
    public void updateString(byte id, String data) {
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        if (callback == null) return true;
        return callback.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack slotClick(int slotNum, int mouseButton, int modifier, EntityPlayer player) {
        Slot slot = slotNum < 0 ? null : (Slot) this.inventorySlots.get(slotNum);
        if (slot instanceof SlotRailcraft && ((SlotRailcraft) slot).isPhantom())
            return slotClickPhantom((SlotRailcraft) slot, mouseButton, modifier, player);
        return super.slotClick(slotNum, mouseButton, modifier, player);
    }

    private ItemStack slotClickPhantom(SlotRailcraft slot, int mouseButton, int modifier, EntityPlayer player) {
        ItemStack stack = null;

        if (mouseButton == 2) {
            if (slot.canAdjustPhantom())
                slot.putStack(null);
        } else if (mouseButton == 0 || mouseButton == 1) {
            InventoryPlayer playerInv = player.inventory;
            slot.onSlotChanged();
            ItemStack stackSlot = slot.getStack();
            ItemStack stackHeld = playerInv.getItemStack();

            if (stackSlot != null)
                stack = stackSlot.copy();

            if (stackSlot == null) {
                if (stackHeld != null && slot.isItemValid(stackHeld))
                    fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
            } else if (stackHeld == null) {
                adjustPhantomSlot(slot, mouseButton, modifier);
                slot.onPickupFromSlot(player, playerInv.getItemStack());
            } else if (slot.isItemValid(stackHeld))
                if (InvTools.isItemEqual(stackSlot, stackHeld))
                    adjustPhantomSlot(slot, mouseButton, modifier);
                else
                    fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
        }
        return stack;
    }

    protected void adjustPhantomSlot(SlotRailcraft slot, int mouseButton, int modifier) {
        if (!slot.canAdjustPhantom())
            return;
        ItemStack stackSlot = slot.getStack();
        int stackSize;
        if (modifier == 1)
            stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
        else
            stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;

        if (stackSize > slot.getSlotStackLimit())
            stackSize = slot.getSlotStackLimit();

        stackSlot.stackSize = stackSize;

        if (stackSlot.stackSize <= 0)
            slot.putStack((ItemStack) null);
    }

    protected void fillPhantomSlot(SlotRailcraft slot, ItemStack stackHeld, int mouseButton, int modifier) {
        if (!slot.canAdjustPhantom())
            return;
        int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
        if (stackSize > slot.getSlotStackLimit())
            stackSize = slot.getSlotStackLimit();
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.stackSize = stackSize;

        slot.putStack(phantomStack);
    }

    protected boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
        boolean changed = false;
        if (stackToShift.isStackable())
            for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
                Slot slot = (Slot) inventorySlots.get(slotIndex);
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot != null && InvTools.isItemEqual(stackInSlot, stackToShift)) {
                    int resultingStackSize = stackInSlot.stackSize + stackToShift.stackSize;
                    int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
                    if (resultingStackSize <= max) {
                        stackToShift.stackSize = 0;
                        stackInSlot.stackSize = resultingStackSize;
                        slot.onSlotChanged();
                        changed = true;
                    } else if (stackInSlot.stackSize < max) {
                        stackToShift.stackSize -= max - stackInSlot.stackSize;
                        stackInSlot.stackSize = max;
                        slot.onSlotChanged();
                        changed = true;
                    }
                }
            }
        if (stackToShift.stackSize > 0)
            for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
                Slot slot = (Slot) inventorySlots.get(slotIndex);
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot == null) {
                    int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
                    stackInSlot = stackToShift.copy();
                    stackInSlot.stackSize = Math.min(stackToShift.stackSize, max);
                    stackToShift.stackSize -= stackInSlot.stackSize;
                    slot.putStack(stackInSlot);
                    slot.onSlotChanged();
                    changed = true;
                }
            }
        return changed;
    }

    private boolean tryShiftItem(ItemStack stackToShift, int numSlots) {
        for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
            Slot slot = (Slot) inventorySlots.get(machineIndex);
            if (slot instanceof SlotRailcraft) {
                SlotRailcraft slotRailcraft = (SlotRailcraft) slot;
                if (slotRailcraft.isPhantom())
                    continue;
                if (!slotRailcraft.canShift())
                    continue;
            }
            if (!slot.isItemValid(stackToShift))
                continue;
            if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1))
                return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack originalStack = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        int numSlots = inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();
            if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots)) {
                // NOOP
            } else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
                if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots))
                    return null;
            } else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
                if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9))
                    return null;
            } else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots))
                return null;
            slot.onSlotChange(stackInSlot, originalStack);
            if (stackInSlot.stackSize <= 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
            if (stackInSlot.stackSize == originalStack.stackSize)
                return null;
            slot.onPickupFromSlot(player, stackInSlot);
        }
        return originalStack;
    }

}
