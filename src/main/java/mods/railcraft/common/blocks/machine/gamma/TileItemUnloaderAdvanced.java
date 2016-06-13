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
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class TileItemUnloaderAdvanced extends TileItemUnloader {

    private EnumFacing direction = EnumFacing.NORTH;

    @Override
    public EnumMachineGamma getMachineType() {
        return EnumMachineGamma.ITEM_UNLOADER_ADVANCED;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setByte("direction", (byte) direction.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        direction = EnumFacing.getFront(data.getByte("direction"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        direction = EnumFacing.getFront(data.readByte());
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase entityliving, ItemStack stack) {
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
