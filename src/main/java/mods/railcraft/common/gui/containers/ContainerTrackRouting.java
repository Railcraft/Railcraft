/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitRouting;
import mods.railcraft.common.gui.slots.SlotSecure;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemTicketGold;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerTrackRouting extends ContainerTrackKit<TrackKitRouting> {

    private final InventoryPlayer playerInv;
    private int lastLockState;
    public String ownerName;
    public boolean canLock;
    private final SlotSecure slotTicket;

    public ContainerTrackRouting(InventoryPlayer playerInv, TileTrackOutfitted tile) {
        super(tile);
        this.playerInv = playerInv;

        slotTicket = new SlotSecure(kit.getInventory(), 0, 44, 24);
        slotTicket.setFilter(ItemTicketGold.FILTER);
        slotTicket.setToolTips(ToolTip.buildToolTip("routing.track.tips.slot"));
        addSlot(slotTicket);

        addPlayerSlots(playerInv, 140);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendWindowProperty(this, 0, kit.getLockController().getCurrentState());

        canLock = PlayerPlugin.isOwnerOrOp(kit.getOwner(), playerInv.player.getGameProfile());
        slotTicket.locked = kit.isSecure() && !canLock;
        listener.sendWindowProperty(this, 2, canLock ? 1 : 0);

        String username = kit.getOwner().getName();
        if (username != null)
            PacketBuilder.instance().sendGuiStringPacket(listener, windowId, 0, username);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener var2 : listeners) {
            int lock = kit.getLockController().getCurrentState();
            if (lastLockState != lock)
                var2.sendWindowProperty(this, 0, lock);
        }

        this.lastLockState = kit.getLockController().getCurrentState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {

        switch (id) {
            case 0:
                kit.getLockController().setCurrentState(value);
                break;
            case 2:
                canLock = value == 1;
                break;
        }
        slotTicket.locked = kit.isSecure() && !canLock;
    }

    @Override
    public void updateString(byte id, String data) {
        if (id == 0) {
            ownerName = data;
        }
    }

}
