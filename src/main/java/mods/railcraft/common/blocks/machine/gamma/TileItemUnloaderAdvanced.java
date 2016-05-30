/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.common.util.misc.MiscTools;

import mods.railcraft.common.util.network.RailcraftDataInputStream;
import mods.railcraft.common.util.network.RailcraftDataOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileItemUnloaderAdvanced extends TileItemUnloader {

    private EnumFacing direction = EnumFacing.NORTH;

    @Override
    public EnumMachineGamma getMachineType() {
        return EnumMachineGamma.ITEM_UNLOADER_ADVANCED;
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);

        nbttagcompound.setByte("direction", (byte) direction.ordinal());
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        direction = EnumFacing.getFront(data.getByte("direction"));
    }

    @Override
    public void writePacketData(@Nonnull RailcraftDataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(@Nonnull RailcraftDataInputStream data) throws IOException {
        super.readPacketData(data);

        direction = EnumFacing.getFront(data.readByte());
    }

    @Override
    public void onBlockPlacedBy(@Nonnull IBlockState state, @Nonnull EntityLivingBase entityliving, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(state, entityliving, stack);
        direction = MiscTools.getSideFacingTrack(worldObj, getPos());
        if (direction == null)
            direction = MiscTools.getSideFacingPlayer(getPos(), entityliving);
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public EnumFacing getOrientation() {
        return direction;
    }
}
