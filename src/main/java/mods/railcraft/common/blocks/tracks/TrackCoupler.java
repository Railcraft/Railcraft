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
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

public class TrackCoupler extends TrackBaseRailcraft implements ITrackPowered {
    private EntityMinecart taggedCart;
    private boolean powered = false;
    private Mode mode = Mode.COUPLER;

    enum Mode {
        COUPLER(8) {
            @Override
            public void onMinecartPass(TrackCoupler track, EntityMinecart cart) {
                CartTools.getLinkageManager(cart.worldObj).createLink(track.taggedCart, cart);
                track.taggedCart = cart;
            }
        },
        DECOUPLER(0) {
            @Override
            public void onMinecartPass(TrackCoupler track, EntityMinecart cart) {
                CartTools.getLinkageManager(cart.worldObj).breakLinks(cart);
                LinkageManager.printDebug("Reason For Broken Link: Passed Decoupler Track.");
            }
        },
        AUTO_COUPLER(0) {
            @Override
            public void onMinecartPass(TrackCoupler track, EntityMinecart cart) {
                LinkageManager.instance().setAutoLink(cart, true);
            }
        };
        public static Mode[] VALUES = values();
        private int powerPropagation;

        Mode(int powerPropagation) {
            this.powerPropagation = powerPropagation;
        }

        public static Mode fromOrdinal(int ordinal) {
            return VALUES[ordinal % VALUES.length];
        }

        public Mode next() {
            return fromOrdinal(ordinal() + 1);
        }

        public Mode previous() {
            return fromOrdinal(ordinal() + VALUES.length - 1);
        }

        public String getTag() {
            return name().replace('_', '.').toLowerCase(Locale.ENGLISH);
        }

        public abstract void onMinecartPass(TrackCoupler track, EntityMinecart cart);
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.COUPLER;
    }

    @Override
    public IIcon getIcon() {
        int iconIndex = 0;
        if (!isPowered())
            iconIndex++;
        iconIndex += mode.ordinal() * 2;
        return getIcon(iconIndex);
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getX(), getY(), getZ())) {
                Mode m;
                if (player.isSneaking())
                    m = mode.previous();
                else
                    m = mode.next();
                crowbar.onWhack(player, current, getX(), getY(), getZ());
                if (Game.isHost(getWorld()))
                    setMode(m);
                else
                    ChatPlugin.sendLocalizedChat(player, "railcraft.gui.track.mode.change", "\u00A75" + LocalizationPlugin.translate("railcraft.gui.track.coupler.mode." + m.getTag()));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            mode.onMinecartPass(this, cart);
        }
    }

    public void setMode(Mode m) {
        if (mode != m) {
            mode = m;
            sendUpdateToClient();
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
    public boolean canPropagatePowerTo(ITrackInstance track) {
        if (track instanceof TrackCoupler) {
            TrackCoupler c = (TrackCoupler) track;
            return mode == c.mode;
        }
        return false;
    }

    @Override
    public int getPowerPropagation() {
        return mode.powerPropagation;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("mode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");

        mode = Mode.fromOrdinal(data.getByte("mode"));

        if (data.getBoolean("decouple"))
            mode = Mode.DECOUPLER;

        if (data.getInteger("trackId") == EnumTrack.DECOUPLER.ordinal())
            mode = Mode.DECOUPLER;
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeByte(mode.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean p = data.readBoolean();
        byte m = data.readByte();

        boolean needsUpdate = false;
        if (p != powered) {
            powered = p;
            needsUpdate = true;
        }
        if (m != mode.ordinal()) {
            mode = Mode.fromOrdinal(m);
            needsUpdate = true;
        }

        if (needsUpdate)
            markBlockNeedsUpdate();
    }

}