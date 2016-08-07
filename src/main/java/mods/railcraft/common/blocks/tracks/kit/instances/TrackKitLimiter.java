/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kit.instances;

import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.kit.TrackKits;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotive.LocoSpeed;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitLimiter extends TrackKitPowered {
    public static final PropertyEnum<LocoSpeed> MODE = PropertyEnum.create("mode", LocoSpeed.class);
    private LocoSpeed mode = LocoSpeed.MAX;

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.LIMITER;
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
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (heldItem != null && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                setMode(mode.ordinal() + 1);
                crowbar.onWhack(player, hand, heldItem, getPos());
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
        NBTPlugin.writeEnumName(data, "locoSpeed", mode);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("locoSpeed")) {
            mode = NBTPlugin.readEnumName(data, "locoSpeed", LocoSpeed.MAX);
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
