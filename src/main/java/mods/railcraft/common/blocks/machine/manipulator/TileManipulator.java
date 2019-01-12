/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Created by CovertJaguar on 9/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileManipulator extends TileMachineItem implements ITileRotate {
    protected EnumFacing facing = getDefaultFacing();

    protected TileManipulator() {
    }

    protected TileManipulator(int invSize) {
        super(invSize);
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        if (canRotate(EnumFacing.NORTH)) {
            EnumFacing newFacing = MiscTools.getSideFacingTrack(world, getPos());
            if (newFacing == null) {
                if (entityLiving != null) {
                    newFacing = MiscTools.getSideFacingPlayer(getPos(), entityLiving);
                } else newFacing = getDefaultFacing();
            }
            facing = newFacing;
        }
    }

    public EnumFacing getDefaultFacing() {
        return getValidRotations()[0];
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (canRotate(EnumFacing.NORTH))
            data.setByte("direction", (byte) facing.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (canRotate(EnumFacing.NORTH))
            facing = EnumFacing.byIndex(data.getByte("direction"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        if (canRotate(EnumFacing.NORTH))
            data.writeByte(facing.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        if (canRotate(EnumFacing.NORTH))
            facing = EnumFacing.byIndex(data.readByte());
    }

}
