/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class TileItemUnloaderAdvanced extends TileItemUnloader {

    private ForgeDirection direction = ForgeDirection.NORTH;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.ITEM_UNLOADER_ADVANCED;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);

        nbttagcompound.setByte("direction", (byte) direction.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        direction = ForgeDirection.getOrientation(data.getByte("direction"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        direction = ForgeDirection.getOrientation(data.readByte());
    }

    @Override
    public IIcon getIcon(int side) {
        if (direction.ordinal() == side)
            return getMachineType().getTexture(3);
        if (side != 0 && side != 1)
            return getMachineType().getTexture(2);
        return getMachineType().getTexture(1);
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        direction = MiscTools.getSideFacingTrack(worldObj, xCoord, yCoord, zCoord);
        if (direction == ForgeDirection.UNKNOWN)
            direction = MiscTools.getSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public ForgeDirection getOrientation() {
        return direction;
    }

}
