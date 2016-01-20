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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class TileBoxBase extends TileSignalFoundation {

    private static final float BOUND = 0.1f;

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        getBlockType().setBlockBounds(BOUND, 0, BOUND, 1 - BOUND, 1 - BOUND / 2, 1 - BOUND);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.fromBounds(i + BOUND, j, k + BOUND, i + 1 - BOUND, j + 1 - BOUND / 2, k + 1 - BOUND);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.fromBounds(i + BOUND, j, k + BOUND, i + 1 - BOUND, j + 1 - BOUND / 2, k + 1 - BOUND);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    public abstract boolean isConnected(EnumFacing side);

    public abstract SignalAspect getBoxSignalAspect(EnumFacing side);

    public boolean canTransferAspect() {
        return false;
    }

    public boolean canReceiveAspect() {
        return false;
    }

    public void onNeighborStateChange(TileBoxBase neighbor, EnumFacing side) {
    }

    public final void updateNeighborBoxes() {
        for (int side = 2; side < 6; side++) {
            EnumFacing forgeSide = EnumFacing.getOrientation(side);
            TileEntity tile = tileCache.getTileOnSide(forgeSide);
            if (tile instanceof TileBoxBase) {
                TileBoxBase box = (TileBoxBase) tile;
                box.onNeighborStateChange(this, forgeSide);
            }
        }
    }

    public boolean isEmittingRedstone(EnumFacing side) {
        return false;
    }

    public boolean canEmitRedstone(EnumFacing side) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int i, int j, int k, EnumFacing side) {
        return side == EnumFacing.UP;
    }

    @Override
    public boolean canConnectRedstone(int dir) {
        return true;
    }

}
