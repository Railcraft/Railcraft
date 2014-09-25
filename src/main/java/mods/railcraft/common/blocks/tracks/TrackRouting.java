/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import com.mojang.authlib.GameProfile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.IRoutingTrack;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TrackRouting extends TrackSecured implements ITrackPowered, IRoutingTrack {

    private StandaloneInventory inv = new StandaloneInventory(1);
    private boolean powered = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.ROUTING;
    }

    public IInventory getInventory() {
        return inv;
    }

    @Override
    public IIcon getIcon() {
        if (isPowered())
            return getIcon(0);
        return getIcon(1);
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getX(), getY(), getZ())) {
                GuiHandler.openGui(EnumGui.TRACK_ROUTING, player, getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                crowbar.onWhack(player, current, getX(), getY(), getZ());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (!isPowered())
            return;
        if (inv.getStackInSlot(0) == null)
            return;
        if (cart instanceof IRoutableCart)
            ((IRoutableCart) cart).setDestination(inv.getStackInSlot(0));
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
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
        ItemStack ticket = ItemTicket.getTicket();
        return ItemTicket.setTicketData(ticket, dest, title, owner);
    }

    @Override
    public void clearTicket() {
        inv.setInventorySlotContents(0, null);
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        InvTools.dropInventory(inv, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
    }

}
