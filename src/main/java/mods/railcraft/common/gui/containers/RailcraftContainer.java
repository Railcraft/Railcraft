/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.RailcraftInputStream;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailcraftContainer extends Container {

    private final IInventory callback;
    private final List<Widget> widgets = new ArrayList<>();

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
        return callback == null || callback.isUsableByPlayer(entityplayer);
    }

    //TODO: test new parameters
    @Override
    public ItemStack slotClick(int slotId, int mouseButton, ClickType clickType, EntityPlayer player) {
        Slot slot = slotId < 0 ? null : inventorySlots.get(slotId);
        if (slot instanceof SlotRailcraft && ((SlotRailcraft) slot).isPhantom())
            return slotClickPhantom((SlotRailcraft) slot, mouseButton, clickType, player);
        return super.slotClick(slotId, mouseButton, clickType, player);
    }

    private ItemStack slotClickPhantom(SlotRailcraft slot, int mouseButton, ClickType clickType, EntityPlayer player) {
        ItemStack stack = ItemStack.EMPTY;

        if (mouseButton == 2) {
            if (slot.canAdjustPhantom())
                slot.putStack(ItemStack.EMPTY);
        } else if (mouseButton == 0 || mouseButton == 1) {
            InventoryPlayer playerInv = player.inventory;
            slot.onSlotChanged();
            ItemStack stackSlot = slot.getStack();
            ItemStack stackHeld = playerInv.getItemStack();

            if (stackSlot != ItemStack.EMPTY)
                stack = stackSlot.copy();

            if (stackSlot.isEmpty()) {
                if (stackHeld != ItemStack.EMPTY && slot.isItemValid(stackHeld))
                    fillPhantomSlot(slot, stackHeld, mouseButton);
            } else if (stackHeld.isEmpty()) {
                adjustPhantomSlot(slot, mouseButton, clickType);
                slot.onTake(player, playerInv.getItemStack());
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
        if (stackSlot.isEmpty())
            return;
        int stackSize;
        if (clickType == ClickType.QUICK_MOVE)
            stackSize = mouseButton == 0 ? (stackSlot.getCount() + 1) / 2 : stackSlot.getCount() * 2;
        else
            stackSize = mouseButton == 0 ? stackSlot.getCount() - 1 : stackSlot.getCount() + 1;

        if (stackSize > slot.getSlotStackLimit())
            stackSize = slot.getSlotStackLimit();

        stackSlot.setCount(stackSize);

        if (stackSlot.getCount() <= 0)
            slot.putStack(ItemStack.EMPTY);
    }

    private void fillPhantomSlot(SlotRailcraft slot, ItemStack stackHeld, int mouseButton) {
        if (!slot.canAdjustPhantom())
            return;
        int stackSize = mouseButton == 0 ? stackHeld.getCount() : 1;
        if (stackSize > slot.getSlotStackLimit())
            stackSize = slot.getSlotStackLimit();
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.setCount(stackSize);

        slot.putStack(phantomStack);
    }

    private boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
        boolean changed = false;
        if (stackToShift.isStackable())
            for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
                Slot slot = inventorySlots.get(slotIndex);
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot != ItemStack.EMPTY && InvTools.isItemEqual(stackInSlot, stackToShift)) {
                    int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
                    int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
                    if (resultingStackSize <= max) {
                        stackToShift.setCount(0);
                        stackInSlot.setCount(resultingStackSize);
                        slot.onSlotChanged();
                        changed = true;
                    } else if (stackInSlot.getCount() < max) {
                        stackToShift.shrink(max - stackInSlot.getCount());
                        stackInSlot.setCount(max);
                        slot.onSlotChanged();
                        changed = true;
                    }
                }
            }
        if (stackToShift.getCount() > 0)
            for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
                Slot slot = inventorySlots.get(slotIndex);
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot.isEmpty()) {
                    int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
                    stackInSlot = stackToShift.copy();
                    stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
                    stackToShift.shrink(stackInSlot.getCount());
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
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);
        int numSlots = inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            assert !stackInSlot.isEmpty();
            originalStack = stackInSlot.copy();
            if (!(slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots))) {
                if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
                    if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
                    if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9))
                        return ItemStack.EMPTY;
                } else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots))
                    return ItemStack.EMPTY;
            }
            slot.onSlotChange(stackInSlot, originalStack);
            if (stackInSlot.getCount() <= 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
            if (stackInSlot.getCount() == originalStack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(player, stackInSlot);
        }
        return originalStack;
    }

}
