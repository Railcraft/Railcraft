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
import mods.railcraft.common.blocks.tracks.TrackRouting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.gui.slots.*;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemTicketGold;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerTrackRouting extends RailcraftContainer {

    private final TrackRouting track;
    private final InventoryPlayer playerInv;
    private int lastLockState;
    public String ownerName;
    public boolean canLock = false;
    private SlotSecure slotTicket;

    public ContainerTrackRouting(InventoryPlayer playerInv, TrackRouting track) {
        super(track.getInventory());
        this.track = track;
        this.playerInv = playerInv;

        slotTicket = new SlotSecure(ItemTicketGold.FILTER, track.getInventory(), 0, 44, 24);
        slotTicket.setToolTips(ToolTip.buildToolTip("routing.track.tip.slot"));
        addSlot(slotTicket);

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInv, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(playerInv, j, 8 + j * 18, 116));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);

        icrafting.sendProgressBarUpdate(this, 0, track.getLockController().getCurrentState());

        canLock = PlayerPlugin.isOwnerOrOp(track.getOwner(), playerInv.player.getGameProfile());
        slotTicket.locked = track.isSecure() && !canLock;
        icrafting.sendProgressBarUpdate(this, 2, canLock ? 1 : 0);

        String username = track.getOwner().getName();
        if (username != null)
            PacketBuilder.instance().sendGuiStringPacket((EntityPlayerMP) icrafting, windowId, 0, username);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            int lock = track.getLockController().getCurrentState();
            if (this.lastLockState != lock)
                var2.sendProgressBarUpdate(this, 0, lock);
        }

        this.lastLockState = track.getLockController().getCurrentState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {

        switch (id) {
            case 0:
                track.getLockController().setCurrentState(value);
                break;
            case 2:
                canLock = value == 1;
                break;
        }
        slotTicket.locked = track.isSecure() && !canLock;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateString(byte id, String data) {
        switch (id) {
            case 0:
                ownerName = data;
                break;
        }
    }

}
