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
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;

public abstract class TileSignalBase extends TileSignalFoundation implements ISignalTile, IAspectProvider {

    private static final ForgeDirection[] UP_DOWN_AXES = new ForgeDirection[]{UP, DOWN};
    protected static final float BOUNDS = 0.15f;
    private ForgeDirection facing = ForgeDirection.NORTH;
    private int prevLightValue;

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
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
    public ForgeDirection[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        getBlockType().setBlockBounds(BOUNDS, 0.35f, BOUNDS, 1 - BOUNDS, 1f, 1 - BOUNDS);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i + BOUNDS, j + 0.35f, k + BOUNDS, i + 1 - BOUNDS, j + 1, k + 1 - BOUNDS);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i + BOUNDS, j + 0.35f, k + BOUNDS, i + 1 - BOUNDS, j + 1, k + 1 - BOUNDS);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(Game.isNotHost(worldObj)){
            boolean needsUpdate = false;
            int lightValue = getLightValue();
            if (prevLightValue != lightValue) {
                prevLightValue = lightValue;
                worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
                needsUpdate = true;
            }
            if (needsUpdate) {
                markBlockForUpdate();
            }
        }
    }

    protected boolean isLit(SignalAspect aspect) {
        return aspect != SignalAspect.OFF && !aspect.isBlinkAspect();
    }

    protected boolean isLit() {
      return isLit(getSignalAspect());
    }

    protected boolean isBlinking() {
        return getSignalAspect().isBlinkAspect();
    }

    @Override
    public int getLightValue() {
        if (isLit()) {
            return 5;
        }
        if(isBlinking()) {
            return 3;
        }
        return 0;
    }

    public void setFacing(ForgeDirection facing) {
        this.facing = facing;
    }

    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        facing = MiscTools.getHorizontalSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("Facing", (byte) facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = ForgeDirection.getOrientation(data.getByte("Facing"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        facing = ForgeDirection.getOrientation(data.readByte());

        markBlockForUpdate();
    }

    public abstract SignalAspect getSignalAspect();

    @Override
    public SignalAspect getTriggerAspect() {
        return getSignalAspect();
    }
}
