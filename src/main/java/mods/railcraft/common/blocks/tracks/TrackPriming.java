/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.carts.IExplosiveCart;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.network.IGuiReturnHandler;

public class TrackPriming extends TrackReinforced implements ITrackPowered, IGuiReturnHandler {

    private boolean powered = false;
    private short fuse = 80;
    public static final short MAX_FUSE = 500;
    public static final short MIN_FUSE = 0;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.PRIMING;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public IIcon getIcon() {
        if (!isPowered()) {
            return getIcon(1);
        }
        return getIcon(0);
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getX(), getY(), getZ())) {
                GuiHandler.openGui(EnumGui.TRACK_PRIMING, player, getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                crowbar.onWhack(player, current, getX(), getY(), getZ());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            if (cart instanceof IExplosiveCart) {
                IExplosiveCart tnt = (IExplosiveCart) cart;
                tnt.setFuse(fuse);
                tnt.setPrimed(true);
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
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeShort(fuse);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        powered = data.readBoolean();
        fuse = data.readShort();

        markBlockNeedsUpdate();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setShort("fuse", getFuse());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        setFuse(data.getShort("fuse"));
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeShort(fuse);
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        fuse = data.readShort();
    }

    public short getFuse() {
        return fuse;
    }

    public void setFuse(short f) {
        f = (short) Math.max(f, MIN_FUSE);
        f = (short) Math.min(f, MAX_FUSE);
        fuse = f;
    }
}
