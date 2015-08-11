/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.gui.slots.SlotBallast;
import mods.railcraft.common.gui.slots.SlotBore;
import mods.railcraft.common.gui.slots.SlotFuel;
import mods.railcraft.common.gui.slots.SlotTrack;

public class ContainerBore extends RailcraftContainer {

    private EntityTunnelBore bore;
    private Slot ballast;
    private Slot fuel;
    private Slot track;
    private int lastBurnTime;
    private int lastFuel;

    public ContainerBore(InventoryPlayer playerInv, EntityTunnelBore bore) {
        super(bore);
        this.bore = bore;

        addSlot(new SlotBore(bore, 0, 17, 36));

        for (int i = 0; i < 6; i++) {
            addSlot(fuel = new SlotFuel(bore, i + 1, 62 + i * 18, 36));
        }

        for (int i = 0; i < 9; i++) {
            addSlot(ballast = new SlotBallast(bore, i + 7, 8 + i * 18, 72));
        }

        for (int i = 0; i < 9; i++) {
            addSlot(track = new SlotTrack(bore, i + 16, 8 + i * 18, 108));
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInv, k + i * 9 + 9, 8 + k * 18, 140 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInv, i, 8 + i * 18, 198));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, bore.getBurnTime());
        icrafting.sendProgressBarUpdate(this, 1, bore.getFuel());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            if (this.lastBurnTime != this.bore.getBurnTime()) {
                var2.sendProgressBarUpdate(this, 0, this.bore.getBurnTime());
            }

            if (this.lastFuel != this.bore.getFuel()) {
                var2.sendProgressBarUpdate(this, 1, this.bore.getFuel());
            }
        }

        this.lastBurnTime = this.bore.getBurnTime();
        this.lastFuel = this.bore.getFuel();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                this.bore.setBurnTime(value);
                break;
            case 1:
                this.bore.setFuel(value);
                break;
        }
    }

//    @Override
//    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
//        ItemStack stackCopy = null;
//        Slot slot = (Slot) inventorySlots.get(i);
//        if (slot != null && slot.getHasStack()) {
//            ItemStack stack = slot.getStack();
//            stackCopy = stack.copy();
//            if (i < 25) {
//                if (!mergeItemStack(stack, 25, inventorySlots.size(), true)) {
//                    return null;
//                }
//            } else {
//                if (SlotBore.canPlaceItem(stack)) {
//                    if (!mergeItemStack(stack, 0, 1, false)) {
//                        return null;
//                    }
//                } else if (fuel.isItemValid(stack)) {
//                    if (!mergeItemStack(stack, 1, 7, false)) {
//                        return null;
//                    }
//                } else if (ballast.isItemValid(stack)) {
//                    if (!mergeItemStack(stack, 7, 16, false)) {
//                        return null;
//                    }
//                } else if (track.isItemValid(stack)) {
//                    if (!mergeItemStack(stack, 16, 25, false)) {
//                        return null;
//                    }
//                } else {
//                    return null;
//                }
//            }
//            if (stack.stackSize == 0) {
//                slot.putStack(null);
//            } else {
//                slot.onSlotChanged();
//            }
//        }
//        return stackCopy;
//    }
}
