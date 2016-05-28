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

import mods.railcraft.api.tracks.ITrackReversible;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackDisembark extends TrackPowered implements ITrackReversible {

    private static final int TIME_TILL_NEXT_MOUNT = 40;
    private boolean mirrored;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.DISEMBARK;
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(REVERSED, mirrored);
        return state;
    }

    @Override
    public void onMinecartPass(@Nonnull EntityMinecart cart) {
        if (isPowered() && cart.canBeRidden() && cart.riddenByEntity != null) {
            double x = getPos().getX();
            double z = getPos().getZ();
            double offset = 1.5;
            IBlockState state = getWorld().getBlockState(getPos());
            EnumRailDirection dir = TrackTools.getTrackDirectionRaw(state);
            if (dir == EnumRailDirection.NORTH_SOUTH)
                if (mirrored)
                    x += offset;
                else
                    x -= offset;
            else if (mirrored)
                z += offset;
            else
                z -= offset;
            CartUtils.dismount(cart, x + 0.5, getPos().getY() + 1, z + 0.5);
            cart.getEntityData().setInteger("MountPrevention", TIME_TILL_NEXT_MOUNT);
        }
    }

    @Override
    public boolean isReversed() {
        return mirrored;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.mirrored = reversed;
    }

    @Override
    public void writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("mirrored", mirrored);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        mirrored = nbttagcompound.getBoolean("mirrored");
    }

    @Override
    public void writePacketData(@Nonnull DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(mirrored);
    }

    @Override
    public void readPacketData(@Nonnull DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean m = data.readBoolean();
        if (mirrored != m) {
            mirrored = m;
            markBlockNeedsUpdate();
        }
    }

}
