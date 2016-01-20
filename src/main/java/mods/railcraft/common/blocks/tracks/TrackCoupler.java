/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackCoupler extends TrackBaseRailcraft implements ITrackPowered {

    private EntityMinecart taggedCart;
    private boolean powered = false;
    private boolean decouple = false;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.COUPLER;
    }

    @Override
    public IIcon getIcon() {
        int iconIndex = 0;
        if (!isPowered())
            iconIndex++;
        if (decouple)
            iconIndex += 2;
        return getIcon(iconIndex);
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getX(), getY(), getZ())) {
                decouple = !decouple;
                crowbar.onWhack(player, current, getX(), getY(), getZ());
                if (Game.isNotHost(getWorld()))
                    markBlockNeedsUpdate();
                else
                    sendUpdateToClient();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            ILinkageManager lm = CartTools.getLinkageManager(cart.worldObj);
            if (decouple) {
                lm.breakLinks(cart);
                LinkageManager.printDebug("Reason For Broken Link: Passed Decoupler Track.");
            } else {
                lm.createLink(taggedCart, cart);
                taggedCart = cart;
            }
        }
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
    public int getPowerPropagation() {
        return 8;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setBoolean("decouple", decouple);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        decouple = data.getBoolean("decouple");

        if (data.getInteger("trackId") == EnumTrack.DECOUPLER.ordinal())
            decouple = true;
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeBoolean(decouple);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean p = data.readBoolean();
        boolean d = data.readBoolean();

        boolean needsUpdate = false;
        if (p != powered) {
            powered = p;
            needsUpdate = true;
        }
        if (d != decouple) {
            decouple = d;
            needsUpdate = true;
        }
        if (needsUpdate)
            markBlockNeedsUpdate();
    }

}
