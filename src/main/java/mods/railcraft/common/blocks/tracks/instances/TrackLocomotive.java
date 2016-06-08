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
import mods.railcraft.common.carts.EntityLocomotive.LocoMode;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
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

public class TrackLocomotive extends TrackPowered {

    public static final PropertyEnum<LocoMode> LOCO_MODE = PropertyEnum.create("locoMode", LocoMode.class);

    private LocoMode mode = LocoMode.RUNNING;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.LOCOMOTIVE;
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(LOCO_MODE, mode);
        return state;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (heldItem != null && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                mode = EnumTools.next(mode, LocoMode.VALUES);
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
                ((EntityLocomotive) cart).setMode(mode);
            }
        }
    }

    @Override
    public int getPowerPropagation() {
        return 8;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("mode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        mode = NBTPlugin.readEnumOrdinal(data, "mode", LocoMode.VALUES, LocoMode.RUNNING);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        RailcraftOutputStream outputStream = new RailcraftOutputStream(data);
        outputStream.writeEnum(mode);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        RailcraftInputStream inputStream = new RailcraftInputStream(data);
        mode = inputStream.readEnum(LocoMode.VALUES);

        markBlockNeedsUpdate();
    }
}
