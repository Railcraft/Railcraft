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

import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackLimiter extends TrackPowered {
    public static final PropertyEnum<LocoSpeed> MODE = PropertyEnum.create("mode", LocoSpeed.class);
    private LocoSpeed mode;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.LIMITER;
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(MODE, getMode());
        return state;
    }

    public LocoSpeed getMode() {
        return mode;
    }

    public void setMode(int i) {
        mode = LocoSpeed.VALUES[i % LocoSpeed.VALUES.length];
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && current.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) current.getItem();
            if (crowbar.canWhack(player, current, getPos())) {
                setMode(mode.ordinal() + 1);
                crowbar.onWhack(player, current, getPos());
                sendUpdateToClient();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            if (cart instanceof EntityLocomotive) {
                ((EntityLocomotive) cart).setSpeed(getMode());
            }
        }
    }

    @Override
    public int getPowerPropagation() {
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setString("locoSpeed", mode.getName());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("locoSpeed")) {
            mode = LocoSpeed.fromName(data.getString("locoSpeed"));
        } else if (data.hasKey("mode")) {
            setMode(data.getByte("mode") % 4);
        } else {
            setMode(data.getByte("mode5"));
        }
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) mode.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        int m = data.readByte();
        if (mode.ordinal() != m) {
            setMode(m);
            markBlockNeedsUpdate();
        }
    }
}
