/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitCoupler extends TrackKitPowered {
    private @Nullable EntityMinecart taggedCart;
    private Mode mode = Mode.COUPLER;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.COUPLER;
    }

    @Override
    public int getRenderState() {
        int state = mode.ordinal();
        if (isPowered())
            state += Mode.VALUES.length;
        return state;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                Mode m;
                if (player.isSneaking())
                    m = mode.previous();
                else
                    m = mode.next();
                crowbar.onWhack(player, hand, heldItem, getPos());
                if (Game.isHost(theWorldAsserted()))
                    setMode(m);
                else
                    ChatPlugin.sendLocalizedChat(player, "gui.railcraft.track.mode.change", "\u00A75" + LocalizationPlugin.translate("gui.railcraft.track.coupler.mode." + m.getName()));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            mode.onMinecartPass(this, cart);
        } else {
            mode.onMinecartPassUnpowered(this, cart);
        }
    }

    public void setMode(Mode m) {
        if (mode != m) {
            mode = m;
            sendUpdateToClient();
        }
    }

    @Override
    public boolean canPropagatePowerTo(ITrackKitInstance track) {
        if (track instanceof TrackKitCoupler) {
            TrackKitCoupler c = (TrackKitCoupler) track;
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
        data.setByte("mode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        mode = Mode.fromOrdinal(data.getByte("mode"));

        if (data.getBoolean("decouple"))
            mode = Mode.DECOUPLER;
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(mode.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        byte m = data.readByte();

        if (m != mode.ordinal()) {
            mode = Mode.fromOrdinal(m);
            markBlockNeedsUpdate();
        }
    }

    public enum Mode implements IStringSerializable {
        COUPLER("coupler", 8) {
            @Override
            public void onMinecartPass(TrackKitCoupler track, EntityMinecart cart) {
                if (track.taggedCart != null)
                    CartToolsAPI.linkageManager().createLink(track.taggedCart, cart);
                track.taggedCart = cart;
            }
        },
        DECOUPLER("decoupler", 0) {
            @Override
            public void onMinecartPass(TrackKitCoupler track, EntityMinecart cart) {
                CartToolsAPI.linkageManager().breakLinks(cart);
                LinkageManager.printDebug("Reason For Broken Link: Passed Decoupler Track.");
            }
        },
        AUTO_COUPLER("auto.coupler", 0) {
            @Override
            public void onMinecartPass(TrackKitCoupler track, EntityMinecart cart) {
                LinkageManager.INSTANCE.setAutoLink(cart, true);
            }

            @Override
            public void onMinecartPassUnpowered(TrackKitCoupler track, EntityMinecart cart) {
                LinkageManager.INSTANCE.setAutoLink(cart, false);
            }
        };
        public static final Mode[] VALUES = values();
        private final int powerPropagation;
        private final String name;

        Mode(String name, int powerPropagation) {
            this.name = name;
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

        @Override
        public String getName() {
            return name;
        }

        public abstract void onMinecartPass(TrackKitCoupler track, EntityMinecart cart);

        public void onMinecartPassUnpowered(TrackKitCoupler track, EntityMinecart cart) {}
    }

}
