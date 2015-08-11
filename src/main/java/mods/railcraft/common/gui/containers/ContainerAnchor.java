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
import mods.railcraft.api.core.items.IStackFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.util.misc.IAnchor;

public class ContainerAnchor extends RailcraftContainer {

    public final IAnchor anchor;
    public short minutesRemaining;
    private short prevMinutesRemaining;

    public ContainerAnchor(InventoryPlayer inventoryplayer, IAnchor a) {
        super(a);
        this.anchor = a;
        addSlot(new SlotStackFilter(new IStackFilter() {
            @Override
            public boolean matches(ItemStack stack) {
                return anchor.getFuelMap().containsKey(stack);
            }

        }, anchor, 0, 60, 24));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, getMinutesRemaining(anchor.getAnchorFuel()));
    }

    private short getMinutesRemaining(long fuel) {
        return (short) Math.ceil((double) fuel / RailcraftConstants.TICKS_PER_MIN);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        short mins = getMinutesRemaining(anchor.getAnchorFuel());

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            if (this.prevMinutesRemaining != mins)
                var2.sendProgressBarUpdate(this, 0, mins);
        }

        this.prevMinutesRemaining = mins;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0)
            minutesRemaining = (short) value;
    }

}
