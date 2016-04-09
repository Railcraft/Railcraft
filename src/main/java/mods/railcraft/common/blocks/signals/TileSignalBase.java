/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public abstract class TileSignalBase extends TileSignalFoundation implements IAspectProvider {

    private static final EnumFacing[] UP_DOWN_AXES = new EnumFacing[]{UP, DOWN};
    protected static final float BOUNDS = 0.15f;
    private EnumFacing facing = EnumFacing.NORTH;
    private int prevLightValue;

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (axis == UP || axis == DOWN) {
            return false;
        }
        if (facing == axis) {
            facing = axis.getOpposite();
        } else {
            facing = axis;
        }
        markBlockForUpdate();
        return true;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        getBlockType().setBlockBounds(BOUNDS, 0.35f, BOUNDS, 1 - BOUNDS, 1f, 1 - BOUNDS);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos) {
        return AABBFactory.make().createBoxForTileAt(pos).expandHorizontally(-BOUNDS).raiseFloor(0.35).build();
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isNotHost(worldObj)) {
            boolean needsUpdate = false;
            int lightValue = getLightValue();
            if (prevLightValue != lightValue) {
                prevLightValue = lightValue;
                worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
                needsUpdate = true;
            }
            if (needsUpdate) {
                markBlockForUpdate();
            }
        }
    }

    public int getLightValue() {
        return getSignalAspect().getLightValue();
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        facing = MiscTools.getHorizontalSideFacingPlayer(entityLiving);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("Facing", (byte) facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = EnumFacing.getFront(data.getByte("Facing"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        facing = EnumFacing.getFront(data.readByte());

        markBlockForUpdate();
    }

    public abstract SignalAspect getSignalAspect();

    @Override
    public SignalAspect getTriggerAspect() {
        return getSignalAspect();
    }
}
