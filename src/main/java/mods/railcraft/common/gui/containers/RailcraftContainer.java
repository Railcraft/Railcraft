/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.RailcraftInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailcraftContainer extends Container {
    @Nullable
    private final IInventory callback;
    private final List<Widget> widgets = new ArrayList<Widget>();

    protected RailcraftContainer(IInventory inv) {
        this.callback = inv;
    }

    protected RailcraftContainer() {
        this.callback = null;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void addSlot(Slot slot) {
        addSlotToContainer(slot);
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
        widget.addToContainer(this);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
//        detectAndSendChanges();
    }

    @Override
    public final void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            sendUpdateToClient();
            sendWidgetsServerData();
        }
    }

    private void sendWidgetsServerData() {
        widgets.forEach(this::sendWidgetServerData);
    }

    private void sendWidgetServerData(Widget widget) {
        listeners.forEach(l -> PacketBuilder.instance().sendGuiWidgetPacket(l, windowId, widget));
    }

    public void sendUpdateToClient() {
    }

    @SideOnly(Side.CLIENT)
    public void updateString(byte id, String data) {
    }

    @SideOnly(Side.CLIENT)
    public void updateData(byte id, RailcraftInputStream data) {
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return callback == null || callback.isUseableByPlayer(entityplayer);
    }

    //TODO: test new parameters
    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int mouseButton, ClickType clickType, EntityPlayer player) {
        Slot slot = slotId < 0 ? null : inventorySlots.get(slotId);
        if (slot instanceof SlotRailcraft && ((SlotRailcraft) slot).isPhantom())
            return slotClickPhantom((SlotRailcraft) slot, mouseButton, clickType, player);
        return super.slotClick(slotId, mouseButton, clickType, player);
    }

    @Nullable
    private ItemStack slotClickPhantom(SlotRailcraft slot, int mouseButton, ClickType clickType, EntityPlayer player) {
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
                    fillPhantomSlot(slot, stackHeld, mouseButton);
            } else if (stackHeld == null) {
                adjustPhantomSlot(slot, mouseButton, clickType);
                slot.onPickupFromSlot(player, playerInv.getItemStack());
            } else if (slot.isItemValid(stackHeld))
                if (InvTools.isItemEqual(stackSlot, stackHeld))
                    adjustPhantomSlot(slot, mouseButton, clickType);
                else
                    fillPhantomSlot(slot, stackHeld, mouseButton);
        }
        return stack;
    }

    private void adjustPhantomSlot(SlotRailcraft slot, int mouseButton, ClickType clickType) {
        if (!slot.canAdjustPhantom())
            return;
        ItemStack stackSlot = slot.getStack();
        if (stackSlot == null)
            return;
        int stackSize;
        if (clickType == ClickType.QUICK_MOVE)
            stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
        else
            stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;

        if (stackSize > slot.getSlotStackLimit())
            stackSize = slot.getSlotStackLimit();

        stackSlot.stackSize = stackSize;

        if (stackSlot.stackSize <= 0)
            slot.putStack(null);
    }

    private void fillPhantomSlot(SlotRailcraft slot, ItemStack stackHeld, int mouseButton) {
        if (!slot.canAdjustPhantom())
            return;
        int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
        if (stackSize > slot.getSlotStackLimit())
            stackSize = slot.getSlotStackLimit();
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.stackSize = stackSize;

        slot.putStack(phantomStack);
    }

    private boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
        boolean changed = false;
        if (stackToShift.isStackable())
            for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
                Slot slot = inventorySlots.get(slotIndex);
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
                Slot slot = inventorySlots.get(slotIndex);
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
            Slot slot = inventorySlots.get(machineIndex);
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
        Slot slot = inventorySlots.get(slotIndex);
        int numSlots = inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            assert stackInSlot != null;
            originalStack = stackInSlot.copy();
            if (!(slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots))) {
                if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
                    if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots))
                        return null;
                } else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
                    if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9))
                        return null;
                } else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots))
                    return null;
            }
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
