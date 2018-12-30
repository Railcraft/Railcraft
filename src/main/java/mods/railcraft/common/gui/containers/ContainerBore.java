/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.carts.IBoreHead;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.gui.slots.SlotFuel;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.gui.slots.SlotTrack;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBore extends RailcraftContainer {

    private final EntityTunnelBore bore;
    private int lastBurnTime;
    private int lastFuel;

    public ContainerBore(InventoryPlayer playerInv, EntityTunnelBore bore) {
        super(bore);
        this.bore = bore;

        addSlot(new SlotStackFilter(StackFilters.of(IBoreHead.class), bore, 0, 17, 36).setStackLimit(1));

        for (int i = 0; i < 6; i++) {
            addSlot(new SlotFuel(bore, i + 1, 62 + i * 18, 36));
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotStackFilter(StandardStackFilters.BALLAST, bore, i + 7, 8 + i * 18, 72));
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotTrack(bore, i + 16, 8 + i * 18, 108));
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
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, bore.getBurnTime());
        listener.sendWindowProperty(this, 1, bore.getFuel());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener var2 : listeners) {
            if (lastBurnTime != bore.getBurnTime()) {
                var2.sendWindowProperty(this, 0, bore.getBurnTime());
            }

            if (lastFuel != bore.getFuel()) {
                var2.sendWindowProperty(this, 1, bore.getFuel());
            }
        }

        this.lastBurnTime = bore.getBurnTime();
        this.lastFuel = bore.getFuel();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                bore.setBurnTime(value);
                break;
            case 1:
                bore.setFuel(value);
                break;
        }
    }
}
