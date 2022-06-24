/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.api.tracks.ITrackKitPowered;
import mods.railcraft.api.tracks.ITrackKitRouting;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitRouting extends TrackKitSecured implements ITrackKitPowered, ITrackKitRouting {

    private final InventoryAdvanced inv = new InventoryAdvanced(1);
    private boolean powered;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.ROUTING;
    }

    public IInventory getInventory() {
        return inv;
    }

    @Override
    public int getRenderState() {
        return powered ? 1 : 0;
    }

    @Override
    protected EnumGui getGUI() {
        return EnumGui.TRACK_ROUTING;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!isPowered())
            return;
        ItemStack stack = inv.getStackInSlot(0);
        if (cart instanceof IRoutableCart && !InvTools.isEmpty(stack))
            ((IRoutableCart) cart).setDestination(stack);
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        if (this.powered != powered) {
            this.powered = powered;
            sendUpdateToClient();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        inv.writeToNBT("inv", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        inv.readFromNBT("inv", data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (p != powered) {
            powered = p;
            markBlockNeedsUpdate();
        }
    }

    @Override
    public boolean setTicket(String dest, String title, GameProfile owner) {
        ItemStack ticket = RailcraftItems.TICKET.getStack();
        return ItemTicket.setTicketData(ticket, dest, title, owner);
    }

    @Override
    public void clearTicket() {
        inv.setInventorySlotContents(0, ItemStack.EMPTY);
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        InvTools.spewInventory(inv, getTile().getWorld(), getPos());
    }

}
