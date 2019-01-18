/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.gui.slots.SlotUntouchable;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLocomotive extends RailcraftContainer {

    private final EntityLocomotive loco;
    private final InventoryPlayer playerInv;
    private LocoSpeed lastSpeed;
    private LocoMode lastMode;
    private int lastLockState;
    private final int guiHeight;
    public String ownerName;

    ContainerLocomotive(InventoryPlayer playerInv, EntityLocomotive loco, int guiHeight) {
        super(loco);
        this.loco = loco;
        this.playerInv = playerInv;
        this.guiHeight = guiHeight;
    }

    public static ContainerLocomotive make(InventoryPlayer playerInv, EntityLocomotive loco) {
        ContainerLocomotive con = new ContainerLocomotive(playerInv, loco, 161);
        con.init();
        return con;
    }

    public final void init() {
        defineSlotsAndWidgets();

        SlotRailcraft slotTicket = new SlotStackFilter(ItemTicket.FILTER, loco, loco.getSizeInventory() - 2, 116, guiHeight - 111).setStackLimit(1);
        slotTicket.setToolTips(ToolTip.buildToolTip("gui.locomotive.tips.slot.ticket"));
        addSlot(slotTicket);
        // TODO: make some way to clear this?
        addSlot(new SlotUntouchable(loco, loco.getSizeInventory() - 1, 134, guiHeight - 111));

        addPlayerSlots(playerInv, guiHeight);
    }

    public void defineSlotsAndWidgets() {

    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendWindowProperty(this, 10, loco.getSpeed().ordinal());
        listener.sendWindowProperty(this, 11, loco.getMode().ordinal());
        listener.sendWindowProperty(this, 12, loco.getLockController().getCurrentState());
        listener.sendWindowProperty(this, 13, PlayerPlugin.isOwnerOrOp(loco.getOwner(), playerInv.player) ? 1 : 0);

        String oName = loco.getOwner().getName();
        if (oName != null)
            PacketBuilder.instance().sendGuiStringPacket(listener, windowId, 0, oName);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener crafter : listeners) {
            LocoSpeed speed = loco.getSpeed();
            if (lastSpeed != speed)
                crafter.sendWindowProperty(this, 10, speed.ordinal());

            LocoMode mode = loco.getMode();
            if (lastMode != mode)
                crafter.sendWindowProperty(this, 11, mode.ordinal());

            int lock = loco.getLockController().getCurrentState();
            if (lastLockState != lock)
                crafter.sendWindowProperty(this, 12, lock);
        }

        this.lastSpeed = loco.getSpeed();
        this.lastMode = loco.getMode();
        this.lastLockState = loco.getLockController().getCurrentState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 10:
                loco.clientSpeed = LocoSpeed.VALUES[value];
                break;
            case 11:
                loco.clientMode = LocoMode.VALUES[value];
                break;
            case 12:
                loco.getLockController().setCurrentState(value);
                break;
            case 13:
                loco.clientCanLock = value == 1;
                break;
        }
    }

    @Override
    public void updateString(byte id, String data) {
        if (id == 0) {
            ownerName = data;
        }
    }

}
