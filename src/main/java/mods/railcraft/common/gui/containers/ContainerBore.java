/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.carts.IBoreHead;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBore extends RailcraftContainer {
    public static final int GUI_HEIGHT = 222;

    private final EntityTunnelBore bore;
    private int lastBurnTime;
    private int lastFuel;

    public ContainerBore(InventoryPlayer playerInv, EntityTunnelBore bore) {
        super(bore);
        this.bore = bore;

        addSlot(new SlotStackFilter(StackFilters.of(IBoreHead.class), bore, 0, 17, 36).setStackLimit(1));

        // Fuel
        for (int i = 0; i < 6; i++) {
            addSlot(new SlotRailcraft(bore, i + 1, 62 + i * 18, 36));
        }

        // Ballast
        for (int i = 0; i < 9; i++) {
            addSlot(new SlotRailcraft(bore, i + 7, 8 + i * 18, 72));
        }

        // Track
        for (int i = 0; i < 9; i++) {
            addSlot(new SlotRailcraft(bore, i + 16, 8 + i * 18, 108));
        }

        addPlayerSlots(playerInv, GUI_HEIGHT);
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
