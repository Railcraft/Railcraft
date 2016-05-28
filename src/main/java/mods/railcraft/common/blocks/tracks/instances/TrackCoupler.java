/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackCoupler extends TrackPowered {
    public static final PropertyEnum<Mode> MODE = PropertyEnum.create("mode", Mode.class);
    private EntityMinecart taggedCart;
    private Mode mode = Mode.COUPLER;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.COUPLER;
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(MODE, mode);
        return state;
    }

    @Override
    public boolean blockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, ItemStack heldItem) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getPos())) {
                Mode m;
                if (player.isSneaking())
                    m = mode.previous();
                else
                    m = mode.next();
                crowbar.onWhack(player, current, getPos());
                if (Game.isHost(getWorld()))
                    setMode(m);
                else
                    ChatPlugin.sendLocalizedChat(player, "railcraft.gui.track.mode.change", "\u00A75" + LocalizationPlugin.translate("railcraft.gui.track.coupler.mode." + m.getName()));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(@Nonnull EntityMinecart cart) {
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
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("mode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        mode = Mode.fromOrdinal(data.getByte("mode"));

        if (data.getBoolean("decouple"))
            mode = Mode.DECOUPLER;

        if (data.getInteger("trackId") == EnumTrack.DECOUPLER.ordinal())
            mode = Mode.DECOUPLER;
    }

    @Override
    public void writePacketData(@Nonnull DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(mode.ordinal());
    }

    @Override
    public void readPacketData(@Nonnull DataInputStream data) throws IOException {
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
            public void onMinecartPass(TrackCoupler track, EntityMinecart cart) {
                CartTools.getLinkageManager(cart.worldObj).createLink(track.taggedCart, cart);
                track.taggedCart = cart;
            }
        },
        DECOUPLER("decoupler", 0) {
            @Override
            public void onMinecartPass(TrackCoupler track, EntityMinecart cart) {
                CartTools.getLinkageManager(cart.worldObj).breakLinks(cart);
                LinkageManager.printDebug("Reason For Broken Link: Passed Decoupler Track.");
            }
        },
        AUTO_COUPLER("auto.coupler", 0) {
            @Override
            public void onMinecartPass(TrackCoupler track, EntityMinecart cart) {
                LinkageManager.instance().setAutoLink(cart, true);
            }
        };
        public static Mode[] VALUES = values();
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

        public abstract void onMinecartPass(TrackCoupler track, EntityMinecart cart);
    }

}
